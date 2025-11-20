package com.konark.util;

import java.util.*;

import com.konark.dto.VendorInventoryProjection;
import com.konark.entity.VendorItemEntity;

public class LevenshteinWordSearch {

    // Compute Levenshtein Distance at the word level
    public static int wordLevelEditDistance(String str1, String str2) {
        String[] words1 = str1.trim().toUpperCase().split("\\s+");
        String[] words2 = str2.trim().toUpperCase().split("\\s+");

        int m = words1.length;
        int n = words2.length;

        int[][] dp = new int[m + 1][n + 1];

        for (int i = 0; i <= m; i++) dp[i][0] = i;
        for (int j = 0; j <= n; j++) dp[0][j] = j;

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (words1[i - 1].equals(words2[j - 1])) {
                    dp[i][j] = dp[i - 1][j - 1]; // no change
                } else {
                    dp[i][j] = 1 + Math.min(
                        dp[i - 1][j - 1], // substitute
                        Math.min(
                            dp[i - 1][j],   // delete
                            dp[i][j - 1]    // insert
                        )
                    );
                }
            }
        }

        return dp[m][n];
    }

    // Helper class to hold results with distance
    static class MatchResult {
        String sentence;
        int distance;

        MatchResult(String sentence, int distance) {
            this.sentence = sentence;
            this.distance = distance;
        }
    }

    // Find and sort matches by word-level edit distance
    public static List<VendorInventoryProjection> findSimilarItems(List<VendorInventoryProjection> items, String input) {
        List<VendorInventoryProjection> matches = new ArrayList<>();
        int maxEdits = 2;

        for (VendorInventoryProjection item : items) {
        	
            int distance = wordLevelEditDistance(input, item.getItemName());
            if (distance <= maxEdits) {
                matches.add(item);
            }
        }

        // Sort matches by ascending distance
        //matches.sort(Comparator.comparingInt(m -> m.distance));

        return matches;
    }

//    public static void main(String[] args) {
//        List<String> data = Arrays.asList(
//            "GRB DATES HALWA 200 GM",
//            "AMUL BUTTER 500 GM",
//            "DAI DATES",
//            "DATES SYRUP ORGANIC",
//            "DATES",
//            "DAI ORGANIC DATES 500GM",
//            "KHAJOOR PACK"
//        );
//
//        String input = "DAI DATES";
//        int maxEdits = 3;
//
//        List<MatchResult> matches = findSimilarItems(data, input);
//
//        System.out.println("Sorted matches for: \"" + input + "\" (max edits: " + maxEdits + ")\n");
//
//        for (MatchResult match : matches) {
//            System.out.println(" - " + match.item + " (word edits: " + match.distance + ")");
//        }
//    }
}
