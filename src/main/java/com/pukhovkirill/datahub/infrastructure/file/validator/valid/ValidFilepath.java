package com.pukhovkirill.datahub.infrastructure.file.validator.valid;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import com.pukhovkirill.datahub.infrastructure.file.validator.FilepathValidator;

@Target({TYPE, FIELD, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = FilepathValidator.class)
@Documented
public @interface ValidFilepath {
    String message() default "Invalid filepath";
    Class<?>[] group() default {};
    Class<? extends Payload>[] payload() default {};
}
