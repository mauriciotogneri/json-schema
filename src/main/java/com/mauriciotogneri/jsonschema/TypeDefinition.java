package com.mauriciotogneri.jsonschema;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.Date;

public class TypeDefinition
{
    private final Class<?> clazz;

    public TypeDefinition(Class<?> clazz)
    {
        this.clazz = clazz;
    }

    public Class<?> type()
    {
        return clazz;
    }

    public String name()
    {
        return clazz.getCanonicalName();
    }

    public FieldDefinition[] fields()
    {
        Field[] fields = clazz.getDeclaredFields();

        FieldDefinition[] result = new FieldDefinition[fields.length];

        for (int i = 0; i < fields.length; i++)
        {
            result[i] = new FieldDefinition(fields[i]);
        }

        return result;
    }

    public Object[] enums()
    {
        return clazz.getEnumConstants();
    }

    public TypeDefinition componentType()
    {
        return new TypeDefinition(clazz.getComponentType());
    }

    public Boolean isString()
    {
        return (clazz.equals(String.class) || (clazz.equals(Character.class) || (clazz.equals(char.class))));
    }

    public Boolean isBoolean()
    {
        return (clazz.equals(Boolean.class) || clazz.equals(boolean.class));
    }

    public Boolean isInteger()
    {
        return (clazz.equals(Integer.class) || clazz.equals(int.class) || clazz.equals(Long.class) || clazz.equals(long.class));
    }

    public Boolean isNumber()
    {
        return (clazz.equals(Float.class) || clazz.equals(float.class) || clazz.equals(Double.class) || clazz.equals(double.class));
    }

    public Boolean isDate()
    {
        return (clazz.equals(Date.class));
    }

    public Boolean isUri()
    {
        return (clazz.equals(URI.class));
    }

    public Boolean isFile()
    {
        return (clazz.equals(File.class));
    }

    public Boolean isEnum()
    {
        return (clazz.isEnum());
    }

    public Boolean isArray()
    {
        return (clazz.isArray());
    }

    public Boolean isPrimitive()
    {
        return (isString() || isBoolean() || isInteger() || isNumber() || isDate() || isUri() || isFile() || isEnum());
    }

    public Boolean isRootObject()
    {
        return (clazz.equals(Object.class));
    }

    public static TypeDefinition[] fromList(Class<?>[] list)
    {
        TypeDefinition[] result = new TypeDefinition[list.length];

        for (int i = 0; i < list.length; i++)
        {
            result[i] = new TypeDefinition(list[i]);
        }

        return result;
    }
}