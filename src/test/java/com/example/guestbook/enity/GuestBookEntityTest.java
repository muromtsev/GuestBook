package com.example.guestbook.enity;


import com.example.guestbook.Entity.GuestBookEntry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Тестирование модели GuestBookEntity")
public class GuestBookEntityTest {

    @Test
    @DisplayName("Создание экземпляра класса с корректными параметрами.")
    void testDefaultConstructor() {
        GuestBookEntry entry = new GuestBookEntry();

        assertNotNull(entry, "Новый объект должен быть создан.");
        assertNull(entry.getId(), "ID должен быть null при сооздании.");
        assertNull(entry.getAuthorName(), "authorName должен быть null.");
        assertNull(entry.getEmail(), "email должен быть null.");
        assertNull(entry.getMessage(), "message должна быть null.");
        assertNull(entry.getCreatedAt(), "сreatedAt должно быть null.");
    }

    @Test
    @DisplayName("Создание объекта и устаноовка полей через сеттеры.")
    void testSettersAndGetters() {
        GuestBookEntry entry = new GuestBookEntry();
        Long expectedId = 1L;
        String expectedName = "Ivan Ivanov";
        String expectedEmail = "ivan@example.com";
        String expectedMessage = "Test message";
        LocalDateTime expectedTime = LocalDateTime.of(2026, 2, 25, 12, 0);

        entry.setId(expectedId);
        entry.setAuthorName(expectedName);
        entry.setEmail(expectedEmail);
        entry.setMessage(expectedMessage);
        entry.setCreatedAt(expectedTime);

        assertEquals(expectedId, entry.getId(), "ID должен совпадать");
        assertEquals(expectedName, entry.getAuthorName(), "Имя автора должно совпадать.");
        assertEquals(expectedEmail, entry.getEmail(), "email должнен совпадать");
        assertEquals(expectedMessage, entry.getMessage(), "message должнен совпадать");
        assertEquals(expectedTime,entry. getCreatedAt(), "createdAt должнен совпадать");
    }

    @Test
    @DisplayName("Тестирование метода getAvatarUrl() с валидным email")
    void testGetAvatarUrlWithValidEmail() {
        GuestBookEntry entry = new GuestBookEntry();
        String email = "test@example.com";

        entry.setEmail(email);
        entry.setAuthorName("Test User");

        String avatarUrl = entry.getAvatarUrl();

        assertNotNull(avatarUrl, "URL аватара не должен быть null");
        assertTrue(avatarUrl.startsWith("https://www.gravatar.com/avatar/"), "URL должен начинаться с Gravatar URL");
        assertTrue(avatarUrl.contains("?d=identicon"), "URL доолжен содержать парамерт identicon");

        String expectedHash = "55502f40dc8b7c769880b10874abc9d0"; // md5 hash test@example.com
        assertTrue(avatarUrl.contains(expectedHash), "URL доолжен содержать правильный MD5 хеш emailЭ");
    }

    @Test
    @DisplayName("Тестирование метода getAvatarUrl() с null email")
    void testGetAvatarUrlWithNullEmail() {
        GuestBookEntry entry = new GuestBookEntry();
        entry.setEmail(null);
        entry.setAuthorName("Test User");

        String avatarUrl = entry.getAvatarUrl();

        assertNotNull(avatarUrl, "URL аватара не должен быть null");
        assertTrue(avatarUrl.startsWith("https://api.dicebear.com/7.x/identicon/svg?seed="),
                "Должен использоваться DiceBear API при отсутствии email");
        assertTrue(avatarUrl.endsWith("Test User"),
                "URL должен содержать имя автора как seed");
    }

    @Test
    @DisplayName("Тестирование метода getAvatarUrl() с пустым email")
    void testGetAvatarUrlWithEmptyEmail() {
        GuestBookEntry entry = new GuestBookEntry();
        entry.setEmail("");
        entry.setAuthorName("Test User");

        String avatarUrl = entry.getAvatarUrl();

        assertNotNull(avatarUrl, "URL аватара не должен быть null");
        assertTrue(avatarUrl.startsWith("https://api.dicebear.com/7.x/identicon/svg?seed="),
                "Пустой email должен обрабатываться как отсутствие email");
    }

    @Test
    @DisplayName("Тестирование метода getAvatarUrl() с email в разном регистре")
    void testGetAvatarUrlWithEmailDifferentCase() {
        GuestBookEntry entry1 = new GuestBookEntry();
        entry1.setEmail("Test@Example.com");
        entry1.setAuthorName("User1");

        GuestBookEntry entry2 = new GuestBookEntry();
        entry2.setEmail("test@example.com");
        entry2.setAuthorName("User2");

        String url1 = entry1.getAvatarUrl();
        String url2 = entry2.getAvatarUrl();

        assertEquals(url1, url2,
                "Email должен приводиться к нижнему регистру перед хешированием");
    }

    @Test
    @DisplayName("Тестирование метода getAvatarUrl() с email содержащим пробелы")
    void testGetAvatarUrlWithEmailContainingSpaces() {
        // Arrange
        GuestBookEntry entry = new GuestBookEntry();
        entry.setEmail("test@example.com ");
        entry.setAuthorName("Test User");

        String avatarUrl = entry.getAvatarUrl();

        assertNotNull(avatarUrl, "URL аватара не должен быть null");
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", "test@", "@example.com", "test.example.com"})
    @DisplayName("Тестирование getAvatarUrl с различными некорректными email")
    void testGetAvatarUrlWithVariousEmails(String invalidEmail) {
        GuestBookEntry entry = new GuestBookEntry();
        entry.setEmail(invalidEmail);
        entry.setAuthorName("Test User");

        String avatarUrl = entry.getAvatarUrl();

        assertNotNull(avatarUrl, "URL аватара не должен быть null");
    }

}


