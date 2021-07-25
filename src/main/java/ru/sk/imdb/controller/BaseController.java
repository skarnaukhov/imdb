package ru.sk.imdb.controller;

import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import ru.sk.imdb.domain.ErrorResponse;
import ru.sk.imdb.exception.ValueNotFoundException;

import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
public class BaseController {

    @ExceptionHandler({ ValueNotFoundException.class })
    public ResponseEntity<ErrorResponse> handleValueNotFound(ValueNotFoundException ex, WebRequest request) {
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({ IllegalArgumentException.class })
    public ResponseEntity<ErrorResponse> handleWrongBody(IllegalArgumentException ex, WebRequest request) {
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    public String validateAndParseInputToString(String key, String body) {
        try {
            return JsonParser.parseString(body).getAsJsonObject().get(key).getAsString();
        } catch (Exception ex) {
            throw new IllegalArgumentException("Body is incorrect: " + body);
        }
    }

    public List validateAndParseInputToListOfStrings(String key, String body) {
        try {
            return StreamSupport.stream(
                    Spliterators.spliteratorUnknownSize(
                            JsonParser.parseString(body).getAsJsonObject().get(key).getAsJsonArray().iterator(),
                            Spliterator.ORDERED),
                    false).collect(Collectors.toList());
        } catch (Exception ex) {
            log.error("Error parsing the input body",ex);
            throw new IllegalArgumentException("Body is incorrect: " + body);
        }
    }
}
