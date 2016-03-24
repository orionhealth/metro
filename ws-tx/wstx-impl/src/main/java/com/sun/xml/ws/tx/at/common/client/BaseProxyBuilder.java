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

package com.sun.xml.ws.tx.at.common.client;

import com.sun.xml.ws.api.addressing.OneWayFeature;
import com.sun.xml.ws.api.addressing.WSEndpointReference;
import com.sun.xml.ws.tx.coord.common.WSCUtil;
import com.sun.xml.ws.tx.at.common.WSATVersion;

import javax.xml.ws.EndpointReference;
import javax.xml.ws.WebServiceFeature;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * This is the base class for building client proxy for participant and coordinator.
 */
public abstract class BaseProxyBuilder<T, B extends BaseProxyBuilder<T,B>> {
    protected WSATVersion<T> version;
    protected EndpointReference to;
    protected EndpointReference replyTo;
    protected List<WebServiceFeature> features;


    protected BaseProxyBuilder(WSATVersion<T> version) {
        this.version = version;
        feature(version.newAddressingFeature());
    }

    /**
     * Add feature to be enabled on the proxy built by this builder.
     * @param feature WebServiceFeature
     */
    public void feature(WebServiceFeature feature){
        if(feature == null) return;
        if(features == null) features = new ArrayList<WebServiceFeature>();
        features.add(feature);
   }

    /**
     * specifiy the wsa:to and endpoint reference parameters of the proxy built by this builder
     * @param to EndpointReference
     * @return
     */
    public B to(EndpointReference to) {
        this.to = to;
        return (B) this;
    }

    /**
     * specifiy the wsa:replyTo of the proxy built by this builder
     * @param replyTo
     * @return
     */
    public B replyTo(EndpointReference replyTo) {
        this.replyTo = replyTo;
        if(replyTo!=null)
          feature(new OneWayFeature(true, WSEndpointReference.create(replyTo)));
        return (B) this;
    }

    /**
     * specify the transaction ID as the reference parameters
     * @param txId
     * @param bqual
     * @return
     */
    public B txIdForReference(String txId, String bqual) {
        EndpointReference endpointReference = version.newEndpointReferenceBuilder().address(getDefaultCallbackAddress()).
                referenceParameter(
                        WSCUtil.referenceElementTxId(txId), WSCUtil.referenceElementBranchQual(bqual), WSCUtil.referenceElementRoutingInfo()
                ).build();
        replyTo(endpointReference);
        return (B) this;
    }


    protected WebServiceFeature[] getEnabledFeatures() {
        return features.toArray(new WebServiceFeature[0]);
    }

    /**
     * the replyto address for the corresponding endpoints.
     * @return
     */
    protected abstract String getDefaultCallbackAddress();
}
