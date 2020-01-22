package com.bilisie.locator.search;

import com.bilisie.locator.storage.Record;
import com.intuit.fuzzymatcher.component.MatchService;
import com.intuit.fuzzymatcher.domain.Document;
import com.intuit.fuzzymatcher.domain.Element;
import com.intuit.fuzzymatcher.domain.Match;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.intuit.fuzzymatcher.domain.ElementType.TEXT;

@Component
public class RecordFinder {

    private static final double MINIMUM_THRESHOLD = .4;

    private final MatchService matchService;

    public RecordFinder(MatchService matchService) {
        this.matchService = matchService;
    }

    public List<Record> findRecord(String queryTerm, List<Record> records) {
        // Keep track of document ids
        Map<String, Record> recordMap = records.stream().collect(Collectors.toMap(Record::getVisitId, Function.identity()));
        // Generate matching documents
        List<Document> documents = records.stream()
                .map(record -> document(record.getVisitId(), record.getName()))
                .collect(Collectors.toList());
        // Query for matches
        Map<Document, List<Match<Document>>> matches = matchService.applyMatch(document(queryTerm, queryTerm), documents);
        if (matches.entrySet().iterator().hasNext()) {
            List<Match<Document>> documentMatches = matches.entrySet().iterator().next().getValue();
            // Transform the results back to a list of Record objects based on visit identifiers
            return documentMatches.stream().map((Match<Document> match) ->
                    recordMap.get(match.getMatchedWith().getKey())
            ).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private Document document(String documentId, String documentQuery) {
        String normalized = documentQuery.toLowerCase().replaceAll(" ", "");
        return new Document.Builder(documentId)
                .addElement(new Element.Builder().setType(TEXT).setValue(normalized).createElement())
                .setThreshold(MINIMUM_THRESHOLD)
                .createDocument();
    }

}
