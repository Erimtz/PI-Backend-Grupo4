package com.gym.configuration;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LevenshteinDistance {
    public static int calculateDistance(String s1, String s2) {
        int[][] dp = new int[s1.length() + 1][s2.length() + 1];
        for (int i = 0; i <= s1.length(); i++) {
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0) {
                    dp[i][j] = j;
                } else if (j == 0) {
                    dp[i][j] = i;
                } else {
                    dp[i][j] = min(
                            dp[i - 1][j - 1] + costOfSubstitution(s1.charAt(i - 1), s2.charAt(j - 1)),
                            dp[i - 1][j] + 1,
                            dp[i][j - 1] + 1
                    );
                }
            }
        }
        return dp[s1.length()][s2.length()];
    }

    private static int costOfSubstitution(char a, char b) {
        return a == b ? 0 : 1;
    }

    private static int min(int... numbers) {
        return Arrays.stream(numbers).min().orElse(Integer.MAX_VALUE);
    }

    public static void main(String[] args) {
        String searchTerm = "aytomovil";
        List<String> products = Arrays.asList("automovil", "automovil", "autolovil", "auto", "camion");

        Map<String, Integer> distances = new HashMap<>();
        for (String product : products) {
            int distance = calculateDistance(searchTerm, product);
            distances.put(product, distance);
        }

        // Ordenar los productos por distancia y mostrar los más cercanos
        distances.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .limit(5)
                .forEach(entry -> System.out.println(entry.getKey() + ": " + entry.getValue()));
    }
}
