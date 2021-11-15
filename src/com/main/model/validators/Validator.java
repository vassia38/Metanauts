package com.main.model.validators;

public interface Validator<T> {
    void validate(T entity) throws ValidationException;
}