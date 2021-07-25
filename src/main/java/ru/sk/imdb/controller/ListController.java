package ru.sk.imdb.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.sk.imdb.domain.Pair;
import ru.sk.imdb.domain.SuccessResponse;
import ru.sk.imdb.service.ListService;

import java.util.List;

@RestController
@RequestMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class ListController extends BaseController implements BaseApi {

    @Autowired
    private ListService listService;

    @Override
    public String getByKey(String key) {
        log.info("Requested value by key {}", key);
        final Pair result = listService.getListByKey(key);
        log.info("Returned {}", result);
        return result.toString();
    }

    @Override
    public Object putValueByKey(String key, String request) {
        final List value = validateAndParseInputToListOfStrings(key, request);
        final Pair pair = listService.upsertListByKey(key, value);
        log.info("Upserting {}", pair);
        return pair.toString();
    }

    @Override
    public Object postValueByKey(String key, String request) {
        final List list = validateAndParseInputToListOfStrings(key, request);
        final Pair pair = listService.appendListToListByKey(key, list);
        log.info("Updating {}", pair);
        return pair.toString();
    }

    @Override
    public SuccessResponse deleteValueByKey(String key, String request) {
        listService.deleteList(key);
        log.info("Deleted list by key {}", key);
        return new SuccessResponse(true);
    }

    @PatchMapping("/{key}/{index}")
    @ResponseStatus(HttpStatus.OK)
    public Object appendValueToListByIndex(
            @PathVariable("key") final String key,
            @PathVariable("index") final Integer index,
            @RequestBody String request) {
        final List list = validateAndParseInputToListOfStrings(key, request);
        final Pair pair = listService.appendValueToListByKeyAndIndex(key, index, list);
        log.info("Appended {} to {}", list, pair);
        return pair.toString();
    }
}
