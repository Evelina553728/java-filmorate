package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class FilmorateApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	@DisplayName("Контекст приложения успешно поднимается")
	void contextLoads() {
	}

	@Test
	@DisplayName("Создание корректного фильма возвращает 200 OK")
	void shouldCreateValidFilm() throws Exception {
		Film film = new Film();
		film.setName("Test Film");
		film.setDescription("Description");
		film.setDuration(120);
		film.setReleaseDate(LocalDate.of(2000, 5, 20));

		mockMvc.perform(post("/films")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(film)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.name").value("Test Film"));
	}

	@Test
	@DisplayName("Создание фильма с недопустимой датой релиза возвращает 400 Bad Request")
	void shouldRejectInvalidFilmReleaseDate() throws Exception {
		Film film = new Film();
		film.setName("Old Film");
		film.setDescription("Bad date");
		film.setDuration(100);
		film.setReleaseDate(LocalDate.of(1800, 1, 1)); // слишком рано

		mockMvc.perform(post("/films")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(film)))
				.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("Создание корректного пользователя возвращает 200 OK")
	void shouldCreateValidUser() throws Exception {
		User user = new User();
		user.setEmail("mail@test.com");
		user.setLogin("userLogin");
		user.setName("User Name");
		user.setBirthday(LocalDate.of(1999, 3, 15));

		mockMvc.perform(post("/users")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(user)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.email").value("mail@test.com"));
	}

	@Test
	@DisplayName("Обновление несуществующего пользователя возвращает 404 Not Found")
	void shouldReturnNotFoundForUnknownUser() throws Exception {
		User user = new User();
		user.setId(999); // такого нет
		user.setEmail("new@test.com");
		user.setLogin("login");
		user.setName("Test");
		user.setBirthday(LocalDate.of(1990, 1, 1));

		mockMvc.perform(put("/users")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(user)))
				.andExpect(status().isNotFound());
	}
}