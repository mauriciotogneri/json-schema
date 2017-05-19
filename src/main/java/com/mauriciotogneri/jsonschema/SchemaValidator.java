package com.mauriciotogneri.jsonschema;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.net.URL;

public class SchemaValidator
{
    public ProcessingReport validate(JsonObject input, JsonSchema schema) throws ProcessingException, IOException
    {
        JsonNode json = JsonLoader.fromString(input.toString());

        return schema.validate(json);
    }

    public ProcessingReport validate(JsonObject input) throws ProcessingException, IOException
    {
        JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
        URL schemaPath = getClass().getResource("/schema.json");
        JsonSchema schema = factory.getJsonSchema(schemaPath.toString());

        return validate(input, schema);
    }
}