package com.khemiri.InternManager.utils;

import java.security.SecureRandom;
public class RandomString {

    private static final String CHAR_UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String CHAR_LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL_CHARACTERS = "!@$&";
    public static String generatePassword(int length,String uniqueWord) {
        StringBuilder password = new StringBuilder(length);
        SecureRandom random = new SecureRandom();

        // Add at least one character from each character set
        password.append(CHAR_UPPER.charAt(random.nextInt(CHAR_UPPER.length())));
        password.append(CHAR_LOWER.charAt(random.nextInt(CHAR_LOWER.length())));
        password.append(DIGITS.charAt(random.nextInt(DIGITS.length())));
        password.append(SPECIAL_CHARACTERS.charAt(random.nextInt(SPECIAL_CHARACTERS.length())));
        password.append(uniqueWord.toLowerCase());

        // Generate remaining characters randomly
        for (int i = 4; i < length; i++) {
            String charSet = CHAR_UPPER + CHAR_LOWER + DIGITS + SPECIAL_CHARACTERS;
            password.append(charSet.charAt(random.nextInt(charSet.length())));
        }

        // Shuffle the characters in the password
        String shuffledPassword = password.toString();
        char[] passwordChars = shuffledPassword.toCharArray();
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(length);
            char temp = passwordChars[i];
            passwordChars[i] = passwordChars[randomIndex];
            passwordChars[randomIndex] = temp;
        }
        return new String(passwordChars);
    }
}
