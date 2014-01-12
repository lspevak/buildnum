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

import java.util.List;
import java.util.Map;

import com.github.buildnum.servlet.Conf;

/**
 * Artifact version service manager used as a singleton.
 *
 * @author spevakl
 */
public class VersionManager {

    private static final VersionManager instance = new VersionManager();

    IVersionService versionService = new VersionService();
    IDatabaseServerService serverService = new DatabaseServerService();

    // singleton
    public static final VersionManager getInstance() {
        return instance;
    }

    public void init(Map<String, String> paramMap) {
        versionService.setDbDriver(paramMap.get(Conf.DB_DRIVER));
        versionService.setDbUrl(paramMap.get(Conf.DB_URL));
        versionService.setDbUser(paramMap.get(Conf.DB_USER));
        versionService.setDbPassword(paramMap.get(Conf.DB_PASSWORD));

        serverService.setDbName(paramMap.get(Conf.DB_NAME));
        serverService.setDbPath(paramMap.get(Conf.DB_PATH));
        serverService.setDbPort(new Integer(paramMap.get(Conf.DB_PORT)));
    }

    public VersionResponse getVersion(VersionRequest versionRequest) {
        return versionService.getVersion(versionRequest);
    }

    public synchronized List<VersionResponse> getVersionList() {
        return versionService.getVersionList();
    }

    public void initDatabase() {
        versionService.initDatabase();
    }

    public void startServer() {
        serverService.startServer();
    }

    public void stopServer() {
        serverService.stopServer();
    }

}
