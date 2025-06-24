package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exceptions.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exceptions.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;

@Slf4j
@RestControllerAdvice
public class ExceptionService {
    @ExceptionHandler
    public static void throwValidationException(String message) {
        log.error(message);
        throw new ConditionsNotMetException(message);
    }

    @ExceptionHandler
    public static void throwNotFoundException(String message) {
        log.error(message);
        throw new NotFoundException(message);
    }

    @ExceptionHandler
    public static void throwDuplicationException(String message) {
        log.error(message);
        throw new DuplicatedDataException(message);
    }
}
