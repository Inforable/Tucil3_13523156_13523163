package algoritma;

import java.util.*;
import model.*;

public class GBFS {
    // Menghitung nilai heuristik
    public static int calculateHeuristic(Board state, String type) {
        if ("manhattan".equalsIgnoreCase(type)) {
            return heuristicManhattan(state);
        } else if ("blockingcars".equalsIgnoreCase(type)) {
            return heuristicBlockers(state);
        } else {
            throw new IllegalArgumentException("Unknown heuristic type: " + type);
        }
    }

        // Jika memilih heuristik jarak terdekat ke exit (Manhattan)
    public static int heuristicManhattan(Board state) {
        Piece primary = state.pieces.get('P');
        if (primary == null) return 0;
        if (primary.isHorizontal) {
            return Math.abs(state.exitX - (primary.x + primary.length - 1));
        } else {
            return Math.abs(state.exitY - (primary.y + primary.length - 1));
        }
    }

    // Jika memilih heuristik banyak pieces yang menghalangi
    public static int heuristicBlockers(Board state) {
        Piece primary = state.pieces.get('P');
        if (primary == null) return 0;

        int blockers = 0;
        if (primary.isHorizontal) {
            int y = primary.y;
            for (int x = primary.x + primary.length; x <= state.exitX; x++) {
                char cell = state.board[y][x];
                if (cell != '.' && cell != primary.id) {
                    blockers++;
                }
            }
        } else {
            int x = primary.x;
            for (int y = primary.y + primary.length; y <= state.exitY; y++) {
                char cell = state.board[y][x];
                if (cell != '.' && cell != primary.id) {
                    blockers++;
                }
            }
        }
        return blockers;
    }

    public static List<Node> solve(Board initialBoard, String heuristicType) {
        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingInt(n -> n.h));
        Set<String> visited = new HashSet<>();
        long startTime = System.currentTimeMillis();

        int h0 = calculateHeuristic(initialBoard, heuristicType);
        Node startNode = new Node(initialBoard, null, null, 0, h0);
        openSet.add(startNode);

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();

            if (current.board.isGoal()) {
                long endTime = System.currentTimeMillis();
                System.out.println("Solusi ditemukan dalam " + current.g + " langkah");
                System.out.println("Waktu pencarian: " + (endTime - startTime) + " ms");
                List<Node> solution = Node.reconstructPath(current);
                return solution;
            }

            String key = current.board.getBoardKey();
            if (visited.contains(key)) continue;
            visited.add(key);

            List<Node> nextNodes = Node.generateNextNodes(current);
            for (Node nextNode : nextNodes) {
                String nextKey = nextNode.board.getBoardKey();
                if (!visited.contains(nextKey)) {
                    int hNext = calculateHeuristic(nextNode.board, heuristicType);
                    nextNode.h = hNext;
                    nextNode.g = current.g + 1; // g untuk langkah aja bukan sebagai perhitungan
                    openSet.add(nextNode);
                }
            }
        }

        System.out.println("Tidak ada solusi");
        long endTime = System.currentTimeMillis();
        System.out.println("Waktu pencarian: " + (endTime - startTime) + " ms");
        return null;
    }
}