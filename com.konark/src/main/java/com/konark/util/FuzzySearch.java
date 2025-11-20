package com.konark.util;

import org.apache.commons.text.similarity.LevenshteinDistance;
import java.util.*;

public class FuzzySearch {
    public static void main(String[] args) {
        // Sample list of 6000 item names (replace with actual data)
        List<String> itemNames = Arrays.asList(
            "Apple", "Applesauce", "Banana", "Orange", "Grapes", "Pineapple", "Mango", "Strawberry"
            // Add your 6000 items here
        );

        String inputItem = "Appl"; // The item to search for

        // Find the top closest matches
        List<String> closestMatches = findClosestItems(inputItem, itemNames, 5); // Get top 5 closest matches

        // Print results
        System.out.println("Closest matches for: " + inputItem);
        for (String match : closestMatches) {
            System.out.println(match);
        }
    }

    public static List<String> findClosestItems(String input, List<String> items, int topN) {
        LevenshteinDistance levenshtein = new LevenshteinDistance();

        // Compute distance for each item
        List<Map.Entry<String, Integer>> distances = new ArrayList<>();
        for (String item : items) {
            int distance = levenshtein.apply(input, item);
            distances.add(new AbstractMap.SimpleEntry<>(item, distance));
        }

        // Sort by distance (ascending order)
        distances.sort(Comparator.comparingInt(Map.Entry::getValue));

        // Return top N closest matches
        List<String> closestMatches = new ArrayList<>();
        for (int i = 0; i < Math.min(topN, distances.size()); i++) {
            closestMatches.add(distances.get(i).getKey());
        }

        return closestMatches;
    }
}
