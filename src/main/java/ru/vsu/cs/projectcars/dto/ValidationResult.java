package ru.vsu.cs.projectcars.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ValidationResult {

    private final List<String> errors;
    private final List<String> warnings;

    public ValidationResult() {
        this.errors = new ArrayList<>();
        this.warnings = new ArrayList<>();
    }

    public void addError(String error) { errors.add(error); }
    public void addWarning(String warning) { warnings.add(warning); }
    public boolean hasErrors() { return !errors.isEmpty(); }
    public boolean hasWarnings() { return !warnings.isEmpty(); }
    public boolean isValid() { return errors.isEmpty(); }
    public List<String> getErrors() { return Collections.unmodifiableList(errors); }
    public List<String> getWarnings() { return Collections.unmodifiableList(warnings); }
}
