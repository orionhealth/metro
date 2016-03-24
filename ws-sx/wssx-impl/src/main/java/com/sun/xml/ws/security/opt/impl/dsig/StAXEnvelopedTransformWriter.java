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

package com.sun.xml.ws.security.opt.impl.dsig;

import com.sun.xml.ws.security.opt.crypto.JAXBData;
import com.sun.xml.ws.security.opt.crypto.StreamWriterData;
import com.sun.xml.ws.security.opt.impl.crypto.OctectStreamData;
import com.sun.xml.wss.XWSSecurityException;
import com.sun.xml.wss.impl.MessageConstants;
import com.sun.xml.wss.impl.c14n.StAXEXC14nCanonicalizerImpl;
import com.sun.xml.wss.logging.LogDomainConstants;
import com.sun.xml.wss.logging.impl.opt.signature.LogStringsMessages;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.crypto.Data;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.jvnet.staxex.NamespaceContextEx;

/**
 *
 * @author K.Venugopal@sun.com
 */
public class StAXEnvelopedTransformWriter implements XMLStreamWriter,StreamWriterData{
    private static final Logger logger = Logger.getLogger(LogDomainConstants.IMPL_OPT_SIGNATURE_DOMAIN,
            LogDomainConstants.IMPL_OPT_SIGNATURE_DOMAIN_BUNDLE);
    
    private XMLStreamWriter nextWriter = null;
    private boolean ignore = false;
    private Data data = null;
    private int index = 0;
    private NamespaceContextEx ns = null;
    
    /** Creates a new instance of StAXEnvelopedTransformWriter */
    public StAXEnvelopedTransformWriter(XMLStreamWriter writer,Data data) {
        this.nextWriter = writer;
        this.data = data;
        if(data instanceof JAXBData){
            ns = ((JAXBData)data).getNamespaceContext();
        }else if(data instanceof StreamWriterData){
            ns = ((StreamWriterData)data).getNamespaceContext();
        }
        
    }
    
    public StAXEnvelopedTransformWriter(Data data) {
        this.data = data;
        if(data instanceof JAXBData){
            ns = ((JAXBData)data).getNamespaceContext();
        }else if(data instanceof StreamWriterData){
            ns = ((StreamWriterData)data).getNamespaceContext();
        }
    }
    
    public NamespaceContextEx getNamespaceContext() {
        return ns;
    }
    
    public void close() throws XMLStreamException {
        nextWriter.close();
    }
    
    public void flush() throws XMLStreamException {
        nextWriter.flush();
    }
    
    public void writeEndDocument() throws XMLStreamException {
        if(index >0){
            int size = index;
            for(int i=0;i<size;i++){
                writeEndElement();
            }
        }
        nextWriter.writeEndDocument();
    }
    
    public void writeEndElement() throws XMLStreamException {
        if(index > 0){
            index --;
        }        
        if(!ignore){
            nextWriter.writeEndElement();
        }        
        if(index == 0){
            ignore = false;
        }
    }
    
    public void writeStartDocument() throws XMLStreamException {
        if(!ignore){
            nextWriter.writeStartDocument();
        }
    }
    
    public void writeCharacters(char[] c, int index, int len) throws XMLStreamException {
        if(!ignore){
            nextWriter.writeCharacters(c,index,len);
        }
    }
    
    public void setDefaultNamespace(String string) throws XMLStreamException {
        if(!ignore){
            nextWriter.setDefaultNamespace(string);
        }
    }
    
    public void writeCData(String string) throws XMLStreamException {
        if(!ignore){
            nextWriter.writeCData(string);
        }
    }
    
    public void writeCharacters(String string) throws XMLStreamException {
        if(!ignore){
            nextWriter.writeCharacters(string);
        }
    }
    
    public void writeComment(String string) throws XMLStreamException {
        if(!ignore){
            nextWriter.writeComment(string);
        }
    }
    
    public void writeDTD(String string) throws XMLStreamException {
        if(!ignore){
            nextWriter.writeDTD(string);
        }
    }
    
    public void writeDefaultNamespace(String string) throws XMLStreamException {
        if(!ignore){
            nextWriter.writeDefaultNamespace(string);
        }
    }
    
    public void writeEmptyElement(String string) throws XMLStreamException {
        if(!ignore){
            nextWriter.writeEmptyElement(string);
        }
    }
    
    public void writeEntityRef(String string) throws XMLStreamException {
        if(!ignore){
            nextWriter.writeEntityRef(string);
        }
    }
    
