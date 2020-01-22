package com.bilisie;

import com.ApiException;
import com.google.common.io.Closer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.org.apache.commons.lang.RandomStringUtils;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("UnstableApiUsage")
class LocatorIntegrationTest {

    private static final Closer CLOSER = Closer.create();

    private static LocatorApi client;

    @BeforeAll
    static void beforeAll() {
        DynamoContainer dynamoContainer = new DynamoContainer(CLOSER);
        LocatorInMemoryBooter booter = new LocatorInMemoryBooter(CLOSER, dynamoContainer.getEndpoint());
        client = booter.getClient();
    }

    @AfterAll
    static void afterAll() throws IOException {
        CLOSER.close();
    }

    @Test
    void testInvalid() {
        assertThrows(ApiException.class, () -> client.visitGet(null, null, null));
    }

    @Test
    void testBasicVisit() throws ApiException {
        // Register a visit
        UserVisit userVisit = new UserVisit().userId("user1").name("Dunkin Donuts");
        UserVisitResponse response = client.registerVisit(userVisit);
        assertNotNull(response.getVisitId());
        assertNotNull(response.getVisitId());
        // Retrieve the visit by the provided identifier
        List<UserVisitDetails> details = client.visitGet(response.getVisitId(), null, null);
        assertNotNull(details);
        assertEquals(1, details.size());
        assertEquals("user1", details.iterator().next().getUserId());
        assertEquals("Dunkin Donuts", details.iterator().next().getName());
    }

    @Test
    void testBasicSearch() throws ApiException {
        // User visits locations location0 to location14
        List<String> locations = IntStream.range(0, 15)
                .mapToObj(value -> RandomStringUtils.randomAlphabetic(12))
                .collect(Collectors.toList());
        for (String location : locations) {
            UserVisit userVisit = new UserVisit().userId("user2").name(location);
            UserVisitResponse response = client.registerVisit(userVisit);
            assertNotNull(response.getVisitId());
        }
        // Querying for the first location in the list should yield no results
        List<UserVisitDetails> details = client.visitGet(null, "user2", locations.iterator().next());
        assertNotNull(details);
        assertTrue(details.isEmpty());
        // Querying for the last 5 locations (location10 to location14) should yield results
        List<String> lastFive = IntStream.range(10, 15)
                .mapToObj(locations::get)
                .collect(Collectors.toList());
        for (String location : lastFive) {
            List<UserVisitDetails> foundDetails = client.visitGet(null, "user2", location);
            assertNotNull(foundDetails);
            assertEquals(1, foundDetails.size());
        }
    }

    @Test
    void testFuzzySearch() throws ApiException {
        // User visits 15 random locations
        List<String> locations = IntStream.range(0, 15)
                .mapToObj(value -> RandomStringUtils.randomAlphabetic(4))
                .collect(Collectors.toList());
        for (String location : locations) {
            UserVisit userVisit = new UserVisit().userId("user3").name(location);
            UserVisitResponse response = client.registerVisit(userVisit);
            assertNotNull(response.getVisitId());
        }
        // User visits McDonald's
        UserVisit userVisit = new UserVisit().userId("user3").name("McDonald's");
        UserVisitResponse response = client.registerVisit(userVisit);
        assertNotNull(response.getVisitId());
        // Query for 'mc donald' and expect to see a record
        List<UserVisitDetails> foundDetails = client.visitGet(null, "user3", "mc donald");
        assertNotNull(foundDetails);
        assertEquals(1, foundDetails.size());
        UserVisitDetails visitDetails = foundDetails.iterator().next();
        assertEquals("McDonald's", visitDetails.getName());
    }

    @Test
    void testMultiSearch() throws ApiException {
        // User visits Burger King
        client.registerVisit(new UserVisit().userId("user4").name("Burger King"));
        // User visits Burgers R Us
        client.registerVisit(new UserVisit().userId("user4").name("Burgers R Us"));
        // Query for burger and expect two results
        List<UserVisitDetails> burgerSearch = client.visitGet(null, "user4", "Burger");
        assertNotNull(burgerSearch);
        assertEquals(2, burgerSearch.size());
        // Query for pizza and expect nothing
        List<UserVisitDetails> pizzaSearch = client.visitGet(null, "user4", "pizza");
        assertNotNull(pizzaSearch);
        assertEquals(0, pizzaSearch.size());
    }

}
