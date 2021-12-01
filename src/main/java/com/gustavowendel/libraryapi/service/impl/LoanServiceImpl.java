package com.gustavowendel.libraryapi.service.impl;

import com.gustavowendel.libraryapi.model.entity.Loan;
import com.gustavowendel.libraryapi.model.entity.repository.LoanRepository;
import com.gustavowendel.libraryapi.service.LoanService;
import org.springframework.stereotype.Service;

@Service
public class LoanServiceImpl implements LoanService {

    private final LoanRepository repository;

    public LoanServiceImpl(LoanRepository repository) {
        this.repository = repository;
    }

    @Override
    public Loan save(Loan loan) {
        return repository.save(loan);
    }
}
