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
	@DisplayName("Создание корректного фильма")
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
	@DisplayName("Создание фильма с ошибочной датой релиза")
	void shouldRejectInvalidFilmReleaseDate() throws Exception {
		Film film = new Film();
		film.setName("Old Film");
		film.setDescription("Bad date");
		film.setDuration(100);
		film.setReleaseDate(LocalDate.of(1800, 1, 1)); // слишком рано

		mockMvc.perform(post("/films")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(film)))
				.andExpect(status().is4xxClientError());
	}

	@Test
	@DisplayName("Создание корректного пользователя")
	void shouldCreateValidUser() throws Exception {
		User user = new User();
		user.setEmail("mail@test.com");
		user.setLogin("userLogin");
	}
}