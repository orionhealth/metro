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

package com.sun.xml.wss;

/**
 *
 * @author K.Venugopal@sun.com
 */
import com.sun.xml.wss.impl.XWSSecurityRuntimeException;
import com.sun.xml.wss.logging.LogDomainConstants;
import com.sun.xml.wss.logging.LogStringsMessages;
import java.util.logging.Level;
import java.util.logging.Logger;
public final class BasicSecurityProfile {
    
    private boolean timeStampFound = false;
    private static Logger log = Logger.getLogger(LogDomainConstants.WSS_API_DOMAIN,LogDomainConstants.WSS_API_DOMAIN_BUNDLE);
    
    /** Creates a new instance of BasicSecurityProfile */
    public BasicSecurityProfile() {
    }
    
    /**
     * 
     */
    public final static void log_bsp_3203(){
        log.log(Level.SEVERE,LogStringsMessages.BSP_3203_ONECREATED_TIMESTAMP());
        throw new XWSSecurityRuntimeException(LogStringsMessages.BSP_3203_ONECREATED_TIMESTAMP());
    }
    
    /**
     * 
     */
    public final static void log_bsp_3224(){
        log.log(Level.SEVERE,LogStringsMessages.BSP_3224_ONEEXPIRES_TIMESTAMP());
        throw new XWSSecurityRuntimeException(LogStringsMessages.BSP_3224_ONEEXPIRES_TIMESTAMP());
    }
    
    /**
     * 
     * @param elementName 
     */
    public final static void log_bsp_3222(String elementName){
        log.log(Level.SEVERE,LogStringsMessages.BSP_3222_ELEMENT_NOT_ALLOWED_UNDER_TIMESTMP(elementName));
        throw new XWSSecurityRuntimeException(LogStringsMessages.BSP_3222_ELEMENT_NOT_ALLOWED_UNDER_TIMESTMP(elementName));
    }
    
    /**
     * 
     */
    public final static void log_bsp_3221(){
        log.log(Level.SEVERE,LogStringsMessages.BSP_3221_CREATED_BEFORE_EXPIRES_TIMESTAMP());
        throw new XWSSecurityRuntimeException(LogStringsMessages.BSP_3221_CREATED_BEFORE_EXPIRES_TIMESTAMP());
    }
    
    /**
     * 
     * @throws com.sun.xml.wss.XWSSecurityException 
     */
    public final static void log_bsp_3227() throws XWSSecurityException{
        log.log(Level.SEVERE,LogStringsMessages.BSP_3227_SINGLE_TIMESTAMP());
        throw new XWSSecurityException(LogStringsMessages.BSP_3227_SINGLE_TIMESTAMP());
    }
    
    /**
     * 
     */
    public final static void log_bsp_3225(){
        log.log(Level.SEVERE,LogStringsMessages.BSP_3225_CREATED_VALUE_TYPE_TIMESTAMP());
        throw new XWSSecurityRuntimeException(LogStringsMessages.BSP_3225_CREATED_VALUE_TYPE_TIMESTAMP());
    }
    
    /**
     * 
     */
    public final static void log_bsp_3226(){
        log.log(Level.SEVERE,LogStringsMessages.BSP_3226_EXPIRES_VALUE_TYPE_TIMESTAMP());
        throw new XWSSecurityRuntimeException(LogStringsMessages.BSP_3226_EXPIRES_VALUE_TYPE_TIMESTAMP());
    }
    
    /**
     * 
     * @throws com.sun.xml.wss.XWSSecurityException 
     */
    public final static void log_bsp_3104()throws XWSSecurityException{
        log.log(Level.WARNING,LogStringsMessages.BSP_3104_ENVELOPED_SIGNATURE_DISCORAGED());
    }
    
    /**
     * 
     * @param value 
     */
    public void setTimeStampFound(boolean value){
        this.timeStampFound = value;
    }
    /**
     * 
     * @return boolean
     */
    public boolean isTimeStampFound(){
        return timeStampFound;
    }
    
}
