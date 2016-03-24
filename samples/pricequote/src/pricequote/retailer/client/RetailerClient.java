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

/*
 $Id: RetailerClient.java,v 1.11 2010-10-21 14:43:39 snajper Exp $
*/

package pricequote.retailer.client;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.WebServiceException;

/**
 * @author Arun Gupta
 */
public class RetailerClient {
    private static final String ENDPOINT = "http://localhost:8080/pricequote/retailer";
    private static final String WSDL_LOCATION = ENDPOINT + "?wsdl";
    private static final QName SERVICE = new QName("http://example.org/retailer", "RetailerQuoteService");
    private static int pid = 10;

    public static void main(String[] args) {
        String endpoint = System.getProperty("endpoint");
        String pid = System.getProperty("pid");
        Quote quote = getQuote(endpoint, pid);
        displayPhotoAndPrice(quote);
    }

    /**
     * Invokes the web service.
     */
    public static Quote getQuote(String endpoint, String spid) {
        RetailerQuoteService service;
        try {
            service = new RetailerQuoteService(new URL(WSDL_LOCATION), SERVICE);
        } catch (MalformedURLException e) {
            throw new WebServiceException(e);
        }
        if (endpoint == null || endpoint.equals("")) {
            endpoint = ENDPOINT;
        }

        if (spid != null && spid != "")
            pid = Integer.valueOf(spid);

        RetailerPortType port = service.getRetailerPort();
        System.out.printf("Invoking endpoint address \"%s\" for product id \"%s\".\n", endpoint, spid);
        ((BindingProvider)port).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpoint);

        Quote quote = port.getPrice(pid);
        return quote;
    }

    private static final void displayPhotoAndPrice(Quote quote) {
        if (quote.getPhoto() != null) {
            try {
                String carName = carname(pid);
                File file = new File(carName + ".jpeg");
                ImageIO.write((BufferedImage)quote.getPhoto(), "jpeg", file);
                String imageLocation = file.getAbsolutePath();
                System.out.printf("Photo is copied to \"%s\" file.\n", imageLocation);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("No photo received.");
        }
        System.out.println("Quoted price: " + quote.getPrice());
        System.out.println("Success!");
    }

    private static final String carname(int pid) {
        switch (pid % 4) {
            case 1:
                return "AM-Vantage-2k6";
            case 2:
                return "BMW-M3-2k6";
            case 3:
                return "MB-SLR-2k6";
            case 0:
            default:
                return "Porsche-911-2k6";
        }
    }
}
