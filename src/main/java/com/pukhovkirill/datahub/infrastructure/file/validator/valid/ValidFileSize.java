package com.pukhovkirill.datahub.infrastructure.file.validator.valid;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import com.pukhovkirill.datahub.infrastructure.file.validator.FileSizeValidator;

@Target({TYPE, FIELD, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = FileSizeValidator.class)
@Documented
public @interface ValidFileSize {
    String message() default "Invalid file size";
    Class<?>[] group() default {};
    Class<? extends Payload>[] payload() default {};
}
