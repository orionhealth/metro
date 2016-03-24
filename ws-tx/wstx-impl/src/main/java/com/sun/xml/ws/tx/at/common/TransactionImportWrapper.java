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

package com.sun.xml.ws.tx.at.common;

import javax.resource.spi.XATerminator;
import javax.transaction.SystemException;
import javax.transaction.xa.Xid;

/**
 * Duplicates GF Transaction Manager extensions interface {@code TransactionImport}
 * that support transaction inflow w/o resource adapter.
 *
 * @author Marek Potociar <marek.potociar at sun.com>
 */
public interface TransactionImportWrapper {

    /**
     * Recreate a transaction based on the Xid. This call causes the calling
     * thread to be associated with the specified transaction.
     *
     * <p>
     * This method imports a transactional context controlled by an external transaction manager.
     *
     * @param xid the Xid object representing a transaction.
     */
    public void recreate(Xid xid, long timeout);

    /**
     * Release a transaction. This call causes the calling thread to be
     * dissociated from the specified transaction.
     *
     * <p>
     * This call releases transactional context imported by recreate method.
     *
     * @param xid the Xid object representing a transaction.
     */
    public void release(Xid xid);

    /**
     * Provides a handle to a <code>XATerminator</code> instance which is used to import
     * an external transaction into Java EE TM.
     *
     * <p> The XATerminator exports 2PC protocol control to an external root transaction coordinator.
     *
     * @return a <code>XATerminator</code> instance.
     */
    public XATerminator getXATerminator();

    /**
     * Return duration before current transaction would timeout.
     *
     * @return Returns the duration in seconds before current transaction would
     *         timeout.
     *         Returns zero if transaction has no timeout set or if any exceptions
     *         occured while looking up remaining transaction timeout.
     *         Returns negative value if transaction already timed out.
     *
     * @exception IllegalStateException Thrown if the current thread is
     *    not associated with a transaction.
     *
     * @exception SystemException Thrown if the transaction manager
     *    encounters an unexpected error condition.
     */
    public int getTransactionRemainingTimeout() throws SystemException;
}
