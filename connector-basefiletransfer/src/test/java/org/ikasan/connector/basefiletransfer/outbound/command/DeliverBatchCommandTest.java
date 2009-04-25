/*
 * $Id: DeliverBatchCommandTest.java 16767 2009-04-23 12:37:52Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/connector-basefiletransfer/src/test/java/org/ikasan/connector/basefiletransfer/outbound/command/DeliverBatchCommandTest.java $
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
package org.ikasan.connector.basefiletransfer.outbound.command;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.resource.ResourceException;

import junit.framework.TestCase;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;

import org.ikasan.connector.base.command.ExecutionContext;
import org.ikasan.connector.base.command.ExecutionOutput;
import org.ikasan.connector.base.command.XidImpl;
import org.ikasan.connector.basefiletransfer.net.ClientCommandCdException;
import org.ikasan.connector.basefiletransfer.net.ClientCommandLsException;
import org.ikasan.connector.basefiletransfer.net.ClientCommandMkdirException;
import org.ikasan.connector.basefiletransfer.net.ClientCommandPutException;
import org.ikasan.connector.basefiletransfer.net.ClientCommandPwdException;
import org.ikasan.connector.basefiletransfer.net.ClientCommandRenameException;
import org.ikasan.connector.basefiletransfer.net.ClientException;
import org.ikasan.connector.basefiletransfer.net.ClientListEntry;
import org.ikasan.connector.basefiletransfer.net.FileTransferClient;
import org.ikasan.connector.basefiletransfer.outbound.command.DeliverBatchCommand;
import org.ikasan.connector.basefiletransfer.outbound.command.util.BatchedFileProvider;
import org.ikasan.connector.basefiletransfer.outbound.command.util.FileHandle;
import org.ikasan.connector.basefiletransfer.outbound.command.util.UniqueIdGenerator;

/**
 * Tests the function of the DeliverBatchCommand
 * 
 * @author Ikasan Development Team
 */
public class DeliverBatchCommandTest extends TestCase
{
    /** we are dealing with pathnames so make sure we stay platform independent */
    final String FILE_SEPARATOR = System.getProperty("file.separator");

    /**
     * directory to deliver file to
     */
    final String outputDir = "outputDir";

    /**
     * starting directory when we first connect
     */
    final String startingDir = "startingDir";

    /**
     * overwrite any existing files of the same name
     */
    boolean overwriteExisting = true;

    /**
     * FileHandles returned by the mocked BatchedFileProvider
     */
    final List<FileHandle> fileHandles = new ArrayList<FileHandle>();

    /**
     * directory path that the file will be stored in
     */
    final File nestedFileDirectory = new File("parent1/parent2");

    /**
     * Deliverable file in a nested dir
     */
    final File nestedFile1 = new File(nestedFileDirectory, "file1");

    /**
     * Deliverable file in a nested dir
     */
    final File nestedFile2 = new File(nestedFileDirectory, "file2");

    /**
     * Deliverable file in a nested dir
     */
    final File nestedFile3 = new File(nestedFileDirectory, "file3");

    /**
     * deliverable content
     */
    final byte[] content = new byte[1024];

    /**
     * dummy random id for generated temp fir
     */
    final String notSoRandomId = "12345";

    /**
     * full generated temp dir name
     */
    final String generatedTempDirectoryName = "temp_" + notSoRandomId;

    /**
     * File representation of the temp dir
     */
    final File tempDirFile = new File(generatedTempDirectoryName);

    /**
     * Dummy dir listing of the temp dir following execute
     */
    final List<ClientListEntry> tempDirectoryListing = new ArrayList<ClientListEntry>();

