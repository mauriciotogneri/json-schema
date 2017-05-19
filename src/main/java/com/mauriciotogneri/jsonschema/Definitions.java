package com.mauriciotogneri.jsonschema;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Definitions implements Iterable<TypeDefinition>
{
    private final Map<String, TypeDefinition> definitions;

    public Definitions(TypeDefinition typeDefinition)
    {
        this.definitions = new HashMap<>();
        addType(typeDefinition);
    }

    public void addType(TypeDefinition typeDefinition)
    {
        if (!typeDefinition.isPrimitive() && !typeDefinition.isRootObject())
        {
            if (typeDefinition.isArray())
            {
                addType(typeDefinition.componentType());
            }
            else
            {
                String className = typeDefinition.name();

                if (!definitions.containsKey(className))
                {
                    definitions.put(className, typeDefinition);

                    for (FieldDefinition field : typeDefinition.fields())
                    {
                        addType(field.typeDefinition());
                    }
                }
            }
        }
    }

    @Override
    public Iterator<TypeDefinition> iterator()
    {
        return definitions.values().iterator();
    }
}