package org.camunda.community.migration.example.resultprocessor;

import io.camunda.client.jobhandling.result.ResultProcessor;
import io.camunda.client.jobhandling.result.ResultProcessorStrategy;

public class ResultVariableResultProcessorStrategy implements ResultProcessorStrategy {

  @Override
  public ResultProcessor createProcessor(ResultProcessorStrategyContext context) {
    ResultProcessor defaultProcessor = ResultProcessorStrategy.super.createProcessor(context);
    return new ResultVariableResultProcessor(defaultProcessor);
  }
}
