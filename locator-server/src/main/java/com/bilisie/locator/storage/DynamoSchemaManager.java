package com.bilisie.locator.storage;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.ResourceInUseException;
import com.bilisie.locator.service.Locator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Small utility for auto-installing dynamo schema for easier local testing and deployment.
 * Reads the schema.json file from the resources folder.
 */
public class DynamoSchemaManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(Locator.class);

    private static final String SCHEMA_FILE = "/schema/schema.json";

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private final AmazonDynamoDB dynamoDB;

    public DynamoSchemaManager(AmazonDynamoDB dynamoDB) {
        this.dynamoDB = dynamoDB;
        initialize();
    }

    private void initialize() {
        try {
            dynamoDB.createTable(createTableRequest());
            LOGGER.info("Successfully installed dynamo schema");
        } catch (ResourceInUseException ex) {
            LOGGER.warn("Schema already installed", ex);
        }
    }

    private CreateTableRequest createTableRequest() {
        try {
            String schema = IOUtils.toString(DynamoSchemaManager.class.getResource(SCHEMA_FILE).openStream());
            return OBJECT_MAPPER.readerFor(CreateTableRequest.class).readValue(schema);
        } catch (IOException ex) {
            throw new RuntimeException("An exception has occurred while reading schema", ex);
        }
    }

}
