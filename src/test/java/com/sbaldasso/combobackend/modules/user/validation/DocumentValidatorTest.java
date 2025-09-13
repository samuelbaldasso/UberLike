package com.sbaldasso.combobackend.modules.user.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import jakarta.validation.ConstraintValidatorContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class DocumentValidatorTest {

    private DocumentValidator validator;
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        validator = new DocumentValidator();
        context = mock(ConstraintValidatorContext.class);
    }

    @Test
    void shouldValidateValidCPF() {
        assertTrue(validator.isValid("529.982.247-25", context));
    }

    @Test
    void shouldValidateValidCNPJ() {
        assertTrue(validator.isValid("63.149.431/0001-20", context));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "111.111.111-11",
        "000.000.000-00",
        "123.456.789-10"
    })
    void shouldNotValidateInvalidCPF(String cpf) {
        assertFalse(validator.isValid(cpf, context));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "11.111.111/1111-11",
        "00.000.000/0000-00",
        "12.345.678/9012-34"
    })
    void shouldNotValidateInvalidCNPJ(String cnpj) {
        assertFalse(validator.isValid(cnpj, context));
    }

    @Test
    void shouldNotValidateEmptyDocument() {
        assertFalse(validator.isValid("", context));
    }

    @Test
    void shouldNotValidateNullDocument() {
        assertFalse(validator.isValid(null, context));
    }
}
