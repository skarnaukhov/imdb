package ru.sk.imdb.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.sk.imdb.domain.SuccessResponse;

public interface BaseApi<T> {

    @GetMapping("/{key}")
    T getByKey(@PathVariable("key") final String key);

    @PutMapping("/{key}")
    @ResponseStatus(HttpStatus.CREATED)
    T putValueByKey(@PathVariable("key") final String key, @RequestBody String request);

    @PostMapping("/{key}")
    @ResponseStatus(HttpStatus.OK)
    T postValueByKey(@PathVariable("key") final String key, @RequestBody String request);

    @DeleteMapping("/{key}")
    @ResponseStatus(HttpStatus.OK)
    SuccessResponse deleteValueByKey(@PathVariable("key") final String key, @RequestBody(required = false) String request);
}
