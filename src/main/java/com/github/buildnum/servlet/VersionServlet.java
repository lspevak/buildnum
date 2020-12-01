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
package com.github.buildnum.servlet;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import com.github.buildnum.ActionType;
import com.github.buildnum.FormatType;
import com.github.buildnum.VersionManager;
import com.github.buildnum.VersionRequest;
import com.github.buildnum.VersionResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Artifact version request servlet.
 *
 * @author spevak
 *
 */
public class VersionServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public static final String LINE_END = "\r\n";

    protected static final Logger log = LoggerFactory.getLogger(VersionServlet.class);

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        try {
            final String action = getParameter(req, "action");
            final String format = getParameter(req, "format");
            final String ignoreSnapshotStr = getParameter(req, "ignoreSnapshot");

            final VersionRequest versionRequest = new VersionRequest();
            versionRequest.setGroupId(getParameter(req, "groupId"));
            versionRequest.setArtifactId(getParameter(req, "artifactId"));
            versionRequest.setArtifactClassifier(getParameter(req, "artifactClassifier"));
            versionRequest.setArtifactVersion(getParameter(req, "artifactVersion"));
            versionRequest.setActionType(ActionType.fromValue(action));
            versionRequest.setFormatType(FormatType.fromValue(format));
            versionRequest.setIgnoreSnapshot("1".equals(ignoreSnapshotStr));
            versionRequest.setBuildNumber(getParameterAsInt(req, "buildNumber")); // for SET action
            versionRequest.setPrefix(getParameter(req, "prefix"));

            VersionResponse versionResponse = null;

            if (versionRequest.getGroupId() != null
                    && versionRequest.getArtifactId() != null
                    && versionRequest.getArtifactVersion() != null) {
                // call service
                versionResponse = VersionManager.getInstance().getVersion(versionRequest);
            }

            final String output = getOutput(versionRequest, versionResponse);
            // write to output stream
            writeOutput(output, resp);
        } catch (Exception e) {
            log.error("Error in VersionServlet", e);
            throw new ServletException(e);
        }
    }

    protected static String getOutput(VersionRequest versionRequest, VersionResponse versionResponse) {
        final StringBuilder b = new StringBuilder();

        if (versionRequest.getFormatType() == FormatType.NUMBER) {
            b.append(
                    versionResponse == null ||
                            versionResponse.getBuildNumber() == null ? "?" : "" + versionResponse.getBuildNumber());
        } else {
            if (versionResponse == null) {
                append(b, versionRequest.getPrefix(), "status", "0");
            } else {
                append(b, versionRequest.getPrefix(), "status", "1");

                append(b, versionRequest.getPrefix(), "groupId", versionResponse.getGroupId());
                append(b, versionRequest.getPrefix(), "artifactId", versionResponse.getArtifactId());
                append(b, versionRequest.getPrefix(), "artifactClassifier", versionResponse.getArtifactClassifier());
                append(b, versionRequest.getPrefix(), "artifactVersion", versionResponse.getArtifactVersion());
                append(b, versionRequest.getPrefix(), "buildNumber", versionResponse.getBuildNumber() + "");
            }
        }

        return b.toString();
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

    private String getParameter(HttpServletRequest req, String paramName) {
        String paramValue = req.getParameter(paramName);
        if (paramValue != null) {
            paramValue = paramValue.trim();

            if (paramValue.length() == 0) {
                return null;
            }
        }

        return paramValue;
    }

    private Integer getParameterAsInt(HttpServletRequest req, String paramName) {
        String paramValue = getParameter(req, paramName);
        if (paramValue != null) {
            paramValue = paramValue.trim();

            try {
                return Integer.valueOf(paramValue);
            } catch (Exception e) {
                return null;
            }
        }

        return null;
    }

}
