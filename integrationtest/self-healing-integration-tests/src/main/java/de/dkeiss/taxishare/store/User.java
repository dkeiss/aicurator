package de.dkeiss.taxishare.store;

import java.util.Set;

public record User(
        String username,
        String email,
        Set<String> roles,
        String password
) {
}