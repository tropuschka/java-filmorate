package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.yandex.practicum.filmorate.exceptions.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exceptions.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;

@Slf4j
@Service
public class ExceptionService {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static void throwValidationException(String message) {
        log.error(message);
        throw new ConditionsNotMetException(message);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public static void throwNotFoundException(String message) {
        log.error(message);
        throw new NotFoundException(message);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public static void throwDuplicationException(String message) {
        log.error(message);
        throw new DuplicatedDataException(message);
    }
}
