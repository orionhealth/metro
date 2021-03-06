/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright (c) 2010 Oracle and/or its affiliates. All rights reserved.
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

package com.sun.xml.ws.commons.ha;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import junit.framework.TestCase;

/**
 *
 * @author Marek Potociar (marek.potociar at sun.com)
 */
public class StickyKeyTest extends TestCase {
    
    public StickyKeyTest(String testName) {
        super(testName);
    }

    /**
     * Test of serialization method
     */
    public void testSerialization() throws Exception {
        StickyKey original = new StickyKey("abc", "def");

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final ObjectOutputStream oos = new ObjectOutputStream(baos);

        oos.writeObject(original);
        oos.close();

        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bais);

        Object _replica = ois.readObject();
        ois.close();

        assertTrue("Unexpected replica class: " + _replica.getClass(), _replica instanceof StickyKey);
        StickyKey replica = (StickyKey) _replica;

        assertEquals("Original and replica are expected to be equal", original, replica);
        assertEquals(original.key, replica.key);
        assertEquals(original.getHashKey(), replica.getHashKey());
    }


    /**
     * Test of equals method, of class StickyKey.
     */
    public void testEquals() {
        assertTrue(new StickyKey("abc", "def").equals(new StickyKey("abc", "def")));
        assertTrue(new StickyKey("abc", "def").equals(new StickyKey("abc", "cba")));
        assertFalse(new StickyKey("cba", "def").equals(new StickyKey("abc", "def")));
    }

    /**
     * Test of hashCode method, of class StickyKey.
     */
    public void testHashCode() {
        assertEquals(new StickyKey("abc", "def").hashCode(), new StickyKey("abc", "def").hashCode());
        assertEquals(new StickyKey("abc", "def").hashCode(), new StickyKey("abc", "cba").hashCode());
        assertFalse(new StickyKey("cba", "def").hashCode() == new StickyKey("abc", "def").hashCode());
    }
}
