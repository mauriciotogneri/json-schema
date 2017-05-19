package com.mauriciotogneri.jsonschema;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.google.gson.JsonObject;

import java.io.IOException;

public class SchemaValidator
{
    private final JsonSchema schema;

    public SchemaValidator(JsonSchema schema)
    {
        this.schema = schema;
    }

    public SchemaValidator(JsonObject json) throws IOException, ProcessingException
    {
        JsonNode schemaNode = JsonLoader.fromString(json.toString());
        JsonSchemaFactory factory = JsonSchemaFactory.byDefault();

        this.schema = factory.getJsonSchema(schemaNode);
    }

    public SchemaValidator(String path) throws ProcessingException
    {
        this.schema = schema(path);
    }

    public SchemaValidator() throws ProcessingException
    {
        this.schema = schema(getClass().getResource("/schema.json").toString());
    }

    private JsonSchema schema(String path) throws ProcessingException
    {
        return JsonSchemaFactory.byDefault().getJsonSchema(path);
    }

    public ProcessingReport validate(JsonObject input) throws ProcessingException, IOException
    {
        return validate(input.toString());
    }

    public ProcessingReport validate(String input) throws ProcessingException, IOException
    {
        return validate(JsonLoader.fromString(input));
    }

    public ProcessingReport validate(JsonNode input) throws ProcessingException
    {
        return schema.validate(input);
    }
}