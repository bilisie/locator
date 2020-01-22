package com.bilisie.locator.storage;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import org.joda.time.DateTimeUtils;

import java.util.Objects;
import java.util.UUID;

/**
 * Mapping for the visit centric global index
 *
 * Hash Key = indexId
 *
 */
@SuppressWarnings({"WeakerAccess", "unused"})
@DynamoDBTable(tableName = "visits")
public class VisitRecord implements Record {

    private String visitId;
    private Long timestamp;
    private String userId;
    private String name;

    @DynamoDBHashKey(attributeName = "visitId")
    public String getVisitId() {
        return visitId;
    }

    @DynamoDBAttribute(attributeName = "transactionTimestamp")
    public Long getTimestamp() {
        return timestamp;
    }

    @DynamoDBAttribute(attributeName = "userId")
    public String getUserId() {
        return userId;
    }

    @DynamoDBAttribute(attributeName = "name")
    public String getName() {
        return name;
    }

    public VisitRecord setVisitId(String visitId) {
        this.visitId = visitId;
        return this;
    }

    public VisitRecord setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public VisitRecord setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public VisitRecord setName(String name) {
        this.name = name;
        return this;
    }

    public Record asRecord() {
        return this;
    }

    static VisitRecord forVisitId(String visitId) {
        return new VisitRecord().setVisitId(visitId);
    }

    static VisitRecord newRecord(String userId, String name) {
        return new VisitRecord()
                .setUserId(userId)
                .setName(name)
                .setTimestamp(DateTimeUtils.currentTimeMillis())
                .setVisitId(UUID.randomUUID().toString());
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        VisitRecord that = (VisitRecord) other;
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
