/*
 * $Id: SchedulerFactoryTest.java 3629 2011-04-18 10:00:52Z mitcje $
 * $URL: http://open.jira.com/svn/IKASAN/branches/ikasaneip-0.9.x/scheduler/src/test/java/org/ikasan/scheduler/SchedulerFactoryTest.java $
 *
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 *
 * Distributed under the Modified BSD License.
 * Copyright notice: The copyright for this software and a full listing
 * of individual contributors are as shown in the packaged copyright.txt
 * file.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  - Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  - Neither the name of the ORGANIZATION nor the names of its contributors may
 *    be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 */
package org.ikasan.sample.spring.boot.builderpattern;

import org.apache.activemq.junit.EmbeddedActiveMQBroker;
import org.ikasan.builder.BuilderFactory;
import org.ikasan.builder.IkasanApplication;
import org.ikasan.builder.IkasanApplicationFactory;
import org.ikasan.builder.ModuleBuilder;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.Module;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * This test class supports the <code>SimpleExample</code> class.
 *
 * @author Ikasan Development Team
 */
@RunWith(SpringRunner.class)
public class MyApplicationTest
{
    private IkasanApplication ikasanApplication;
    private MyApplication myApplication;

    @Rule
    public EmbeddedActiveMQBroker broker = new EmbeddedActiveMQBroker();

    @Before
    public  void setup(){
        String[] args = {""};

        myApplication = new MyApplication();
        ikasanApplication = IkasanApplicationFactory.getIkasanApplication(args);
        broker.start();
    }


//    @Before
//    public void setupTest()
//    {
//
//    }


    @After
    public void shutdown(){
        ikasanApplication.close();
    }
    /**
     * Test simple invocation.
     */
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void test_scheduled_start_and_stop_flow() throws Exception
    {
        BuilderFactory builderFactory = ikasanApplication.getBuilderFactory();
        ModuleBuilder moduleBuilder = builderFactory.getModuleBuilder("moduleName");
        Flow scheduldeFlow = myApplication.getScheduledFlow(moduleBuilder, builderFactory.getComponentBuilder());

        Module module = moduleBuilder.addFlow(scheduldeFlow).build();

        ikasanApplication.run(module);

        System.out.println("Check is module healthy.");

        scheduldeFlow.start();
        pause(5000);
        assertEquals("running",scheduldeFlow.getState());
        scheduldeFlow.stop();
        pause(2000);
        assertEquals("stopped",scheduldeFlow.getState());

    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void test_jmsFlow_start_and_stop_flow() throws Exception
    {
        BuilderFactory builderFactory = ikasanApplication.getBuilderFactory();
        ModuleBuilder moduleBuilder = builderFactory.getModuleBuilder("moduleName");

        Flow jmsFlow = myApplication.getJmsFlow(moduleBuilder, builderFactory.getComponentBuilder() );

        Module module = moduleBuilder.addFlow(jmsFlow).build();

        ikasanApplication.run(module);

        System.out.println("Check is module healthy.");

        // Prepare test data
        JmsTemplate jmsTemplate = (JmsTemplate) ikasanApplication.getBean(JmsTemplate.class);

        String message  = "Hello world!";
        System.out.println("Sending a JMS message.["+message+"]");
        jmsTemplate.convertAndSend("source",message );

        jmsFlow.start();
        pause(5000);
        assertEquals("running",jmsFlow.getState());
        jmsFlow.stop();
        pause(2000);
        assertEquals("stopped",jmsFlow.getState());

        // Verify the expectations
        MessageVerifierConsumer consumer = (MessageVerifierConsumer) ikasanApplication.getBean(MessageVerifierConsumer.class);

        // Set expectation
        assertThat(consumer.getCaptureResults(), hasItem(message));


    }
    /**
     * Sleep for value in millis
     * @param value
     */
    private void pause(long value)
    {
        try
        {
            Thread.sleep(value);
        }
        catch(Exception e)
        {
            throw new RuntimeException(e);
        }
    }
}