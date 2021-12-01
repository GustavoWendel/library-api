package com.gustavowendel.libraryapi.service;


import com.gustavowendel.libraryapi.exception.BusinessException;
import com.gustavowendel.libraryapi.model.entity.Book;
import com.gustavowendel.libraryapi.model.entity.repository.BookRepository;
import com.gustavowendel.libraryapi.service.impl.BookServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.*;


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
        when(repository.save(book)).thenReturn((
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
        when( repository.existsByIsbn(anyString()) ).thenReturn(true);

        //Execução
        Throwable exception = catchThrowable(() -> service.save(book));

        //Vericações
        assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Isbn já cadastrado");

        verify(repository, never()).save(book);
    }

    @Test
    @DisplayName("Deve obter um livro por id")
    public void getByIdTest(){
        //Cenário
        Long id = 1l;
        Book book = createValidBook();
        book.setId(id);
        when(repository.findById(id)).thenReturn(Optional.of(book));

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

        when(repository.findById(id)).thenReturn(Optional.empty());

        //Execução
        Optional<Book> foundBook = service.getById(id);

        //Verificações
        assertThat(foundBook.isPresent()).isFalse();
    }


    @Test
    @DisplayName("Deve deletar um livro")
    public void deleteBookTest() {
        //Cenário
        Book book = Book.builder().id(1L).isbn("123").author("fulano").title("As aventuras").build();

        //Execução
        service.delete(book);

        //Verificações
        verify(repository, times(1)).delete(book);
    }

    @Test
    @DisplayName("Deve ocorrer erro ao tentar deletar um livro inexistente.")
    public void deleteInvalidBookTest() {
        //Cenário
        Book book = new Book();
        //Execução
        Assertions.assertThrows(IllegalArgumentException.class, () ->  service.delete(book));
        //Vericação
        verify(repository, never()).delete(book);
    }

    @Test
    @DisplayName("Deve atualizar um livro ")
    public void updateBookTest() {
        //Cenário
        long id = 1L;

        //livro a atualizar
        Book updatingBook = Book.builder().id(id).build();

        //Simulação
        Book bookUpdated = createValidBook();
        bookUpdated.setId(id);

        when(repository.save(updatingBook)).thenReturn(bookUpdated);

        //Execução
        Book book = service.update(updatingBook);

        //Verificações
        assertThat(book.getId()).isEqualTo(bookUpdated.getId());
        assertThat(book.getTitle()).isEqualTo(bookUpdated.getTitle());
        assertThat(book.getIsbn()).isEqualTo(bookUpdated.getIsbn());
        assertThat(book.getAuthor()).isEqualTo(bookUpdated.getAuthor());

    }

    @Test
    @DisplayName("Deve ocorrer erro ao tentar atualizar um livro inexistente.")
    public void updateInvalidBookTest() {
        //Cenário
        Book book = new Book();
        //Execução
        Assertions.assertThrows(IllegalArgumentException.class, () ->  service.update(book));
        //Vericação
        verify(repository, never()).save(book);
    }

    @Test
    @DisplayName("Deve filtrar os livros pelas propriedades")
    public void findBookTest() {
        //Cenário
        Book book = createValidBook();

        Pageable pageRequest = PageRequest.of(0, 10);

        List<Book> lista = Collections.singletonList(book);

        Page<Book> page = new PageImpl<>(lista, pageRequest, 1);
        when( repository.findAll(any(Example.class), any(PageRequest.class)) )
                .thenReturn(page);

        //Execução
        Page<Book> result = service.find(book, pageRequest);

        //Verificações
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).isEqualTo(lista);
        assertThat(result.getPageable().getPageNumber()).isZero();
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);
    }

    @Test
    @DisplayName("deve obter um livro pelo isbn")
    public void getBookByIsbnTest(){
        String isbn = "1230";
        when(repository.findByIsbn(isbn)).thenReturn(Optional.of(Book.builder().id(1l).isbn(isbn).build()));

        Optional<Book> book = service.getBookByIsbn(isbn);

        assertThat(book).isPresent();
        assertThat(book.get().getId()).isEqualTo(1L);
        assertThat(book.get().getIsbn()).isEqualTo(isbn);

        verify(repository, times(1)).findByIsbn(isbn);
    }

    private Book createValidBook() {
        return Book.builder().isbn("123").author("fulano").title("As aventuras").build();
    }
}
