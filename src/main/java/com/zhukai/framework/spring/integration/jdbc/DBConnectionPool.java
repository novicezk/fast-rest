package com.zhukai.framework.spring.integration.jdbc;

import com.zhukai.framework.spring.integration.config.DataSource;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;

/**
 * Created by zhukai on 17-1-18.
 */
public class DBConnectionPool {
    private static Logger logger = Logger.getLogger(DBConnectionPool.class);

    private int checkOutSize = 0;
    private LinkedList<Connection> freeConnPool = new LinkedList<>();
    private DataSource dataSource;

    private DBConnectionPool() {

    }

    private static DBConnectionPool instance = new DBConnectionPool();

    public static DBConnectionPool getInstance() {
        return instance;
    }

    public synchronized void freeConnection(Connection con) {
        if (con == null) {
            return;
        }
        try {
            con.setAutoCommit(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        freeConnPool.addLast(con);
        checkOutSize--;
        notify();
    }

    public synchronized Connection getConnection(boolean isFirst) throws Exception {
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
            throw new Exception("获取数据库连接超时");
        }
    }

    public Connection getConnection() throws Exception {
        if (dataSource == null) {
            return null;
        }
        return getConnection(true);
    }

    public void init(DataSource source) {
        dataSource = source;
        try {
            Class.forName(source.getDriverClass());
            for (int i = 0; i < source.getMinConn(); i++) {
                Connection connection = DriverManager.getConnection(source.getUrl(),
                        source.getUsername(), source.getPassword());
                freeConnPool.add(connection);
            }
        } catch (Exception e) {
            logger.error("init datasource error", e);
        }
    }

    public static void commit(Connection conn) throws Exception {
        try {
            conn.commit();
            logger.info("Transactional over ");
        } catch (SQLException ex) {
            conn.rollback();
            logger.error("Transactional rollback：", ex);
        } finally {
            instance.freeConnection(conn);
        }
    }


}
