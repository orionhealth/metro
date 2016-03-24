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

package com.sun.xml.ws.config.metro.parser;

import com.sun.istack.logging.Logger;
import com.sun.xml.ws.config.metro.parser.jsr109.WebserviceDescriptionType;
import com.sun.xml.ws.config.metro.parser.jsr109.WebservicesType;

import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLStreamReader;
import javax.xml.ws.WebServiceException;

/**
 * Parse webservices.xml.
 *
 * @author Fabian Ritzmann
 */
class WsParser {

    private static final Logger LOGGER = Logger.getLogger(WsParser.class);

    private static JAXBContext context;

    public WsParser() throws WebServiceException {
        try {
            // We don't need to care about race conditions here, in the worst case
            // the context gets initialized several times. We don't instantiate context
            // in a static block because we would lose the exception message.
            if (context == null) {
                context = JAXBContext.newInstance("com.sun.xml.ws.config.metro.parser.jsr109");
            }
        } catch (JAXBException e) {
            // TODO logging message
            throw LOGGER.logSevereException(new WebServiceException("Failed to initialize", e));
        }
    }

    public List<WebserviceDescriptionType> parse(final XMLStreamReader reader) throws WebServiceException {
        try {
            final Unmarshaller unmarshaller = context.createUnmarshaller();
            final JAXBElement<WebservicesType> elements = unmarshaller.unmarshal(reader, WebservicesType.class);
            final WebservicesType root = elements.getValue();
            final List<WebserviceDescriptionType> descriptions = root.getWebserviceDescription();
            return descriptions;
        } catch (JAXBException e) {
            // TODO logging message
            throw LOGGER.logSevereException(new WebServiceException("Failed to unmarshal webservices.xml", e));
        }
    }

}
