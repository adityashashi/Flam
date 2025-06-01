package com.example.dependencychecker;

import java.util.*;

public class DependencyChecker {

    public static boolean hasCircularDependency(int n, List<int[]> edges) {
        // Build adjacency list
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            adj.add(new ArrayList<>());
        }

        for (int[] edge : edges) {
            adj.get(edge[0]).add(edge[1]);
        }

        // 0 = unvisited, 1 = visiting, 2 = visited
        int[] state = new int[n];

        for (int i = 0; i < n; i++) {
            if (state[i] == 0) {
                if (dfs(i, adj, state)) {
                    return true;
                }
            }
        }

        return false;
    }

    private static boolean dfs(int node, List<List<Integer>> adj, int[] state) {
        state[node] = 1; // Mark as visiting

        for (int neighbor : adj.get(node)) {
            if (state[neighbor] == 1) {
                // Found a cycle
                return true;
            } else if (state[neighbor] == 0) {
                if (dfs(neighbor, adj, state)) {
                    return true;
                }
            }
        }

        state[node] = 2; // Mark as visited
        return false;
    }

    // Main method to test
    public static void main(String[] args) {
        // Test case 1: No cycle
        int n1 = 4;
        List<int[]> edges1 = Arrays.asList(
                new int[]{0, 1},
                new int[]{1, 2},
                new int[]{2, 3}
        );

        System.out.println("Test Case 1 - Expected: false, Actual: " + hasCircularDependency(n1, edges1));

        // Test case 2: Cycle exists
        int n2 = 4;
        List<int[]> edges2 = Arrays.asList(
                new int[]{0, 1},
                new int[]{1, 2},
                new int[]{2, 0}
        );

        System.out.println("Test Case 2 - Expected: true, Actual: " + hasCircularDependency(n2, edges2));

        // Test case 3: Self loop
        int n3 = 3;
        List<int[]> edges3 = Arrays.asList(
                new int[]{1, 1}
        );

        System.out.println("Test Case 3 - Expected: true, Actual: " + hasCircularDependency(n3, edges3));
    }
}
