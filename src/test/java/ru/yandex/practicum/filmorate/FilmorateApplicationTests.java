package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.mappers.FilmDbMapper;
import ru.yandex.practicum.filmorate.mappers.UserDbMapper;
import ru.yandex.practicum.filmorate.storage.*;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserDbStorage.class, UserDbMapper.class, FilmDbStorage.class, FilmDbMapper.class})
class FilmorateApplicationTests {
	@Autowired
	protected UserDbStorage userStorage;
	@Autowired
	protected FilmDbStorage filmStorage;
}
