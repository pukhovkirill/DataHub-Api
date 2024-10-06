package com.pukhovkirill.datahub.infrastructure.file.validator;

import com.pukhovkirill.datahub.infrastructure.file.validator.valid.ValidFilepath;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FilepathValidator implements ConstraintValidator<ValidFilepath, String> {

    // allowed characters:
    // -> A-Za-z
    // -> 0-9
    // -> . _ - ! @ # $ % ^ & * ( ) { } [ ] < > = ~ : ; | ? ' " \ +`
    private static final String POSIX_FILENAME_PATTERN =
            "^[A-Za-z0-9 ._\\-!@#$%^&*(){}\\[\\]<>=`~:;|?'\"\\\\+]+$";

    @Override
    public void initialize(ValidFilepath constraintAnnotation) {

    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return validateFilepath(s);
    }

    private boolean validateFilepath(String filepath){
        if(filepath.length() > 4096) return false;

        String[] words = filepath.split("/");

        Pattern pattern = Pattern.compile(POSIX_FILENAME_PATTERN);

        for(String word : words){
            Matcher matcher = pattern.matcher(word);
            if(!matcher.matches()) return false;
        }

        return true;
    }
}
