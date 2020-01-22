package com.bilisie.locator.storage;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;

import java.util.HashMap;
import java.util.Objects;

/**
 * Mapping for the user centric table
 *
 * Hash Key = userId
 * Sort Key = timestamp
 *
 */
//@SuppressWarnings({"unused", "WeakerAccess"})
@SuppressWarnings({"WeakerAccess", "unused", "SameParameterValue"})
@DynamoDBTable(tableName = "visits")
public class UserRecord implements Record {

    private String visitId;
    private Long timestamp;
    private String userId;
    private String name;

    @DynamoDBAttribute(attributeName = "visitId")
    public String getVisitId() {
        return visitId;
    }

    @DynamoDBHashKey(attributeName = "userId")
    @DynamoDBIndexHashKey(globalSecondaryIndexName = "userIndex", attributeName = "userId")
    public String getUserId() {
        return userId;
    }

    @DynamoDBRangeKey(attributeName = "transactionTimestamp")
    @DynamoDBIndexRangeKey(globalSecondaryIndexName = "userIndex", attributeName = "timestamp")
    public Long getTimestamp() {
        return timestamp;
    }

    @DynamoDBAttribute(attributeName = "name")
    public String getName() {
        return name;
    }

    public UserRecord setVisitId(String visitId) {
        this.visitId = visitId;
        return this;
    }

    public UserRecord setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public UserRecord setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public UserRecord setName(String name) {
        this.name = name;
        return this;
    }

    public UserRecord asRecord() {
        return this;
    }

    static UserRecord forUserId(String userId) {
        return new UserRecord().setUserId(userId);
    }

    protected DynamoDBQueryExpression<UserRecord> toQueryExpression(long cutoffTimestamp, int limit) {
        // Note: The look-back can be adjusted to look for activity within a recent window rather than globally
        HashMap<String, AttributeValue> attributeValues = new HashMap<>();
        attributeValues.put(":userId", new AttributeValue().withS(getUserId()));
        attributeValues.put(":cutoff", new AttributeValue().withN(Long.toString(cutoffTimestamp)));
        return new DynamoDBQueryExpression<UserRecord>()
                .withIndexName("userIndex")
                .withLimit(limit)
                .withScanIndexForward(false)
                .withConsistentRead(false)
                .withKeyConditionExpression("userId = :userId and transactionTimestamp > :cutoff")
                .withExpressionAttributeValues(attributeValues);
    }


    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        UserRecord that = (UserRecord) other;
        return Objects.equals(visitId, that.visitId) &&
                Objects.equals(timestamp, that.timestamp) &&
                Objects.equals(userId, that.userId) &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(visitId, timestamp, userId, name);
    }

}
