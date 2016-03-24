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

package com.sun.xml.ws.tx.at.runtime;

import java.security.NoSuchAlgorithmException;
import javax.transaction.xa.Xid;


/**
 * Helper class for converting between  Xids and WS-AT transaction Ids.
 * 
 */
public abstract class TransactionIdHelper {
  private static TransactionIdHelper singleton;

  static {
    try {
      singleton = new TransactionIdHelperImpl();
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
  }
  
  /**
   * Returns the TransactionIdHelper instance.
   * 
   * @return the TransactionIdHelper instance.
   */
  public static TransactionIdHelper getInstance() {
    return singleton;
  }

  /**
   * Convert a  Xid to WS-AT Id format.
   * 
   * @param xid
   *          A  Xid.
   * @return The transaction id in WS-AT format
   */
  public abstract String xid2wsatid(Xid xid);

  /**
   * Convert a WS-AT Id that was generated from a  Xid back into a Xid
   * 
   * @param wsatid
   *          A -based WS-AT tid
   * @return A  Xid
   */
  public abstract Xid wsatid2xid(String wsatid);

  /**
   * Returns a foreign Xid that is mapped to the specified WS-AT transaction Id.
   * 
   * @param tid
   *          A foreign WS-AT tid in string representation.
   * @return A foreign Xid that is mapped ot the tid.
   */
  public abstract Xid getOrCreateXid(byte[] tid);

  /**
   * Returns the foreign Xid that is mapped to the specified WS-AT transaction
   * Id.
   * 
   * @param tid
   *          A foreign WS-AT tid.
   * @return The foreign Xid corresponding to the tid.
   */
  public abstract Xid getXid(byte[] tid);

  /**
   * Returns the foreign WS-AT transaction Id that is mapped to the foreign Xid.
   * 
   * @param xid
   *          A foreign Xid that was created from the foreign tid.
   * @return The foreign tid corresponding to the foreign Xid.
   */
  public abstract byte[] getTid(Xid xid);

  /**
   * Removes the foreign WS-AT tid to Xid mapping
   * 
   * @param tid
   *          A foreign WS-AT transaction Id.
   * @return The mapped foreign Xid, or null if no mapping exists
   */
  public abstract Xid remove(byte[] tid);

  /**
   * Removes the foreign WS-AT tid to Xid mapping
   * 
   * @param xid
   *          A foreign Xid that is mapped to a foreign tid.
   * @return The mapped tid, or null if no mapping exists.
   */
  public abstract byte[] remove(Xid xid);

}
