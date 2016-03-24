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

import com.sun.enterprise.web.portunif.ProtocolFinder;
import com.sun.enterprise.web.portunif.util.ProtocolInfo;
import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * A <code>ProtocolFinder</code> implementation that parse the available
 * SocketChannel bytes looking for the 'http' bytes. An http request will
 * always has the form of:
 *
 * METHOD URI PROTOCOL/VERSION
 *
 * example: GET / HTTP/1.1
 *
 * The algorithm will try to find the protocol token. 
 *
 * This object shoudn't be called by several threads simultaneously.
 *
 * @author Jeanfrancois Arcand
 */
public class HttpRedirectorProtocolFinder implements ProtocolFinder {
    
    public HttpRedirectorProtocolFinder() {
    }
    
    
    /**
     * Try to find if the current connection is using the HTTP protocol.
     * 
     * @param ProtocolInfo The ProtocolInfo that contains the information 
     *                      about the current protocol.
     */
    public void find(ProtocolInfo protocolInfo) {      
        SelectionKey key = protocolInfo.key;    
        SocketChannel socketChannel = (SocketChannel)key.channel();
        ByteBuffer byteBuffer = protocolInfo.byteBuffer;
                      
        int loop = 0;
        int count = -1;
        
        if (protocolInfo.bytesRead == 0) {
            try {
                while ( socketChannel.isOpen() && 
                        ((count = socketChannel.read(byteBuffer))> -1)){

                    if ( count == 0 ){
                        loop++;
                        if (loop > 2){
                            break;
                        }
                        continue;
                    }
                } 
            } catch (IOException ex){
                ;
            } finally {               
                protocolInfo.bytesRead = count;
                if ( count == -1 ){
                    return;
                }
            }  
        }
                          
        int curPosition = byteBuffer.position();
        int curLimit = byteBuffer.limit();
      
        // Rule a - If nothing, return to the Selector.
        if (byteBuffer.position() == 0){
            protocolInfo.byteBuffer = byteBuffer;
            return;
        }
       
        byteBuffer.position(0);
        byteBuffer.limit(curPosition);
        int state=0;
        int start=0;
        int end= 0;   
        
        try {                         
            byte c;            
            
            // Rule b - try to determine the context-root
            while(byteBuffer.hasRemaining()) {
                c = byteBuffer.get();
                // State Machine
                // 0 - Search for the first SPACE ' ' between the method and the
                //     the request URI
                // 1 - Search for the second SPACE ' ' between the request URI
                //     and the method
                switch(state) {
                    case 0: // Search for first ' '
                        if (c == 0x20){
                            state = 1;
                            start = byteBuffer.position();
                        }
                        break;
                    case 1: // Search for next ' '
                        if (c == 0x20){
                           state = 2;
                           end = byteBuffer.position() - 1; 	                                              
                           byteBuffer.position(start);
                           byte[] requestURI = new byte[end-start]; 	 
                           byteBuffer.get(requestURI);    
                           protocolInfo.requestURI = new String(requestURI);
                        }
                        break;
                     case 2: // Search for /
                        if (c == 0x2f){
                            protocolInfo.protocol = 
                                    protocolInfo.isSecure?"redirect-https":"redirect-http";
                            protocolInfo.byteBuffer = byteBuffer;
                            protocolInfo.socketChannel = 
                                    (SocketChannel)key.channel();
                            return;
                       }
                       break;                       
                    default:
                        throw new IllegalArgumentException("Unexpected state");
                }      
            }
        } catch (BufferUnderflowException bue) {
        } finally {     
            byteBuffer.limit(curLimit);
            byteBuffer.position(curPosition); 
            protocolInfo.bytesRead = byteBuffer.position();                            
        }    
        return;
    }
    
}
