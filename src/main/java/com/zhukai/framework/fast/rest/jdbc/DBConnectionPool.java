package com.zhukai.framework.fast.rest.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zhukai.framework.fast.rest.config.DataSource;
import com.zhukai.framework.fast.rest.exception.DBConnectTimeoutException;

public class DBConnectionPool {
	private static final Logger logger = LoggerFactory.getLogger(DBConnectionPool.class);

	private static LongAdder checkOutSize = new LongAdder();
	private static BlockingQueue<Connection> freeConnPool = new LinkedBlockingQueue<>();
	private static DataSource dataSource;

	public static void freeConnection(Connection con) throws InterruptedException {
		try {
			con.setAutoCommit(true);
		} catch (SQLException e) {
			logger.error("Set autoCommit error", e);
		}
		freeConnPool.put(con);
		checkOutSize.add(-1);
	}

	public static void init(DataSource source) throws Exception {
		dataSource = source;
		Class.forName(source.getDriverClass());
		for (int i = 0; i < source.getMinConn(); i++) {
			Connection connection = DriverManager.getConnection(source.getUrl(), source.getUsername(), source.getPassword());
			freeConnPool.put(connection);
		}
	}

	public static void commit(Connection conn) throws SQLException, InterruptedException {
		try {
			conn.commit();
		} catch (SQLException ex) {
			conn.rollback();
			logger.error("Transactional rollback error", ex);
		} finally {
			freeConnection(conn);
		}
	}

	public static Connection getConnection() throws Exception {
		if (!freeConnPool.isEmpty()) {
			checkOutSize.add(1);
			return freeConnPool.take();
		} else if (checkOutSize.intValue() < dataSource.getMaxConn()) {
			Connection connection = DriverManager.getConnection(dataSource.getUrl(), dataSource.getUsername(), dataSource.getPassword());
			checkOutSize.add(1);
			return connection;
		} else {
			Connection connection = freeConnPool.poll(dataSource.getTimeout(), TimeUnit.MILLISECONDS);
			if (connection == null) {
				throw new DBConnectTimeoutException();
			}
			return connection;
		}
	}
}
