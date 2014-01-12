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

/**
 * HSQL database service interface.
 *
 * @author spevakl
 *
 */
public interface IDatabaseServerService {

    String getDbPath();

    void setDbPath(String dbPath);

    String getDbName();

    void setDbName(String dbName);

    int getDbPort();

    void setDbPort(int dbPort);

    void startServer();

    void stopServer();

}