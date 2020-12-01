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

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.github.buildnum.VersionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Lifecycle listener managed by JEE servlet container.
 *
 * @author spevakl
 *
 */
public class AppLifecycleListener implements ServletContextListener {

    private static final Logger log = LoggerFactory
            .getLogger(AppLifecycleListener.class);

    final VersionManager versionManager = VersionManager.getInstance();

    @Override
    public void contextInitialized(ServletContextEvent servletcontextevent) {
        final ServletContext sc = servletcontextevent.getServletContext();

        final Conf conf = new Conf();
        conf.init(sc);

        versionManager.init(conf.getParameterMap());

        // start HSQL server
        versionManager.startServer();

        // create table and sequence
        versionManager.initDatabase();

        log.info("Application initialized.");
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletcontextevent) {
        // stop HSQL server
        versionManager.stopServer();
        log.info("Application destroyed.");
    }

}