    @Override
    public void setUp() throws URISyntaxException
    {
        fileHandles.add(new FileHandle(nestedFile1.getPath(), new ByteArrayInputStream(content)));
        fileHandles.add(new FileHandle(nestedFile2.getPath(), new ByteArrayInputStream(content)));
        fileHandles.add(new FileHandle(nestedFile3.getPath(), new ByteArrayInputStream(content)));

        tempDirectoryListing.add(BaseFileTransferCommandJUnitHelper.createEntry(outputDir + "/" + generatedTempDirectoryName + "/parent1"));
        tempDirectoryListing.add(BaseFileTransferCommandJUnitHelper.createEntry(outputDir + "/" + generatedTempDirectoryName + "/."));
        tempDirectoryListing.add(BaseFileTransferCommandJUnitHelper.createEntry(outputDir + "/" + generatedTempDirectoryName + "/.."));
    }

    /**
     * 
     * Tests the function of the execute method
     * 
     * @throws ClientCommandPwdException
     * @throws ClientCommandCdException
     * @throws ResourceException
     * @throws ClientCommandMkdirException
     * @throws ClientCommandLsException
     * @throws ClientCommandPutException
     * @throws URISyntaxException
     */
    public void testExecute() throws ClientCommandPwdException, ClientCommandCdException, ResourceException,
            ClientCommandPutException, ClientCommandLsException, ClientCommandMkdirException,
            URISyntaxException
    {
        DeliverBatchCommand command = new DeliverBatchCommand(outputDir, overwriteExisting);
        Mockery interfaceMockery = new Mockery();
        Mockery classMockery = new Mockery()
        {
            {
                setImposteriser(ClassImposteriser.INSTANCE);
            }
        };
        final FileTransferClient client = classMockery.mock(FileTransferClient.class);
        classMockery.checking(new Expectations()
        {
            {
                // capture the directory that we start out in
                one(client).pwd();
                will(returnValue(startingDir));
                // cd to the output dir
                one(client).cd(outputDir);

                // check for existing file/directory with same name as the batch name
                one(client).ls(".");
                // the output dir is empty
                will(returnValue(new ArrayList<ClientListEntry>()));
                // put the new file
                one(client).putWithOutputStream(with(new TempPathMatcher(nestedFile1.getPath())), with(any(InputStream.class)));
                // put the new file
                one(client).putWithOutputStream(with(new TempPathMatcher(nestedFile2.getPath())), with(any(InputStream.class)));
                // put the new file
                one(client).putWithOutputStream(with(new TempPathMatcher(nestedFile3.getPath())), with(any(InputStream.class)));
                // return back to the starting dir
                one(client).cd(startingDir);
            }
        });

        final UniqueIdGenerator notSoRandomIdGenerator = interfaceMockery.mock(UniqueIdGenerator.class);

        final BatchedFileProvider batchedFileProvider = interfaceMockery.mock(BatchedFileProvider.class);
        interfaceMockery.checking(new Expectations()
        {
            {
                exactly(4).of(batchedFileProvider).hasNext();
                will(onConsecutiveCalls(returnValue(true), returnValue(true), returnValue(true), returnValue(false)));
                exactly(3).of(batchedFileProvider).next();
                will(onConsecutiveCalls(returnValue(fileHandles.get(0)), returnValue(fileHandles.get(1)),
                    returnValue(fileHandles.get(2))));

                atLeast(0).of(notSoRandomIdGenerator).getUniqueId();
                will(returnValue(notSoRandomId));
            }

        });

        ExecutionContext executionContext = createExecutionContext(batchedFileProvider, "filename.zip");
        command.setExecutionContext(executionContext);
        command.setTransactionJournal(BaseFileTransferCommandJUnitHelper.getTransactionJournal(command, 3));
        command.setIdGenerator(notSoRandomIdGenerator);

        command.execute(client, new XidImpl(new byte[0], new byte[0], 0));
    }

