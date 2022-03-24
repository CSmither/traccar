package org.traccar.helper;

import java.util.Optional;

public class Ternary {

    private final Optional<Boolean> value;

    public Ternary(String value) {
        if (value.isBlank() || value.isEmpty()) {
            this.value = Optional.empty();
        } else {
            this.value = Optional.of(Boolean.parseBoolean(value));
        }
    }

    public Optional<Boolean> getValue() {
        return value;
    }
}
