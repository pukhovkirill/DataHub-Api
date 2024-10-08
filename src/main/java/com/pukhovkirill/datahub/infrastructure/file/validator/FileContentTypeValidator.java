package com.pukhovkirill.datahub.infrastructure.file.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import com.pukhovkirill.datahub.infrastructure.file.validator.valid.ValidFileContentType;

public class FileContentTypeValidator implements ConstraintValidator<ValidFileContentType, String> {
    
    @Override
    public void initialize(ValidFileContentType constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return validateFileContentType(s);
    }
    
    private boolean validateFileContentType(String fileContentType) {
        // todo: write file content type validation
        return true;
    }
}
