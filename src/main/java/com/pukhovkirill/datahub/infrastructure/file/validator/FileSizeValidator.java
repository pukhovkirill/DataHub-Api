package com.pukhovkirill.datahub.infrastructure.file.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import com.pukhovkirill.datahub.infrastructure.file.validator.valid.ValidFileSize;

public class FileSizeValidator implements ConstraintValidator<ValidFileSize, Long> {

    @Override
    public void initialize(ValidFileSize constraintAnnotation) {

    }

    @Override
    public boolean isValid(Long aLong, ConstraintValidatorContext constraintValidatorContext) {
        return validateFileSize(aLong);
    }

    private boolean validateFileSize(Long fileSize) {
        return fileSize >= 0;
    }
}
