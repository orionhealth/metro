/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://glassfish.dev.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

package com.sun.xml.ws.commons;

import com.sun.istack.NotNull;
import com.sun.istack.logging.Logger;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

/**
 *
 * @author Marek Potociar <marek.potociar at sun.com>
 */
public final class DelayedTaskManager {

    private static final Logger LOGGER = Logger.getLogger(DelayedTaskManager.class);

    public static interface DelayedTask {
        public String getName();

        public void run(DelayedTaskManager manager);
    }

    public static DelayedTaskManager createSingleThreadedManager(String name){
        return new DelayedTaskManager(Executors.newSingleThreadScheduledExecutor(createThreadFactory(name)));
    }

    public static DelayedTaskManager createManager(String name, int coreThreadPoolSize){
        return new DelayedTaskManager(Executors.newScheduledThreadPool(coreThreadPoolSize, createThreadFactory(name)));
    }

    private static final ThreadFactory createThreadFactory(String name) {
        return new NamedThreadFactory(name);
    }

    private class Worker implements Runnable {

        public final DelayedTask task;

        public Worker(DelayedTask handler) {
            this.task = handler;
        }

        /**
         * This method contains main task loop. It should not be called directly from outside.
         */
        public void run() {
            LOGGER.entering();

            if (LOGGER.isLoggable(Level.FINER)) {
                LOGGER.finer(String.format("Starting delayed execution of [ %s ]", task.getName()));
            }
            try {
                task.run(DelayedTaskManager.this);
            } catch (Exception ex) {
                LOGGER.warning(String.format("An exception occured during execution of [ %s ]", task.getName()), ex);
            } finally {
                if (LOGGER.isLoggable(Level.FINER)) {
                    LOGGER.finer(String.format("Delayed execution of [ %s ] finished", task.getName()));
                }

                LOGGER.exiting();
            }
        }
    }
    //
    private final ScheduledExecutorService executorService;
    private final AtomicBoolean isClosed;

    private DelayedTaskManager(ScheduledExecutorService executorService) {
        this.executorService = executorService;
        this.isClosed = new AtomicBoolean(false);
    }

    public boolean register(@NotNull DelayedTask task, long delay, TimeUnit timeUnit) {
        if (isClosed.get()) {
            LOGGER.finer(String.format("Attempt to register a new task has failed. This '%s' instance has already been closed", this.getClass().getName()));
            return false;
        }

        assert task != null;
        executorService.schedule(new Worker(task), delay, timeUnit);

        return true;
    }

    public void close() {
        if (isClosed.compareAndSet(false, true)) {
            executorService.shutdown();
        }
    }

    public boolean isClosed() {
        return isClosed.get();
    }
}
