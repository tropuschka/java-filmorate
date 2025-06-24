package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exceptions.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exceptions.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;

import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ExceptionService {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> throwValidationException(final ConditionsNotMetException e) {
        log.error(e.getMessage());
        return Map.of("ConditionsNotMetException", e.getMessage());
    }

    @ExceptionHandler(value = NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String throwNotFoundException(final NotFoundException e) {
        log.error(e.getMessage());
        return e.getMessage();
    }

    @ExceptionHandler(value = DuplicatedDataException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String throwDuplicationException(final DuplicatedDataException e) {
        log.error(e.getMessage());
        return e.getMessage();
    }
}
