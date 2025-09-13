package com.sbaldasso.combobackend.modules.user.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.util.StringUtils;

public class DocumentValidator implements ConstraintValidator<ValidDocument, String> {

    @Override
    public void initialize(ValidDocument constraintAnnotation) {
    }

    @Override
    public boolean isValid(String document, ConstraintValidatorContext context) {
        if (!StringUtils.hasText(document)) {
            return false;
        }
        
        document = document.replaceAll("[^0-9]", "");
        
        if (document.length() == 11) {
            return isValidCPF(document);
        } else if (document.length() == 14) {
            return isValidCNPJ(document);
        }
        
        return false;
    }

    private boolean isValidCPF(String cpf) {
        if (cpf.matches("(\\d)\\1{10}")) return false;

        int[] numbers = new int[11];
        for (int i = 0; i < 11; i++) {
            numbers[i] = Character.getNumericValue(cpf.charAt(i));
        }

        // Validação primeiro dígito
        int sum = 0;
        for (int i = 0; i < 9; i++) {
            sum += numbers[i] * (10 - i);
        }

        int remainder = sum % 11;
        int firstDigit = remainder < 2 ? 0 : 11 - remainder;

        if (numbers[9] != firstDigit) return false;

        // Validação segundo dígito
        sum = 0;
        for (int i = 0; i < 10; i++) {
            sum += numbers[i] * (11 - i);
        }

        remainder = sum % 11;
        int secondDigit = remainder < 2 ? 0 : 11 - remainder;

        return numbers[10] == secondDigit;
    }

    private boolean isValidCNPJ(String cnpj) {
        if (cnpj.matches("(\\d)\\1{13}")) return false;

        int[] numbers = new int[14];
        for (int i = 0; i < 14; i++) {
            numbers[i] = Character.getNumericValue(cnpj.charAt(i));
        }

        // Validação primeiro dígito
        int[] weight1 = {5,4,3,2,9,8,7,6,5,4,3,2};
        int sum = 0;
        for (int i = 0; i < 12; i++) {
            sum += numbers[i] * weight1[i];
        }

        int remainder = sum % 11;
        int firstDigit = remainder < 2 ? 0 : 11 - remainder;

        if (numbers[12] != firstDigit) return false;

        // Validação segundo dígito
        int[] weight2 = {6,5,4,3,2,9,8,7,6,5,4,3,2};
        sum = 0;
        for (int i = 0; i < 13; i++) {
            sum += numbers[i] * weight2[i];
        }

        remainder = sum % 11;
        int secondDigit = remainder < 2 ? 0 : 11 - remainder;

        return numbers[13] == secondDigit;
    }
}
