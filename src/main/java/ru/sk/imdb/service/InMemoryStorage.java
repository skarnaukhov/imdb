package ru.sk.imdb.service;

import com.google.common.util.concurrent.Striped;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import ru.sk.imdb.exception.ValueNotFoundException;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;

@Service
@Scope(value =  ConfigurableBeanFactory.SCOPE_SINGLETON)
@Slf4j
public class InMemoryStorage {

    @Getter
    @Value("${imdb.concurrency.keyValueConcurrencyLevel}")
    private int keyValueConcurrencyLevel;

    @Getter
    @Value("${imdb.concurrency.keyListConcurrencyLevel}")
    private int keyListConcurrencyLevel;

    @Getter
    @Value("${imdb.concurrency.listsLocksInitialCount}")
    private int listsLocksInitialCount;

    private ConcurrentHashMap<String, String> keyValueDB;
    private ConcurrentHashMap<String, List<String>> keyListDB;
    private Striped<Lock> listsLock;

    public InMemoryStorage() {
        log.debug("Setting up a singleton");
    }

    @PostConstruct
    public void setUp() {
        log.debug("Initialising storage with configuration: " +
                        "\n KeyValue concurrencyLevel: {} " +
                        "\n KeyList concurrencyLevel: {}" +
                        "\n KeyList listsLocksInitialCount: {}",
                keyListConcurrencyLevel, keyListConcurrencyLevel, listsLocksInitialCount);
        this.keyValueDB = new ConcurrentHashMap(32,0.75f,getKeyValueConcurrencyLevel());
        this.keyListDB = new ConcurrentHashMap(32,0.75f,getKeyListConcurrencyLevel());
        this.listsLock = Striped.lazyWeakLock(listsLocksInitialCount);
    }

    public String getValue(String key) {
        return this.keyValueDB.get(key);
    }

    public void putValue(String key, String value) {
        this.keyValueDB.put(key, value);
    }

    public boolean deleteValue(String key, String value) {
        return this.keyValueDB.remove(key, value);
    }

    public List<String> getList(String key) {
        final List<String> list = this.keyListDB.get(key);
        if (Objects.isNull(list)) {
            log.debug("List is null by key: {}", key);
            throw new ValueNotFoundException("cannot find value by " + key);
        }
        return list;
    }

    public void putList(String key, List<String> value) {
        this.keyListDB.put(key, value);
    }

    public void deleteList(String key) {
        final boolean deletionResult = !Objects.isNull(this.keyListDB.remove(key));
        if (!deletionResult) {
            log.debug("Can't delete. Value is null by key : {}", key);
            throw new ValueNotFoundException("cannot find value by " + key);
        }
    }

    public List<String> appendListToList(String key, List<String> listSupplier, Optional<Integer> indexOptional) {
        final Lock lock = listsLock.get(key);
        lock.lock();
        try {
            final List<String> listConsumer = getList(key);
            if (Objects.isNull(listConsumer)){
                throw new ValueNotFoundException("cannot find value by "+key);
            }
            if (indexOptional.isPresent()) {
                final Integer index = indexOptional.get();
                if (listConsumer.size() < index) {
                    throw new ValueNotFoundException("cannot find value by "+key+" and "+index);
                }
                listConsumer.addAll(index, listSupplier);
            } else {
                listConsumer.addAll(listSupplier);
            }
            putList(key, listConsumer);
            return listConsumer;
        } finally {
            lock.unlock();
        }
    }
}
