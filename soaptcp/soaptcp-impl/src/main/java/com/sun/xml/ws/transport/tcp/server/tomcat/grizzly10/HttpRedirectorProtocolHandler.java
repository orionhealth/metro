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

package com.sun.xml.ws.transport.tcp.server.tomcat.grizzly10;

import com.sun.enterprise.web.connector.grizzly.ByteBufferInputStream;
import com.sun.enterprise.web.portunif.ProtocolHandler;
import com.sun.enterprise.web.portunif.util.ProtocolInfo;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;

/**
 * Redirect the request to the proper protocol, which can be http or https.
 *
 * @author Jeanfrancois Arcand
 */
public class HttpRedirectorProtocolHandler implements ProtocolHandler{
    
    private static final int DEFAULT_HTTP_HEADER_BUFFER_SIZE = 48 * 1024;
    
    /**
     * The protocols supported by this handler.
     */
    protected String[] protocols = {"redirect-https", "redirect-http"};
    
    
    /**
     * Util to redirect protocol.
     */
    private HttpRedirector redirector;
    
    private int redirectPort;
    
    public HttpRedirectorProtocolHandler(int redirectPort) {
        this.redirectPort = redirectPort;
    }
    
    
    /**
     * Redirect the request to the protocol defined in the
     * <code>protocolInfo</code>. Protocols supported are http and https.
     *
     * @param protocolInfo The protocol that needs to be redirected.
     */
    public void handle(ProtocolInfo protocolInfo) throws IOException {
        if (redirector == null){
            redirector = new HttpRedirector(redirectPort);
        }
        
        if (protocolInfo.protocol.equalsIgnoreCase("https")) {
            redirector.redirectSSL(protocolInfo);
        } else {
            redirector.redirect(protocolInfo);
        }
        protocolInfo.keepAlive = false;
        
        /* ======================================================
         * Java HTTP(S) client sends request in 2 chunks: header, payload
         * We need to make sure client started to send payload before redirecting/closing
         * the connection. Otherwise client can not receive "HTTP 302 redirect" response.
         */ 
        ByteBuffer tmpBuffer = protocolInfo.byteBuffer;
        tmpBuffer.clear();
        ByteBufferInputStream is = new ByteBufferInputStream(tmpBuffer);
        try {
            is.setReadTimeout(2);
            is.setSelectionKey(protocolInfo.key);
            int count = 0;
            while (tmpBuffer.hasRemaining() && count < DEFAULT_HTTP_HEADER_BUFFER_SIZE) {
                tmpBuffer.position(tmpBuffer.limit());
                int readBytes = is.read();
                if (readBytes == -1) break;
                count += readBytes;
            }
        } catch(IOException e) {
            // ignore
        } finally {
            is.close();
        }
        //=========================================================
    }
    
    
    /**
     * Returns an array of supported protocols.
     * @return an array of supported protocols.
     */
    public String[] getProtocols() {
        return protocols;
    }
    
    
    /**
     * Invoked when the SelectorThread is about to expire a SelectionKey.
     * @return true if the SelectorThread should expire the SelectionKey, false
     *              if not.
     */
    public boolean expireKey(SelectionKey key){
        return true;
    }
}

