package org.camunda.community.migration.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.camunda.spring.client.annotation.Deployment;

@SpringBootApplication
@Deployment(resources = "classpath*:/**/*.bpmn")
public class Application {

  public static void main(String... args) {
    SpringApplication.run(Application.class, args);
  }
}
