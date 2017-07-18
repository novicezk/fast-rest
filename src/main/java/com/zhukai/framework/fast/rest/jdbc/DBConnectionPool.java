package com.zhukai.framework.fast.rest.jdbc;

import com.zhukai.framework.fast.rest.config.DataSource;
import com.zhukai.framework.fast.rest.exception.DBConnectTimeoutException;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;

public class DBConnectionPool {
    private static Logger logger = Logger.getLogger(DBConnectionPool.class);

    private int checkOutSize = 0;
    private LinkedList<Connection> freeConnPool = new LinkedList<>();
    private DataSource dataSource;

    public static DBConnectionPool getInstance() {
        return instance;
    }

    private DBConnectionPool() {

    }

    private static DBConnectionPool instance = new DBConnectionPool();

    public synchronized void freeConnection(Connection con) {
        if (con == null) {
            return;
        }
        try {
            con.setAutoCommit(true);
        } catch (SQLException e) {
            logger.error(e);
        }
        freeConnPool.addLast(con);
        checkOutSize--;
        notify();
    }

    public Connection getConnection() throws Exception {
        if (dataSource == null) {
            return null;
        }
        return getConnection(true);
    }

    public void init(DataSource source) throws ClassNotFoundException, SQLException {
        dataSource = source;
        Class.forName(source.getDriverClass());
        for (int i = 0; i < source.getMinConn(); i++) {
            Connection connection = DriverManager.getConnection(source.getUrl(),
                    source.getUsername(), source.getPassword());
            freeConnPool.add(connection);
        }
    }

    public static void commit(Connection conn) throws SQLException {
        try {
            conn.commit();
            logger.info("Transactional over");
        } catch (SQLException ex) {
            conn.rollback();
            logger.error("Transactional rollback：", ex);
        } finally {
            instance.freeConnection(conn);
        }
    }

    private synchronized Connection getConnection(boolean isFirst) throws SQLException, InterruptedException, DBConnectTimeoutException {
        if (freeConnPool.size() > 0) {
            checkOutSize++;
            return freeConnPool.poll();
        } else if (checkOutSize < dataSource.getMaxConn()) {
            Connection connection = DriverManager.getConnection(dataSource.getUrl(),
                    dataSource.getUsername(), dataSource.getPassword());
            checkOutSize++;
            return connection;
        } else if (isFirst) {
            wait(dataSource.getTimeout());
            return getConnection(false);
        } else {
            throw new DBConnectTimeoutException();
        }
    }
}
