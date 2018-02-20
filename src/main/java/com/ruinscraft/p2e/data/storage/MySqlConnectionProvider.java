package com.ruinscraft.p2e.data.storage;

import java.sql.Connection;
import java.sql.SQLException;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class MySqlConnectionProvider implements SqlConnectionProvider {

	private final String jdbcUrl;
	private final String username;
	private final String password;
	private HikariDataSource hikari;
	
	public MySqlConnectionProvider(String jdbcUrl, String username, String password) {
	
		this.jdbcUrl = jdbcUrl;
		this.username = username;
		this.password = password;
		
		init();
		
	}
	
	public void init() {
		
		HikariConfig config = new HikariConfig();
		
		config.setPoolName("Ruinscraft-Data");
		config.setJdbcUrl(jdbcUrl);
		config.setUsername(username);
		config.setPassword(password);
		config.setMaximumPoolSize(10);
		config.setConnectionTestQuery("/* Ruinscraft Data ping */ SELECT 1");
		
		hikari = new HikariDataSource(config);
		
	}

	public void close() {
		if (hikari != null) {
			hikari.close();
		}
	}

	public Connection getConnection() throws SQLException {
		
		return hikari.getConnection();
		
	}

	
	
}
