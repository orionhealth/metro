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
package com.sun.xml.ws.tx.at.tube;

import com.sun.istack.logging.Logger;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.model.wsdl.WSDLBoundOperation;
import com.sun.xml.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.ws.tx.at.WSATHelper;
import com.sun.xml.ws.api.tx.at.Transactional;
import com.sun.xml.ws.api.tx.at.TransactionalFeature;

public class WSATTubeHelper {   
    private static final Logger LOGGER = Logger.getLogger(WSATTubeHelper.class);

    public static TransactionalAttribute getTransactionalAttribute(TransactionalFeature feature, Packet packet, WSDLPort port) {
        if (feature == null) {
            feature =
                    new TransactionalFeature(true, Transactional.TransactionFlowType.SUPPORTS, Transactional.Version.DEFAULT);
        }
        //a dynamic service client can be created without a wsdl.
        if (port == null) {
            boolean isEnabled = feature.isEnabled() && Transactional.TransactionFlowType.NEVER != feature.getFlowType();
            boolean isRequired = Transactional.TransactionFlowType.MANDATORY == feature.getFlowType();
            if (WSATHelper.isDebugEnabled()) {
                debug("no wsdl port found, the effective transaction attribute is: enabled(" + isEnabled + "),required(" + isRequired + "), version(" + feature.getVersion() + ").");
            }
            return new TransactionalAttribute(isEnabled, isRequired, feature.getVersion());
        }
        WSDLBoundOperation wsdlBoundOperation = packet.getMessage().getOperation(port);
        if (wsdlBoundOperation != null
                && wsdlBoundOperation.getOperation() != null
                && !wsdlBoundOperation.getOperation().isOneWay()) {
            String opName = wsdlBoundOperation.getName().getLocalPart();
            boolean isEnabled = feature.isEnabled(opName)
                    && Transactional.TransactionFlowType.NEVER != feature.getFlowType(opName);
            boolean isRequired = Transactional.TransactionFlowType.MANDATORY == feature.getFlowType(opName);

            if (WSATHelper.isDebugEnabled()) {
                debug("the effective transaction attribute for operation' " + opName + "' is : enabled(" + isEnabled + "),required(" + isRequired + "), version(" + feature.getVersion() + ").");
            }
            return new TransactionalAttribute(isEnabled, isRequired, feature.getVersion());
        }
        if (WSATHelper.isDebugEnabled()) {
            debug("no twoway operation found for this request, the effective transaction attribute is disabled.");
        }
        return new TransactionalAttribute(false, false, Transactional.Version.DEFAULT);
    }

    private static void debug(String message) {
        LOGGER.info(message);
    }
}
