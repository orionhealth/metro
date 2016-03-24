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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package common;

import com.sun.xml.ws.api.security.trust.Status;
import com.sun.xml.ws.api.security.trust.client.IssuedTokenManager;
import com.sun.xml.ws.security.IssuedTokenContext;
import com.sun.xml.ws.security.trust.GenericToken;
import com.sun.xml.ws.security.trust.impl.client.DefaultSTSIssuedTokenConfiguration;
import com.sun.xml.wss.XWSSecurityException;
import com.sun.xml.wss.impl.callback.SAMLAssertionValidator;
import com.sun.xml.wss.saml.util.SAMLUtil;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.w3c.dom.Element;

/**
 *
 * @author TOSHIBA USER
 */
public class SamlValidator implements SAMLAssertionValidator {

    public void validate(Element assertion) throws SAMLValidationException {
        if (assertion != null){   
            String stsEndpoint = "http://localhost:8080/jaxws-fs-sts/sts/validate";
            String stsMexAddress = "http://localhost:8080/jaxws-fs-sts/sts/mex";
            DefaultSTSIssuedTokenConfiguration config = new DefaultSTSIssuedTokenConfiguration(
                        stsEndpoint, stsMexAddress);
            Status status = null;
            try{
                IssuedTokenManager manager = IssuedTokenManager.getInstance();

                IssuedTokenContext ctx = manager.createIssuedTokenContext(config, null);
                ctx.setTarget(new GenericToken(assertion));
                manager.validateIssuedToken(ctx);
                status = (Status)ctx.getOtherProperties().get(IssuedTokenContext.STATUS);
            }catch(Exception ex){
                throw new SAMLValidationException(ex);
            }
            
            if (!status.isValid()){
                throw new SAMLValidationException(status.getReason());
            }
        }
    }

    public void validate(XMLStreamReader assertion) throws SAMLValidationException {
        if (assertion != null){
            try {
                Element element = SAMLUtil.createSAMLAssertion(assertion);
                this.validate(element);
            } catch (XWSSecurityException ex) {
               throw new SAMLValidationException(ex);
            } catch (XMLStreamException ex) {
                 throw new SAMLValidationException(ex);
            }
        }
    }
}
