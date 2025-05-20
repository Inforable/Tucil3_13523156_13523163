package algoritma;

import java.util.*;
import model.*;

public class IDA {
    public static List<Node> solve(Board initialBoard, String heuristicType) {
        long startTime = System.currentTimeMillis();
        Piece primaryPiece = initialBoard.pieces.get('P');
        int h0 = getHeuristic(initialBoard, primaryPiece, heuristicType);
        Node startNode = new Node(initialBoard, null, null, 0, h0);
        int threshold = h0;
        while (true) {
            Set<String> visited = new HashSet<>();
            Result result = search(startNode, 0, threshold, heuristicType, visited);
            if (result.found) {
                long endTime = System.currentTimeMillis();
                List<Node> solution = Node.reconstructPath(result.goalNode);
                System.out.println("Solusi ditemukan dalam " + result.goalNode.g + " langkah");
                System.out.println("Waktu pencarian: " + (endTime - startTime) + " ms");
                return solution;
            }
            if (result.minThreshold == Integer.MAX_VALUE) {
                long endTime = System.currentTimeMillis();
                System.out.println("Tidak ditemukan solusi");
                System.out.println("Waktu pencarian: " + (endTime - startTime) + " ms");
                return null;
            }
            threshold = result.minThreshold;
        }
    }

    private static Result search(Node node, int g, int threshold, String heuristicType, Set<String> visited) {
        int f = g + node.h;
        if (f > threshold) {
            return new Result(false, null, f);
        }
        if (node.board.isGoal()) {
            return new Result(true, node, f);
        }
        String key = node.board.getBoardKey();
        visited.add(key);
        int min = Integer.MAX_VALUE;
        List<Node> children = Node.generateNextNodes(node);
        for (Node child : children) {
            String childKey = child.board.getBoardKey();
            if (visited.contains(childKey)) continue;
            Piece nextPrimary = child.board.pieces.get('P');
            child.h = getHeuristic(child.board, nextPrimary, heuristicType);
            child.g = node.g + 1;

            Piece currentPrimary = node.board.pieces.get('P');
            String moveDirection = "";
            if (nextPrimary.x > currentPrimary.x) {
                moveDirection = "ke kanan " + (nextPrimary.x - currentPrimary.x);
            } else if (nextPrimary.x < currentPrimary.x) {
                moveDirection = "ke kiri " + (currentPrimary.x - nextPrimary.x);
            } else if (nextPrimary.y > currentPrimary.y) {
                moveDirection = "ke bawah " + (nextPrimary.y - currentPrimary.y);
            } else if (nextPrimary.y < currentPrimary.y) {
                moveDirection = "ke atas " + (currentPrimary.y - nextPrimary.y);
            }
            child.moveDesc = "Geser P " + moveDirection;

            Result result = search(child, g + 1, threshold, heuristicType, visited);
            if (result.found) {
                return result;
            }
            if (result.minThreshold < min) {
                min = result.minThreshold;
            }
            visited.remove(childKey);
        }
        return new Result(false, null, min);
    }

    private static int getHeuristic(Board board, Piece primaryPiece, String heuristicType) {
        if (primaryPiece == null) return 0;
        switch (heuristicType.toLowerCase()) {
            case "obstacle":
                return A.calculateObstacleHeuristic(board, primaryPiece);
            case "combined":
                return A.calculateCombinedHeuristic(board, primaryPiece);
            default:
                return A.calculateManhattanHeuristic(board, primaryPiece);
        }
    }

    private static class Result {
        boolean found;
        Node goalNode;
        int minThreshold;
        Result(boolean found, Node goalNode, int minThreshold) {
            this.found = found;
            this.goalNode = goalNode;
            this.minThreshold = minThreshold;
        }
    }
}
