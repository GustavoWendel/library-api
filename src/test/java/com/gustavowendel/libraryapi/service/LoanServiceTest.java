package com.gustavowendel.libraryapi.service;

import com.gustavowendel.libraryapi.model.entity.Book;
import com.gustavowendel.libraryapi.model.entity.Loan;
import com.gustavowendel.libraryapi.model.entity.repository.LoanRepository;
import com.gustavowendel.libraryapi.service.impl.BookServiceImpl;
import com.gustavowendel.libraryapi.service.impl.LoanServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class LoanServiceTest {

    @MockBean
    private LoanRepository repository;

    private LoanService service;

    @BeforeEach
    public void setUp(){
        this.service = new LoanServiceImpl(repository);
    }

    @Test
    @DisplayName("deve salvar um empréstimo")
    public void saveLoanTest(){
        Book book = Book.builder().id(1L).build();
        String customer = "Fulano";
        Loan savingLoan = Loan
                .builder()
                .book(book)
                .customer("Fulano")
                .loanDate(LocalDate.now())
                .build();

        Loan savedLoan = Loan
                .builder()
                .id(1l)
                .loanDate(LocalDate.now())
                .customer(customer)
                .book(book)
                .build();

        when(repository.save(savingLoan) ).thenReturn(savedLoan);

        Loan loan = service.save(savingLoan);

        assertThat(loan.getId()).isEqualTo(savedLoan.getId());
        assertThat(loan.getBook().getId()).isEqualTo(savedLoan.getBook().getId());
        assertThat(loan.getCustomer()).isEqualTo(savedLoan.getCustomer());
        assertThat(loan.getLoanDate()).isEqualTo(savedLoan.getLoanDate());
    }
}
