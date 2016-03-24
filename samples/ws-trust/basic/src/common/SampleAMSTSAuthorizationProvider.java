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

package common;

import com.sun.xml.wss.SubjectAccessor;

import java.util.*;

import javax.security.auth.Subject;

import com.iplanet.am.util.Debug;
import com.iplanet.sso.SSOToken;
import com.iplanet.sso.SSOException;

import com.sun.identity.policy.PolicyException;
import com.sun.identity.policy.client.PolicyEvaluator;

import com.sun.xml.ws.api.security.trust.STSAuthorizationProvider;

public class SampleAMSTSAuthorizationProvider implements STSAuthorizationProvider {
    
    private static Debug debug = Debug.getInstance("SampleAMSTSAuthorizationProvider");

    private static SSOToken getSSOToken(Subject subject)
    {
        Set pc = //subject.getPublicCredentials();
       // if (pc == null){
                SubjectAccessor.getRequesterSubject().getPublicCredentials();
        //}
        
        if (pc == null)
            System.out.println("No pc in the subject");
        
        if (pc != null){
            if (pc == null){
                pc = SubjectAccessor.getRequesterSubject().getPublicCredentials();
            }
            Iterator ite = pc.iterator();
            while (ite.hasNext()){
                Object obj = ite.next();
                if (obj instanceof com.iplanet.sso.SSOToken){
                    return (SSOToken)obj;
                }
            }
        }
        return null;
    }
    
    public boolean isAuthorized(Subject subject, String appliesTo, String tokenType, String keyType)
    {
        String serviceName = "iPlanetAMWebAgentService";
        String action = "POST";
        SSOToken token = getSSOToken(subject);

        try
        {
            debug.message("Authorizing access - SSOToken is "+token);
            debug.message("Checking policy for "+action+" on URL "+appliesTo);
            PolicyEvaluator pe = new PolicyEvaluator(serviceName);
            debug.message("Got PolicyEvaluator for "+serviceName);
            boolean isAllowed = pe.isAllowed(token, appliesTo, action);
            debug.message("Access " + (isAllowed ? "is" : "is not" ) + " allowed");
            return isAllowed;
        }
        catch ( PolicyException pe )
        {
            debug.error("Exception evaluating policy", pe);
        }
        catch ( SSOException ssoe )
        {
            debug.error("Exception evaluating policy", ssoe);
        }

        return false;
    }  
}
