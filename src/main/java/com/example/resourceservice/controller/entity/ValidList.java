package com.example.resourceservice.controller.entity;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {ListIdsValidator.class})
public @interface ValidList {

    Class<? extends Payload> [] payload() default{};

    Class<?>[] groups() default {};

    String message() default "Validation failed or only comma separated values with length less than 200 characters are allowed";

}
