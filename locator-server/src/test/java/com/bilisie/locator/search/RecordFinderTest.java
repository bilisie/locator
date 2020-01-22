package com.bilisie.locator.search;

import com.bilisie.locator.storage.Record;
import com.bilisie.locator.storage.UserRecord;
import com.intuit.fuzzymatcher.component.DocumentMatch;
import com.intuit.fuzzymatcher.component.ElementMatch;
import com.intuit.fuzzymatcher.component.MatchService;
import com.intuit.fuzzymatcher.component.TokenMatch;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.UUID;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@SuppressWarnings("unused")
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {MatchService.class, RecordFinder.class, DocumentMatch.class, ElementMatch.class, TokenMatch.class})
class RecordFinderTest {

    @Autowired
    private RecordFinder recordFinder;

    @Test
    void testLowerCase() {
        List<Record> candidates = asList(record("mcdonalds"), record("burger king"));
        List<Record> foundRecords = recordFinder.findRecord("McDonald's", candidates);
        assertNotNull(foundRecords);
        assertEquals(1, foundRecords.size());
        assertEquals("mcdonalds", foundRecords.iterator().next().getName());
    }

    @Test
    void testSpaces() {
        List<Record> candidates = asList(record("mc   donalds"), record("burger king"));
        List<Record> foundRecords = recordFinder.findRecord("McDonald's", candidates);
        assertNotNull(foundRecords);
        assertEquals(1, foundRecords.size());
        assertEquals("mc   donalds", foundRecords.iterator().next().getName());
    }

    @Test
    void testNoMatch() {
        List<Record> candidates = asList(record("mc   nope"), record("burger king"));
        List<Record> foundRecords = recordFinder.findRecord("McDonald's", candidates);
        assertNotNull(foundRecords);
        assertEquals(0, foundRecords.size());
    }

    @Test
    void testWordDistance() {
        List<Record> candidates = asList(record("McDonaldz"), record("burger king"));
        List<Record> foundRecords = recordFinder.findRecord("McDonald's", candidates);
        assertNotNull(foundRecords);
        assertEquals(1, foundRecords.size());
    }


    private Record record(String name) {
        return (new UserRecord()).setName(name).setVisitId(UUID.randomUUID().toString());
    }


}