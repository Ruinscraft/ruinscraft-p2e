package com.ruinscraft.p2e.data.storage;

import java.sql.Connection;
import java.sql.SQLException;

public interface SqlConnectionProvider {

	void init();
	
	void close();
	
	Connection getConnection() throws SQLException;
	
}