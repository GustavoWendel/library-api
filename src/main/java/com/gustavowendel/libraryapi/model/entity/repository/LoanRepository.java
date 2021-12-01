package com.gustavowendel.libraryapi.model.entity.repository;

import com.gustavowendel.libraryapi.model.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
}
