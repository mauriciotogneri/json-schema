package com.mauriciotogneri.jsonschema.app;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.net.URL;

public class Main
{
    public static void main(String[] args) throws Exception
    {
        Main main = new Main();
        main.run();
    }

    public void run()
    {
        com.mauriciotogneri.jsonschema.JsonSchema schema = new com.mauriciotogneri.jsonschema.JsonSchema(Person.class);
        JsonObject json = schema.schema();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        System.out.println(gson.toJson(json));

        checkSchema(json);
    }

    private void checkSchema(JsonObject input)
    {
        try
        {
            JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
            URL schemaPath = getClass().getResource("/schema.json");
            JsonSchema schema = factory.getJsonSchema(schemaPath.toString());
            JsonNode json = JsonLoader.fromString(input.toString());

            ProcessingReport report = schema.validate(json);
            System.out.println(report);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}