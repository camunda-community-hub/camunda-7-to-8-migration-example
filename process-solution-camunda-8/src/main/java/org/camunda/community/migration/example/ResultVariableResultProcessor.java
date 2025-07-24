package org.camunda.community.migration.example;

import io.camunda.spring.client.jobhandling.result.DefaultResultProcessor;
import io.camunda.spring.client.jobhandling.result.ResultProcessorContext;
import java.util.Map;

public class ResultVariableResultProcessor extends DefaultResultProcessor {
  @Override
  public Object process(ResultProcessorContext context) {
    String resultVariable = context.getJob().getCustomHeaders().get("resultVariable");
    if (resultVariable == null) {
      return super.process(context);
    }
    return Map.of(resultVariable, context.getResult());
  }
}
