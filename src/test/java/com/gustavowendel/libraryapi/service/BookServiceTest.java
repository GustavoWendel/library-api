package com.gustavowendel.libraryapi.service;


import com.gustavowendel.libraryapi.exception.BusinessException;
import com.gustavowendel.libraryapi.model.entity.Book;
import com.gustavowendel.libraryapi.model.entity.repository.BookRepository;
import com.gustavowendel.libraryapi.service.impl.BookServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;


@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BookServiceTest {

    static String BOOK_API = "/api/books";

    BookService service;

    MockMvc mvc;

    @MockBean
    BookRepository repository;

    @BeforeEach
    public void setUp(){
        this.service = new BookServiceImpl(repository);
    }

    @Test
    @DisplayName("Deve salvar um livro")
    public void saveBookTest(){
        // Cenário
        Book book = createValidBook();
        Mockito.when(repository.save(book)).thenReturn((
                Book.builder()
                        .isbn("123")
                        .id(1L)
                        .author("Jefferson")
                        .title("As aventuras")
                        .build()));

        //Execução
        Book saveBook = service.save(book);

        //Verificação
        assertThat(saveBook.getId()).isNotNull();
        assertThat(saveBook.getIsbn()).isEqualTo("123");
        assertThat(saveBook.getAuthor()).isEqualTo("Jefferson");
        assertThat(saveBook.getTitle()).isEqualTo("As aventuras");
    }

    @Test
    @DisplayName("Deve lançar erro de negócio ao tentar salvar um livro com Isbn duplicado")
    public void shoulNotSaveBookWithDuplicatedISBN(){
        //Cenário
        Book book = createValidBook();
        Mockito.when( repository.existsByIsbn(Mockito.anyString()) ).thenReturn(true);

        //Execução
        Throwable exception = catchThrowable(() -> service.save(book));

        //Vericações
        assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Isbn já cadastrado");

        Mockito.verify(repository, Mockito.never()).save(book);
    }

    private Book createValidBook() {
        return Book.builder().isbn("123").author("fulano").title("As aventuras").build();
    }
}
