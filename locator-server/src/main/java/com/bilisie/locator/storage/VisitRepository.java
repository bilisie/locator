package com.bilisie.locator.storage;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Repository for fetching user data from the dynamo store
 */
public class VisitRepository {

    // Note: The look-back can be adjusted to look for activity within a recent window rather than globally
    private static final long DEFAULT_TIMESTAMP_CUTOFF = 0;

    // Note: This can be made configurable
    private static final int SEARCH_LIMIT = 5;

    private final DynamoDBMapper mapper;

    public VisitRepository(DynamoDBMapper mapper) {
        this.mapper = mapper;
    }

    public String save(String userId, String location) {
        VisitRecord record = VisitRecord.newRecord(userId, location);
        mapper.save(record);
        return record.getVisitId();
    }

    public List<Record> getVisit(String visitId) {
        VisitRecord partitionKey = VisitRecord.forVisitId(visitId);
        DynamoDBQueryExpression<VisitRecord> expression = new DynamoDBQueryExpression<VisitRecord>()
                .withHashKeyValues(partitionKey);
        return mapper.query(VisitRecord.class, expression).stream()
                .map(VisitRecord::asRecord).collect(Collectors.toList());
    }

    public List<Record> search(String userId) {
        DynamoDBQueryExpression<UserRecord> expression = UserRecord.forUserId(userId).toQueryExpression(DEFAULT_TIMESTAMP_CUTOFF, SEARCH_LIMIT);
        return mapper.queryPage(UserRecord.class, expression).getResults().stream()
                .map(UserRecord::asRecord).collect(Collectors.toList());
    }

}
