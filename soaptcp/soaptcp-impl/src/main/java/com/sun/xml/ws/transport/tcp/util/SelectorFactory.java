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

package com.sun.xml.ws.transport.tcp.util;

import java.io.IOException;
import java.nio.channels.Selector;
import java.util.EmptyStackException;
import java.util.Stack;

/**
 * Class was copied from GlassFish Grizzly sources to be available
 * also for client side and don't require GlassFish to be installed

 * Factory used to dispatch/share <code>Selector</code>.
 *
 * @author Scott Oaks
 * @author Jean-Francois Arcand
 */
public final class SelectorFactory{
    
    /**
     * The timeout before we exit.
     */
    static long timeout = 5000;
    
    
    /**
     * The number of <code>Selector</code> to create.
     */
    static int maxSelectors = 20;
    
    
    /**
     * Cache of <code>Selector</code>
     */
    private final static Stack<Selector> selectors = new Stack<Selector>();
    
    
    /**
     * Creates the <code>Selector</code>
     */
    static {
        try{
            for (int i = 0; i < maxSelectors; i++) 
                selectors.add(Selector.open());
        } catch (IOException ex){
        }
    }

    
    /**
     * Get a exclusive <code>Selector</code>
     */
    public static Selector getSelector() {
        synchronized(selectors) {
            Selector s = null;
            try {
                if ( selectors.size() != 0 )
                    s = selectors.pop();
            } catch (EmptyStackException ex){}
                       
            int attempts = 0;
            try{
                while (s == null && attempts < 2) {
                    selectors.wait(timeout);
                    try {
                        if ( selectors.size() != 0 )
                            s = selectors.pop();
                    } catch (EmptyStackException ex){
                        break;
                    }
                    attempts++;
                }
            } catch (InterruptedException ex){};
            return s;
        }
    }


    /**
     * Return the <code>Selector</code> to the cache
     */
    public static void returnSelector(final Selector s) {
        synchronized(selectors) {
            selectors.push(s);
            if (selectors.size() == 1)
                selectors.notify();
        }
    }

}
