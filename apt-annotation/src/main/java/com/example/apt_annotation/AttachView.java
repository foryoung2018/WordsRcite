package com.example.apt_annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.CLASS) //定义被保留的时间长短  RetentionPoicy.SOURCE、RetentionPoicy.CLASS、RetentionPoicy.RUNTIME
@Target(ElementType.TYPE)         // 定义所修饰的对象范围 TYPE、FIELD、METHOD、PARAMETER、CONSTRUCTOR、LOCAL_VARIABLE等
public @interface AttachView {
}