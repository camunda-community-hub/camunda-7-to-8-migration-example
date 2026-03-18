package org.camunda.community.migration.example.resultprocessor;

import io.camunda.client.jobhandling.result.ResultProcessor;
import io.camunda.client.jobhandling.result.ResultProcessorContext;
import java.util.Map;

public class ResultVariableResultProcessor implements ResultProcessor {

  private final ResultProcessor delegate;

  public ResultVariableResultProcessor(ResultProcessor delegate) {
    this.delegate = delegate;
  }

  @Override
  public Object process(ResultProcessorContext context) {
    String resultVariable = context.getJob().getCustomHeaders().get("resultVariable");
    if (resultVariable == null) {
      return delegate.process(context);
    }
    return Map.of(resultVariable, context.getResult());
  }
}
