package com.mauriciotogneri.jsonschema;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.lang.reflect.Field;

public class JsonSchema
{
    private final TypeDefinition typeDefinition;
    private final Definitions definitions;

    private static final String TYPE_STRING = "string";
    private static final String TYPE_BOOLEAN = "boolean";
    private static final String TYPE_INTEGER = "integer";
    private static final String TYPE_NUMBER = "number";
    private static final String TYPE_OBJECT = "object";
    private static final String TYPE_ARRAY = "array";
    private static final String TYPE_FILE = "file";

    public JsonSchema(Class<?> clazz)
    {
        this.typeDefinition = new TypeDefinition(clazz);
        this.definitions = new Definitions(typeDefinition);
    }

    public JsonObject schema()
    {
        JsonObject root = schema(true);
        root.addProperty("$schema", "http://json-schema.org/schema#");

        JsonObject defs = new JsonObject();

        for (Class<?> clazz : definitions.classes())
        {
            JsonSchema jsonSchema = new JsonSchema(clazz);
            defs.add(clazz.getCanonicalName(), jsonSchema.schema(false));
        }

        if (defs.size() > 0)
        {
            root.add("definitions", defs);
        }

        return root;
    }

    private JsonObject schema(boolean useReferences)
    {
        JsonObject schema = new JsonObject();

        if (typeDefinition.isPrimitive())
        {
            fillPrimitive(schema, typeDefinition);
        }
        else if (typeDefinition.isArray())
        {
            schema.addProperty("type", TYPE_ARRAY);

            JsonSchema componentSchema = new JsonSchema(typeDefinition.componentType());
            schema.add("items", componentSchema.schema(true));
        }
        else
        {
            if (useReferences)
            {
                schema.addProperty("$ref", String.format("#/definitions/%s", typeDefinition.name()));
            }
            else
            {
                schema.addProperty("type", TYPE_OBJECT);
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

        for (Field field : typeDefinition.fields())
        {
            Annotations annotations = new Annotations(field);

            String name = field.getName();

            if (annotations.name() != null)
            {
                name = annotations.name();
            }

            properties.add(name, property(field, annotations));
        }

        return properties;
    }

    private JsonObject property(Field field, Annotations annotations)
    {
        JsonObject json = new JsonObject();

        TypeDefinition typeDefinition = new TypeDefinition(field.getType());

        if (typeDefinition.isPrimitive())
        {
            fillPrimitive(json, typeDefinition);
        }
        else if (typeDefinition.isArray())
        {
            json.addProperty("type", TYPE_ARRAY);

            JsonSchema componentSchema = new JsonSchema(typeDefinition.componentType());
            json.add("items", componentSchema.schema(true));
        }
        else
        {
            json.addProperty("$ref", String.format("#/definitions/%s", typeDefinition.name()));
        }

        applyAnnotations(json, annotations);
    }

    private void fillPrimitive(JsonObject json, TypeDefinition typeDefinition)
    {
        if (typeDefinition.isString())
        {
            json.addProperty("type", TYPE_STRING);
        }
        else if (typeDefinition.isBoolean())
        {
            json.addProperty("type", TYPE_BOOLEAN);
        }
        else if (typeDefinition.isInteger())
        {
            json.addProperty("type", TYPE_INTEGER);
        }
        else if (typeDefinition.isNumber())
        {
            json.addProperty("type", TYPE_NUMBER);
        }
        else if (typeDefinition.isDate())
        {
            json.addProperty("type", TYPE_STRING);
            json.addProperty("format", "date-time");
        }
        else if (typeDefinition.isUri())
        {
            json.addProperty("type", TYPE_STRING);
            json.addProperty("format", "uri");
        }
        else if (typeDefinition.isFile())
        {
            json.addProperty("type", TYPE_FILE);
        }
        else if (typeDefinition.isEnum())
        {
            Object[] constants = typeDefinition.enums();

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

        if (annotations.title() != null)
        {
            json.addProperty("title", annotations.title());
        }

        if (annotations.defaultValue() != null)
        {
            json.addProperty("default", annotations.defaultValue());
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
            json.addProperty("minimum", annotations.minimum());
        }

        if (annotations.maximum() != null)
        {
            json.addProperty("maximum", annotations.maximum());
        }

        if (annotations.multipleOf() != null)
        {
            json.addProperty("multipleOf", annotations.multipleOf());
        }

        if (annotations.exclusiveMinimum() != null)
        {
            json.addProperty("exclusiveMinimum", annotations.exclusiveMinimum());
        }

        if (annotations.exclusiveMaximum() != null)
        {
            json.addProperty("exclusiveMaximum", annotations.exclusiveMaximum());
        }

        if (annotations.uniqueItems() != null)
        {
            json.addProperty("uniqueItems", annotations.uniqueItems());
        }

        if (annotations.additionalItems() != null)
        {
            json.addProperty("additionalItems", annotations.additionalItems());
        }

        if (annotations.additionalProperties() != null)
        {
            json.addProperty("additionalProperties", annotations.additionalProperties());
        }

        if (annotations.minProperties() != null)
        {
            json.addProperty("minProperties", annotations.minProperties());
        }

        if (annotations.maxProperties() != null)
        {
            json.addProperty("maxProperties", annotations.maxProperties());
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

        for (Field field : typeDefinition.fields())
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