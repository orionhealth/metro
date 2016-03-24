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

package com.sun.xml.ws.rx.rm.policy.wsrm200702;

import com.sun.xml.ws.rx.rm.api.ReliableMessagingFeature.DeliveryAssurance;
import com.sun.xml.ws.rx.rm.api.ReliableMessagingFeature.SecurityBinding;
import com.sun.xml.ws.rx.testutil.ResourceLoader;
import junit.framework.TestCase;

/**
 *
 * @author Marek Potociar (marek.potociar at sun.com)
 */
public class Rm11AssertionTest extends TestCase {

    public Rm11AssertionTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test of getInstantiator method, of class Rm11Assertion.
     */
    public void testGetInstantiator() {
        assertNotNull(Rm11Assertion.getInstantiator());
    }

    /**
     * Test of getDeliveryAssurance method, of class Rm11Assertion.
     */
    public void testGetDeliveryAssurance() {
        assertEquals(DeliveryAssurance.getDefault(), ResourceLoader.getAssertionFromPolicy(ResourceLoader.RM_1_1_DEFAULT_POLICY_RESOURCE_NAME, Rm11Assertion.class).getDeliveryAssurance());
        assertEquals(DeliveryAssurance.AT_LEAST_ONCE, ResourceLoader.getAssertionFromPolicy(ResourceLoader.RM_1_1_CUSTOM_1_POLICY_RESOURCE_NAME, Rm11Assertion.class).getDeliveryAssurance());
        assertEquals(DeliveryAssurance.AT_MOST_ONCE, ResourceLoader.getAssertionFromPolicy(ResourceLoader.RM_1_1_CUSTOM_2_POLICY_RESOURCE_NAME, Rm11Assertion.class).getDeliveryAssurance());
        assertEquals(DeliveryAssurance.EXACTLY_ONCE, ResourceLoader.getAssertionFromPolicy(ResourceLoader.RM_1_1_CUSTOM_3_POLICY_RESOURCE_NAME, Rm11Assertion.class).getDeliveryAssurance());
    }

    /**
     * Test of isOrderedDelivery method, of class Rm11Assertion.
     */
    public void testIsOrderedDelivery() {
        assertEquals(false, ResourceLoader.getAssertionFromPolicy(ResourceLoader.RM_1_1_DEFAULT_POLICY_RESOURCE_NAME, Rm11Assertion.class).isOrderedDelivery());
        assertEquals(true, ResourceLoader.getAssertionFromPolicy(ResourceLoader.RM_1_1_CUSTOM_1_POLICY_RESOURCE_NAME, Rm11Assertion.class).isOrderedDelivery());
        assertEquals(false, ResourceLoader.getAssertionFromPolicy(ResourceLoader.RM_1_1_CUSTOM_2_POLICY_RESOURCE_NAME, Rm11Assertion.class).isOrderedDelivery());
    }

    /**
     * Test of getSecurityBinding method, of class Rm11Assertion.
     */
    public void testGetSecurityBinding() {
        assertEquals(SecurityBinding.NONE, ResourceLoader.getAssertionFromPolicy(ResourceLoader.RM_1_1_DEFAULT_POLICY_RESOURCE_NAME, Rm11Assertion.class).getSecurityBinding());
        assertEquals(SecurityBinding.TRANSPORT, ResourceLoader.getAssertionFromPolicy(ResourceLoader.RM_1_1_CUSTOM_1_POLICY_RESOURCE_NAME, Rm11Assertion.class).getSecurityBinding());
        assertEquals(SecurityBinding.STR, ResourceLoader.getAssertionFromPolicy(ResourceLoader.RM_1_1_CUSTOM_2_POLICY_RESOURCE_NAME, Rm11Assertion.class).getSecurityBinding());
    }
}
