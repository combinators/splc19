package org.combinators.guidemo.concepts;

import com.google.inject.BindingAnnotation;

import java.lang.annotation.*;

public class Concepts {

    public enum Locatable {Database, Logo}

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD, ElementType.PARAMETER})
    @BindingAnnotation
    public @interface Location {
        Locatable of();
    }
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD, ElementType.PARAMETER})
    @BindingAnnotation
    public @interface BranchName {}

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD, ElementType.PARAMETER})
    @BindingAnnotation
    public @interface ChoiceDialog {}

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD, ElementType.PARAMETER})
    @BindingAnnotation
    public @interface  OrderMenu {}

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD, ElementType.PARAMETER})
    @BindingAnnotation
    public @interface ExtraDependencies {}

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD, ElementType.PARAMETER})
    @BindingAnnotation
    public @interface BuildFile {}

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD, ElementType.PARAMETER})
    @BindingAnnotation
    public @interface DatabaseAccessCode {}
}