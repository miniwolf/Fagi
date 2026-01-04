package com.fagi.http.server.validation.test.api.valid;

import java.util.List;

public class ValidRestService implements ValidRestApi {
    @Override
    public List<String> getAllUsernames() {
        return List.of();
    }

    @Override
    public List<String> getUsersOfType(String type) {
        return List.of();
    }

    @Override
    public List<String> getUsernamesOfUsersFromBeforeDate(String date) {
        return List.of();
    }

    @Override
    public String createUser(
            String username,
            String password) {
        return "";
    }

    @Override
    public void updateUser(
            String username,
            String password) {
    }

    @Override
    public List<String> getUserHistory(
            String username,
            String history,
            int age,
            String status,
            int accountAge,
            boolean working,
            boolean returnAll,
            long timeoutms,
            String mediaTypeToInclude) {
        return List.of();
    }
}
