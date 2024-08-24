package br.com.erudio.unittests.service;

import br.com.erudio.data.vo.v1.BookVO;
import br.com.erudio.data.vo.v1.PersonVO;
import br.com.erudio.exceptions.RequiredObjectIsNullException;
import br.com.erudio.model.Book;
import br.com.erudio.model.Person;
import br.com.erudio.repository.BookRepository;
import br.com.erudio.service.BookService;
import br.com.erudio.unittests.mapper.mocks.MockBook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class BookServiceTest {

    MockBook input;

    @InjectMocks
    private BookService bookService;

    @Mock
    private BookRepository bookRepository;

    @BeforeEach
    void setUpMocks() throws Exception {
        input = new MockBook();
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindById() {
        Book entity = input.mockEntity(1);
        entity.setId(1L);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(entity));

        var result = bookService.findById(1L);
        assertNotNull(result);
        assertNotNull(result.getKey());
        assertNotNull(result.getLinks());
        assertTrue(result.toString().contains("links: [</api/book/v1/1>;rel=\"self\"]"));
        assertEquals("Author Test1", result.getAuthor());
        assertEquals("Title Test1", result.getTitle());
        assertEquals(new BigDecimal(551), result.getPrice());
        assertNotNull(result.getLaunchDate());
    }

    @Test
    void testCreate() {
        Book persisted = input.mockEntity(1);
        persisted.setId(1L);

        BookVO vo = input.mockVO(1);
        vo.setKey(1L);

        when(bookRepository.save(any(Book.class))).thenReturn(persisted);

        var result = bookService.create(vo);
        assertNotNull(result);
        assertNotNull(result.getKey());
        assertNotNull(result.getLinks());
        assertTrue(result.toString().contains("links: [</api/book/v1/1>;rel=\"self\"]"));
        assertEquals("Author Test1", result.getAuthor());
        assertEquals("Title Test1", result.getTitle());
        assertEquals(new BigDecimal(551), result.getPrice());
        assertNotNull(result.getLaunchDate());
    }

    @Test
    void testCreateWithNullBook() {
        Exception exception = assertThrows(RequiredObjectIsNullException.class, () -> {
            bookService.create(null);
        });

        String expectedMessage = "It is not allowed to persist a null object";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testUpdate() {
        Book entity = input.mockEntity(1);
        entity.setId(1L);

        Book persisted = entity;
        persisted.setId(1L);

        BookVO vo = input.mockVO(1);
        vo.setKey(1L);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(bookRepository.save(entity)).thenReturn(persisted);

        var result = bookService.update(vo);
        assertNotNull(result);
        assertNotNull(result.getKey());
        assertNotNull(result.getLinks());
        assertTrue(result.toString().contains("links: [</api/book/v1/1>;rel=\"self\"]"));
        assertEquals("Author Test1", result.getAuthor());
        assertEquals("Title Test1", result.getTitle());
        assertEquals(new BigDecimal(551), result.getPrice());
        assertNotNull(result.getLaunchDate());
    }

    @Test
    void testUpdateWithNullPerson() {
        Exception exception = assertThrows(RequiredObjectIsNullException.class, () -> {
            bookService.update(null);
        });

        String expectedMessage = "It is not allowed to persist a null object";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testDelete() {
        Book entity = input.mockEntity(1);
        entity.setId(1L);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(entity));

        bookService.delete(1L);
    }

}
