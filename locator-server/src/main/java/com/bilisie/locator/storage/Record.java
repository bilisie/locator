package com.bilisie.locator.storage;

/**
 * Mapping of common attributes found in both the main table and the global index
 */
@SuppressWarnings("unused")
public interface Record {

    String getVisitId();

    Long getTimestamp();

    String getUserId();

    String getName();

}
