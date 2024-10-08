package com.pukhovkirill.datahub.infrastructure.file.validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import com.pukhovkirill.datahub.infrastructure.file.validator.valid.ValidFilename;

public class FilenameValidator implements ConstraintValidator<ValidFilename, String> {

    // allowed characters:
    // -> A-Za-z
    // -> 0-9
    // -> . _ - ! @ # $ % ^ & * ( ) { } [ ] < > = ~ : ; | ? ' " \ +`
    private static final String POSIX_FILENAME_PATTERN =
            "^[A-Za-z0-9 ._\\-!@#$%^&*(){}\\[\\]<>=`~:;|?'\"\\\\+]+$";

    @Override
    public void initialize(ValidFilename constraintAnnotation) {

    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return validateFilename(s);
    }

    private boolean validateFilename(String filename){
        if(filename.length() > 255) return false;
        Pattern pattern = Pattern.compile(POSIX_FILENAME_PATTERN);
        Matcher matcher = pattern.matcher(filename);
        return matcher.matches();
    }
}
