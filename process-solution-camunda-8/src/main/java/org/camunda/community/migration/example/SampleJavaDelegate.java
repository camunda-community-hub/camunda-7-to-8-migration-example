package org.camunda.community.migration.example;

import io.camunda.client.api.response.ActivatedJob;
import io.camunda.spring.client.annotation.JobWorker;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class SampleJavaDelegate {

  @JobWorker(type = "sampleJavaDelegate", autoComplete = true)
  public Map<String, Object> executeJobMigrated(ActivatedJob job) throws Exception {
    Map<String, Object> resultMap = new HashMap<>();
    Object x = job.getVariablesAsMap().get("x");
    System.out.println("SampleJavaDelegate " + x);
    resultMap.put("y", "hello world");
    return resultMap;
  }
}
