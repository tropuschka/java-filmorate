package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exceptions.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;

@Slf4j
@Service
public class ExceptionService {
    public static void throwValidationException(String message) {
        log.error(message);
        throw new ConditionsNotMetException(message);
    }

    public static void throwNotFoundException(String message) {
        log.error(message);
        throw new NotFoundException(message);
    }

    public static void throwDuplicationException(String message) {
        log.error(message);
        throw new DuplicatedDataException(message);
    }
}
