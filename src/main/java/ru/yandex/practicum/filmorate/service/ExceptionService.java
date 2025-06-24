package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exceptions.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exceptions.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;

@Slf4j
@RestControllerAdvice
public class ExceptionService {
    @ExceptionHandler(value = ConditionsNotMetException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static void throwValidationException(String message) {
        log.error(message);
        throw new ConditionsNotMetException(message);
    }

    @ExceptionHandler(value = NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public static void throwNotFoundException(String message) {
        log.error(message);
        throw new NotFoundException(message);
    }

    @ExceptionHandler(value = DuplicatedDataException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public static void throwDuplicationException(String message) {
        log.error(message);
        throw new DuplicatedDataException(message);
    }
}
