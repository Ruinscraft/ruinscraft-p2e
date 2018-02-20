package com.ruinscraft.p2e.data.storage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import com.ruinscraft.p2e.data.RuinscraftPlayer;
import com.ruinscraft.p2e.data.RuinscraftPlayerMetaEntry;


public class MySqlStorage implements SqlStorage {

	private final MySqlConnectionProvider provider;

	// ruinscraft_player_data
	public static final String CREATE_TABLE_PLAYER_DATA = "CREATE TABLE IF NOT EXISTS ruinscraft_player_data (id INT UNSIGNED AUTO_INCREMENT NOT NULL, uuid VARCHAR (36), PRIMARY KEY (id));";
	public static final String INSERT_INTO_PLAYER_DATA = "INSERT INTO ruinscraft_player_data (uuid) VALUES (?);";
	public static final String SELECT_FROM_PLAYER_DATA = "SELECT * FROM ruinscraft_player_data WHERE uuid = ?;";

	// ruinscraft_player_meta
	public static final String CREATE_TABLE_PLAYER_META = "CREATE TABLE IF NOT EXISTS ruinscraft_player_meta (ruinscraft_player_id INT UNSIGNED NOT NULL, last_updated BIGINT DEFAULT 0, server VARCHAR (64) DEFAULT \"global\", meta_key VARCHAR (64), meta_value VARCHAR (256), FOREIGN KEY (ruinscraft_player_id) REFERENCES ruinscraft_player_data (id), UNIQUE KEY meta_entry (ruinscraft_player_id, server, meta_key));";
	public static final String INSERT_INTO_PLAYER_META = "INSERT INTO ruinscraft_player_meta (ruinscraft_player_id, last_updated, server, meta_key, meta_value) VALUES (?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE last_updated = ?, meta_value = ?;";
	public static final String SELECT_FROM_PLAYER_META = "SELECT * FROM ruinscraft_player_meta WHERE ruinscraft_player_id = ? AND server = ?;";

	// ruinscraft_server_meta
	public static final String CREATE_TABLE_SERVER_META = "CREATE TABLE IF NOT EXISTS ruinscraft_server_meta (ruinscraft_server_name VARCHAR (64) NOT NULL, program VARCHAR (64), meta_key VARCHAR (64), meta_value VARCHAR (256), UNIQUE KEY meta_entry (ruinscraft_server_name, program, meta_key));";
	public static final String INSERT_INTO_SERVER_META = "INSERT INTO ruinscraft_server_meta (ruinscraft_server_name, program, meta_key, meta_value) VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE meta_value = ?;";
	public static final String SELECT_FROM_SERVER_META = "SELECT * FROM ruinscraft_server_meta WHERE ruinscraft_server_name = ? AND program = ? AND meta_key = ?;";

	public MySqlStorage(MySqlConnectionProvider provider) {
		this.provider = provider;
	}

	@Override
	public MySqlConnectionProvider getProvider() {
		return provider;
	}

	@Override
	public void checkTables() {

		try (Connection c = provider.getConnection()) {

			try (PreparedStatement ps = c.prepareStatement(CREATE_TABLE_PLAYER_DATA)) {

				ps.executeUpdate();

			}

			try (PreparedStatement ps = c.prepareStatement(CREATE_TABLE_PLAYER_META)) {

				ps.executeUpdate();

			}

			try (PreparedStatement ps = c.prepareStatement(CREATE_TABLE_SERVER_META)) {

				ps.executeUpdate();

			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	@Override
	public RuinscraftPlayer getPlayer(UUID uuid) {

		checkTables();

		RuinscraftPlayer ruinscraftPlayer = new RuinscraftPlayer();

		ruinscraftPlayer.setMojangUuid(uuid);

		try (Connection c = provider.getConnection()) {

			try (PreparedStatement ps = c.prepareStatement(SELECT_FROM_PLAYER_DATA)) {

				ps.setString(1, uuid.toString());

				try (ResultSet rs = ps.executeQuery()) {

					if (rs.next()) {
						// Player found by uuid in the database

						ruinscraftPlayer.setId(rs.getLong("id"));

					} else {
						// Player not found by uuid, insert

						try (PreparedStatement ps1 = c.prepareStatement(INSERT_INTO_PLAYER_DATA, Statement.RETURN_GENERATED_KEYS)) {

							ps1.setString(1, uuid.toString());

							ps1.executeUpdate();

							try (ResultSet rs1 = ps1.getGeneratedKeys()) {

								while (rs1.next()) {

									ruinscraftPlayer.setId(rs1.getLong(1));

								}

							}

						}

					}

					return ruinscraftPlayer;

				}

			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return ruinscraftPlayer;

	}

	@Override
	public void loadMeta(RuinscraftPlayer ruinscraftPlayer, String server) {

		try (Connection c = provider.getConnection()) {

			try (PreparedStatement ps = c.prepareStatement(SELECT_FROM_PLAYER_META)) {

				ps.setLong(1, ruinscraftPlayer.getId());
				ps.setString(2, server);

				try (ResultSet rs = ps.executeQuery()) {

					while (rs.next()) {

						RuinscraftPlayerMetaEntry rpme = new RuinscraftPlayerMetaEntry();

						rpme.setLastUpdated(rs.getLong("last_updated"));
						rpme.setValue(rs.getObject("meta_value"));

						ruinscraftPlayer.setMeta(server.toLowerCase(), rs.getString("meta_key"), rpme);

					}

				}

			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void saveMeta(long ruinscraftPlayerId, String server, String key, RuinscraftPlayerMetaEntry meta) {

		try (Connection c = provider.getConnection()) {

			try (PreparedStatement ps = c.prepareStatement(INSERT_INTO_PLAYER_META)) {

				ps.setLong(1, ruinscraftPlayerId);
				ps.setLong(2, meta.getLastUpdated());
				ps.setString(3, server);
				ps.setString(4, key);
				ps.setObject(5, meta.getValue());
				ps.setLong(6, meta.getLastUpdated());
				ps.setObject(7, meta.getValue());

				ps.executeUpdate();

			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	@Override
	public String getLastUsername(long ruinscraftPlayerId) {

		String username = "";

		try (Connection c = provider.getConnection()) {

			try (PreparedStatement ps = c.prepareStatement("SELECT meta_value FROM ruinscraft_player_meta WHERE ruinscraft_player_id = ? AND server = \"global\" AND meta_key = \"last-username\";")) {

				ps.setLong(1, ruinscraftPlayerId);
				
				try (ResultSet rs = ps.executeQuery()) {

					while (rs.next()) {
						username = rs.getString("meta_value");
					}

				}

			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return username;
		
	}

}
