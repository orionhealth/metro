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

package com.sun.xml.wss.provider.wsit;

import com.sun.xml.ws.api.model.wsdl.WSDLBoundOperation;
import com.sun.xml.ws.policy.AssertionSet;
import com.sun.xml.ws.policy.Policy;
import com.sun.xml.ws.security.impl.policyconv.SecurityPolicyHolder;
import com.sun.xml.ws.security.policy.SecurityPolicyVersion;
import com.sun.xml.wss.provider.wsit.logging.LogDomainConstants;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Logger;

/**
 *
 * Holds all the Translated info for one PolicyAlternative
 */
public class PolicyAlternativeHolder {

    protected static final Logger log =
            Logger.getLogger(
            LogDomainConstants.WSIT_PVD_DOMAIN,
            LogDomainConstants.WSIT_PVD_DOMAIN_BUNDLE);    
    private HashMap<WSDLBoundOperation, SecurityPolicyHolder> outMessagePolicyMap = null;
    private HashMap<WSDLBoundOperation, SecurityPolicyHolder> inMessagePolicyMap = null;
    private HashMap<String, SecurityPolicyHolder> outProtocolPM = null;
    private HashMap<String, SecurityPolicyHolder> inProtocolPM = null;
//TODO:POLALT in future all of these can be per-alternative
//    private boolean hasIssuedTokens = false;
//    private boolean hasSecureConversation = false;
//    private boolean hasReliableMessaging = false;
//    private boolean hasMakeConnection = false;
//    private boolean hasKerberosToken = false;
//    protected AlgorithmSuite bindingLevelAlgSuite = null;
//    private AlgorithmSuite bootStrapAlgoSuite;
    protected Policy bpMSP = null;
    protected SecurityPolicyVersion spVersion;
    private String uuid;

    public PolicyAlternativeHolder(AssertionSet assertions, SecurityPolicyVersion sv, Policy bpMSP) {
        //this.alternative = assertions;
        this.spVersion = sv;
        this.bpMSP = bpMSP;
        uuid = UUID.randomUUID().toString();
        this.inMessagePolicyMap = new HashMap<WSDLBoundOperation, SecurityPolicyHolder>();
        this.outMessagePolicyMap = new HashMap<WSDLBoundOperation, SecurityPolicyHolder>();
        this.inProtocolPM = new HashMap<String, SecurityPolicyHolder>();
        this.outProtocolPM = new HashMap<String, SecurityPolicyHolder>();
    }

    public void putToOutMessagePolicyMap(WSDLBoundOperation op, SecurityPolicyHolder sh) {
        this.outMessagePolicyMap.put(op, sh);
    }

    public SecurityPolicyHolder getFromOutMessagePolicyMap(WSDLBoundOperation op) {
        return this.outMessagePolicyMap.get(op);
    }

    public void putToInMessagePolicyMap(WSDLBoundOperation op, SecurityPolicyHolder sh) {
        this.inMessagePolicyMap.put(op, sh);
    }

    public SecurityPolicyHolder getFromInMessagePolicyMap(WSDLBoundOperation op) {
        return this.inMessagePolicyMap.get(op);
    }

    public void putToOutProtocolPolicyMap(String protocol, SecurityPolicyHolder sh) {
        this.outProtocolPM.put(protocol, sh);
    }

    public SecurityPolicyHolder getFromOutProtocolPolicyMap(String protocol) {
        return this.outProtocolPM.get(protocol);
    }

    public void putToInProtocolPolicyMap(String protocol, SecurityPolicyHolder sh) {
        this.inProtocolPM.put(protocol, sh);
    }

    public SecurityPolicyHolder getFromInProtocolPolicyMap(String protocol) {
        return this.inProtocolPM.get(protocol);
    }

    /**
     * @return the uuid, a unique ID to identify the PolicyAlternative
     *         for use by the Security Runtime
     */
    public String getId() {
        return uuid;
    }

    /**
     * @return the outMessagePolicyMap
     */
    public HashMap<WSDLBoundOperation, SecurityPolicyHolder> getOutMessagePolicyMap() {
        return outMessagePolicyMap;
    }

    /**
     * @return the inMessagePolicyMap
     */
    public HashMap<WSDLBoundOperation, SecurityPolicyHolder> getInMessagePolicyMap() {
        return inMessagePolicyMap;
    }

    /**
     * @return the outProtocolPM
     */
    public HashMap<String, SecurityPolicyHolder> getOutProtocolPM() {
        return outProtocolPM;
    }

    /**
     * @return the inProtocolPM
     */
    public HashMap<String, SecurityPolicyHolder> getInProtocolPM() {
        return inProtocolPM;
    }
}
