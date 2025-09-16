package org.camunda.community.migration.example.resultprocessor;

import io.camunda.spring.client.bean.MethodInfo;
import io.camunda.spring.client.jobhandling.result.DefaultResultProcessorStrategy;
import io.camunda.spring.client.jobhandling.result.ResultProcessor;

public class ResultVariableResultProcessorStrategy extends DefaultResultProcessorStrategy {
  @Override
  public ResultProcessor createProcessor(MethodInfo methodInfo) {
    return new ResultVariableResultProcessor();
  }
}
