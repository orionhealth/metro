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
 * SCVAdapter.java
 *
 * Created on August 9, 2006, 2:47 PM
 */

package com.sun.xml.security.core.xenc;
import com.sun.xml.ws.security.opt.impl.util.CVDataContentHandlerFactory;
import com.sun.xml.ws.security.opt.impl.enc.CryptoProcessor;
import javax.activation.CommandMap;
import javax.activation.MailcapCommandMap;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.activation.DataHandler;

/**
 *
 * @author K.Venugopal@sun.com
 */
public class CVAdapter extends XmlAdapter<DataHandler,byte[]>{
    
    static{
//        DataHandler.setDataContentHandlerFactory(new CVDataContentHandlerFactory());
        
        CommandMap map = CommandMap.getDefaultCommandMap();
        if (map instanceof MailcapCommandMap) {
            MailcapCommandMap mailMap = (MailcapCommandMap) map;            
            mailMap.addMailcap("application/ciphervalue"+";;x-java-content-handler="+"com.sun.xml.ws.security.opt.impl.util.CVDataHandler");
        }        
    }
    
    private CryptoProcessor cp;
    
    public CVAdapter(){
    }
    
    public CVAdapter(CryptoProcessor cp){
        this.cp = cp;
    }
    
    public DataHandler marshal(byte[] value){
        return new DataHandler(cp,"application/ciphervalue");
    }
    
    public byte[] unmarshal(DataHandler dh){
        throw new UnsupportedOperationException();
    }
    
}
