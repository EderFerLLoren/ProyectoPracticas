package executioncontiditions;

import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;

public class MyExecutionCondition implements ExecutionCondition {

    @Override
    public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
        // Aquí puedes colocar tu lógica de evaluación condicional
        String testName = context.getDisplayName();
    	if(!testName.toLowerCase().startsWith("test")) {
        	return ConditionEvaluationResult.disabled("La prueba no se ejecuta, no se cumple el criterio de nombrado");
        }
        return ConditionEvaluationResult.enabled("se ejecuta");
    }

	
}