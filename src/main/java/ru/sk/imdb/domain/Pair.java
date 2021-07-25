package ru.sk.imdb.domain;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
public class Pair<K, V> {

    @Getter
    private K key;

    @Getter
    @Setter
    private V value;

    @Override
    public String toString() {
        if (value instanceof List) {
            return "{\"" + key + "\": "+ new Gson().toJson(value) +"}";
        } else {
            return "{\"" + key + "\": \"" + value + "\"}";
        }
    }
}
