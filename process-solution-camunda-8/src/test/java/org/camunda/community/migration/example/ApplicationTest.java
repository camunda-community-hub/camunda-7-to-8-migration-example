package org.camunda.community.migration.example;

import static io.camunda.process.test.api.CamundaAssert.assertThat;
import static io.camunda.process.test.api.assertions.ElementSelectors.byName;

import io.camunda.client.CamundaClient;
import io.camunda.client.api.response.ProcessInstanceEvent;
import io.camunda.process.test.api.CamundaProcessTestContext;
import io.camunda.process.test.api.CamundaSpringProcessTest;
import io.camunda.process.test.api.assertions.UserTaskSelectors;
import java.time.Duration;
import java.util.HashMap;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@CamundaSpringProcessTest
@ActiveProfiles("test")
public class ApplicationTest {

  @Autowired private CamundaClient client;

  @Autowired private CamundaProcessTestContext processTestContext;

  @Test
  void testHappyPathWithUserTask() {
    // An execution listener was added for the Data Migrator - mock it in our test case. This might
    // be removed once you migrated!
    processTestContext.mockJobWorker("migrator").thenComplete();

    HashMap<String, Object> variables = new HashMap<String, Object>();
    variables.put("x", 7);

    ProcessInstanceEvent processInstance =
        client
            .newCreateInstanceCommand()
            .bpmnProcessId("sample-process-solution-process")
            .latestVersion() //
            .variables(variables) //
            .send()
            .join();

    // assert / verify that we arrive in the user task with the name "Say hello to demo"
    assertThat(processInstance).isActive();
    assertThat(processInstance).hasActiveElements(byName("Say hello to demo"));

    assertThat(UserTaskSelectors.byTaskName("Say hello to demo")) //
        .isCreated()
        .hasName("Say hello to demo")
        .hasAssignee("demo");

    // Using utility method to complete user task found by name
    processTestContext.completeUserTask("Say hello to demo");

    // Assert that it completed in the right end event, and that a Spring Bean hooked into the
    // service task has written the expected process variable
    assertThat(processInstance) //
        .isCompleted() //
        .hasCompletedElements("EndEvent_GreaterThan5");

    // Additional check to verify the expression is working properly
    assertThat(processInstance).hasVariableNames("theAnswer");
    assertThat(processInstance).hasVariable("theAnswer", 42);
  }

  @Test
  void testTimerPath() {
    // An execution listener was added for the Data Migrator - mock it in our test case. This might
    // be removed once you migrated!
    processTestContext.mockJobWorker("migrator").thenComplete();

    HashMap<String, Object> variables = new HashMap<String, Object>();
    variables.put("x", 5);

    ProcessInstanceEvent processInstance =
        client
            .newCreateInstanceCommand()
            .bpmnProcessId("sample-process-solution-process")
            .latestVersion() //
            .variables(variables) //
            .send()
            .join();

    assertThat(processInstance).hasActiveElements(byName("5 minutes"));
    // increase time so that the timer event is triggered and the process moves on
    processTestContext.increaseTime(Duration.ofMinutes(6));

    assertThat(processInstance).isCompleted().hasCompletedElements("EndEvent_SmallerThan5");
  }
}
