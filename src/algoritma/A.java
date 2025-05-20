package algoritma;

import java.util.*;
import model.*;

public class A {
    private static List<Node> generateNextNodes(Node currentNode) {
        Board state = currentNode.board;
        List<Node> nextNodes = new ArrayList<>();
        for (Map.Entry<Character, Piece> entry : state.pieces.entrySet()) {
            Piece piece = entry.getValue();
            char id = entry.getKey();
            if (piece.isHorizontal) {
                // Geser ke kiri
                for (int step = 1; piece.x - step >= 0; step++) {
                    int checkX = piece.x - step;
                    int checkY = piece.y;
                    if (checkX < 0) break;
                    if (!(checkX == state.exitX && checkY == state.exitY) && state.board[checkY][checkX] != '.') break;
                    Board newBoard = state.cloneBoard();
                    newBoard.movePiece(id, -step);
                    String desc = "Geser " + id + " ke kiri " + step;
                    Node nextNode = new Node(newBoard, currentNode, desc, currentNode.g + 1, 0);
                    nextNodes.add(nextNode);
                }
                // Geser ke kanan
                for (int step = 1; piece.x + piece.length - 1 + step < state.board[0].length; step++) {
                    int checkX = piece.x + piece.length - 1 + step;
                    int checkY = piece.y;
                    if (checkX >= state.board[0].length) break;
                    if (!(checkX == state.exitX && checkY == state.exitY) && state.board[checkY][checkX] != '.') break;
                    Board newBoard = state.cloneBoard();
                    newBoard.movePiece(id, step);
                    String desc = "Geser " + id + " ke kanan " + step;
                    Node nextNode = new Node(newBoard, currentNode, desc, currentNode.g + 1, 0);
                    nextNodes.add(nextNode);
                }
            } else {
                // Geser ke atas
                for (int step = 1; piece.y - step >= 0; step++) {
                    int checkX = piece.x;
                    int checkY = piece.y - step;
                    if (checkY < 0) break;
                    if (!(checkX == state.exitX && checkY == state.exitY) && state.board[checkY][checkX] != '.') break;
                    Board newBoard = state.cloneBoard();
                    newBoard.movePiece(id, -step);
                    String desc = "Geser " + id + " ke atas " + step;
                    Node nextNode = new Node(newBoard, currentNode, desc, currentNode.g + 1, 0);
                    nextNodes.add(nextNode);
                }
                // Geser ke bawah
                for (int step = 1; piece.y + piece.length - 1 + step < state.board.length; step++) {
                    int checkX = piece.x;
                    int checkY = piece.y + piece.length - 1 + step;
                    if (checkY >= state.board.length) break;
                    if (!(checkX == state.exitX && checkY == state.exitY) && state.board[checkY][checkX] != '.') break;
                    Board newBoard = state.cloneBoard();
                    newBoard.movePiece(id, step);
                    String desc = "Geser " + id + " ke bawah " + step;
                    Node nextNode = new Node(newBoard, currentNode, desc, currentNode.g + 1, 0);
                    nextNodes.add(nextNode);
                }
            }
        }
        return nextNodes;
    }

    // Hitung jumlah penghalang untuk Heuristik Obstacle
    public static int calculateObstacleHeuristic(Board board, Piece primaryPiece) {
        int obstacles = 0;
        int targetX = board.exitX;
        int primaryX = primaryPiece.x;
        int primaryY = primaryPiece.y;
        for (Piece piece : board.pieces.values()) {
            if (piece != primaryPiece && piece.isHorizontal && piece.y == primaryY) {
                if ((primaryX < targetX && piece.x > primaryX && piece.x < targetX) ||
                    (primaryX > targetX && piece.x < primaryX && piece.x + piece.length > targetX)) {
                    obstacles++;
                }
            } else if (piece != primaryPiece && !piece.isHorizontal) {
                if (piece.x >= Math.min(primaryX, targetX) && piece.x <= Math.max(primaryX, targetX) && piece.y <= primaryY && piece.y + piece.length > primaryY) {
                    obstacles++;
                }
            }
        }
        return obstacles;
    }

    // Hitung jarak Manhattan untuk Heuristik Manhattan Distance
    public static int calculateManhattanHeuristic(Board board, Piece primaryPiece) {
        int horizontalDistance = Math.abs(primaryPiece.x - board.exitX);
        int verticalDistance = Math.abs(primaryPiece.y - board.exitY) / 2;
        return horizontalDistance + verticalDistance;
    }
    
    // Hitung kombinasi dari Heuristik Obstacle dan Manhattan Distance
    public static int calculateCombinedHeuristic(Board board, Piece primaryPiece) {
        int obstacleCount = calculateObstacleHeuristic(board, primaryPiece);
        int manhattanDistance = calculateManhattanHeuristic(board, primaryPiece);
        return obstacleCount + (manhattanDistance * 2);
    }

    public static List<Node> solve(Board initialBoard, String heuristicType) {
        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingInt(n -> n.g + n.h));
        Set<String> visited = new HashSet<>();
        long startTime = System.currentTimeMillis();

        Piece primaryPiece = initialBoard.pieces.get('P');
        int h0 = 0;
        h0 = switch (heuristicType.toLowerCase()) {
            case "obstacle" -> calculateObstacleHeuristic(initialBoard, primaryPiece);
            case "combined" -> calculateCombinedHeuristic(initialBoard, primaryPiece);
            default -> calculateManhattanHeuristic(initialBoard, primaryPiece);
        };
        
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

            for (Node nextNode : generateNextNodes(current)) {
                String nextKey = nextNode.board.getBoardKey();
                if (!visited.contains(nextKey)) {
                    Piece nextPrimary = nextNode.board.pieces.get('P');
                    int hNext = 0;
                    hNext = switch (heuristicType.toLowerCase()) {
                        case "obstacle" -> calculateObstacleHeuristic(nextNode.board, nextPrimary);
                        case "combined" -> calculateCombinedHeuristic(nextNode.board, nextPrimary);
                        default -> calculateManhattanHeuristic(nextNode.board, nextPrimary);
                    };
                    nextNode.h = hNext;
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
