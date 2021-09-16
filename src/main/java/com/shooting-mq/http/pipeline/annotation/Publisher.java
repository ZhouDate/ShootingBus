package com.shooting-mq.http.pipeline.annotation;


import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Inherited
public @interface Publisher {
    String topic() default "";

    Class target() default Object.class;
}
