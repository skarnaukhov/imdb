package ru.sk.imdb.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.sk.imdb.domain.Pair;
import ru.sk.imdb.domain.SuccessResponse;
import ru.sk.imdb.service.ValService;

@RestController
@RequestMapping(value = "/val", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class ValController extends BaseController implements BaseApi {

    @Autowired
    private ValService valService;

    @Override
    public String getByKey(@PathVariable("key") final String key) {
        log.info("Requested value by key {}", key);
        final Pair result = valService.getValueByKey(key);
        log.info("Returned {}", result);
        return result.toString();
    }

    @Override
    public String putValueByKey(@PathVariable("key") final String key, @RequestBody String request) {
        final String value = validateAndParseInputToString(key, request);
        final Pair pair = valService.upsertValueByKey(key, value);
        log.info("Upserting {}", pair);
        return pair.toString();
    }

    @Override
    public String postValueByKey(@PathVariable("key") final String key, @RequestBody String request) {
        final String value = validateAndParseInputToString(key, request);
        final Pair pair = valService.updateValueByKey(key, value);
        log.info("Updating {}", pair);
        return pair.toString();
    }

    public SuccessResponse deleteValueByKey(@PathVariable("key") final String key, @RequestBody String request) {
        final String value = validateAndParseInputToString(key, request);
        valService.deleteValueByKey(key, value);
        log.info("Deleted {}", new Pair<>(key, value));
        return new SuccessResponse(true);
    }

}
