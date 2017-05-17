package com.mauriciotogneri.jsonschema;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Definitions
{
    private final Map<String, Class<?>> classes;

    public Definitions(TypeDefinition typeDef)
    {
        this.classes = new HashMap<>();
        addType(typeDef);
    }

    private void addType(TypeDefinition typeDef)
    {
        if (!typeDef.isPrimitive())
        {
            if (typeDef.isArray())
            {
                addType(new TypeDefinition(typeDef.componentType()));
            }
            else
            {
                String className = typeDef.name();

                if (!classes.containsKey(className))
                {
                    classes.put(className, typeDef.clazz());

                    for (Field field : typeDef.fields())
                    {
                        addType(new TypeDefinition(field.getType()));
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