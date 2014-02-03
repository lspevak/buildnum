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

/**
 * Artifact version service.
 *
 * @author spevakl
 *
 */
public interface IVersionService {

    // HSQL support
     String SQL_INSERT = "INSERT INTO MVN_VERSION (ID, GROUP_ID, ARTIFACT_ID, ARTIFACT_VERSION, ARTIFACT_CLASSIFIER, BUILD_NUMBER, CREATED_AT, UPDATED_AT) "
            + "VALUES (NEXT VALUE FOR SQ_MVN_VERSION,?,?,?,?,?,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP)";

    String SQL_UPDATE_SET = "UPDATE MVN_VERSION SET BUILD_NUMBER=?, UPDATED_AT=CURRENT_TIMESTAMP "
            + "WHERE GROUP_ID=? AND ARTIFACT_ID=? AND ARTIFACT_VERSION=?";

    String SQL_SELECT = "SELECT ID, GROUP_ID, ARTIFACT_ID, ARTIFACT_VERSION, ARTIFACT_CLASSIFIER, BUILD_NUMBER, CREATED_AT, UPDATED_AT FROM MVN_VERSION WHERE 1=1";

    String SQL_CREATE_TABLE = "CREATE TABLE MVN_VERSION (ID INTEGER NOT NULL, GROUP_ID VARCHAR(100) NOT NULL, ARTIFACT_ID VARCHAR(100) NOT NULL, ARTIFACT_VERSION VARCHAR(50) NOT NULL, ARTIFACT_CLASSIFIER VARCHAR(50), BUILD_NUMBER INTEGER NOT NULL, CREATED_AT TIMESTAMP NOT NULL, UPDATED_AT TIMESTAMP NOT NULL, PRIMARY KEY(ID))";

    String SQL_CREATE_SEQ = "CREATE SEQUENCE SQ_MVN_VERSION";

    String getDbUser();

    void setDbUser(String dbUser);

    String getDbPassword();

    void setDbPassword(String dbPassword);

    String getDbUrl();

    void setDbUrl(String dbUrl);

    String getDbDriver();

    void setDbDriver(String dbDriver);

    VersionResponse getVersion(VersionRequest versionRequest);

    List<VersionResponse> getVersionList();

    void initDatabase();

}