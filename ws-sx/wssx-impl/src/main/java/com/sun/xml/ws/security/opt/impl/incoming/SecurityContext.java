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

package com.sun.xml.ws.security.opt.impl.incoming;

import com.sun.xml.ws.api.message.AttachmentSet;
import com.sun.xml.ws.api.message.HeaderList;
import com.sun.xml.ws.security.opt.impl.attachment.AttachmentSetImpl;
import com.sun.xml.wss.ProcessingContext;
import com.sun.xml.wss.impl.policy.MLSPolicy;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author K.Venugopal@sun.com
 */
public class SecurityContext {
    
    private ArrayList processedSecurityHeaders = new ArrayList(2);
    private ArrayList bufferedSecurityHeaders = null;
    private HeaderList nonSecurityHeaders = null;
    private HashMap<String,String> shND = null;
    private HashMap<String,String> envND = null;
    private AttachmentSet attachments = null; 
    private AttachmentSet decryptedAttachments = null;
    
    private MLSPolicy inferredKB = null;
    
    private ProcessingContext pc = null;
    
    private boolean isSAMLKB = false;
    
    /** Creates a new instance of SecurityContext */
    public SecurityContext() {
      
    }
    
    public void setAttachmentSet(AttachmentSet attachments){
        this.attachments = attachments;
    }
    
    public AttachmentSet getAttachmentSet(){
        if(attachments == null){
            attachments = new AttachmentSetImpl();
        }
        return attachments;
    }
    
    public AttachmentSet getDecryptedAttachmentSet(){
        if(decryptedAttachments == null){
            decryptedAttachments = new AttachmentSetImpl();
        }
        return decryptedAttachments;
    }
    
    public MLSPolicy getInferredKB(){
        return inferredKB;
    }
    
    public void setInferredKB(MLSPolicy inferredKB){
        this.inferredKB = inferredKB;
    }
    
    public void setProcessedSecurityHeaders(ArrayList headers){
        this.processedSecurityHeaders =  headers;
    }
    
    public ArrayList getProcessedSecurityHeaders(){
        return processedSecurityHeaders;
    }
    
    public void setBufferedSecurityHeaders(ArrayList headers){
        this.bufferedSecurityHeaders =  headers;
    }
    
    public ArrayList getBufferedSecurityHeaders(){
        return bufferedSecurityHeaders;
    }
    
    public HeaderList getNonSecurityHeaders(){
        return nonSecurityHeaders;
    }
    
    public void setNonSecurityHeaders(HeaderList list){
        this.nonSecurityHeaders = list;
    }
    
    public void setSecurityHdrNSDecls(HashMap<String,String> nsDecls){
        this.shND = nsDecls;
    }
    
    public HashMap<String,String> getSecurityHdrNSDecls(){
        return this.shND;
    }
    
    public void setSOAPEnvelopeNSDecls(HashMap<String,String> nsDecls){
        this.envND = nsDecls;
    }
    
    public HashMap<String,String> getSOAPEnvelopeNSDecls(){
        return envND;
    }
    
    
    
    public ProcessingContext getProcessingContext() {
        return pc;
    }
    
    public void setProcessingContext(ProcessingContext pc){
        this.pc = pc;
    }
      
    public void setIsSAMLKeyBinding(boolean flag) {
        this.isSAMLKB = flag;
    }
    
    public boolean getIsSAMLKeyBinding() {
       return this.isSAMLKB;
    }
}
