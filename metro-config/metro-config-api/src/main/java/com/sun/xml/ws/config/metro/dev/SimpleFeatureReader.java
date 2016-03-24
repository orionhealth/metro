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

package com.sun.xml.ws.config.metro.dev;

import com.sun.istack.logging.Logger;
import com.sun.xml.ws.config.metro.util.ParserUtil;

import java.util.Iterator;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;

/**
 * Parse a feature with no further attributes than "enabled" and return it as
 * a WebServiceFeature instance.
 *
 * @author Fabian Ritzmann
 */
public abstract class SimpleFeatureReader<T extends WebServiceFeature> implements FeatureReader {

    private static final Logger LOGGER = Logger.getLogger(SimpleFeatureReader.class);

    public T parse(final XMLEventReader reader) throws WebServiceException {
        try {
            final StartElement element = reader.nextEvent().asStartElement();
            boolean attributeEnabled = true;
            final QName elementName = element.getName();
            final Iterator iterator = element.getAttributes();
            while (iterator.hasNext()) {
                final Attribute nextAttribute = (Attribute) iterator.next();
                final QName attributeName = nextAttribute.getName();
                if (ENABLED_ATTRIBUTE_NAME.equals(attributeName)) {
                    attributeEnabled = ParserUtil.parseBooleanValue(nextAttribute.getValue());
                }
                else {
                    // TODO logging message
                    throw LOGGER.logSevereException(new WebServiceException("Unexpected attribute, was " + nextAttribute));
                }
            }

            loop:
            while (reader.hasNext()) {
                try {
                    final XMLEvent event = reader.nextEvent();
                    switch (event.getEventType()) {
                        case XMLStreamConstants.COMMENT:
                            break; // skipping the comments and start document events
                        case XMLStreamConstants.CHARACTERS:
                            if (event.asCharacters().isWhiteSpace()) {
                                break;
                            }
                            else {
                                // TODO: logging message
                                throw LOGGER.logSevereException(new WebServiceException("No character data allowed, was " + event.asCharacters()));
                            }
                        case XMLStreamConstants.END_ELEMENT:
                            final EndElement endElement = event.asEndElement();
                            if (!elementName.equals(endElement.getName())) {
                                // TODO logging message
                                throw LOGGER.logSevereException(new WebServiceException("Expected end element"));
                            }
                            break loop;
                        default:
                            throw LOGGER.logSevereException(new WebServiceException("Unexpected event, was " + event));
                    }
                } catch (XMLStreamException e) {
                    // TODO logging message
                    throw LOGGER.logSevereException(new WebServiceException("Failed to unmarshal XML document", e));
                }
            }
            return createFeature(attributeEnabled);
        } catch (XMLStreamException e) {
            // TODO logging message
            throw LOGGER.logSevereException(new WebServiceException("Failed to unmarshal XML document", e));
        }
    }

    /**
     * Instantiate the proper feature class.
     */
    protected abstract T createFeature(boolean enabled) throws WebServiceException;

}
