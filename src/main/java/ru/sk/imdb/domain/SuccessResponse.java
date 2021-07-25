package ru.sk.imdb.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class SuccessResponse {
    @Getter
    private boolean success;
}
