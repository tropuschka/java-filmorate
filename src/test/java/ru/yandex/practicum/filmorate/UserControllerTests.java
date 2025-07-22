package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

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
}
