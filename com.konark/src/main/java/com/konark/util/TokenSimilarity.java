package com.konark.util;

import java.util.*;

public class TokenSimilarity {
	 // Compute Jaccard Similarity between two sets of words
    public static double jaccardSimilarity(Set<String> set1, Set<String> set2) {
        Set<String> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);

        Set<String> union = new HashSet<>(set1);
        union.addAll(set2);

        if (union.isEmpty()) return 0.0;

        return (double) intersection.size() / union.size();
    }

    // Convert item to set of words (tokens)
    public static Set<String> toWordSet(String item) {
        String[] words = item.trim().toUpperCase().split("\\s+");
        return new HashSet<>(Arrays.asList(words));
    }

    // Find matching items with Jaccard similarity >= threshold
    public static List<String> findSimilarItems(List<String> items, String input) {
    	
    	double threshold = 0.5;
        Set<String> inputWords = toWordSet(input);
        List<String> result = new ArrayList<>();

        for (String item : items) {
            Set<String> itemWords = toWordSet(item);
            double similarity = jaccardSimilarity(inputWords, itemWords);

            if (similarity >= threshold) {
                result.add(item + " (score: " + String.format("%.2f", similarity) + ")");
            }
        }

        return result;
    }

    public static void main(String[] args) {
        List<String> data = Arrays.asList(
            "GRB DATES HALWA 200 GM",
            "AMUL BUTTER 500 GM",
            "DAI DATES",
            "DATES SYRUP ORGANIC",
            "DAI ORGANIC DATES 500GM"
        );

        String input = "DAI DATES";
        double threshold = 0.1;

        List<String> matches = findSimilarItems(data, input);

        System.out.println("Matches for: \"" + input + "\"");
        for (String match : matches) {
            System.out.println(" - " + match);
        }
    }
}
