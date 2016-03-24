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

package com.sun.xml.ws.security.impl.policy;

import com.sun.xml.ws.policy.AssertionSet;
import com.sun.xml.ws.policy.NestedPolicy;
import com.sun.xml.ws.policy.PolicyAssertion;
import com.sun.xml.ws.policy.sourcemodel.AssertionData;
import com.sun.xml.ws.security.policy.SecurityAssertionValidator;
import com.sun.xml.ws.security.policy.SecurityPolicyVersion;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import com.sun.xml.ws.security.policy.Constants;
import static com.sun.xml.ws.security.impl.policy.Constants.*;
import java.util.logging.Level;

/**
 *
 * @author ashutosh.shahi@sun.com
 */
public class Trust13 extends PolicyAssertion implements com.sun.xml.ws.security.policy.TrustAssertion, SecurityAssertionValidator{
    Set<String> requiredProps;
    String version = "1.3";
    private boolean populated = false;
    private AssertionFitness fitness = AssertionFitness.IS_VALID;
    private SecurityPolicyVersion spVersion;
    
    /** Creates a new instance of Trust13 */
    public Trust13() {
        spVersion = SecurityPolicyVersion.SECURITYPOLICY12NS;
    }
    
    public Trust13(AssertionData name,Collection<PolicyAssertion> nestedAssertions, AssertionSet nestedAlternative) {
        super(name,nestedAssertions,nestedAlternative);
        String nsUri = getName().getNamespaceURI();
        spVersion = PolicyUtil.getSecurityPolicyVersion(nsUri);
    }
    
    public void addRequiredProperty(String requirement) {
        if(requiredProps == null){
            requiredProps = new HashSet<String>();
        }
        requiredProps.add(requirement);
    }

    public Set getRequiredProperties() {
        populate();
        return requiredProps;
    }

    public String getType() {
        return version;
    }

    public SecurityAssertionValidator.AssertionFitness validate(boolean isServer) {
        return populate(isServer);
    }
    
    private void populate(){
        populate(false);
    }
    
    private synchronized AssertionFitness populate(boolean isServer) {
        if(!populated){
            NestedPolicy policy = this.getNestedPolicy();
            if(policy == null){
                if(logger.getLevel() == Level.FINE){
                    logger.log(Level.FINE,"NestedPolicy is null");
                }
                populated = true;
                return fitness;
            }
            AssertionSet as = policy.getAssertionSet();
            for(PolicyAssertion assertion:as){
                if(PolicyUtil.isSupportClientChallenge(assertion, spVersion)){
                    addRequiredProperty(Constants.MUST_SUPPORT_CLIENT_CHALLENGE);
                }else if(PolicyUtil.isSupportServerChallenge(assertion, spVersion)){
                    addRequiredProperty(Constants.MUST_SUPPORT_SERVER_CHALLENGE);
                }else if(PolicyUtil.isRequireClientEntropy(assertion, spVersion)){
                    addRequiredProperty(Constants.REQUIRE_CLIENT_ENTROPY);
                }else if(PolicyUtil.isRequireServerEntropy(assertion, spVersion)){
                    addRequiredProperty(Constants.REQUIRE_SERVER_ENTROPY);
                }else if(PolicyUtil.isSupportIssuedTokens(assertion, spVersion)){
                    addRequiredProperty(Constants.MUST_SUPPORT_ISSUED_TOKENS);
                } else if(PolicyUtil.isRequestSecurityTokenCollection(assertion, spVersion)){
                    addRequiredProperty(Constants.REQUIRE_REQUEST_SECURITY_TOKEN_COLLECTION);
                } else if(PolicyUtil.isAppliesTo(assertion, spVersion)){
                    addRequiredProperty(Constants.REQUIRE_APPLIES_TO);
                } else{
                    if(!assertion.isOptional()){
                        log_invalid_assertion(assertion, isServer,"Trust13");
                        fitness = AssertionFitness.HAS_UNKNOWN_ASSERTION;
                    }
                }
            }
            populated = true;
        }
        return fitness;
    }
    
}
