package org.camunda.community.migration.example;

import java.util.HashMap;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import io.camunda.client.CamundaClient;
import io.camunda.client.api.response.ProcessInstanceEvent;

@Component
@Profile("!test")
public class SampleProcessStarter {
	
	@Autowired
	private CamundaClient processEngine;
	
	public void startSomeProcesses() {
    System.out.println( "Let's start some processes to have data in the system..." );
    
    Random random = new Random();
    for (int i = 0; i < 100; i++) {
        int rand = random.nextInt(10); // 0–9
        
        HashMap<String, Object> variables = new HashMap<String, Object>();
        variables.put("x", rand);
        
        ProcessInstanceEvent processInstance = processEngine.newCreateInstanceCommand()
            .bpmnProcessId("sample-process-solution-process")
            .latestVersion()
            .variables(variables)
            .send().join();
      System.out.println( "Started " + processInstance.getProcessInstanceKey() );
    }
	}

}
