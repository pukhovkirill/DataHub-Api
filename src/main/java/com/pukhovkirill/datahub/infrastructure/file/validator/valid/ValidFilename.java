package com.pukhovkirill.datahub.infrastructure.file.validator.valid;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import com.pukhovkirill.datahub.infrastructure.file.validator.FilenameValidator;

@Target({TYPE, FIELD, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = FilenameValidator.class)
@Documented
public @interface ValidFilename {
    String message() default "Invalid filename";
    Class<?>[] group() default {};
    Class<? extends Payload>[] payload() default {};
}