    /**
     * Tests the commit function
     * 
     * @throws ClientCommandPwdException
     * @throws ClientCommandCdException
     * @throws ClientCommandPutException
     * @throws ClientCommandLsException
     * @throws ClientCommandMkdirException
     * @throws ResourceException
     * @throws URISyntaxException
     * @throws ClientCommandRenameException
     */
    public void testCommit() throws ClientCommandPwdException, ClientCommandCdException,
            ClientCommandPutException, ClientCommandLsException, ClientCommandMkdirException,
            ResourceException, URISyntaxException, ClientCommandRenameException
    {
        final String batchName = "filename.zip";

        DeliverBatchCommand command = new DeliverBatchCommand(outputDir, overwriteExisting);
        Mockery interfaceMockery = new Mockery();
        Mockery classMockery = new Mockery()
        {
            {
                setImposteriser(ClassImposteriser.INSTANCE);
            }
        };
        final FileTransferClient client = classMockery.mock(FileTransferClient.class);
        classMockery.checking(new Expectations()
        {
            {
                // capture the directory that we start out in
                one(client).pwd();
                will(returnValue(startingDir));
                // cd to the output dir
                one(client).cd(outputDir);

                // check for existing file/dir with same name as the batch name
                one(client).ls(".");
                // the output dir is empty
                will(returnValue(new ArrayList<ClientListEntry>()));
                // put the new file
                one(client).putWithOutputStream(with(new TempPathMatcher(nestedFile1.getPath())), with(any(InputStream.class)));
                // put the new file                
                one(client).putWithOutputStream(with(new TempPathMatcher(nestedFile2.getPath())), with(any(InputStream.class)));
                // put the new file
                one(client).putWithOutputStream(with(new TempPathMatcher(nestedFile3.getPath())), with(any(InputStream.class)));
                // return back to the starting dir
                one(client).cd(startingDir);
            }
        });

        final UniqueIdGenerator notSoRandomIdGenerator = interfaceMockery.mock(UniqueIdGenerator.class);

        final BatchedFileProvider batchedFileProvider = interfaceMockery.mock(BatchedFileProvider.class);
        interfaceMockery.checking(new Expectations()
        {
            {
                exactly(4).of(batchedFileProvider).hasNext();
                will(onConsecutiveCalls(returnValue(true), returnValue(true), returnValue(true), returnValue(false)));
                exactly(3).of(batchedFileProvider).next();
                will(onConsecutiveCalls(returnValue(fileHandles.get(0)), returnValue(fileHandles.get(1)),
                    returnValue(fileHandles.get(2))));

                atLeast(0).of(notSoRandomIdGenerator).getUniqueId();
                will(returnValue(notSoRandomId));
            }

        });

        ExecutionContext executionContext = new ExecutionContext();
        executionContext.put(ExecutionContext.BATCHED_FILE_PROVIDER, batchedFileProvider);

        executionContext.put(ExecutionContext.BATCHED_FILE_NAME, batchName);
        command.setExecutionContext(executionContext);
        command.setTransactionJournal(BaseFileTransferCommandJUnitHelper.getTransactionJournal(command, 5));
        command.setIdGenerator(notSoRandomIdGenerator);

        ExecutionOutput output = command.execute(client, new XidImpl(new byte[0], new byte[0], 0));
        final String tempDir = (String) output.getResult();

        classMockery.checking(new Expectations()
        {
            {

                // and the commit stuff
                one(client).ensureConnection();
                one(client).pwd();
                // capture the directory that we start out in
                will(returnValue(startingDir));
                // cd to the output dir
                one(client).cd(outputDir);

                one(client).ensureConnection();

                one(client).rename(tempDir, batchName);

                // finally return to the starting dir
                one(client).cd(startingDir);

            }
        });

        command.commit();
    }

