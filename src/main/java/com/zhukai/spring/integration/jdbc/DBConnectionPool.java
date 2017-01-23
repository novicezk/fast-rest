package com.zhukai.spring.integration.jdbc;

import com.zhukai.spring.integration.logger.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;

/**
 * Created by zhukai on 17-1-18.
 */
public class DBConnectionPool {

    private static int checkOutSize = 0;

    private static LinkedList<Connection> freeConnPool = new LinkedList<>();

    private static DataSource dataSource;

    public synchronized static void freeConnection(Connection con) {
        freeConnPool.addLast(con);
        checkOutSize--;
    }

    public static synchronized Connection getConnection() {
        if (freeConnPool.size() > 0) {
            checkOutSize++;
            return freeConnPool.poll();
        } else if (checkOutSize < dataSource.getMaxConn()) {
            try {
                Connection connection = DriverManager.getConnection(dataSource.getUrl(),
                        dataSource.getUsername(), dataSource.getPassword());
                checkOutSize++;
                return connection;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static synchronized Connection getConnection(long time) {
        return null;
    }

    public static synchronized void release() {

    }

    public static void init(DataSource source) {
        dataSource = source;
        try {
            Class.forName(source.getDriverClass());
            for (int i = 0; i < source.getMinConn(); i++) {
                Connection connection = DriverManager.getConnection(source.getUrl(),
                        source.getUsername(), source.getPassword());
                freeConnPool.add(connection);
            }
        } catch (Exception e) {
            Logger.error();
            e.printStackTrace();
        }
    }

}
