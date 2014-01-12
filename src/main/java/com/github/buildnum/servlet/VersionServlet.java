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
package com.github.buildnum.servlet;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.github.buildnum.VersionManager;
import com.github.buildnum.VersionRequest;
import com.github.buildnum.VersionResponse;

/**
 * Artifact version request servlet.
 *
 * @author spevak
 *
 */
public class VersionServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public static final String LINE_END = "\r\n";

    protected static final Logger log = Logger.getLogger(VersionServlet.class);

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        try {
            final String action = req.getParameter("action");
            final String ignoreSnapshotStr = req.getParameter("ignoreSnapshot");
            final String prefix = req.getParameter("prefix");
            // increment, get
            final boolean increment = "increment".equals(action);
            final boolean reset = "reset".equals(action);
            final boolean ignoreSnapshot = "1".equals(ignoreSnapshotStr);
            // return number only, not properties file content
            final boolean versionOnly = "1".equals(req.getParameter("versionOnly"));

            final VersionRequest versionRequest = new VersionRequest();
            versionRequest.setGroupId(req.getParameter("groupId"));
            versionRequest.setArtifactId(req.getParameter("artifactId"));
            versionRequest.setArtifactClassifier(req
                    .getParameter("artifactClassifier"));
            versionRequest.setArtifactVersion(req
                    .getParameter("artifactVersion"));
            versionRequest.setIncrement(increment);
            versionRequest.setReset(reset);
            versionRequest.setIgnoreSnapshot(ignoreSnapshot);

            VersionResponse versionResponse = null;

            if (versionRequest.getGroupId() != null
                    && versionRequest.getGroupId().trim().length() > 0
                    && versionRequest.getArtifactId() != null
                    && versionRequest.getArtifactId().trim().length() > 0
                    && versionRequest.getArtifactVersion() != null
                    && versionRequest.getArtifactVersion().trim().length() > 0) {

                if (versionRequest.getArtifactClassifier() != null
                        && versionRequest.getArtifactClassifier().trim()
                                .length() == 0) {
                    versionRequest.setArtifactClassifier(null);
                }

                versionResponse = VersionManager.getInstance().getVersion(versionRequest);
            }

            final StringBuilder b = new StringBuilder();

            if (versionOnly) {
                b.append(
                        versionResponse == null ||
                        versionResponse.getBuildVersion() == null ? "?" : "" + versionResponse.getBuildVersion());
            } else {
                if (versionResponse == null) {
                    append(b, prefix, "status", "0");
                } else {
                    append(b, prefix, "status", "1");

                    append(b, prefix, "groupId", versionResponse.getGroupId());
                    append(b, prefix, "artifactId", versionResponse.getArtifactId());
                    append(b, prefix, "artifactClassifier",
                            versionResponse.getArtifactClassifier());
                    append(b, prefix, "artifactVersion",
                            versionResponse.getArtifactVersion());
                    append(b, prefix, "buildNumber",
                            versionResponse.getBuildVersion() + "");
                }
            }

            writeOutput(b.toString(), resp);
        } catch (Exception e) {
            log.error("Error in VersionServlet", e);
            throw new ServletException(e);
        }
    }

    protected static void writeOutput(String s, HttpServletResponse resp)
            throws UnsupportedEncodingException, IOException {
        resp.setContentType("text/plain; encoding=ISO-8859-1");
        IOUtils.write(s.getBytes("8859_1"), resp.getOutputStream());
    }

    protected static void append(StringBuilder b, String prefix, String key,
            String value) {
        if (prefix != null) {
            b.append(prefix).append(".");
        }

        b.append(key).append("=");
        if (value != null) {
            b.append(value);
        }

        b.append(LINE_END);
    }

}
