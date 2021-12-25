package com.seasidechachacha.ConnectionPool;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;

public class ConnectionPool {
	private static final LinkedList<Connection> availableConnections = new LinkedList<>();
	private int maxConnection;

	public ConnectionPool(int maxConnection) {
		this.maxConnection = maxConnection;
		initializeConnectionPool();
	}

	private synchronized void initializeConnectionPool() {
		try {
			while (!checkIfConnectionPoolIsFull()) {
				Connection newConnection = BasicConnection.getConnection();
				availableConnections.add(newConnection);
			}
			notifyAll();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private boolean checkIfConnectionPoolIsFull() {
		return availableConnections.size() >= maxConnection;
	}

	public synchronized Connection getConnection() {
		while (availableConnections.size() == 0) {
			// Wait for an existing connection to be freed up.
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		// Retrieves and removes the first element of this list
		return availableConnections.poll();
	}

	public synchronized boolean releaseConnection(Connection connection) {
		boolean isReleased = false;
		try {
			if (connection.isClosed()) {
				initializeConnectionPool();
			} else {
				// Adds the specified element as the last element of this list
				isReleased = availableConnections.offer(connection);
				// Wake up threads that are waiting for a connection
				notifyAll();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return isReleased;
	}
}
