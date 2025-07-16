package org.camunda.community.migration.example;

import io.camunda.spring.client.annotation.JobWorker;
import io.camunda.spring.client.annotation.Variable;
import org.springframework.stereotype.Component;

@Component
public class SampleBean {

  @JobWorker(type = "sampleBeanSomeMethod")
  public int someMethod(@Variable("y") String text) {
    System.out.println("SampleBean.someMethod('" + text + "')");
    return 42;
  }
}
