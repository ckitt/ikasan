<%@ include file="/WEB-INF/jsp/top.jsp"%>
<!-- 
# //
# //
# // $Id: specifyJob.jsp 16798 2009-04-24 14:12:09Z mitcje $
# // $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/webconsole/war/src/main/webapp/WEB-INF/triggers/specifyJob.jsp $
# // 
# // ====================================================================
# // Ikasan Enterprise Integration Platform
# // Copyright (c) 2003-2008 Mizuho International plc. and individual contributors as indicated
# // by the @authors tag. See the copyright.txt in the distribution for a
# // full listing of individual contributors.
# //
# // This is free software; you can redistribute it and/or modify it
# // under the terms of the GNU Lesser General Public License as
# // published by the Free Software Foundation; either version 2.1 of
# // the License, or (at your option) any later version.
# //
# // This software is distributed in the hope that it will be useful,
# // but WITHOUT ANY WARRANTY; without even the implied warranty of
# // MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
# // Lesser General Public License for more details.
# //
# // You should have received a copy of the GNU Lesser General Public
# // License along with this software; if not, write to the 
# // Free Software Foundation Europe e.V. Talstrasse 110, 40217 Dusseldorf, Germany 
# // or see the FSF site: http://www.fsfeurope.org/.
# // ====================================================================
# //
# // Author:  Ikasan Development Team
# // 
-->
<div class="middle">

<h2>Specify Job</h2>


<form:form id="eventTriggerJobForm" commandName="specifyJob" method="post" modelAttribute="triggerDetails" cssClass="dataform fancydataform">
    <fieldset>
       <legend>Job Details</legend>
    
            <ol>  
                <li>
                    <label for="moduleName">Module</label>
		            <span class="nonEditable"><c:out value="${triggerDetails.moduleName}" /></span>
                </li>
                <li>
                    <label for="flowName">Flow</label>
                    <span class="nonEditable"><c:out value="${triggerDetails.flowName}" /></span>
                </li>
                <li>
                     <label for="flowElement">Flow Element</label>
                     <form:select id="flowElementName" path="flowElementName" items="${flowElementNames}"/>
                </li>
                 <li>
                     <label for="relationship">Relationship</label>                                   
			         <form:select id="relationship" path="relationship" items="${relationships}"/>
                </li>
                 <li>
                      <label for="job">Job</label>                  
                      <form:select id="jobName" path="jobName" items="${registeredJobs}"/>
                </li>
             </ol>
    </fieldset>                          
    <p>
		<input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}"/>
		<input type="submit" class="button" name="_eventId_submit" value="Submit"/>
		<input type="submit" class="button" name="_eventId_cancel" value="Cancel"/>
    </p>
</form:form>

</div>



<%@ include file="/WEB-INF/jsp/bottom.jsp"%>