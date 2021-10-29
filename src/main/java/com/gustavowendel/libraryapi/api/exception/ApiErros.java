package com.gustavowendel.libraryapi.api.exception;

import com.gustavowendel.libraryapi.exception.BusinessException;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ApiErros {
    List<String> errors;

    public ApiErros(BindingResult bindingResult) {
        this.errors = new ArrayList<>();
        bindingResult.getAllErrors().forEach(error -> this.errors.add(error.getDefaultMessage()));
    }

    public ApiErros(BusinessException ex) {
        this.errors = Collections.singletonList(ex.getMessage());
    }

    public List<String> getErrors(){
        return errors;
    }
}
