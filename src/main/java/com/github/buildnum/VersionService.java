/**
 * Copyright 2014 Libor Spevak
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */
package com.github.buildnum;

import static com.github.buildnum.Utils.close;
import static com.github.buildnum.Utils.getConnection;
import static com.github.buildnum.Utils.getConnectionWithTransaction;
import static com.github.buildnum.Utils.rollback;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Artifact version service implementation.
 *
 * @author spevakl
 */
public class VersionService implements IVersionService {

    private static final Logger log = LoggerFactory.getLogger(VersionManager.class);

    String dbUser;
    String dbPassword;
    String dbUrl;
    String dbDriver;

    @Override
    public String getDbDriver() {
        return dbDriver;
    }

    @Override
    public void setDbDriver(String dbDriver) {
        this.dbDriver = dbDriver;
    }

    /* (non-Javadoc)
     * @see com.github.buildnum.IVersionService#getDbUser()
     */
    @Override
    public String getDbUser() {
        return dbUser;
    }

    /* (non-Javadoc)
     * @see com.github.buildnum.IVersionService#setDbUser(java.lang.String)
     */
    @Override
    public void setDbUser(String dbUser) {
        this.dbUser = dbUser;
    }

    /* (non-Javadoc)
     * @see com.github.buildnum.IVersionService#getDbPassword()
     */
    @Override
    public String getDbPassword() {
        return dbPassword;
    }

    /* (non-Javadoc)
     * @see com.github.buildnum.IVersionService#setDbPassword(java.lang.String)
     */
    @Override
    public void setDbPassword(String dbPassword) {
        this.dbPassword = dbPassword;
    }

    /* (non-Javadoc)
     * @see com.github.buildnum.IVersionService#getDbUrl()
     */
    @Override
    public String getDbUrl() {
        return dbUrl;
    }

