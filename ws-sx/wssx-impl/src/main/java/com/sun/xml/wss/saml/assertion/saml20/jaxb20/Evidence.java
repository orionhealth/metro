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
 * $Id: Evidence.java,v 1.2 2010-10-21 15:38:03 snajper Exp $
 */

package com.sun.xml.wss.saml.assertion.saml20.jaxb20;

import com.sun.xml.wss.saml.SAMLException;

import com.sun.xml.wss.logging.LogDomainConstants;
import com.sun.xml.wss.saml.internal.saml20.jaxb20.EvidenceType;
import com.sun.xml.wss.saml.util.SAML20JAXBUtil;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;

/**
 * The <code>Evidence</code> element specifies an assertion either by
 * reference or by value. An assertion is specified by reference to the value of
 * the assertion's  <code>AssertionIDReference</code> element.
 * An assertion is specified by value by including the entire
 * <code>Assertion</code> object
 */
public class Evidence extends EvidenceType
    implements com.sun.xml.wss.saml.Evidence {
    
    protected static final Logger log = Logger.getLogger(
            LogDomainConstants.WSS_API_DOMAIN,
            LogDomainConstants.WSS_API_DOMAIN_BUNDLE);

    /**
     * Constructs an <code>Evidence</code> object from a block of existing XML
     * that has already been built into a DOM.
     *
     * @param assertionSpecifierElement A <code>org.w3c.dom.Element</code>
     *        representing DOM tree for <code>Evidence</code> object.
     * @exception SAMLException if it could not process the Element properly,
     *            implying that there is an error in the sender or in the
     *            element definition.
     */
    public static EvidenceType fromElement(org.w3c.dom.Element element)
        throws SAMLException {
        try {
            JAXBContext jc = SAML20JAXBUtil.getJAXBContext();
                
            javax.xml.bind.Unmarshaller u = jc.createUnmarshaller();
            return (EvidenceType)u.unmarshal(element);
        } catch ( Exception ex) {
            throw new SAMLException(ex.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void setAssertionIDReferenceOrAssertion(List evidence) {
        this.assertionIDRefOrAssertionURIRefOrAssertion = evidence;
    }
    
    
    /**
     * Constructs an Evidence from a Set of <code>Assertion</code> and
     * <code>AssertionIDReference</code> objects.
     *
     * @param assertionIDRef Set of <code>AssertionIDReference</code> objects.
     * @param assertion Set of <code>Assertion</code> objects.
     * @exception SAMLException if either Set is empty or has invalid object.
     */
    public Evidence(List assertionIDRef, List assertion)
        {
        
        if ( assertionIDRef != null)
            setAssertionIDReferenceOrAssertion(assertionIDRef);
        else if ( assertion != null)
            setAssertionIDReferenceOrAssertion(assertion);
    }
    
    public Evidence(EvidenceType eveType){
        setAssertionIDReferenceOrAssertion(eveType.getAssertionIDRefOrAssertionURIRefOrAssertion());
    }
}
