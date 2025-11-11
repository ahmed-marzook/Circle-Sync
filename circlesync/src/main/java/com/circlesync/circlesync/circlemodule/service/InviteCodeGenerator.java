package com.circlesync.circlesync.circlemodule.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
@RequiredArgsConstructor
public class InviteCodeGenerator {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGTH = 8;
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * Generate a unique invite code
     * Format: 8 characters (uppercase letters and numbers)
     * Example: AB12CD34
     */
    public String generate() {
        StringBuilder code = new StringBuilder(CODE_LENGTH);

        for (int i = 0; i < CODE_LENGTH; i++) {
            int index = RANDOM.nextInt(CHARACTERS.length());
            code.append(CHARACTERS.charAt(index));
        }

        return code.toString();
    }

    /**
     * Generate a readable invite code with dashes
     * Format: XXXX-XXXX
     * Example: AB12-CD34
     */
    public String generateReadable() {
        String code = generate();
        return code.substring(0, 4) + "-" + code.substring(4);
    }

    /**
     * Validate invite code format
     */
    public boolean isValid(String code) {
        if (code == null || code.isEmpty()) {
            return false;
        }

        // Remove dashes if present
        String normalized = code.replace("-", "");

        // Check length
        if (normalized.length() != CODE_LENGTH) {
            return false;
        }

        // Check characters
        return normalized.matches("[A-Z0-9]+");
    }
}