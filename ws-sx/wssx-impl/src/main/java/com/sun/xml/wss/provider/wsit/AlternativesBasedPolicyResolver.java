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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sun.xml.wss.provider.wsit;

import com.sun.xml.ws.api.message.HeaderList;
import com.sun.xml.ws.api.message.Message;
import com.sun.xml.ws.api.message.Messages;
import com.sun.xml.ws.api.model.wsdl.WSDLFault;
import com.sun.xml.ws.api.model.wsdl.WSDLOperation;
import com.sun.xml.ws.policy.PolicyAssertion;
import com.sun.xml.ws.security.impl.policyconv.SCTokenWrapper;
import com.sun.xml.ws.security.opt.impl.JAXBFilterProcessingContext;
import com.sun.xml.wss.impl.PolicyResolver;
import com.sun.xml.wss.impl.policy.mls.MessagePolicy;
import com.sun.xml.ws.api.model.wsdl.WSDLBoundOperation;
import com.sun.xml.ws.security.impl.policyconv.SecurityPolicyHolder;
import com.sun.xml.wss.ProcessingContext;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConstants;
import org.w3c.dom.Node;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPException;
import org.w3c.dom.NodeList;
import com.sun.xml.ws.security.policy.Token;
import com.sun.xml.ws.api.addressing.*;
import com.sun.xml.ws.rx.mc.api.McProtocolVersion;
import com.sun.xml.ws.rx.rm.api.RmProtocolVersion;
import com.sun.xml.ws.security.policy.SecurityPolicyVersion;
import com.sun.xml.ws.security.secconv.WSSCVersion;
import com.sun.xml.ws.security.trust.WSTrustVersion;
import com.sun.xml.wss.impl.ProcessingContextImpl;
import com.sun.xml.wss.impl.policy.PolicyAlternatives;
import com.sun.xml.wss.impl.policy.SecurityPolicy;
import com.sun.xml.wss.jaxws.impl.TubeConfiguration;
import com.sun.xml.wss.provider.wsit.logging.LogDomainConstants;
import com.sun.xml.wss.provider.wsit.logging.LogStringsMessages;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author vbkumarjayanti
 */
class AlternativesBasedPolicyResolver implements PolicyResolver {
    protected static final Logger log =
            Logger.getLogger(
            LogDomainConstants.WSIT_PVD_DOMAIN,
            LogDomainConstants.WSIT_PVD_DOMAIN_BUNDLE);

    private WSDLBoundOperation cachedOperation = null;
    //private PolicyAttributes pa = null;
    private AddressingVersion addVer = null;
    private RmProtocolVersion rmVer = null;
    private McProtocolVersion mcVer = null;
    private TubeConfiguration tubeConfig = null;
    private boolean isClient = false;
    private boolean isSCMessage = false;
    //private boolean isTrustOrSCMessage = false;
    private String action = "";
    private WSTrustVersion wstVer = WSTrustVersion.WS_TRUST_10;
    private WSSCVersion wsscVer = WSSCVersion.WSSC_10;
    private List<PolicyAlternativeHolder> policyAlternatives = null;

    /**
     * Creates a new instance of OperationResolverImpl
     */
     public AlternativesBasedPolicyResolver(List<PolicyAlternativeHolder> alternatives,
            WSDLBoundOperation cachedOperation, TubeConfiguration tubeConfig,
            AddressingVersion addVer, boolean client, RmProtocolVersion rmVer, McProtocolVersion mcVer) {

        this.policyAlternatives = alternatives;
        this.cachedOperation = cachedOperation;
        this.tubeConfig = tubeConfig;
        this.addVer = addVer;
        this.isClient = client;
        this.rmVer = rmVer;
        this.mcVer = mcVer;
    }

