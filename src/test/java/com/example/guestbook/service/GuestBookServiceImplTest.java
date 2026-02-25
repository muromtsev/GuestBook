package com.example.guestbook.service;

import com.example.guestbook.Entity.GuestBookEntry;
import com.example.guestbook.Repository.GuestBookRepository;
import com.example.guestbook.Service.GuestBookServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тестирование GuestBookServiceImpl")
public class GuestBookServiceImplTest {

    @Mock
    private GuestBookRepository guestBookRepository;

    @InjectMocks
    private GuestBookServiceImpl guestBookService;

    private GuestBookEntry entry1;
    private GuestBookEntry entry2;
    private GuestBookEntry entry3;
    private List<GuestBookEntry> entries;

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

        entries = Arrays.asList(entry1, entry2, entry3);
    }

    @Test
    @DisplayName("getGuestBookEntries должен возвращать все записи в ообратном поорядке")
    void testGetGuestBookEntries() {
        when(guestBookRepository.findAll()).thenReturn(entries);

        List<GuestBookEntry> result = guestBookService.getGuestBookEntries();

        assertNotNull(result, "Результат не должен быть null");
        assertEquals(3, result.size(), "Должно быть 3 записи");

        assertEquals(entry3, result.get(0), "Первоой записью должна быть запись с id=3");
        assertEquals(entry2, result.get(1), "Первоой записью должна быть запись с id=2");
        assertEquals(entry1, result.get(2), "Первоой записью должна быть запись с id=1");

        verify(guestBookRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("getGuestBookEntries должен возвращать пустой список, если записей нет")
    void testGetGuestBookEntriesEmpty() {
        when(guestBookRepository.findAll()).thenReturn(List.of());

        List<GuestBookEntry> result = guestBookService.getGuestBookEntries();

        assertNotNull(result, "Результат не должен быть null");
        assertTrue(result.isEmpty(), "Список должен быть пустым");

        verify(guestBookRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("getGuestBookEntries должен выбрасывать исключение, если репозиторий выбросил исключение")
    void testGetGuestBookEntriesThrowsException() {
        when(guestBookRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        assertThrows(RuntimeException.class,
                () -> guestBookService.getGuestBookEntries(),
                "Должно быть выброшено исключение при ошибке БД");

        verify(guestBookRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("getGuestBookEntryById должен возвращать запись по существующему ID")
    void testGetGuestBookEntryByIdFound() {
        Long id = 2L;
        when(guestBookRepository.findById(id)).thenReturn(Optional.of(entry2));

        GuestBookEntry result = guestBookService.getGuestBookEntryById(id);

        assertNotNull(result, "Результат не должен быть null");
        assertEquals(id, result.getId(), "ID должен совпадать");
        assertEquals("Мария Сидорова", result.getAuthorName(), "Имя должно совпадать");

        verify(guestBookRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("getGuestBookEntryById должен возвращать null для несуществующего ID")
    void testGetGuestBookEntryByIdNotFound() {
        Long id = 999L;
        when(guestBookRepository.findById(id)).thenReturn(Optional.empty());

        GuestBookEntry result = guestBookService.getGuestBookEntryById(id);

        assertNull(result, "Для несуществующего ID должен возвращаться null");

        verify(guestBookRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("getGuestBookEntryById должен пробрасывать исключение от репозитория")
    void testGetGuestBookEntryByIdThrowsException() {
        Long id = 1L;
        when(guestBookRepository.findById(id)).thenThrow(new RuntimeException("Database error"));

        assertThrows(RuntimeException.class,
                () -> guestBookService.getGuestBookEntryById(id),
                "Должно быть выброшено исключение при ошибке БД");

        verify(guestBookRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("saveGuestBookEntry должен сохранять новую запись")
    void testSaveGuestBookEntry() {
        GuestBookEntry newEntry = new GuestBookEntry();
        newEntry.setAuthorName("Новый пользователь");
        newEntry.setEmail("new@example.com");
        newEntry.setMessage("Новое сообщение");

        GuestBookEntry savedEntry = new GuestBookEntry();
        savedEntry.setId(4L);
        savedEntry.setAuthorName("Новый пользователь");
        savedEntry.setEmail("new@example.com");
        savedEntry.setMessage("Новое сообщение");
        savedEntry.setCreatedAt(LocalDateTime.now());

        when(guestBookRepository.save(any(GuestBookEntry.class))).thenReturn(savedEntry);

        GuestBookEntry result = guestBookService.saveGuestBookEntry(newEntry);

        assertNotNull(result, "Результат не должен быть null");
        assertEquals(4L, result.getId(), "ID должен быть присвоен");
        assertEquals("Новый пользователь", result.getAuthorName(), "Имя должно сохраниться");

        verify(guestBookRepository, times(1)).save(newEntry);
    }

    @Test
    @DisplayName("saveGuestBookEntry должен сохранять существующую запись (обновление)")
    void testSaveGuestBookEntryUpdate() {
        GuestBookEntry existingEntry = entry1;
        existingEntry.setMessage("Обновленное сообщение");

        when(guestBookRepository.save(existingEntry)).thenReturn(existingEntry);

        GuestBookEntry result = guestBookService.saveGuestBookEntry(existingEntry);

        assertNotNull(result, "Результат не должен быть null");
        assertEquals(1L, result.getId(), "ID должен остаться тем же");
        assertEquals("Обновленное сообщение", result.getMessage(), "Сообщение должно обновиться");

        verify(guestBookRepository, times(1)).save(existingEntry);
    }

    @Test
    @DisplayName("saveGuestBookEntry должен пробрасывать исключение при ошибке сохранения")
    void testSaveGuestBookEntryThrowsException() {
        GuestBookEntry newEntry = new GuestBookEntry();
        newEntry.setAuthorName("Тест");

        when(guestBookRepository.save(any(GuestBookEntry.class)))
                .thenThrow(new RuntimeException("Save error"));

        assertThrows(RuntimeException.class,
                () -> guestBookService.saveGuestBookEntry(newEntry),
                "Должно быть выброшено исключение при ошибке сохранения");

        verify(guestBookRepository, times(1)).save(newEntry);
    }

    @Test
    @DisplayName("saveGuestBookEntry должен сохранять запись с null полями (если валидация пропустит)")
    void testSaveGuestBookEntryWithNullFields() {
        GuestBookEntry incompleteEntry = new GuestBookEntry();

        when(guestBookRepository.save(incompleteEntry)).thenReturn(incompleteEntry);

        GuestBookEntry result = guestBookService.saveGuestBookEntry(incompleteEntry);

        assertNotNull(result, "Результат не должен быть null");

        verify(guestBookRepository, times(1)).save(incompleteEntry);
    }

    @Test
    @DisplayName("deleteGuestBookEntryById должен удалять запись по ID")
    void testDeleteGuestBookEntryById() {
        Long id = 2L;
        doNothing().when(guestBookRepository).deleteById(id);

        guestBookService.deleteGuestBookEntryById(id);

        verify(guestBookRepository, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("deleteGuestBookEntryById должен успешно работать при удалении несуществующего ID")
    void testDeleteGuestBookEntryByIdNotFound() {
        Long id = 999L;
        doNothing().when(guestBookRepository).deleteById(id);

        guestBookService.deleteGuestBookEntryById(id);

        verify(guestBookRepository, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("deleteGuestBookEntryById должен пробрасывать исключение при ошибке удаления")
    void testDeleteGuestBookEntryByIdThrowsException() {
        Long id = 1L;
        doThrow(new RuntimeException("Delete error"))
                .when(guestBookRepository).deleteById(id);

        assertThrows(RuntimeException.class,
                () -> guestBookService.deleteGuestBookEntryById(id),
                "Должно быть выброшено исключение при ошибке удаления");

        verify(guestBookRepository, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("AssertJ: проверка getGuestBookEntries с использованием AssertJ")
    void testGetGuestBookEntriesWithAssertJ() {
        when(guestBookRepository.findAll()).thenReturn(entries);

        List<GuestBookEntry> result = guestBookService.getGuestBookEntries();

        assertThat(result)
                .isNotNull()
                .hasSize(3)
                .containsExactly(entry3, entry2, entry1)
                .doesNotContainNull()
                .extracting(GuestBookEntry::getId)
                .containsExactly(3L, 2L, 1L);

        verify(guestBookRepository).findAll();
    }

    @Test
    @DisplayName("AssertJ: проверка сохранения записи")
    void testSaveWithAssertJ() {
        GuestBookEntry newEntry = new GuestBookEntry();
        newEntry.setAuthorName("AssertJ User");
        newEntry.setEmail("assertj@example.com");
        newEntry.setMessage("Testing with AssertJ");

        GuestBookEntry savedEntry = new GuestBookEntry();
        savedEntry.setId(5L);
        savedEntry.setAuthorName("AssertJ User");
        savedEntry.setEmail("assertj@example.com");
        savedEntry.setMessage("Testing with AssertJ");

        when(guestBookRepository.save(any(GuestBookEntry.class))).thenReturn(savedEntry);

        GuestBookEntry result = guestBookService.saveGuestBookEntry(newEntry);

        assertThat(result)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", 5L)
                .hasFieldOrPropertyWithValue("authorName", "AssertJ User")
                .hasFieldOrPropertyWithValue("email", "assertj@example.com")
                .hasFieldOrPropertyWithValue("message", "Testing with AssertJ");

        verify(guestBookRepository).save(newEntry);
    }

    @Test
    @DisplayName("Использование ArgumentCaptor для проверки сохраняемого объекта")
    void testSaveWithArgumentCaptor() {
        GuestBookEntry newEntry = new GuestBookEntry();
        newEntry.setAuthorName("Captor Test");
        newEntry.setEmail("captor@example.com");
        newEntry.setMessage("Testing with ArgumentCaptor");

        GuestBookEntry savedEntry = new GuestBookEntry();
        savedEntry.setId(6L);
        savedEntry.setAuthorName("Captor Test");
        savedEntry.setEmail("captor@example.com");
        savedEntry.setMessage("Testing with ArgumentCaptor");

        when(guestBookRepository.save(any(GuestBookEntry.class))).thenReturn(savedEntry);

        ArgumentCaptor<GuestBookEntry> entryCaptor = ArgumentCaptor.forClass(GuestBookEntry.class);

        GuestBookEntry result = guestBookService.saveGuestBookEntry(newEntry);

        verify(guestBookRepository).save(entryCaptor.capture());

        GuestBookEntry capturedEntry = entryCaptor.getValue();
        assertThat(capturedEntry)
                .hasFieldOrPropertyWithValue("authorName", "Captor Test")
                .hasFieldOrPropertyWithValue("email", "captor@example.com")
                .hasFieldOrPropertyWithValue("message", "Testing with ArgumentCaptor");

        assertThat(result).isEqualTo(savedEntry);
    }

}