    public void writeProcessingInstruction(String string) throws XMLStreamException {
        if(!ignore){
            nextWriter.writeProcessingInstruction(string);
        }
    }
    
    public void writeStartDocument(String string) throws XMLStreamException {
        if(!ignore){
            nextWriter.writeStartDocument(string);
        }
    }
    
    public void writeStartElement(String string) throws XMLStreamException {
        if(!ignore){
            nextWriter.writeStartElement(string);
        }
    }
    
    public void setNamespaceContext(NamespaceContext namespaceContext) throws XMLStreamException {
        if(!ignore){
            nextWriter.setNamespaceContext(namespaceContext);
        }
    }
    
    public Object getProperty(String string) throws IllegalArgumentException {
        return nextWriter.getProperty(string);
    }
    
    public String getPrefix(String string) throws XMLStreamException {
        return nextWriter.getPrefix(string);
    }
    
    public void setPrefix(String string, String string0) throws XMLStreamException {
        if(!ignore){
            nextWriter.setPrefix(string,string0);
        }
    }
    
    public void writeAttribute(String localName, String value)throws XMLStreamException {
        if(!ignore){
            nextWriter.writeAttribute(localName,value);
        }
    }
    
    public void writeEmptyElement(String string, String string0) throws XMLStreamException {
        if(!ignore){
            nextWriter.writeEmptyElement(string,string0);
        }
    }
    
    public void writeNamespace(String string, String string0) throws XMLStreamException {
        if(!ignore){
            nextWriter.writeNamespace(string,string0);
        }
    }
    
    public void writeProcessingInstruction(String string, String string0) throws XMLStreamException {
        if(!ignore){
            nextWriter.writeProcessingInstruction(string,string0);
        }
    }
    
    public void writeStartDocument(String string, String string0) throws XMLStreamException {
        if(!ignore){
            nextWriter.writeStartDocument(string,string0);
        }
    }
    
    public void writeStartElement(String namespaceURI, String localName)  throws XMLStreamException {
        if(!ignore){
            if(localName == MessageConstants.SIGNATURE_LNAME && namespaceURI == MessageConstants.DSIG_NS){
                if(!((StAXEXC14nCanonicalizerImpl)nextWriter).isParentToParentAdvice()){
                    ignore = true;
                    index ++;
                    return;    
                }                
            }            
            nextWriter.writeStartElement(namespaceURI,localName);
        }else{
            index ++;
        }
    }
    
    public void writeAttribute(String prefix, String namespaceURI, String localName, String value) throws XMLStreamException {
        if(!ignore){
            nextWriter.writeAttribute(prefix,namespaceURI,localName,value);
        }
    }
    
    public void writeEmptyElement(String string, String string0, String string1) throws XMLStreamException {
        if(!ignore){
            nextWriter.writeEmptyElement(string,string0,string1);
        }
    }
    
    public void writeStartElement(String prefix, String localName, String namespaceURI)  throws XMLStreamException {
        if(!ignore){
            if(localName == MessageConstants.SIGNATURE_LNAME && namespaceURI == MessageConstants.DSIG_NS){
                if(!((StAXEXC14nCanonicalizerImpl)nextWriter).isParentToParentAdvice()){
                    ignore = true;
                    index ++;
                    return;
                }
            }
            nextWriter.writeStartElement(prefix,localName,namespaceURI);
        }else{
            index ++;
        }
    }
    
    public void writeAttribute(String namespaceURI, String localName, String value) throws XMLStreamException {
        if(!ignore){
            nextWriter.writeAttribute(namespaceURI,localName,value);
        }
    }
    /**
     * processes the envelop transform and writes it to the data
     * @param writer XMLStreamWriter
     * @throws XMLStreamException
     */
    public void write(XMLStreamWriter writer) throws XMLStreamException {
        this.nextWriter = writer;
        if(data instanceof JAXBData){
            try {
                ((JAXBData)data).writeTo(this);
            } catch (XWSSecurityException ex) {
                logger.log(Level.SEVERE, LogStringsMessages.WSS_1706_ERROR_ENVELOPED_SIGNATURE(),ex);
                throw new XMLStreamException("Error occurred while performing Enveloped Signature");
            }
        }else if(data instanceof StreamWriterData){
            ((StreamWriterData)data).write(this);
        }else if(data instanceof OctectStreamData){
            ((OctectStreamData)data).write(this);
        }
    }
}
