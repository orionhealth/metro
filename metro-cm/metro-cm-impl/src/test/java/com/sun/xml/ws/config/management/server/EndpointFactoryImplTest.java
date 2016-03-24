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

package com.sun.xml.ws.config.management.server;

import com.sun.xml.ws.api.WSBinding;
import com.sun.xml.ws.api.config.management.EndpointCreationAttributes;
//import com.sun.xml.ws.api.config.management.ManagedEndpoint;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.model.SEIModel;
import com.sun.xml.ws.api.model.wsdl.WSDLModel;
import com.sun.xml.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.ws.api.pipe.Codec;
import com.sun.xml.ws.api.pipe.FiberContextSwitchInterceptor;
import com.sun.xml.ws.api.pipe.ServerTubeAssemblerContext;
import com.sun.xml.ws.api.server.Container;
import com.sun.xml.ws.api.server.EndpointComponent;
import com.sun.xml.ws.api.server.ServiceDefinition;
import com.sun.xml.ws.api.server.WSEndpoint;
import com.sun.xml.ws.api.server.WSEndpoint.CompletionCallback;
import com.sun.xml.ws.api.server.WSEndpoint.PipeHead;
import com.sun.xml.ws.metro.api.config.management.ManagedEndpoint;
import com.sun.xml.ws.policy.PolicyMap;
import com.sun.xml.ws.policy.parser.PolicyResourceLoader;
import com.sun.xml.ws.wsdl.OperationDispatcher;

import java.net.URL;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;
import javax.xml.namespace.QName;
import javax.xml.ws.EndpointReference;

import junit.framework.TestCase;
import org.glassfish.gmbal.ManagedObjectManager;
import org.w3c.dom.Element;

/**
 *
 * @author Fabian Ritzmann
 */
public class EndpointFactoryImplTest extends TestCase {
    
    public EndpointFactoryImplTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test of createEndpoint method, of class EndpointFactoryImpl.
     */
    public void testCreateEndpointPolicyMapNull() {
        WSEndpoint<String> endpoint = new MockEndpoint(null, null, null);
        EndpointCreationAttributes attributes = null;
        EndpointFactoryImpl instance = new EndpointFactoryImpl();
        WSEndpoint<String> expResult = endpoint;
        WSEndpoint<String> result = instance.createEndpoint(endpoint, attributes);
        assertTrue(result instanceof ManagedEndpoint);
        assertTrue(result.getImplementationClass().equals(expResult.getImplementationClass()));
    }

    /**
     * Test of createEndpoint method, of class EndpointFactoryImpl.
     * @throws Exception 
     */
//    public void testCreateEndpointNoManagedAssertion() throws Exception {
//        final URL resourceUrl = Thread.currentThread().getContextClassLoader().getResource("management/factory/unmanaged.wsdl");
//        final WSDLModel wsdlModel = PolicyResourceLoader.getWsdlModel(resourceUrl, true);
//        final PolicyMap policyMap = wsdlModel.getPolicyMap();
//        WSEndpoint<String> endpoint = new MockEndpoint(new QName("http://example.org/", "AddNumbersService"), new QName("http://example.org/", "AddNumbersPort"), policyMap);
//        EndpointCreationAttributes attributes = null;
//        EndpointFactoryImpl instance = new EndpointFactoryImpl();
//        WSEndpoint<String> expResult = endpoint;
//        WSEndpoint<String> result = instance.createEndpoint(endpoint, attributes);
//        assertSame(expResult, result);
//    }

    /**
     * Test of createEndpoint method, of class EndpointFactoryImpl.
     */
    public void testCreateEndpointManagedAssertion() throws Exception {
        final URL resourceUrl = Thread.currentThread().getContextClassLoader().getResource("management/factory/unmanaged.wsdl");
        final WSDLModel wsdlModel = PolicyResourceLoader.getWsdlModel(resourceUrl, true);
        final PolicyMap policyMap = wsdlModel.getPolicyMap();
        WSEndpoint<String> endpoint = new MockEndpoint(new QName("http://example.org/", "AddNumbersService"), new QName("http://example.org/", "AddNumbersPort"), policyMap);
        EndpointCreationAttributes attributes = null;
        EndpointFactoryImpl instance = new EndpointFactoryImpl();
        WSEndpoint<String> result = instance.createEndpoint(endpoint, attributes);
        assertTrue(result instanceof ManagedEndpoint);
    }


    private static class MockEndpoint extends WSEndpoint<String> {

        private final QName serviceName;
        private final QName portName;
        private final PolicyMap policyMap;

        public MockEndpoint(final QName service, final QName port, final PolicyMap map) {
            this.serviceName = service;
            this.portName = port;
            this.policyMap = map;
        }

        @Override
        public Codec createCodec() {
            return null;
        }

        @Override
        public QName getServiceName() {
            return this.serviceName;
        }

        @Override
        public QName getPortName() {
            return this.portName;
        }

        @Override
        public Class<String> getImplementationClass() {
            return String.class;
        }

        @Override
        public WSBinding getBinding() {
            return null;
        }

        @Override
        public Container getContainer() {
            return null;
        }

        @Override
        public WSDLPort getPort() {
            return null;
        }

        @Override
        public void setExecutor(Executor exec) {
        }

        @Override
        public void schedule(Packet request, CompletionCallback callback, FiberContextSwitchInterceptor interceptor) {
        }

        @Override
        public PipeHead createPipeHead() {
            return null;
        }

        @Override
        public void dispose() {
        }

        @Override
        public ServiceDefinition getServiceDefinition() {
            return null;
        }

        @Override
        public Set<EndpointComponent> getComponentRegistry() {
            return null;
        }

        @Override
        public SEIModel getSEIModel() {
            return null;
        }

        @Override
        public PolicyMap getPolicyMap() {
            return this.policyMap;
        }

        @Override
        public ManagedObjectManager getManagedObjectManager() {
            return null;
        }

        @Override
        public ServerTubeAssemblerContext getAssemblerContext() {
            return null;
        }

        @Override
        public void closeManagedObjectManager() {
        }

        @Override
        public <T extends EndpointReference> T getEndpointReference(Class<T> clazz, String address, String wsdlAddress, Element... referenceParameters) {
            return null;
        }

        @Override
        public <T extends EndpointReference> T getEndpointReference(Class<T> clazz, String address, String wsdlAddress, List<Element> metadata, List<Element> referenceParameters) {
            return null;
        }

        @Override
        public OperationDispatcher getOperationDispatcher() {
            return null;
        }
    }
}
