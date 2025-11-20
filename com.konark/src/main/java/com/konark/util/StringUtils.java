package com.konark.util;

import java.util.*;

public class StringUtils {

    // Levenshtein distance algorithm
    public static int levenshteinDistance(String str1, String str2) {
        int len1 = str1.length();
        int len2 = str2.length();
        int[][] dp = new int[len1 + 1][len2 + 1];

        for (int i = 0; i <= len1; i++) {
            for (int j = 0; j <= len2; j++) {
                if (i == 0) {
                    dp[i][j] = j; // If the first string is empty, insert all characters of the second string
                } else if (j == 0) {
                    dp[i][j] = i; // If the second string is empty, remove all characters of the first string
                } else {
                    dp[i][j] = Math.min(Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                                        dp[i - 1][j - 1] + (str1.charAt(i - 1) == str2.charAt(j - 1) ? 0 : 1));
                }
            }
        }
        return dp[len1][len2];
    }

    // Tokenize strings into words
    public static List<String> tokenize(String str) {
        // Use regex to split the string by spaces, punctuation, etc.
        String[] tokens = str.toLowerCase().split("\\W+");
        return Arrays.asList(tokens);
    }
}
