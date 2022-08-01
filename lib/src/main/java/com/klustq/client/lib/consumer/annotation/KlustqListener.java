package com.klustq.client.lib.consumer.annotation;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface KlustqListener {

    String topic();
    String group() default "group_0";
}
