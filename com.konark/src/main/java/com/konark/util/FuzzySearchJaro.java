package com.konark.util;
import org.apache.commons.text.similarity.JaroWinklerDistance;
import java.util.*;

public class FuzzySearchJaro {
    public static void main(String[] args) {
        // Sample list of item names (Replace this with your 6000 items)
        List<String> itemNames = Arrays.asList(
            "Apple", "Applesauce", "Banana", "Orange", "Grapes", "Pineapple", "Mango", "Strawberry"
        );

        String inputItem = "Appl"; // Input to search for

        // Find the top closest matches
        List<String> closestMatches = findClosestItems(inputItem, itemNames, 5); // Get top 5 matches

        // Print results
        System.out.println("Closest matches for: " + inputItem);
        for (String match : closestMatches) {
            System.out.println(match);
        }
    }

    public static List<String> findClosestItems(String input, List<String> items, int topN) {
        JaroWinklerDistance jwd = new JaroWinklerDistance();

        // Compute similarity for each item
        List<Map.Entry<String, Double>> similarities = new ArrayList<>();
        for (String item : items) {
            double score = jwd.apply(input, item); // Compute Jaro-Winkler similarity
            similarities.add(new AbstractMap.SimpleEntry<>(item, score));
        }

        // Sort by similarity score (descending order)
        similarities.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        // Return top N closest matches
        List<String> closestMatches = new ArrayList<>();
        for (int i = 0; i < Math.min(topN, similarities.size()); i++) {
            closestMatches.add(similarities.get(i).getKey());
        }

        return closestMatches;
    }
}
