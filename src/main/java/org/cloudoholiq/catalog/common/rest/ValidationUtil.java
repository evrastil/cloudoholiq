package org.cloudoholiq.catalog.common.rest;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by vrastil on 19.1.2015.
 */
public class ValidationUtil {

    public static <T> void validateEntity(T entity) {
        Set<String> errors = new HashSet<>();
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

        Set<ConstraintViolation<T>> violations = validator.validate(entity);
        if (!violations.isEmpty()) {
            for (ConstraintViolation<T> v : violations) {
                errors.add(String.format("%s %s (was %s)",
                        v.getPropertyPath(), v.getMessage(), v.getInvalidValue()));
            }
        }

        if(!errors.isEmpty()) {
            throw new WebApplicationException(
                    Response.status(422)
                            .entity(errors)
                            .build());

        }
    }
}
