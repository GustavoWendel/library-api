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

import java.util.Optional;

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

    @Test
    @DisplayName("Deve obter um livro por id")
    public void getByIdTest(){
        //Cenário
        Long id = 1l;
        Book book = createValidBook();
        book.setId(id);
        Mockito.when(repository.findById(id)).thenReturn(Optional.of(book));

        //Execução
        Optional<Book> foundBook = service.getById(id);

        //Verificações
        assertThat(foundBook.isPresent()).isTrue();
        assertThat(foundBook.get().getId()).isEqualTo(id);
        assertThat(foundBook.get().getTitle()).isEqualTo(book.getTitle());
        assertThat(foundBook.get().getAuthor()).isEqualTo(book.getAuthor());
        assertThat(foundBook.get().getIsbn()).isEqualTo(book.getIsbn());
    }

    @Test
    @DisplayName("Deve retornar vazio ao obter um livro por id, quando ele não existe na base.")
    public void bookNotFoundByIdTest(){
        //Cenário
        Long id = 1l;

        Mockito.when(repository.findById(id)).thenReturn(Optional.empty());

        //Execução
        Optional<Book> foundBook = service.getById(id);

        //Verificações
        assertThat(foundBook.isPresent()).isFalse();
    }

    private Book createValidBook() {
        return Book.builder().isbn("123").author("fulano").title("As aventuras").build();
    }
}