    /* (non-Javadoc)
     * @see com.github.buildnum.IVersionService#setDbUrl(java.lang.String)
     */
    @Override
    public void setDbUrl(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    /* (non-Javadoc)
     * @see com.github.buildnum.IVersionService#getVersion(com.github.buildnum.VersionRequest, boolean, boolean)
     */
    @Override
    public VersionResponse getVersion(VersionRequest versionRequest) {

        log.info("Getting version..." + "(groupId="
                + versionRequest.getGroupId() + ", artifactId="
                + versionRequest.getArtifactId() + ", version="
                + versionRequest.getArtifactVersion() + ", classifier="
                + versionRequest.getArtifactClassifier() + ", action="
                + versionRequest.getActionType() +
                ")");

        final String ver = versionRequest.getArtifactVersion();
        versionRequest.setArtifactVersion(versionRequest.isIgnoreSnapshot()
                && ver.endsWith("-SNAPSHOT") ? ver.substring(0,
                ver.length() - 9) : ver);

        final List<VersionResponse> list = getVersionList(versionRequest);
        final boolean exists = !list.isEmpty();

        VersionResponse versionResponse = null;

        if (!exists) {
            versionResponse = new VersionResponse();
            versionResponse.setGroupId(versionRequest.getGroupId());
            versionResponse.setArtifactId(versionRequest.getArtifactId());
            versionResponse.setArtifactClassifier(versionRequest.getArtifactClassifier());
            versionResponse.setArtifactVersion(versionRequest.getArtifactVersion());
            versionResponse.setBuildNumber(1);
        } else {
            versionResponse = list.get(0);
        }

        switch (versionRequest.getActionType()) {
            case RESET:
                versionResponse.setBuildNumber(1);
                break;
            case INCREMENT:
                if (exists) {
                    versionResponse.setBuildNumber(versionResponse.getBuildNumber() + 1);
                }

                break;
            case SET:
                if (versionRequest.getBuildNumber() != null && versionRequest.getBuildNumber() > 0) {
                    versionResponse.setBuildNumber(versionRequest.getBuildNumber());
                }

                break;
            default:
                break;
        }

        updateVersion(versionResponse, !exists);

        final List<VersionResponse> list2 = getVersionList(versionRequest);
        if (list2.isEmpty()) {
            log.warn("Nothing found.");
            return null;
        }

        versionResponse = list2.get(0);
        log.info("Generated build number: " + versionResponse.getBuildNumber());

        return versionResponse;
    }

    /* (non-Javadoc)
     * @see com.github.buildnum.IVersionService#getVersionList()
     */
    @Override
    public List<VersionResponse> getVersionList() {
        return getVersionList(null);
    }

    private void updateVersion(VersionResponse v, boolean insert) {
        PreparedStatement s = null;
        Connection conn = null;

        try {
            final StringBuilder b = new StringBuilder();

            if (insert) {
                b.append(SQL_INSERT);
            } else {
                b.append(SQL_UPDATE_SET);

                if (v.getArtifactClassifier() == null) {
                    b.append(" AND ARTIFACT_CLASSIFIER IS NULL");
                } else {
                    b.append(" AND ARTIFACT_CLASSIFIER=?");
                }
            }

            conn = getConnectionWithTransaction(dbUrl, dbUser, dbPassword);
            s = conn.prepareStatement(b.toString());

            int i = 1;

            if (insert) {
                s.setString(i++, v.getGroupId());
                s.setString(i++, v.getArtifactId());
                s.setString(i++, v.getArtifactVersion());

                if (v.getArtifactClassifier() != null) {
                    s.setString(i++, v.getArtifactClassifier());
                } else {
                    s.setNull(i++, Types.VARCHAR);
                }

                s.setInt(i++, v.getBuildNumber().intValue());
            } else {
                s.setInt(i++, v.getBuildNumber());
                s.setString(i++, v.getGroupId());
                s.setString(i++, v.getArtifactId());
                s.setString(i++, v.getArtifactVersion());

                if (v.getArtifactClassifier() != null) {
                    s.setString(i++, v.getArtifactClassifier());
                }
            }

            s.executeUpdate();

            conn.commit();
        } catch (Exception e) {
            rollback(conn);
            throw new RuntimeException("Cannot update version", e);
        } finally {
            // clean up
            close(null, s, conn);
        }
    }

    private List<VersionResponse> getVersionList(VersionRequest r) {
        Connection conn = null;
        PreparedStatement s = null;
        ResultSet rs = null;

        try {
            conn = getConnection(dbUrl, dbUser, dbPassword);

            final StringBuilder b = new StringBuilder(SQL_SELECT);

            if (r != null) {
                if (r.getGroupId() != null) {
                    b.append(" AND GROUP_ID=?");
                }

                if (r.getArtifactId() != null) {
                    b.append(" AND ARTIFACT_ID=?");
                }

                if (r.getArtifactVersion() != null) {
                    b.append(" AND ARTIFACT_VERSION=?");
                }

                if (r.getArtifactClassifier() != null) {
                    b.append(" AND ARTIFACT_CLASSIFIER=?");
                } else {
                    b.append(" AND ARTIFACT_CLASSIFIER IS NULL");
                }
            }

            if (r == null) {
                b.append(" ORDER BY GROUP_ID,ARTIFACT_ID,ARTIFACT_CLASSIFIER,ARTIFACT_VERSION,UPDATED_AT DESC");
            }

            // find current items in database
            s = conn.prepareStatement(b.toString());

            int i = 1;

            if (r != null) {
                if (r.getGroupId() != null) {
                    s.setString(i++, r.getGroupId());
                }

                if (r.getArtifactId() != null) {
                    s.setString(i++, r.getArtifactId());
                }

                if (r.getArtifactVersion() != null) {
                    s.setString(i++, r.getArtifactVersion());
                }

                if (r.getArtifactClassifier() != null) {
                    s.setString(i++, r.getArtifactClassifier());
                }
            }

            final List<VersionResponse> list = new ArrayList<VersionResponse>();

            rs = s.executeQuery();

            while (rs.next()) {
                i = 1;

                final VersionResponse v = new VersionResponse();
                v.setId(Long.valueOf(rs.getLong(i++)));
                v.setGroupId(rs.getString(i++));
                v.setArtifactId(rs.getString(i++));
                v.setArtifactVersion(rs.getString(i++));
                v.setArtifactClassifier(rs.getString(i++));
                v.setBuildNumber(Integer.valueOf(rs.getInt(i++)));
                v.setCreatedAt(rs.getTimestamp(i++));
                v.setUpdatedAt(rs.getTimestamp(i++));

                list.add(v);
            }

            return list;
        } catch (Exception e) {
            throw new RuntimeException("Cannot get version list", e);
        } finally {
            // clean up
            close(rs, s, conn);
        }
    }

    /* (non-Javadoc)
     * @see com.github.buildnum.IVersionService#initDatabase()
     */
    @Override
    public void initDatabase() {
        Utils.registerDriver(getDbDriver());

        Connection conn = null;
        PreparedStatement s = null;

        try {
            conn = getConnectionWithTransaction(dbUrl, dbUser, dbPassword);

            try {
                // find current items in database
                s = conn.prepareStatement(SQL_CREATE_SEQ);
                s.executeUpdate();

                close(null, s, null);

                // find current items in database
                s = conn.prepareStatement(SQL_CREATE_TABLE);
                s.executeUpdate();

                conn.commit();

                log.info("Database initialized.");
            } catch (Exception e) {
                // e.printStackTrace();
            }
        } catch (Exception e) {
            throw new RuntimeException(
                    "Cannot  init tables: " + e.getMessage(), e);
        } finally {
            // clean up
            close(null, s, conn);
        }
    }

}
