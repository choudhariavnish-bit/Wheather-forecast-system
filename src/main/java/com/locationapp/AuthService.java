package com.locationapp;

import java.util.UUID;

public class AuthService {

    public String authenticate(String username, String password, String country, String state, String city) {
        if (country == null || country.trim().isEmpty() || city == null || city.trim().isEmpty()) {
            return "{\"success\": false, \"message\": \"Please select your Country, State, and City location.\"}";
        }

        String token = UUID.randomUUID().toString();
        String safeCountry = country.replace("\"", "\\\"");
        String safeState = (state != null) ? state.replace("\"", "\\\"") : "";
        String safeCity = city.replace("\"", "\\\"");

        return String.format("{"
                + "\"success\": true,"
                + "\"message\": \"Welcome to Location Portal from %s, %s!\","
                + "\"token\": \"%s\","
                + "\"user\": {"
                + "  \"location\": {"
                + "    \"country\": \"%s\","
                + "    \"state\": \"%s\","
                + "    \"city\": \"%s\""
                + "  }"
                + "}"
                + "}", safeCity, safeCountry, token, safeCountry, safeState, safeCity);
    }
}
