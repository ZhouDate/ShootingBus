package com.shooting-bus.http.pipeline.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Inherited
public @interface Subscriber {
    String topic() default "";

    Class paramType() default Object.class;
}
