/**
 * Copyright 2014 Libor Spevak
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */
package com.github.buildnum;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Locale;

import org.apache.log4j.Logger;

/**
 * Utilities.
 *
 * @author spevak
 *
 */
public class Utils {

    private static final Logger log = Logger.getLogger(Utils.class);

    private Utils() {
    }

    public static void registerDriver(String dbDriver) {
        try {
            Class.forName(dbDriver);
        } catch (Exception e) {
            throw new RuntimeException("Cannot register DB driver", e);
        }
    }

    public static Connection getConnection(String dbUrl, String dbUser,
            String dbPassword) {
        try {
            final Connection conn = DriverManager.getConnection(dbUrl, dbUser,
                    dbPassword);
            conn.setAutoCommit(true);

            return conn;
        } catch (Exception e) {
            throw new RuntimeException("Cannot get connection", e);
        }
    }

    public static Connection getConnectionWithTransaction(String dbUrl,
            String dbUser, String dbPassword) {
        try {
            final Connection conn = DriverManager.getConnection(dbUrl, dbUser,
                    dbPassword);
            conn.setAutoCommit(false);

            return conn;

        } catch (Exception e) {
            throw new RuntimeException("Cannot get connection", e);
        }
    }

    public static void rollback(Connection conn) {
        if (conn != null)
            try {
                conn.rollback();
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    public static void close(ResultSet rs, PreparedStatement s, Connection conn) {
        try {
            if (rs != null)
                rs.close();

            if (s != null)
                s.close();

            if (conn != null)
                conn.close();
        } catch (Exception e) {
            log.info("Cannot close DB resource: " + e.getMessage());
        }
    }

    public static PreparedStatement getPreparedStatement(Connection conn,
            String sql) {
        try {
            final PreparedStatement s = conn.prepareStatement(sql);
            s.setQueryTimeout(60);
            return s;
        } catch (Exception e) {
            throw new RuntimeException("Cannot prepare expression", e);
        }
    }

    public static String formatDate(java.util.Date d) {
        SimpleDateFormat f = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss",
                Locale.ENGLISH);
        return f.format(d);

    }

}
