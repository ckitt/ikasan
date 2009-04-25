/*
 * $Id: SFTPManagedConnectionMetaData.java 16794 2009-04-24 13:27:17Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/connector-sftp/src/main/java/org/ikasan/connector/sftp/outbound/SFTPManagedConnectionMetaData.java $
 * 
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 * Copyright (c) 2003-2008 Mizuho International plc. and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the 
 * Free Software Foundation Europe e.V. Talstrasse 110, 40217 Dusseldorf, Germany 
 * or see the FSF site: http://www.fsfeurope.org/.
 * ====================================================================
 */
package org.ikasan.connector.sftp.outbound;

import org.ikasan.connector.base.command.TransactionalCommandConnection;
import org.ikasan.connector.base.outbound.*;

/**
 * This is the implementation of the metadata for the SFTP resource adapter.
 * 
 * @author Ikasan Development Team
 */  
public class SFTPManagedConnectionMetaData extends EISManagedConnectionMetaData 
{
    /** Max number of connections, we support only 1 for SFTP */
    private static final int MAX_NUMBER_OF_CONNECTIONS = 1;
    
    /** The managed connection that this meta data is assocaited with */
    TransactionalCommandConnection smc;

    /**
     * Store a reference to the SFTPManagedConnection object 
     * from which this metadata is derived
     *  
     * @param smc 
     */ 
    public SFTPManagedConnectionMetaData(TransactionalCommandConnection smc) 
    { 
        this.smc = smc; 
    } 

    public String getEISProductName() 
    { 
        return "SFTP Server";  //$NON-NLS-1$
    } 

    public String getEISProductVersion() 
    { 
        return "N/A"; //$NON-NLS-1$
    } 

    @Override
    public int getMaxConnections() 
    { 
        // We support only one connection 
        return MAX_NUMBER_OF_CONNECTIONS; 
    }
} 