    /**
     * Tests the rollback function
     * 
     * @throws ResourceException
     * @throws ClientException
     * @throws ClientCommandMkdirException
     * @throws ClientCommandLsException
     * @throws ClientCommandPutException
     * @throws ClientCommandCdException
     * @throws ClientCommandPwdException
     * @throws URISyntaxException
     */
    public void testRollback() throws ResourceException, ClientCommandPwdException, ClientCommandCdException,
            ClientCommandPutException, ClientCommandLsException, ClientCommandMkdirException,
            ClientException, URISyntaxException
    {
        DeliverBatchCommand command = new DeliverBatchCommand(outputDir, overwriteExisting);
        Mockery interfaceMockery = new Mockery();
        Mockery classMockery = new Mockery()
        {
            {
                setImposteriser(ClassImposteriser.INSTANCE);
            }
        };
        final FileTransferClient client = classMockery.mock(FileTransferClient.class);
        classMockery.checking(new Expectations()
        {
            {
                // capture the directory that we start out in
                one(client).pwd();
                will(returnValue(startingDir));
                // cd to the output dir
                one(client).cd(outputDir);

                // check for existing file/dir with same name as the batch name
                one(client).ls(".");
                // the output dir is empty
                will(returnValue(new ArrayList<ClientListEntry>()));
                // put the new file
                one(client).putWithOutputStream(with(new TempPathMatcher(nestedFile1.getPath())), with(any(InputStream.class)));
                // put the new file
                one(client).putWithOutputStream(with(new TempPathMatcher(nestedFile2.getPath())), with(any(InputStream.class)));
                // put the new file
                one(client).putWithOutputStream(with(new TempPathMatcher(nestedFile3.getPath())), with(any(InputStream.class)));
                // return back to the starting dir
                one(client).cd(startingDir);
            }
        });

        final UniqueIdGenerator notSoRandomIdGenerator = interfaceMockery.mock(UniqueIdGenerator.class);

        final BatchedFileProvider batchedFileProvider = interfaceMockery.mock(BatchedFileProvider.class);
        interfaceMockery.checking(new Expectations()
        {
            {
                exactly(4).of(batchedFileProvider).hasNext();
                will(onConsecutiveCalls(returnValue(true), returnValue(true), returnValue(true), returnValue(false)));
                exactly(3).of(batchedFileProvider).next();
                will(onConsecutiveCalls(returnValue(fileHandles.get(0)), returnValue(fileHandles.get(1)),
                    returnValue(fileHandles.get(2))));

                atLeast(0).of(notSoRandomIdGenerator).getUniqueId();
                will(returnValue(notSoRandomId));
            }

        });

        ExecutionContext executionContext = createExecutionContext(batchedFileProvider, "filename.zip");
        command.setExecutionContext(executionContext);
        command.setTransactionJournal(BaseFileTransferCommandJUnitHelper.getTransactionJournal(command, 5));
        command.setIdGenerator(notSoRandomIdGenerator);

        ExecutionOutput output = command.execute(client, new XidImpl(new byte[0], new byte[0], 0));
        final String tempDir = (String) output.getResult();

        classMockery.checking(new Expectations()
        {
            {
                // and the rollback stuff
                one(client).deleteRemoteDirectory(new File(outputDir, tempDir).getPath(), true);
            }
        });

        command.rollback();
    }

    /**
     * @param batchedFileProvider
     * @param batchName
     * @return An executionContext
     */
    private ExecutionContext createExecutionContext(final BatchedFileProvider batchedFileProvider, String batchName)
    {
        ExecutionContext executionContext = new ExecutionContext();
        executionContext.put(ExecutionContext.BATCHED_FILE_PROVIDER, batchedFileProvider);
        executionContext.put(ExecutionContext.BATCHED_FILE_NAME, batchName);
        return executionContext;
    }

    /**
     * @author verbma
     */
    class TempPathMatcher extends BaseMatcher<String>
    {

        /**
         * The path
         */
        private String path;

        /**
         * @param path
         */
        public TempPathMatcher(String path)
        {
            this.path = path;
        }

        public boolean matches(Object item)
        {
            String pathToTest = (String) item;
            boolean result = true;

            if (!pathToTest.startsWith("temp"))
            {
                result = false;
            }

            if (!pathToTest.endsWith(path))
            {
                result = false;
            }

            return result;
        }

        public void describeTo(Description description)
        {
            description.appendText("matches a string that starts with temp and ends with").appendValue(path);
        }

    }
}
