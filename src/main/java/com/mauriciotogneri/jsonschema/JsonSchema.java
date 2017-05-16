package com.mauriciotogneri.jsonschema;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.lang.reflect.Field;

public class JsonSchema
{
    private final TypeDefinition typeDef;
    private final Definitions definitions;

    private static final String TYPE_STRING = "string";
    private static final String TYPE_BOOLEAN = "boolean";
    private static final String TYPE_INTEGER = "integer";
    private static final String TYPE_NUMBER = "number";
    private static final String TYPE_FILE = "file";
    private static final String TYPE_ARRAY = "array";

    public JsonSchema(Class<?> clazz)
    {
        this.typeDef = new TypeDefinition(clazz);
        this.definitions = new Definitions(typeDef);
    }

    public JsonObject schema()
    {
        JsonObject schema = schema(true);
        JsonObject defs = new JsonObject();

        for (Class<?> clazz : definitions.classes())
        {
            JsonSchema jsonSchema = new JsonSchema(clazz);
            defs.add(clazz.getCanonicalName(), jsonSchema.schema(false));
        }

        schema.add("definitions", defs);

        return schema;
    }

    private JsonObject schema(Boolean useReferences)
    {
        JsonObject schema = new JsonObject();

        if (typeDef.isPrimitive())
        {
            fillPrimitive(schema, typeDef);
        }
        else if (typeDef.isArray())
        {
            schema.addProperty("type", TYPE_ARRAY);

            JsonSchema componentSchema = new JsonSchema(typeDef.componentType());
            schema.add("items", componentSchema.schema(true));
        }
        else
        {
            if (useReferences)
            {
                schema.addProperty("$ref", String.format("#/definitions/%s", typeDef.name()));
            }
            else
            {
                schema.addProperty("type", "object");
                schema.add("properties", properties());

                JsonArray required = required();

                if (required.size() > 0)
                {
                    schema.add("required", required);
                }
            }
        }

        return schema;
    }

    private JsonObject properties()
    {
        JsonObject properties = new JsonObject();

        for (Field field : typeDef.fields())
        {
            Annotations annotations = new Annotations(field);
            String name = field.getName();
            TypeDefinition typeDef = new TypeDefinition(field.getType());

            JsonObject fieldObject = new JsonObject();

            if (typeDef.isPrimitive())
            {
                fillPrimitive(fieldObject, typeDef);
            }
            else if (typeDef.isArray())
            {
                fieldObject.addProperty("type", TYPE_ARRAY);

                JsonSchema componentSchema = new JsonSchema(typeDef.componentType());
                fieldObject.add("items", componentSchema.schema(true));
            }
            else
            {
                fieldObject.addProperty("$ref", String.format("#/definitions/%s", typeDef.name()));
            }

            applyAnnotations(fieldObject, annotations);

            properties.add(name, fieldObject);
        }

        return properties;
    }

    private void fillPrimitive(JsonObject json, TypeDefinition typeDef)
    {
        if (typeDef.isString())
        {
            json.addProperty("type", TYPE_STRING);
        }
        else if (typeDef.isBoolean())
        {
            json.addProperty("type", TYPE_BOOLEAN);
        }
        else if (typeDef.isInteger())
        {
            json.addProperty("type", TYPE_INTEGER);
        }
        else if (typeDef.isNumber())
        {
            json.addProperty("type", TYPE_NUMBER);
        }
        else if (typeDef.isDate())
        {
            json.addProperty("type", TYPE_STRING);
            json.addProperty("format", "date-time");
        }
        else if (typeDef.isUri())
        {
            json.addProperty("type", TYPE_STRING);
            json.addProperty("format", "uri");
        }
        else if (typeDef.isFile())
        {
            json.addProperty("type", TYPE_FILE);
        }
        else if (typeDef.isEnum())
        {
            Object[] constants = typeDef.enums();

            JsonArray values = new JsonArray();

            for (Object constant : constants)
            {
                values.add(constant.toString());
            }

            json.addProperty("type", TYPE_STRING);
            json.add("enum", values);
        }
    }

    private void applyAnnotations(JsonObject json, Annotations annotations)
    {
        if (annotations.description() != null)
        {
            json.addProperty("description", annotations.description());
        }

        if (annotations.format() != null)
        {
            json.addProperty("format", annotations.format());
        }

        if (annotations.pattern() != null)
        {
            json.addProperty("pattern", annotations.pattern());
        }

        if (annotations.minimum() != null)
        {
            json.addProperty("minimum", annotations.maximum());
        }

        if (annotations.maximum() != null)
        {
            json.addProperty("maximum", annotations.maximum());
        }

        if (annotations.minLength() != null)
        {
            json.addProperty("minLength", annotations.minLength());
        }

        if (annotations.maxLength() != null)
        {
            json.addProperty("maxLength", annotations.maxLength());
        }

        if (annotations.minItems() != null)
        {
            json.addProperty("minItems", annotations.minItems());
        }

        if (annotations.maxItems() != null)
        {
            json.addProperty("maxItems", annotations.maxItems());
        }
    }

    private JsonArray required()
    {
        JsonArray required = new JsonArray();

        for (Field field : typeDef.fields())
        {
            Annotations annotations = new Annotations(field);

            if (!annotations.optional())
            {
                required.add(field.getName());
            }
        }

        return required;
    }
}