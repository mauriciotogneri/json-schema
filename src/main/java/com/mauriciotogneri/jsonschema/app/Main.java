package com.mauriciotogneri.jsonschema.app;

import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.mauriciotogneri.jsonschema.JsonSchema;
import com.mauriciotogneri.jsonschema.SchemaValidator;

public class Main
{
    public static void main(String[] args) throws Exception
    {
        Main main = new Main();
        main.run();
    }

    public void run() throws Exception
    {
        JsonSchema schema = new JsonSchema(Person.class);
        JsonObject json = schema.schema();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        System.out.println(gson.toJson(json));

        SchemaValidator schemaValidator = new SchemaValidator();
        ProcessingReport report = schemaValidator.validate(json);

        if (!report.isSuccess())
        {
            System.out.println(report);
        }
    }
}