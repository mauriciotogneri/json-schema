package com.mauriciotogneri.jsonschema;

import java.lang.reflect.Field;

public class FieldDefinition
{
    private final Field field;

    public FieldDefinition(Field field)
    {
        this.field = field;
    }

    public String name()
    {
        return field.getName();
    }

    public TypeDefinition typeDefinition()
    {
        return new TypeDefinition(field.getType());
    }

    public Annotations annotations()
    {
        return new Annotations(field);
    }
}