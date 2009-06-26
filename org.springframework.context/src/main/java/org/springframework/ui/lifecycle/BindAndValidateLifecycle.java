/*
 * Copyright 2002-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.ui.lifecycle;

import java.util.Map;

import org.springframework.ui.alert.AlertContext;
import org.springframework.ui.binding.Binder;
import org.springframework.ui.binding.BindingResult;
import org.springframework.ui.binding.BindingResults;
import org.springframework.ui.validation.Validator;

/**
 * Implementation of the model bind and validate lifecycle lifecycle.
 * @author Keith Donald
 * @since 3.0
 */
public final class BindAndValidateLifecycle {

	private Binder binder;

	private Validator validator;

	private ValidationDecider validationDecider = ValidationDecider.ALWAYS_VALIDATE;

	private final AlertContext alertContext;

	/**
	 * Create a new bind and validate lifecycle.
	 * @param binder the binder to use for model binding
	 * @param validator the validator to use for model validation
	 * @param alertContext a context for adding binding and validation-related alerts
	 */
	public BindAndValidateLifecycle(Binder binder, Validator validator, AlertContext alertContext) {
		this.binder = binder;
		this.validator = validator;
		this.alertContext = alertContext;
	}
	
	/**
	 * Configures the strategy that determines if validation should execute after binding.
	 * @param validationDecider the validation decider
	 */
	public void setValidationDecider(ValidationDecider validationDecider) {
		this.validationDecider = validationDecider;
	}

	public void execute(Map<String, ? extends Object> sourceValues) {
		BindingResults bindingResults = binder.bind(sourceValues);
		if (validator != null && validationDecider.shouldValidateAfter(bindingResults)) {
			// TODO get validation results
			validator.validate(binder.getModel(), bindingResults.successes().properties());
		}
		for (BindingResult result : bindingResults.failures()) {
			// TODO - you may want to ignore some alerts like propertyNotFound
			alertContext.add(result.getProperty(), result.getAlert());
		}
		// TODO translate validation results into messages
	}

}