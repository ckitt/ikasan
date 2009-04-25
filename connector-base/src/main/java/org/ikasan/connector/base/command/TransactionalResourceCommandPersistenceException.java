/*
 * $Id: TransactionalResourceCommandPersistenceException.java 16756 2009-04-22 12:35:57Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/connector-base/src/main/java/org/ikasan/connector/base/command/TransactionalResourceCommandPersistenceException.java $
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
package org.ikasan.connector.base.command;

import org.hibernate.HibernateException;

/**
 * Exception representing a problem within the persitence layer for
 * TransactionalResourceCommands
 * 
 * @author Ikasan Development Team
 *
 */
public class TransactionalResourceCommandPersistenceException extends Exception
{

    /**
     * Constructor
     * 
     * @param message
     * @param cause
     */
    public TransactionalResourceCommandPersistenceException(String message,
            Throwable cause)
    {
        super(message, cause);
    }

    /**
     * Constructor
     * @param e
     */
    public TransactionalResourceCommandPersistenceException(HibernateException e)
    {
        super(e);
    }

    /**
     * Constructor
     * @param message
     */
    public TransactionalResourceCommandPersistenceException(String message)
    {
        super(message);
    }

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
}