    public SecurityPolicy resolvePolicy(ProcessingContext ctx) {
        Message msg = null;
        SOAPMessage soapMsg = null;
        if (ctx instanceof JAXBFilterProcessingContext) {
            msg = ((JAXBFilterProcessingContext) ctx).getJAXWSMessage();
        } else {
            soapMsg = ctx.getSOAPMessage();
            msg = Messages.create(soapMsg);
        }
        if (((ProcessingContextImpl) ctx).getSecurityPolicyVersion().equals(
                SecurityPolicyVersion.SECURITYPOLICY12NS.namespaceUri)) {
            wstVer = WSTrustVersion.WS_TRUST_13;
            wsscVer = WSSCVersion.WSSC_13;
        }

        

        action = getAction(msg);
        if (isRMMessage() || isMCMessage()) {
            return getProtocolPolicy("RM");
        }

        if (isSCCancel()) {
            return getProtocolPolicy("SC-CANCEL");
        }
        SecurityPolicy mp = null;
        isSCMessage = isSCMessage();
        if (isSCMessage) {
            Token scToken = (Token) getInBoundSCP();
            return getInboundXWSBootstrapPolicy(scToken);
        }

        if (msg.isFault()) {
            if (soapMsg == null) {
                try {
                    soapMsg = msg.readAsSOAPMessage();
                } catch (SOAPException ex) {
                    //ex.printStackTrace();
                }
            }
            mp = getInboundFaultPolicy(soapMsg);
        } else {
            mp = getInboundXWSSecurityPolicy(msg);
        }

        if (mp == null) {
            return new MessagePolicy();
        }
        return mp;
    }

    protected PolicyAssertion getInBoundSCP() {

        SecurityPolicyHolder sph = null;
        Collection coll = new ArrayList();
        for (PolicyAlternativeHolder p : this.policyAlternatives) {
            coll.addAll(p.getInMessagePolicyMap().values());
        }

        Iterator itr = coll.iterator();
        while (itr.hasNext()) {
            SecurityPolicyHolder ph = (SecurityPolicyHolder) itr.next();
            if (ph != null) {
                sph = ph;
                break;
            }
        }
        if (sph == null) {
            return null;
        }
        List<PolicyAssertion> policies = sph.getSecureConversationTokens();
        if (!policies.isEmpty()) {
            return (PolicyAssertion) policies.get(0);
        }
        return null;
    }

    //TODO:POLALT modify this to return a PolicyAlternatives object when applicable
    private SecurityPolicy getInboundXWSSecurityPolicy(Message msg) {
        SecurityPolicy mp = null;

        //Review : Will this return operation name in all cases , doclit,rpclit, wrap / non wrap ?
        WSDLBoundOperation operation = null;
        if (cachedOperation != null) {
            operation = cachedOperation;
        } else {
            operation = msg.getOperation(tubeConfig.getWSDLPort());
            if (operation == null) {
                operation = getWSDLOpFromAction();
            }
        }

        List<MessagePolicy> mps = new ArrayList<MessagePolicy>();
        for (PolicyAlternativeHolder p : this.policyAlternatives) {
            SecurityPolicyHolder sph = (SecurityPolicyHolder) p.getInMessagePolicyMap().get(operation);
            //TODO: pass isTrustMessage Flag to this method later
            if (sph == null && (isTrustMessage() || isSCMessage)) {
                operation = getWSDLOpFromAction();
                sph = (SecurityPolicyHolder) p.getInMessagePolicyMap().get(operation);
            }
            if (sph != null) {
                mps.add(cloneWithId(sph.getMessagePolicy(), p.getId()));
            }
        }
        return new PolicyAlternatives(mps);
    }

    //TODO:POLALT modify this to return a PolicyAlternatives object when applicable
    private SecurityPolicy getInboundFaultPolicy(SOAPMessage msg) {
        if (cachedOperation != null) {
            List<MessagePolicy> mps = new ArrayList<MessagePolicy>();
            for (PolicyAlternativeHolder p : this.policyAlternatives) {
                WSDLOperation operation = cachedOperation.getOperation();
                try {
                    SOAPBody body = msg.getSOAPBody();
                    NodeList nodes = body.getElementsByTagName("detail");
                    if (nodes.getLength() == 0) {
                        nodes = body.getElementsByTagNameNS(SOAPConstants.URI_NS_SOAP_1_2_ENVELOPE, "Detail");
                    }
                    if (nodes.getLength() > 0) {
                        Node node = nodes.item(0);
                        Node faultNode = node.getFirstChild();
                        while (faultNode != null && faultNode.getNodeType() != Node.ELEMENT_NODE)
                            faultNode = faultNode.getNextSibling();   //fix for bug #1487
                        
                        if (faultNode == null) {
                            return new MessagePolicy();
                        }
                        final String uri = faultNode.getNamespaceURI();
                        final QName faultDetail;
                        if (uri != null && uri.length() > 0) {
                            faultDetail = new QName(uri, faultNode.getLocalName());
                        } else {
                            faultDetail = new QName(faultNode.getLocalName());
                        }
                        WSDLFault fault = operation.getFault(faultDetail);
                        SecurityPolicyHolder sph = p.getInMessagePolicyMap().get(cachedOperation);
                        SecurityPolicyHolder faultPolicyHolder = sph.getFaultPolicy(fault);
                        if (faultPolicyHolder != null) {
                            mps.add(cloneWithId(faultPolicyHolder.getMessagePolicy(), p.getId()));
                        }
                    }
                } catch (SOAPException sx) {
                    //sx.printStackTrace();
                    log.log(Level.WARNING, LogStringsMessages.WSITPVD_0065_ERROR_RESOLVING_ALTERNATIVES(), sx);
                }
            }
            return new PolicyAlternatives(mps);
        }
        return new MessagePolicy();

    }

