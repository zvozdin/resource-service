package com.example.resourceservice.controller.entity;

import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

@Slf4j
public final class ListIdsValidator implements ConstraintValidator<ValidList, List<String>> {

    @Override
    public boolean isValid(List<String> value, ConstraintValidatorContext context) {
        log.info("Validating list of ids request param");

        return String.join(",", value).length() < 200;
    }

}
