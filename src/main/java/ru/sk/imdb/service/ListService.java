package ru.sk.imdb.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.sk.imdb.domain.Pair;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ListService {

    @Autowired
    private InMemoryStorage inMemoryStorage;

    public Pair<String, List<String>> getListByKey(String key) {
        List<String> list = inMemoryStorage.getList(key);
        log.debug("Returned list");
        return new Pair(key, list);
    }

    public Pair<String, List<String>> upsertListByKey(String key, List<String> value) {
        inMemoryStorage.putList(key, value);
        return new Pair<>(key, value);
    }


    public Pair appendListToListByKey(String key, List<String> listToAppend) {
        final List<String> result = inMemoryStorage.appendListToList(key, listToAppend, Optional.empty());
        return new Pair(key, result);
    }

    public Pair appendValueToListByKeyAndIndex(String key, Integer index, List<String> list) {
        final List<String> result = inMemoryStorage.appendListToList(key, list, Optional.of(index));
        return new Pair(key, result);
    }

    public void deleteList(String key) {
        inMemoryStorage.deleteList(key);
    }


}
