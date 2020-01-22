package com.bilisie.locator.service;

import com.bilisie.UserVisit;
import com.bilisie.UserVisitResponse;
import com.bilisie.locator.search.RecordFinder;
import com.bilisie.locator.storage.Record;
import com.bilisie.locator.storage.VisitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@SuppressWarnings("unused")
@RestController
public class LocatorService {

    private final VisitRepository visitRepository;
    private final RecordFinder recordFinder;

    @Autowired
    public LocatorService(VisitRepository visitRepository, RecordFinder recordFinder) {
        this.visitRepository = visitRepository;
        this.recordFinder = recordFinder;
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    @ResponseBody
    public UserVisitResponse index(@RequestBody UserVisit userVisit) {
        validate(isNotEmpty(userVisit.getUserId()), "Invalid user id");
        validate(isNotEmpty(userVisit.getName()), "Invalid store name");
        String visitId = visitRepository.save(userVisit.getUserId(), userVisit.getName());
        return (new UserVisitResponse()).visitId(visitId);
    }


    @RequestMapping(value = "/visit", method = RequestMethod.GET)
    @ResponseBody
    public List<Record> getUserVisit(
            @RequestParam(required = false) String visitId,
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String searchString) {
        if (isNotEmpty(visitId)) {
            // Fetch singular visit
            return visitRepository.getVisit(visitId);
        } else {
            // Retrieve all of the recent user records
            validate(isNotEmpty(userId) && isNotEmpty(searchString), "Invalid search request. Both 'userId' and 'name' are required.");
            List<Record> records = visitRepository.search(userId);
            // Perform a search against the found records
            return recordFinder.findRecord(searchString, records);
        }
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Visit not found")
    private class VisitNotFoundException extends RuntimeException {
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Invalid request")
    private class InvalidRequest extends RuntimeException {

        InvalidRequest(String message) {
            super(message);
        }

    }

    private void validate(boolean argument, String errorMessage) {
        if (!argument) {
            throw new InvalidRequest(errorMessage);
        }
    }

}