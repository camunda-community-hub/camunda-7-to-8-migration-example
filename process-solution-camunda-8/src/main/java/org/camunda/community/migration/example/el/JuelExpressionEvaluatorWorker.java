package org.camunda.community.migration.example.el;

import java.beans.FeatureDescriptor;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.el.CompositeELResolver;
import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.ExpressionFactory;
import javax.el.StandardELContext;
import javax.el.ValueExpression;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import io.camunda.client.api.response.ActivatedJob;
import io.camunda.spring.client.annotation.JobWorker;

/**
 * Sample worker to evaluate JUEL Expression Language from service tasks.
 * 
 */
@Component
public class JuelExpressionEvaluatorWorker {

    private final ApplicationContext applicationContext;
    private final ExpressionFactory expressionFactory;

	@Autowired
	public JuelExpressionEvaluatorWorker(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
        this.expressionFactory = ExpressionFactory.newInstance();
    }

    public Object evaluate(String expression, Map<String, Object> variables) {
        CompositeELResolver compositeResolver = new CompositeELResolver();

        // 1. Variables from the job (highest priority)
        compositeResolver.add(new ELResolver() {
            @Override
            public Object getValue(ELContext context, Object base, Object property) {
                if (base == null && property != null) {
                    String name = property.toString();
                    if (variables.containsKey(name)) {
                        context.setPropertyResolved(true);
                        return variables.get(name);
                    }
                    if (applicationContext.containsBean(name)) {
                        context.setPropertyResolved(true);
                        return applicationContext.getBean(name);
                    }
                }
                return null;
            }

            @Override public Class<?> getType(ELContext context, Object base, Object property) { return Object.class; }
            @Override public void setValue(ELContext context, Object base, Object property, Object value) {}
            @Override public boolean isReadOnly(ELContext context, Object base, Object property) { return true; }
            @Override public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context, Object base) { return null; }
            @Override public Class<?> getCommonPropertyType(ELContext context, Object base) { return Object.class; }
        });

        StandardELContext elContext = new StandardELContext(expressionFactory);
        elContext.addELResolver(compositeResolver);

        ValueExpression ve = expressionFactory.createValueExpression(elContext, expression, Object.class);
        return ve.getValue(elContext);
    }


	@JobWorker(type = "JuelExpressionEvaluatorWorker")
	public Map<String, Object> executeJobMigrated(ActivatedJob job) throws Exception {
		Map<String, Object> resultMap = new HashMap<>();
		
	    String expression = job.getCustomHeaders().get("expression");
		
		Object result = evaluate(expression, job.getVariablesAsMap());
	    String resultVariable = job.getCustomHeaders().get("resultVariable");
		
		resultMap.put(resultVariable, result);
		return resultMap;
	}

}
