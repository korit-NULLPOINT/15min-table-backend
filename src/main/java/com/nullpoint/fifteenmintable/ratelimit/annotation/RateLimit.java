package com.nullpoint.fifteenmintable.ratelimit.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimit {

    long millis();

    Scope scope() default Scope.USER;

    String key() default "";

    enum Scope {
        USER,
        IP
    }
}
