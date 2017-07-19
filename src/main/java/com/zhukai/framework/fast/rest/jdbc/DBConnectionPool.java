package com.zhukai.framework.fast.rest.jdbc;

import com.zhukai.framework.fast.rest.config.DataSource;
import com.zhukai.framework.fast.rest.exception.DBConnectTimeoutException;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.LongAdder;

public class DBConnectionPool {
    private static Logger logger = Logger.getLogger(DBConnectionPool.class);

    private LongAdder checkOutSize = new LongAdder();
    private BlockingQueue<Connection> freeConnPool = new LinkedBlockingQueue<>();
    private DataSource dataSource;

    public static DBConnectionPool getInstance() {
        return instance;
    }

    private DBConnectionPool() {

    }

    private static DBConnectionPool instance = new DBConnectionPool();

    public void freeConnection(Connection con) throws InterruptedException {
        if (con == null) {
            return;
        }
        try {
            con.setAutoCommit(true);
        } catch (SQLException e) {
            logger.error(e);
        }
        freeConnPool.put(con);
        checkOutSize.add(-1);
        notify();
    }

    public Connection getConnection() throws Exception {
        if (dataSource == null) {
            return null;
        }
        return getConnection(true);
    }

    public void init(DataSource source) throws Exception {
        dataSource = source;
        Class.forName(source.getDriverClass());
        for (int i = 0; i < source.getMinConn(); i++) {
            Connection connection = DriverManager.getConnection(source.getUrl(),
                    source.getUsername(), source.getPassword());
            freeConnPool.put(connection);
        }
    }

    public static void commit(Connection conn) throws SQLException, InterruptedException {
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

    private Connection getConnection(boolean isFirst) throws Exception {
        if (!freeConnPool.isEmpty()) {
            checkOutSize.add(1);
            return freeConnPool.take();
        } else if (checkOutSize.intValue() < dataSource.getMaxConn()) {
            Connection connection = DriverManager.getConnection(dataSource.getUrl(),
                    dataSource.getUsername(), dataSource.getPassword());
            checkOutSize.add(1);
            return connection;
        } else if (isFirst) {
            wait(dataSource.getTimeout());
            return getConnection(false);
        } else {
            throw new DBConnectTimeoutException();
        }
    }
}
