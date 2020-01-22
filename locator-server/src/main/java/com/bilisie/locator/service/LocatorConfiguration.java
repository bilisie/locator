package com.bilisie.locator.service;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.bilisie.locator.search.RecordFinder;
import com.bilisie.locator.storage.DynamoSchemaManager;
import com.bilisie.locator.storage.VisitRepository;
import com.intuit.fuzzymatcher.component.MatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@SuppressWarnings("unused")
@Configuration
public class LocatorConfiguration {

    @Value("${aws.region}")
    private String region;

    @Value("${aws.dynamo.endpoint}")
    private String dynamoEndpoint;

    @Bean
    public AmazonDynamoDB amazonDynamoDB() {
        return AmazonDynamoDBClientBuilder
                .standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(dynamoEndpoint, region))
                .withCredentials(new DefaultAWSCredentialsProviderChain())
                .build();
    }

    @Bean
    public RecordFinder recordFinder(@Autowired MatchService matchService) {
        return new RecordFinder(matchService);
    }

    @Bean
    @Autowired
    public DynamoDBMapper dynamoDBMapper(AmazonDynamoDB client) {
        return new DynamoDBMapper(client);
    }

    @Bean
    @Autowired
    public VisitRepository userVisitRepository(DynamoDBMapper mapper) {
        return new VisitRepository(mapper);
    }

    @Bean
    @Autowired
    public DynamoSchemaManager dynamoSchemaManager(AmazonDynamoDB amazonDynamoDB) {
        return new DynamoSchemaManager(amazonDynamoDB);
    }

}