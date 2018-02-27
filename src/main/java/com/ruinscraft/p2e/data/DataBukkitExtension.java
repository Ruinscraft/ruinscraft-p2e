package com.ruinscraft.p2e.data;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.ruinscraft.p2e.Extension;
import com.ruinscraft.p2e.P2Extensions;
import com.ruinscraft.p2e.data.storage.MySqlConnectionProvider;
import com.ruinscraft.p2e.data.storage.MySqlStorage;
import com.ruinscraft.p2e.data.storage.SaveMetaToStorageTask;
import com.ruinscraft.p2e.data.storage.SqlStorage;

public class DataBukkitExtension implements Data, Listener, Extension {
	
	private MySqlStorage sqlStorage;
	private RuinscraftPlayerManager playerManager;
	private SaveMetaToStorageTask saveTask;
	
	private String server;
	private static P2Extensions instance = P2Extensions.getInstance();
	
	private static DataBukkitExtension dataBukkit;
	
	public static DataBukkitExtension getDataBukkit() {
		return dataBukkit;
	}
	
	@Override
	public SqlStorage getSqlStorage() {
		return sqlStorage;
	}

	@Override
	public RuinscraftPlayerManager getRuinscraftPlayerManager() {
		return playerManager;
	}
	
	@Override
	public boolean enable() {
		
		dataBukkit = this;

		server = instance.getConfig().getString("server");

		instance.getServer().getPluginManager().registerEvents(this, instance);

		MySqlConnectionProvider provider = new MySqlConnectionProvider(instance.getConfig().getString("database.url"),
				instance.getConfig().getString("database.username"), instance.getConfig().getString("database.password"));
		
		sqlStorage = new MySqlStorage(provider);
		
		sqlStorage.checkTables();

		playerManager = new RuinscraftPlayerManager();

		saveTask = new SaveMetaToStorageTask(playerManager, sqlStorage);

		instance.getServer().getScheduler().runTaskTimerAsynchronously(instance, saveTask, 10L, 50L);
		
		return true;
		
	}
	
	@Override
	public boolean disable() {
		
		saveTask.cancel();

		System.out.println("Saving meta...");

		new SaveMetaToStorageTask(playerManager, sqlStorage).run();

		sqlStorage.getProvider().close();

		dataBukkit = null;

		return true;
		
	}

	// Run asynchronously
	public void loadRuinscraftPlayer(final UUID uuid) {

		instance.getServer().getScheduler().runTaskAsynchronously(instance, new Runnable() {

			@Override
			public void run() {

				final RuinscraftPlayer ruinscraftPlayer = sqlStorage.getPlayer(uuid);

				loadPlayerMeta(ruinscraftPlayer, "global");
				loadPlayerMeta(ruinscraftPlayer, server);

				playerManager.load(ruinscraftPlayer);

			}

		});

	}

	// Run asynchronously
	public void savePlayerMeta(final RuinscraftPlayer ruinscraftPlayer) {

		instance.getServer().getScheduler().runTaskAsynchronously(instance, new Runnable() {

			@Override
			public void run() {

				for (String server : ruinscraftPlayer.getMetaMap().keySet()) {

					for (String key : ruinscraftPlayer.getMetaMap().get(server).keySet()) {

						RuinscraftPlayerMetaEntry rpme = ruinscraftPlayer.getMetaMap().get(server).get(key);

						sqlStorage.saveMeta(ruinscraftPlayer.getId(), server, key, rpme);

					}

				}

			}

		});

	}

	// Run asynchronously
	public void loadPlayerMeta(RuinscraftPlayer ruinscraftPlayer, String server) {

		instance.getServer().getScheduler().runTaskAsynchronously(instance, new Runnable() {

			@Override
			public void run() {

				sqlStorage.loadMeta(ruinscraftPlayer, server);

			}

		});

	}

	public void unloadRuinscraftPlayer(long ruinscraftId) {

		playerManager.unload(ruinscraftId);

	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {

		event.setJoinMessage(null);

		Player player = event.getPlayer();

		loadRuinscraftPlayer(player.getUniqueId());

	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {

		event.setQuitMessage(null);

		Player player = event.getPlayer();

		unloadRuinscraftPlayer(playerManager.get(player.getUniqueId()).getId());

	}
	
	@EventHandler(priority=EventPriority.HIGH)
	public void onAsyncChat(AsyncPlayerChatEvent event) {
		
		Player player = event.getPlayer();
		
		String message = event.getMessage();
		
		if (!FilterUtils.isAppropriate(message, DataBukkitExtension.getWebpurifyKey())) {
			
			event.setCancelled(true);
			
			player.sendMessage(ChatColor.RED + "You can only use appropriate language in chat.");
			player.sendMessage(ChatColor.RED + "Message a staff member if what you said was a false-positive.");
			
			return;
			
		}
		
	}

	@EventHandler(priority=EventPriority.HIGH)
	public void onCommandPreprocess(PlayerCommandPreprocessEvent event) {
		
		Player player = event.getPlayer();
		
		String command = event.getMessage();
		
		if (!FilterUtils.isASCII(command)) {
			
			event.setCancelled(true);
			
			player.sendMessage(ChatColor.RED + "You may only use ASCII characters in commands.");
			
		}
		
	}
	
	public static String getWebpurifyKey() {

		return instance.getConfig().getString("webpurify-key");

	}
	
}
