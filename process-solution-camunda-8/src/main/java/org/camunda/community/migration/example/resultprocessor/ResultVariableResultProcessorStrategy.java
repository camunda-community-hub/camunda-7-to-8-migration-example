package org.camunda.community.migration.example.resultprocessor;

import io.camunda.client.bean.MethodInfo;
import io.camunda.client.jobhandling.result.ResultProcessor;
import io.camunda.client.jobhandling.result.ResultProcessorStrategy;

public class ResultVariableResultProcessorStrategy implements ResultProcessorStrategy {

  @Override
  public ResultProcessor createProcessor(MethodInfo methodInfo) {
    ResultProcessor defaultProcessor = ResultProcessorStrategy.super.createProcessor(methodInfo);
    return new ResultVariableResultProcessor(defaultProcessor);
  }
}
