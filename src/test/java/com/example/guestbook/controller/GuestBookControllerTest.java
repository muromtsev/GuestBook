package com.example.guestbook.controller;

import com.example.guestbook.Controller.GuestBookController;
import com.example.guestbook.Entity.GuestBookEntry;
import com.example.guestbook.Service.GuestBookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GuestBookController.class)
@DisplayName("Тестирование GuestBookController")
class GuestBookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GuestBookService guestBookService;

    @Autowired
    private ObjectMapper objectMapper;

    private GuestBookEntry entry1;
    private GuestBookEntry entry2;
    private GuestBookEntry entry3;
    private List<GuestBookEntry> mockEntries;

    @BeforeEach
    void setUp() {
        entry1 = new GuestBookEntry();
        entry1.setId(1L);
        entry1.setAuthorName("Иван Петров");
        entry1.setEmail("ivan@example.com");
        entry1.setMessage("Первое сообщение");
        entry1.setCreatedAt(LocalDateTime.now().minusDays(2));

        entry2 = new GuestBookEntry();
        entry2.setId(2L);
        entry2.setAuthorName("Мария Сидорова");
        entry2.setEmail("maria@example.com");
        entry2.setMessage("Второе сообщение");
        entry2.setCreatedAt(LocalDateTime.now().minusDays(1));

        entry3 = new GuestBookEntry();
        entry3.setId(3L);
        entry3.setAuthorName("Петр Иванов");
        entry3.setEmail("petr@example.com");
        entry3.setMessage("Третье сообщение");
        entry3.setCreatedAt(LocalDateTime.now());

        mockEntries = Arrays.asList(entry1, entry2, entry3);
    }

    @Test
    @DisplayName("GET / - должен возвращать страницу index со списком записей")
    void testIndexPage() throws Exception {
        when(guestBookService.getGuestBookEntries()).thenReturn(mockEntries);

        mockMvc.perform(get("/"))
                .andDo(print()) // выводит детали запроса/ответа
                .andExpect(status().isOk()) // проверяем статус 200
                .andExpect(view().name("index")) // проверяем имя view
                .andExpect(model().attributeExists("entries")) // проверяем наличие атрибута
                .andExpect(model().attributeExists("entry")) // проверяем наличие формы
                .andExpect(model().attribute("entries", hasSize(3))) // проверяем размер списка
                .andExpect(model().attribute("entries", hasItem(
                        allOf(
                                hasProperty("id", is(1L)),
                                hasProperty("authorName", is("Иван Петров"))
                        )
                )));

        verify(guestBookService, times(1)).getGuestBookEntries();
    }

    @Test
    @DisplayName("GET / - должен возвращать пустой список, если записей нет")
    void testIndexPageWithEmptyList() throws Exception {
        // Arrange
        when(guestBookService.getGuestBookEntries()).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("entries"))
                .andExpect(model().attribute("entries", hasSize(0)))
                .andExpect(model().attribute("entries", emptyIterable()));

        verify(guestBookService, times(1)).getGuestBookEntries();
    }

    @Test
    @DisplayName("GET / - должна создаваться новая пустая запись для формы")
    void testIndexPageNewEntryForm() throws Exception {
        // Arrange
        when(guestBookService.getGuestBookEntries()).thenReturn(mockEntries);

        // Act & Assert
        MvcResult result = mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("entry"))
                .andReturn();

        GuestBookEntry formEntry = (GuestBookEntry) result.getModelAndView()
                .getModel().get("entry");

        assertThat(formEntry)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", null)
                .hasFieldOrPropertyWithValue("authorName", null)
                .hasFieldOrPropertyWithValue("email", null)
                .hasFieldOrPropertyWithValue("message", null);
    }

    // ========== ТЕСТЫ ДЛЯ POST ЗАПРОСА (добавление записи) ==========

    @Test
    @DisplayName("POST / - успешное добавление валидной записи")
    void testAddEntrySuccess() throws Exception {
        // Arrange
        GuestBookEntry newEntry = new GuestBookEntry();
        newEntry.setAuthorName("Тестовый пользователь");
        newEntry.setEmail("test@example.com");
        newEntry.setMessage("Это тестовое сообщение длиной более 10 символов");

        GuestBookEntry savedEntry = new GuestBookEntry();
        savedEntry.setId(4L);
        savedEntry.setAuthorName("Тестовый пользователь");
        savedEntry.setEmail("test@example.com");
        savedEntry.setMessage("Это тестовое сообщение длиной более 10 символов");
        savedEntry.setCreatedAt(LocalDateTime.now());

        when(guestBookService.saveGuestBookEntry(any(GuestBookEntry.class)))
                .thenReturn(savedEntry);

        // Создаем параметры запроса
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("authorName", "Тестовый пользователь");
        params.add("email", "test@example.com");
        params.add("message", "Это тестовое сообщение длиной более 10 символов");

        // Act & Assert
        mockMvc.perform(post("/")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .params(params))
                .andExpect(status().is3xxRedirection()) // проверяем редирект
                .andExpect(redirectedUrl("/")); // проверяем URL редиректа

        verify(guestBookService, times(1)).saveGuestBookEntry(any(GuestBookEntry.class));
    }

    @Test
    @DisplayName("POST / - ошибка валидации при пустом имени автора")
    void testAddEntryWithEmptyAuthorName() throws Exception {
        // Arrange
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("authorName", "");
        params.add("email", "test@example.com");
        params.add("message", "Это тестовое сообщение длиной более 10 символов");

        // Act & Assert
        mockMvc.perform(post("/")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .params(params))
                .andExpect(status().isOk()) // остаемся на той же странице
                .andExpect(view().name("index"))
                .andExpect(model().attributeHasFieldErrors("entry", "authorName"))
                .andExpect(model().attributeExists("errors"));

        verify(guestBookService, never()).saveGuestBookEntry(any(GuestBookEntry.class));
    }

    @Test
    @DisplayName("POST / - ошибка валидации при слишком коротком имени")
    void testAddEntryWithShortAuthorName() throws Exception {
        // Arrange
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("authorName", "И"); // 1 символ
        params.add("email", "test@example.com");
        params.add("message", "Это тестовое сообщение длиной более 10 символов");

        // Act & Assert
        mockMvc.perform(post("/")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .params(params))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeHasFieldErrors("entry", "authorName"))
                .andExpect(model().attributeExists("errors"));

        verify(guestBookService, never()).saveGuestBookEntry(any(GuestBookEntry.class));
    }

    @Test
    @DisplayName("POST / - ошибка валидации при некорректном email")
    void testAddEntryWithInvalidEmail() throws Exception {
        // Arrange
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("authorName", "Иван Петров");
        params.add("email", "invalid-email");
        params.add("message", "Это тестовое сообщение длиной более 10 символов");

        // Act & Assert
        mockMvc.perform(post("/")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .params(params))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeHasFieldErrors("entry", "email"))
                .andExpect(model().attributeExists("errors"));

        verify(guestBookService, never()).saveGuestBookEntry(any(GuestBookEntry.class));
    }

    @Test
    @DisplayName("POST / - ошибка валидации при пустом сообщении")
    void testAddEntryWithEmptyMessage() throws Exception {
        // Arrange
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("authorName", "Иван Петров");
        params.add("email", "test@example.com");
        params.add("message", "");

        // Act & Assert
        mockMvc.perform(post("/")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .params(params))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeHasFieldErrors("entry", "message"))
                .andExpect(model().attributeExists("errors"));

        verify(guestBookService, never()).saveGuestBookEntry(any(GuestBookEntry.class));
    }

    @Test
    @DisplayName("POST / - ошибка валидации при слишком коротком сообщении")
    void testAddEntryWithShortMessage() throws Exception {
        // Arrange
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("authorName", "Иван Петров");
        params.add("email", "test@example.com");
        params.add("message", "Короткое"); // меньше 10 символов

        // Act & Assert
        mockMvc.perform(post("/")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .params(params))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeHasFieldErrors("entry", "message"))
                .andExpect(model().attributeExists("errors"));

        verify(guestBookService, never()).saveGuestBookEntry(any(GuestBookEntry.class));
    }

    @Test
    @DisplayName("POST / - несколько ошибок валидации одновременно")
    void testAddEntryWithMultipleErrors() throws Exception {
        // Arrange
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("authorName", ""); // пустое имя
        params.add("email", "invalid-email"); // неверный email
        params.add("message", "кор"); // короткое сообщение

        // Act & Assert
        mockMvc.perform(post("/")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .params(params))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeHasFieldErrors("entry", "authorName"))
                .andExpect(model().attributeHasFieldErrors("entry", "email"))
                .andExpect(model().attributeHasFieldErrors("entry", "message"))
                .andExpect(model().attribute("errors", not(empty())));

        verify(guestBookService, never()).saveGuestBookEntry(any(GuestBookEntry.class));
    }

    @Test
    @DisplayName("POST / - email может быть пустым (необязательное поле)")
    void testAddEntryWithEmptyEmail() throws Exception {
        // Arrange
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("authorName", "Иван Петров");
        params.add("email", "");
        params.add("message", "Это тестовое сообщение длиной более 10 символов");

        GuestBookEntry savedEntry = new GuestBookEntry();
        savedEntry.setId(5L);
        savedEntry.setAuthorName("Иван Петров");
        savedEntry.setEmail("");
        savedEntry.setMessage("Это тестовое сообщение длиной более 10 символов");
        savedEntry.setCreatedAt(LocalDateTime.now());

        when(guestBookService.saveGuestBookEntry(any(GuestBookEntry.class)))
                .thenReturn(savedEntry);

        // Act & Assert
        mockMvc.perform(post("/")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .params(params))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(guestBookService, times(1)).saveGuestBookEntry(any(GuestBookEntry.class));
    }

    // ========== ТЕСТЫ С ПРОВЕРКОЙ МОДЕЛИ ==========

    @Test
    @DisplayName("POST / - при ошибке валидации модель содержит список записей")
    void testAddEntryWithErrorsStillShowsEntries() throws Exception {
        // Arrange
        when(guestBookService.getGuestBookEntries()).thenReturn(mockEntries);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("authorName", "");
        params.add("email", "test@example.com");
        params.add("message", "Это тестовое сообщение длиной более 10 символов");

        // Act & Assert
        mockMvc.perform(post("/")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .params(params))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("entries"))
                .andExpect(model().attribute("errors", not(empty())));

        verify(guestBookService, never()).saveGuestBookEntry(any(GuestBookEntry.class));
        verify(guestBookService, times(1)).getGuestBookEntries();
    }

    @Test
    @DisplayName("POST / - при ошибке валидации введенные данные сохраняются в форме")
    void testAddEntryWithErrorsPreservesInput() throws Exception {
        // Arrange
        String authorName = "Тест";
        String email = "invalid-email";
        String message = "Тестовое сообщение";

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("authorName", authorName);
        params.add("email", email);
        params.add("message", message);

        // Act & Assert
        MvcResult result = mockMvc.perform(post("/")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .params(params))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("entry"))
                .andReturn();

        GuestBookEntry formEntry = (GuestBookEntry) result.getModelAndView()
                .getModel().get("entry");

        assertThat(formEntry)
                .hasFieldOrPropertyWithValue("authorName", authorName)
                .hasFieldOrPropertyWithValue("email", email)
                .hasFieldOrPropertyWithValue("message", message);
    }

    // ========== ТЕСТЫ ДЛЯ ОБРАБОТКИ ИСКЛЮЧЕНИЙ ==========

    @Test
    @DisplayName("GET / - обработка исключения от сервиса")
    void testIndexPageServiceException() throws Exception {
        // Arrange
        when(guestBookService.getGuestBookEntries())
                .thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attribute("error", containsString("Database error")))
                .andExpect(model().attributeExists("entry"))
                .andExpect(model().attributeExists("entries"))
                .andExpect(model().attribute("entries", hasSize(0)));

        verify(guestBookService, times(1)).getGuestBookEntries();
    }

    @Test
    @DisplayName("POST / - обработка исключения при сохранении")
    void testAddEntryServiceException() throws Exception {
        // Arrange
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("authorName", "Иван Петров");
        params.add("email", "test@example.com");
        params.add("message", "Это тестовое сообщение длиной более 10 символов");

        when(guestBookService.saveGuestBookEntry(any(GuestBookEntry.class)))
                .thenThrow(new RuntimeException("Save error"));

        // Нужно замокать getGuestBookEntries, так как он может вызываться
        when(guestBookService.getGuestBookEntries()).thenReturn(mockEntries);

        // Act
        MvcResult result = mockMvc.perform(post("/")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .params(params))
                .andReturn();

        // Assert
        System.out.println("Статус ответа: " + result.getResponse().getStatus());
        System.out.println("View name: " + result.getModelAndView().getViewName());
        System.out.println("Атрибуты модели: " + result.getModelAndView().getModel().keySet());

        // Проверяем результат
        assertEquals(200, result.getResponse().getStatus());
        assertEquals("index", result.getModelAndView().getViewName());

        verify(guestBookService, times(1)).saveGuestBookEntry(any(GuestBookEntry.class));
    }
}
