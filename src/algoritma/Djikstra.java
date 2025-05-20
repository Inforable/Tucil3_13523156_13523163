package algoritma;

import java.util.*;
import model.*;

public class Djikstra {
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

    public static List<Node> solve(Board initialBoard) {
        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingInt(n -> n.g));
        Set<String> visited = new HashSet<>();
        long startTime = System.currentTimeMillis();

        Node startNode = new Node(initialBoard, null, null, 0, 0);
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
