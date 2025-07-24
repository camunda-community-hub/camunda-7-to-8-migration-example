package org.camunda.community.migration.example;

import io.camunda.spring.client.annotation.Deployment;
import io.camunda.spring.client.annotation.JobWorker;
import org.springframework.beans.BeansException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@Deployment(resources = "classpath*:/**/*.bpmn")
public class Application {

  public static void main(String... args) {
    ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
    try {
      context.getBean(SampleProcessStarter.class).startSomeProcesses();
    } catch (BeansException ex) {
      // Ignore - in test cases we don't include this bean!
    }
  }

  @JobWorker(name = "noop")
  public void noop() {}
}
