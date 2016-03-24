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

package com.sun.xml.ws.transport.tcp.policy;

import com.sun.xml.ws.api.model.wsdl.WSDLPort;
import com.sun.istack.logging.Logger;
import com.sun.xml.ws.policy.AssertionSet;
import com.sun.xml.ws.policy.Policy;
import com.sun.xml.ws.policy.PolicyAssertion;
import com.sun.xml.ws.policy.PolicyException;
import com.sun.xml.ws.policy.PolicyMap;
import com.sun.xml.ws.policy.PolicyMapKey;
import com.sun.xml.ws.policy.jaxws.spi.PolicyFeatureConfigurator;
import com.sun.xml.ws.api.transport.tcp.SelectOptimalTransportFeature;
import java.util.Collection;
import java.util.LinkedList;
import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceFeature;

/**
 * {@link PolicyFeatureConfigurator}, which will transform SOAP/TCP policy
 * assertions to features on corresponding ports.
 *
 * @author Alexey Stashok
 */
public class OptimalTransportFeatureConfigurator implements PolicyFeatureConfigurator {

    private static final QName ENABLED = new QName("enabled");
    private static final Logger LOGGER = Logger.getLogger(OptimalTransportFeatureConfigurator.class);

    /**
     * process optimized transport policy assertions
     * {@link WSDLPort}
     *
     * @param key Key that identifies the endpoint scope
     * @param policyMap must be non-null
     * @return The list of features
     * @throws PolicyException If retrieving the policy triggered an exception
     */
    public Collection<WebServiceFeature> getFeatures(PolicyMapKey key, PolicyMap policyMap) throws PolicyException {
        final Collection<WebServiceFeature> features = new LinkedList<WebServiceFeature>();
        if ((key != null) && (policyMap != null)) {
            Policy policy = policyMap.getEndpointEffectivePolicy(key);
            if (policy != null) {
                for (AssertionSet alternative : policy) {
                    for (PolicyAssertion assertion : alternative) {
                        if (assertion.getName().equals(com.sun.xml.ws.transport.tcp.wsit.TCPConstants.SELECT_OPTIMAL_TRANSPORT_ASSERTION)) {
                            boolean isEnabled = true;
                            String value = assertion.getAttributeValue(ENABLED);
                            if (value != null) {
                                value = value.trim();
                                isEnabled = Boolean.valueOf(value) || value.equalsIgnoreCase("yes");
                            }

                            features.add(new SelectOptimalTransportFeature(isEnabled));
                        }
                    }
                }
            } // end-if policy not null
        }

        return features;
    }
}
