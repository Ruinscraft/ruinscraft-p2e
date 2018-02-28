package com.ruinscraft.p2e;

import com.google.common.io.ByteStreams;
import com.intellectualcrafters.plot.object.PlotPlayer;
import com.intellectualcrafters.plot.util.MathMan;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;

public class P2Util {
	
	private static Logger logger = Bukkit.getLogger();
	
	private static InputStream in;
	private static OutputStream out;
	
	public static final String prefix = "&8[&6Ruinscraft&8]&7 ";
	
	// Get com.intellectualcrafters.plot.object.Location from org.bukkit.Location
	public static com.intellectualcrafters.plot.object.Location getLocation(Location location) {
		return new com.intellectualcrafters.plot.object.Location(location.getWorld().getName(), 
				MathMan.roundInt(location.getX()), MathMan.roundInt(location.getY()), MathMan.roundInt(location.getZ()));
	}
	
	// Get org.bukkit.Location from com.intellectualcrafters.plot.object.Location
	public static Location getLocation(com.intellectualcrafters.plot.object.Location location) {
		return new Location(Bukkit.getWorld(location.getWorld()), location.getX(), location.getY(), location.getZ());
	}
	
	// Get PlotPlayer from Player
	public static PlotPlayer getPlayer(Player player) {
		return PlotPlayer.get(player.getName());
	}
	
	// Get Player from PlotPlayer
	public static Player getPlayer(PlotPlayer player) {
		return Bukkit.getPlayer(player.getUUID());
	}

	public static void messagePlayer(Player player, String msg) {
		player.sendMessage(P2Util.color(msg));
	}

	public static void log(String msg) { 
		logger.info(msg);
	}
	
	public static void warning(String msg) { 
		logger.warning(msg);
	}

	public static String color(String msg) {
		return ChatColor.translateAlternateColorCodes('&', msg);
	}

	public static String getNameEnding(String name) {
		
		if (name.endsWith("s") || name.endsWith("z")) { 
			return name + '\'';
		}
		
		return name + "'s";
		
	}

	public static File loadResource(String resource) {
		
		File folder = P2Extensions.getInstance().getDataFolder();
		if (!folder.exists()) folder.mkdir();

		File file = new File(folder, resource);

		try {
			
			if (!file.exists()) {
				
				file.createNewFile();

				try {
					in = P2Extensions.getInstance().getResource(resource); 
					out = new FileOutputStream(file);
				} finally {
					ByteStreams.copy(in, out);
				}
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return file;
		
	}
	
}