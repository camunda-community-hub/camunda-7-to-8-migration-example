package org.camunda.community.migration.example.resultprocessor;

import io.camunda.spring.client.jobhandling.result.ResultProcessorStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CamundaSdkCustomConfiguration {
  @Bean
  public ResultProcessorStrategy resultProcessor() {
    return new ResultVariableResultProcessorStrategy();
  }
}
