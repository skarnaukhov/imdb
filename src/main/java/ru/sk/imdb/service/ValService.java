package ru.sk.imdb.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.sk.imdb.domain.Pair;
import ru.sk.imdb.exception.ValueNotFoundException;

import java.util.Objects;

@Service
@Slf4j
public class ValService {

    @Autowired
    private InMemoryStorage inMemoryStorage;

    public Pair getValueByKey(String key) {
        String value = inMemoryStorage.getValue(key);
        if (Objects.isNull(value)) {
            log.debug("Value is null by key: {}", key);
            throw new ValueNotFoundException("cannot find value by " + key);
        }
        return new Pair(key, value);
    }

    public Pair upsertValueByKey(String key, String value) {
        inMemoryStorage.putValue(key, value);
        return new Pair(key, value);
    }

    public Pair updateValueByKey(String key, String value) {
        String oldValue = inMemoryStorage.getValue(key);
        if (Objects.isNull(oldValue)) {
            log.debug("Can't update. Value is null by key : {}", key);
            throw new ValueNotFoundException("cannot find value by " + key);
        }
        inMemoryStorage.putValue(key, value);
        return new Pair(key, value);
    }

    public void deleteValueByKey(String key, String value) {
        boolean deletionSuccessful = inMemoryStorage.deleteValue(key, value);
        if (!deletionSuccessful) {
            log.debug("Can't delete. Value is null by key : {}", key);
            throw new ValueNotFoundException("cannot find value by " + key);
        }
    }
}
