package com.mauriciotogneri.jsonschema.app;

import com.mauriciotogneri.jsonschema.annotations.OneOf;

public class Child
{
    Long id;

    @OneOf({Integer.class, Float.class})
    Object age;
}