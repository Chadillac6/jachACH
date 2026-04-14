package com.afrunt.jach.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ValidationResultDTO {
    private boolean valid;
    private List<ValidationError> errors = new ArrayList<>();

    public boolean isValid() { return valid; }
    public void setValid(boolean valid) { this.valid = valid; }

    public List<ValidationError> getErrors() { return errors; }
    public void setErrors(List<ValidationError> errors) { this.errors = errors; }

    public void addError(ValidationError error) {
        this.errors.add(error);
    }

    public static class ValidationError {
        private int lineNumber;
        private String field;
        private String message;

        public ValidationError() {}

        public ValidationError(int lineNumber, String field, String message) {
            this.lineNumber = lineNumber;
            this.field = field;
            this.message = message;
        }

        public int getLineNumber() { return lineNumber; }
        public void setLineNumber(int lineNumber) { this.lineNumber = lineNumber; }

        public String getField() { return field; }
        public void setField(String field) { this.field = field; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}
