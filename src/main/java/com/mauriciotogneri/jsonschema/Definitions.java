package com.mauriciotogneri.jsonschema;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Definitions
{
    private final Map<String, Class<?>> classes;

    public Definitions(TypeDefinition typeDefinition)
    {
        this.classes = new HashMap<>();
        addType(typeDefinition);
    }

    private void addType(TypeDefinition typeDefinition)
    {
        if (!typeDefinition.isPrimitive())
        {
            if (typeDefinition.isArray())
            {
                addType(new TypeDefinition(typeDefinition.componentType()));
            }
            else
            {
                String className = typeDefinition.name();

                if (!classes.containsKey(className))
                {
                    classes.put(className, typeDefinition.clazz());

                    for (FieldDefinition field : typeDefinition.fields())
                    {
                        addType(field.typeDefinition());
                    }
                }
            }
        }
    }

    public Collection<Class<?>> classes()
    {
        return classes.values();
    }
}