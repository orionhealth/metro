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

package com.sun.xml.ws.tx.at.runtime;

import com.sun.xml.ws.api.model.wsdl.WSDLBoundOperation;
import com.sun.xml.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.ws.api.pipe.PipelineAssembler;
import com.sun.xml.ws.api.pipe.Tube;
import com.sun.xml.ws.api.tx.at.Transactional;
import com.sun.xml.ws.assembler.dev.ClientTubelineAssemblyContext;
import com.sun.xml.ws.assembler.dev.ServerTubelineAssemblyContext;
import com.sun.xml.ws.assembler.dev.TubeFactory;
import com.sun.xml.ws.policy.Policy;
import com.sun.xml.ws.policy.PolicyException;
import com.sun.xml.ws.policy.PolicyMap;
import com.sun.xml.ws.policy.PolicyMapKey;
import com.sun.xml.ws.api.tx.at.TransactionalFeature;
import com.sun.xml.ws.tx.at.internal.WSATGatewayRM;
import com.sun.xml.ws.tx.at.tube.WSATClientTube;
import com.sun.xml.ws.tx.at.tube.WSATServerTube;
import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceException;

public final class AtTubeFactory implements TubeFactory {

    private static final String WSAT_SOAP_NSURI = "http://schemas.xmlsoap.org/ws/2004/10/wsat";
    private static final QName AT_ALWAYS_CAPABILITY = new QName(WSAT_SOAP_NSURI, "ATAlwaysCapability");
    private static final QName AT_ASSERTION = new QName(WSAT_SOAP_NSURI, "ATAssertion");

    /**
     * Adds TX tube to the client-side tubeline, depending on whether TX is enabled or not.
     *
     * @param context wsit client tubeline assembler context
     * @return new tail of the client-side tubeline
     */
    public Tube createTube(ClientTubelineAssemblyContext context) {
        final TransactionalFeature feature = context.getBinding().getFeature(TransactionalFeature.class);
        if (isWSATPolicyEnabled(context.getPolicyMap(), context.getWsdlPort(), false)
                || (feature != null && feature.isEnabled())) { //todo add the case where policy is enabled but annotation is NEVER
            WSATGatewayRM.create();
            return new WSATClientTube(context.getTubelineHead(), context, feature);
        } else {
            return context.getTubelineHead();
        }
    }

    /**
     * Adds TX tube to the service-side tubeline, depending on whether TX is enabled or not.
     *
     * @param context wsit service tubeline assembler context
     * @return new head of the service-side tubeline
     */
    public Tube createTube(ServerTubelineAssemblyContext context) {
        final TransactionalFeature feature = context.getEndpoint().getBinding().getFeature(TransactionalFeature.class);
        if (isWSATPolicyEnabled(context.getPolicyMap(), context.getWsdlPort(), true)
                || (feature != null && feature.isEnabled())) { //todo add the case where policy is enabled but annotation is NEVER
            WSATGatewayRM.create();
            return new WSATServerTube(context.getTubelineHead(), context, feature);
        } else {
            return context.getTubelineHead();
        }
    }


    /**
     * Checks to see whether WS-Atomic Transactions are enabled or not.
     *
     * @param policyMap policy map for {@link this} assembler
     * @param wsdlPort the WSDLPort object
     * @param isServerSide true if this method is being called from {@link PipelineAssembler#createServer(ServerPipeAssemblerContext)}
     * @return true if Transactions is enabled, false otherwise
     */
    private boolean isWSATPolicyEnabled(PolicyMap policyMap, WSDLPort wsdlPort, boolean isServerSide) {
        if (policyMap == null || wsdlPort == null /* TODO : fix missing util method || !Util.isJTAAvailable()*/) {
            // false for standalone WSIT client or WSIT Service in Tomcat
            return false;
        }
        try {
            PolicyMapKey endpointKey = PolicyMap.createWsdlEndpointScopeKey(wsdlPort.getOwner().getName(), wsdlPort.getName());
            Policy policy = policyMap.getEndpointEffectivePolicy(endpointKey);
            for (WSDLBoundOperation wbo : wsdlPort.getBinding().getBindingOperations()) {
                PolicyMapKey operationKey = PolicyMap.createWsdlOperationScopeKey(wsdlPort.getOwner().getName(), wsdlPort.getName(), wbo.getName());
                policy = policyMap.getOperationEffectivePolicy(operationKey);
                if (policy != null) {
                    // look for ATAlwaysCapable on the server side
                    if ((isServerSide) && (policy.contains(AT_ALWAYS_CAPABILITY))) {
                        return true;
                    }
                    // look for ATAssertion in both client and server
                    if (policy.contains(AT_ASSERTION)) {
                        return true;
                    }
                }
            }
        } catch (PolicyException e) {
            throw new WebServiceException(e);
        }
        return false;
    }
}
