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

import java.io.PrintWriter;

import org.hsqldb.Server;

/**
 * HSQL database service.
 *
 * @author spevakl
 *
 */
public class DatabaseServerService implements IDatabaseServerService {
    Server server;
    String dbPath;
    String dbName;
    int dbPort;

     /* (non-Javadoc)
     * @see com.github.buildnum.IDatabaseServerService#getDbPath()
     */
    @Override
    public String getDbPath() {
        return dbPath;
    }

    /* (non-Javadoc)
     * @see com.github.buildnum.IDatabaseServerService#setDbPath(java.lang.String)
     */
    @Override
    public void setDbPath(String dbPath) {
        this.dbPath = dbPath;
    }

    /* (non-Javadoc)
     * @see com.github.buildnum.IDatabaseServerService#getDbName()
     */
    @Override
    public String getDbName() {
        return dbName;
    }

    /* (non-Javadoc)
     * @see com.github.buildnum.IDatabaseServerService#setDbName(java.lang.String)
     */
    @Override
    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    /* (non-Javadoc)
     * @see com.github.buildnum.IDatabaseServerService#getDbPort()
     */
    @Override
    public int getDbPort() {
        return dbPort;
    }

    /* (non-Javadoc)
     * @see com.github.buildnum.IDatabaseServerService#setDbPort(int)
     */
    @Override
    public void setDbPort(int dbPort) {
        this.dbPort = dbPort;
    }

    /* (non-Javadoc)
     * @see com.github.buildnum.IDatabaseServerService#startServer()
     */
    @Override
    public void startServer() {
        server = new Server();

        server.setDatabaseName(0, dbName);
        server.setDatabasePath(0, dbPath);
        server.setPort(dbPort);
        //server.setLogWriter(new PrintWriter(System.out));
        //server.setErrWriter(new PrintWriter(System.out));
        server.start();
    }

    /* (non-Javadoc)
     * @see com.github.buildnum.IDatabaseServerService#stopServer()
     */
    @Override
    public void stopServer() {
        if (server != null) {
            server.stop();
        }
    }

}
