package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.Month;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class UserControllerTests extends FilmorateApplicationTests {
    @Test
    public void testFindUserById() {
        Optional<User> userOptional = userStorage.findUserById(0);

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 0)
                );
    }

    @Test
    public void testCreateUser() {
        User user = new User();
        user.setName("Nana");
        user.setLogin("Nana789");
        user.setEmail("nana@mail.com");
        user.setBirthday(LocalDate.of(2000, Month.JANUARY, 1));

        Optional<User> userOptional = Optional.of(userStorage.create(user));
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(dbUuser ->
                        assertThat(user).hasFieldOrPropertyWithValue("email", "nana@mail.com")
                );
    }
}
