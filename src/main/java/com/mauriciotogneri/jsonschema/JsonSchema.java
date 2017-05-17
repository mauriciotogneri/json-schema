package com.mauriciotogneri.jsonschema;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

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

    private JsonSchema(TypeDefinition typeDefinition)
    {
        this.typeDefinition = typeDefinition;
        this.definitions = new Definitions(typeDefinition);
    }

    public JsonSchema(Class<?> clazz)
    {
        this(new TypeDefinition(clazz));
    }

    public JsonObject schema()
    {
        JsonObject root = schema(true);
        root.addProperty("$schema", "http://json-schema.org/schema#");

        JsonObject defs = new JsonObject();

        for (TypeDefinition definition : definitions)
        {
            JsonSchema schema = new JsonSchema(definition);
            defs.add(definition.name(), schema.schema(false));
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
            fillArray(schema, typeDefinition);
        }
        else
        {
            if (useReferences)
            {
                fillReference(schema, typeDefinition);
            }
            else
            {
                fillObject(schema, typeDefinition);
            }
        }

        return schema;
    }

    private void fillObject(JsonObject json, TypeDefinition typeDefinition)
    {
        json.addProperty("type", TYPE_OBJECT);
        json.add("properties", properties(typeDefinition));

        JsonArray required = required();

        if (required.size() > 0)
        {
            json.add("required", required);
        }
    }

    private JsonObject properties(TypeDefinition typeDefinition)
    {
        JsonObject properties = new JsonObject();

        for (FieldDefinition field : typeDefinition.fields())
        {
            Annotations annotations = field.annotations();

            String name = field.name();

            if (annotations.name() != null)
            {
                name = annotations.name();
            }

            properties.add(name, property(field, annotations));
        }

        return properties;
    }

    private JsonObject property(FieldDefinition field, Annotations annotations)
    {
        JsonObject json = new JsonObject();

        TypeDefinition[] allOf = annotations.allOf();

        if (allOf != null)
        {
            fillMultipleTypes(json, "allOf", allOf);

            return json;
        }

        TypeDefinition[] anyOf = annotations.anyOf();

        if (anyOf != null)
        {
            fillMultipleTypes(json, "anyOf", anyOf);

            return json;
        }

        TypeDefinition[] oneOf = annotations.oneOf();

        if (oneOf != null)
        {
            fillMultipleTypes(json, "oneOf", oneOf);

            return json;
        }

        TypeDefinition not = annotations.not();

        if (not != null)
        {
            JsonSchema schema = new JsonSchema(typeDefinition);
            json.add("not", schema.schema(true));

            return json;
        }

        TypeDefinition typeDefinition = field.typeDefinition();

        if (typeDefinition.isPrimitive())
        {
            fillPrimitive(json, typeDefinition);
        }
        else if (typeDefinition.isArray())
        {
            fillArray(json, typeDefinition);
        }
        else
        {
            fillReference(json, typeDefinition);
        }

        applyAnnotations(json, annotations);

        return json;
    }

    private void fillMultipleTypes(JsonObject json, String name, TypeDefinition[] typeDefinitions)
    {
        JsonArray types = new JsonArray();

        for (TypeDefinition typeDefinition : typeDefinitions)
        {
            JsonSchema schema = new JsonSchema(typeDefinition);
            types.add(schema.schema(true));
        }

        json.add(name, types);
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

    private void fillArray(JsonObject json, TypeDefinition typeDefinition)
    {
        json.addProperty("type", TYPE_ARRAY);

        JsonSchema schema = new JsonSchema(typeDefinition.componentType());
        json.add("items", schema.schema(true));
    }

    private void fillReference(JsonObject json, TypeDefinition typeDefinition)
    {
        json.addProperty("$ref", String.format("#/definitions/%s", typeDefinition.name()));
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

        for (FieldDefinition field : typeDefinition.fields())
        {
            Annotations annotations = field.annotations();

            if (!annotations.optional())
            {
                required.add(field.name());
            }
        }

        return required;
    }
}