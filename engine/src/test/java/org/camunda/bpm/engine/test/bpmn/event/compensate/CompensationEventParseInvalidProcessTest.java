/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.camunda.bpm.engine.test.bpmn.event.compensate;

import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collection;

import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.impl.test.PluggableProcessEngineTestCase;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import junit.framework.AssertionFailedError;

/**
 * Parse an invalid process definition and assert the error message.
 *
 * @author Philipp Ossler
 */
@RunWith(Parameterized.class)
public class CompensationEventParseInvalidProcessTest {

  private static final String PROCESS_DEFINITION_DIRECTORY = "org/camunda/bpm/engine/test/bpmn/event/compensate/";

  @Parameters(name = "{index}: process definition = {0}, expected error message = {1}")
  public static Collection<Object[]> data() {
    return Arrays.asList(new Object[][] {
        { "CompensationEventParseInvalidProcessTest.illegalCompensateActivityRefParentScope.bpmn20.xml", "Invalid attribute value for 'activityRef': no activity with id 'someServiceInMainProcess' in scope 'subProcess'" },
        { "CompensationEventParseInvalidProcessTest.illegalCompensateActivityRefNestedScope.bpmn20.xml", "Invalid attribute value for 'activityRef': no activity with id 'someServiceInNestedScope' in scope 'subProcess'" },
        { "CompensationEventParseInvalidProcessTest.invalidActivityRefFails.bpmn20.xml", "Invalid attribute value for 'activityRef':" },
        { "CompensationEventParseInvalidProcessTest.multipleCompensationCatchEventsCompensationAttributeMissingFails.bpmn20.xml", "compensation boundary catch must be connected to element with isForCompensation=true" },
        { "CompensationEventParseInvalidProcessTest.multipleCompensationCatchEventsFails.bpmn20.xml", "multiple boundary events with compensateEventDefinition not supported on same activity"},
        { "CompensationEventParseInvalidProcessTest.multipleCompensationEventSubProcesses.bpmn20.xml", "multiple event subprocesses with compensation start event are not supported on the same scope" },
        { "CompensationEventParseInvalidProcessTest.compensationEventSubProcessesAtProcessLevel.bpmn20.xml", "event subprocess with compensation start event is only supported for embedded subprocess" },
        { "CompensationEventParseInvalidProcessTest.compensationEventSubprocessAndBoundaryEvent.bpmn20.xml", "compensation boundary event and event subprocess with compensation start event are not supported on the same scope" }
    });
  }

  @Parameter(0)
  public String processDefinitionResource;

  @Parameter(1)
  public String expectedErrorMessage;

  @Rule
  public ProcessEngineRule rule = new ProcessEngineRule(PluggableProcessEngineTestCase.getProcessEngine());

  protected RepositoryService repositoryService;

  @Before
  public void initServices() {
    repositoryService = rule.getRepositoryService();
  }

  @Test
  public void testParseInvalidProcessDefinition() {
    try {
      repositoryService.createDeployment()
        .addClasspathResource(PROCESS_DEFINITION_DIRECTORY + processDefinitionResource)
        .deploy();

      fail("exception expected: " + expectedErrorMessage);
    } catch (Exception e) {
      assertExceptionMessageContainsText(e, expectedErrorMessage);
    }
  }

  public void assertExceptionMessageContainsText(Exception e, String expectedMessage) {
    String actualMessage = e.getMessage();
    if (actualMessage == null || !actualMessage.contains(expectedMessage)) {
      throw new AssertionFailedError("expected presence of [" + expectedMessage + "], but was [" + actualMessage + "]");
    }
  }
}