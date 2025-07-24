package org.camunda.community.migration.example;

import jakarta.annotation.PostConstruct;
import java.util.Random;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.Variables;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
public class SampleProcessStarter {

  @Autowired private RuntimeService runtimeService;

  @PostConstruct
  public void startSomeProcesses() {
    System.out.println("Let's start some processes to have data in the system...");

    Random random = new Random();
    for (int i = 0; i < 100; i++) {
      int rand = random.nextInt(10); // 0–9

      VariableMap variables = Variables.createVariables();
      variables.putValue("x", rand);

      ProcessInstance processInstance =
          runtimeService.startProcessInstanceByKey("sample-process-solution-process", variables);
      System.out.println("Started " + processInstance.getId());
    }
  }
}
