package com.konark.util;

import java.util.*;

import com.konark.entity.InventoryEntity;
import com.konark.entity.VendorItemEntity;

public class LevenshteinItemSearch {

    public static List<InventoryEntity> findSimilarItemsByLevenshtein(String selectedName, List<InventoryEntity> allItemNames) {
        List<InventoryEntity> similarItems = new ArrayList<>();
//        String selectedName = selectedItem.getName();  // Keep original for tokenization
        List<String> selectedTokens = StringUtils.tokenize(selectedName);
        
        for (InventoryEntity inventoryItem : allItemNames) {
        	String itemName = inventoryItem.getItemName();
            List<String> itemTokens = StringUtils.tokenize(itemName);

            // Calculate the Levenshtein distance between the two tokenized item names
            int totalDistance = 0;
            for (String selectedToken : selectedTokens) {
                int minDistance = Integer.MAX_VALUE;
                for (String itemToken : itemTokens) {
                    int distance = StringUtils.levenshteinDistance(selectedToken, itemToken);
                    minDistance = Math.min(minDistance, distance);
                }
                totalDistance += minDistance;
            }

            // Set a threshold for similarity (adjust as needed)
            if (totalDistance <= 5
              //  && !selectedName.equals(itemName)
            ) {  // Avoid returning the selected item itself
                similarItems.add(inventoryItem);
            }
        }        
        return similarItems;
    }
    
    public static List<VendorItemEntity> findAlternateItemsByLevenshtein(String selectedName, List<VendorItemEntity> vendorItems) {
        List<VendorItemEntity> alternateVendorItems = new ArrayList<>();
//        String selectedName = selectedItem.getName();  // Keep original for tokenization
        List<String> selectedTokens = StringUtils.tokenize(selectedName);
        
        for (VendorItemEntity vendorItem : vendorItems) {
        	String itemName = vendorItem.getItemName();
            List<String> itemTokens = StringUtils.tokenize(itemName);

            // Calculate the Levenshtein distance between the two tokenized item names
            int totalDistance = 0;
            for (String selectedToken : selectedTokens) {
                int minDistance = Integer.MAX_VALUE;
                for (String itemToken : itemTokens) {
                    int distance = StringUtils.levenshteinDistance(selectedToken, itemToken);
                    minDistance = Math.min(minDistance, distance);
                }
                totalDistance += minDistance;
            }

            // Set a threshold for similarity (adjust as needed)
            if (totalDistance <= 5 && !selectedName.equals(itemName)) {  // Avoid returning the selected item itself
                alternateVendorItems.add(vendorItem);
            }
        }        
        return alternateVendorItems;
    }
    
//    public static List<String> findSimilarItemsByLevenshtein(String selectedName, List<String> allItems) {
//        List<String> similarItems = new ArrayList<>();
////        String selectedName = selectedItem.getName();  // Keep original for tokenization
//        List<String> selectedTokens = StringUtils.tokenize(selectedName);
//        
//        for (String itemName : allItems) {
//            List<String> itemTokens = StringUtils.tokenize(itemName);
//
//            // Calculate the Levenshtein distance between the two tokenized item names
//            int totalDistance = 0;
//            for (String selectedToken : selectedTokens) {
//                int minDistance = Integer.MAX_VALUE;
//                for (String itemToken : itemTokens) {
//                    int distance = StringUtils.levenshteinDistance(selectedToken, itemToken);
//                    minDistance = Math.min(minDistance, distance);
//                }
//                totalDistance += minDistance;
//            }
//
//            // Set a threshold for similarity (adjust as needed)
//            if (totalDistance <= 5 && !selectedName.equals(itemName)) {  // Avoid returning the selected item itself
//                similarItems.add(itemName);
//            }
//        }
//        
//        return similarItems;
//    }
}
