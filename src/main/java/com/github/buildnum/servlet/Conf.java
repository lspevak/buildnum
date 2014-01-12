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

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;

/**
 *
 * @author spevakl
 *
 */
public class Conf {

    // web.xml context parameter names
    public static final String DB_URL = "DB_URL";
    public static final String DB_USER = "DB_USER";
    public static final String DB_PASSWORD = "DB_PASSWORD";
    public static final String DB_PATH = "DB_PATH";
    public static final String DB_NAME = "DB_NAME";
    public static final String DB_PORT = "DB_PORT";
    public static final String DB_DRIVER = "DB_DRIVER";

    final Map<String, String> confMap = new HashMap<String, String>();

    public void init(ServletContext sc) {
        final Enumeration<String> paramEn = sc.getInitParameterNames();

        while (paramEn.hasMoreElements()) {
            final String paramName = paramEn.nextElement();
            confMap.put(paramName, getParam(sc, paramName));
        }
    }

    private static String getParam(ServletContext sc, String paramName) {
        String value = System.getProperty(paramName);
        if (value != null) {
            return value;
        }

        value = System.getenv(paramName);
        if (value != null) {
            return value;
        }

        value = sc.getInitParameter(paramName);

        return value;
    }

    public String getParameter(String name) {
        return confMap.get(name);
    }

    public Map<String, String> getParameterMap() {
        return confMap;
    }
}