    private boolean isTrustMessage() {
        if (wstVer.getIssueRequestAction().equals(action) ||
                wstVer.getIssueResponseAction().equals(action)) {
            return true;
        }
        return false;

    }

    private boolean isRMMessage() {
        return rmVer.isProtocolAction(action);
    }

    private boolean isMCMessage() {
        return mcVer.isProtocolAction(action);
    }

    private String getAction(Message msg) {
        if (addVer != null) {
            HeaderList hl = msg.getHeaders();
            String retVal = hl.getAction(addVer, tubeConfig.getBinding().getSOAPVersion());
            return retVal;
        }
        return "";

    }

    private SecurityPolicy getInboundXWSBootstrapPolicy(Token scAssertion) {
        if(scAssertion == null){
            return null;
        }
        return ((SCTokenWrapper) scAssertion).getMessagePolicy();
    }

    private boolean isSCMessage() {
        if (wsscVer.getSCTRequestAction().equals(action) ||
                wsscVer.getSCTResponseAction().equals(action) ||
                wsscVer.getSCTRenewRequestAction().equals(action) ||
                wsscVer.getSCTRenewResponseAction().equals(action)) {
            return true;
        }
        return false;
    }

    private boolean isSCCancel() {

        if (wsscVer.getSCTCancelResponseAction().equals(action) ||
                wsscVer.getSCTCancelRequestAction().equals(action)) {
            return true;
        }
        return false;
    }

    private String getAction(WSDLOperation operation) {
        if (!isClient) {
            return operation.getInput().getAction();
        } else {
            return operation.getOutput().getAction();
        }
    }

    private WSDLBoundOperation getWSDLOpFromAction() {
        for (PolicyAlternativeHolder p : this.policyAlternatives) {
        Set<WSDLBoundOperation> keys = p.getInMessagePolicyMap().keySet();
        for (WSDLBoundOperation wbo : keys) {
            WSDLOperation wo = wbo.getOperation();
            // WsaWSDLOperationExtension extensions = wo.getExtension(WsaWSDLOperationExtension.class);
            String confAction = getAction(wo);
            if (confAction != null && confAction.equals(action)) {
                return wbo;
            }
        }
        }
        return null;
    }

    private SecurityPolicy getProtocolPolicy(String protocol) {
        List<MessagePolicy> mps = new ArrayList<MessagePolicy>();
        for (PolicyAlternativeHolder p : this.policyAlternatives) {
            SecurityPolicyHolder sph = p.getInProtocolPM().get(protocol);
            if (sph != null) {
                mps.add(cloneWithId(sph.getMessagePolicy(), p.getId()));
            }
        }
        PolicyAlternatives p = new PolicyAlternatives(mps);
        return p;
    }

    private MessagePolicy cloneWithId (MessagePolicy toClone, String id) {
        if (toClone == null) {
            return null;
        }
        try {
            MessagePolicy copy = new MessagePolicy();
            copy.setPolicyAlternativeId(id);
            Iterator it = toClone.iterator();
            while (it.hasNext()) {
                copy.append((SecurityPolicy) it.next());
            }
            return copy;
        } catch (Exception ex) {
            return null;
        }
    }

}
