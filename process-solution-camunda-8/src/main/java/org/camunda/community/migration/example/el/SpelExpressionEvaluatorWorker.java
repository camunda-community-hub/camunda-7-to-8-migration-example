package org.camunda.community.migration.example.el;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import io.camunda.client.api.response.ActivatedJob;
import io.camunda.spring.client.annotation.JobWorker;

/**
 * Sample worker to evaluate Spring Expression Language from service tasks.
 * Could also be rewritten to use JUEL if required for the expressions at hand.
 * 
 */
@Component
public class SpelExpressionEvaluatorWorker {

	private final ApplicationContext applicationContext;
	private final ExpressionParser parser;

	@Autowired
	public SpelExpressionEvaluatorWorker(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
		parser = new SpelExpressionParser();
	}

	public Object evaluate(String expression, Map<String, Object> variables) {
		StandardEvaluationContext context = new StandardEvaluationContext();

		// Register variables
		variables.forEach(context::setVariable);

		// Optional: expose Spring beans if needed
		context.setBeanResolver((ctx, beanName) -> applicationContext.getBean(beanName));

		return parser.parseExpression(expression).getValue(context);
	}

	@JobWorker(type = "ExpressionEvaluatorWorker")
	public Map<String, Object> executeJobMigrated(ActivatedJob job) throws Exception {
		Map<String, Object> resultMap = new HashMap<>();
		
	    String expression = juelToSpel(job.getCustomHeaders().get("expression"));
		
		Object result = evaluate(expression, job.getVariablesAsMap());
	    String resultVariable = job.getCustomHeaders().get("resultVariable");
		
		resultMap.put(resultVariable, result);
		return resultMap;
	}

	private String juelToSpel(String expression) {
		expression = expression.trim();

		// Strip "#{...}" if present
		if (expression.startsWith("#{") && expression.endsWith("}")) {
			expression = expression.substring(2, expression.length() - 1);
		}
		// Strip "${...}" if present
		if (expression.startsWith("${") && expression.endsWith("}")) {
			expression = expression.substring(2, expression.length() - 1);
		}

		return expression;
	}
}
