package algoritma;

import java.util.*;
import model.*;

public class GBFS {
    // Mengecek state sudah mencapai exit atau belum
    public static boolean isGoal(Board state) {
        Piece p = state.pieces.get('P');
        if (p == null) return false;
        for (int i = 0; i < p.length; i++) {
            int x = p.x + (p.isHorizontal ? i : 0);
            int y = p.y + (p.isHorizontal ? 0 : i);
            if (x == state.exitX && y == state.exitY) {
                return true;
            }
        }
        return false;
    }

    // Menghasilkan string unik
    public static String getBoardKey(Board state) {
        StringBuilder sb = new StringBuilder();
        List<Character> sortedKeys = new ArrayList<>(state.pieces.keySet());
        Collections.sort(sortedKeys);
        for (char id : sortedKeys) {
            Piece p = state.pieces.get(id);
            sb.append(id).append(p.x).append(",").append(p.y).append(";");
        }
        return sb.toString();
    }

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

    // Menghasilkan semua kemungkinan gerakan
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
                    nextNodes.add(new Node(newBoard, currentNode, desc, 0, 0));
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
                    nextNodes.add(new Node(newBoard, currentNode, desc, 0, 0));
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
                    nextNodes.add(new Node(newBoard, currentNode, desc, 0, 0));
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
                    nextNodes.add(new Node(newBoard, currentNode, desc, 0, 0));
                }
            }
        }
        return nextNodes;
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

            if (isGoal(current.board)) {
                long endTime = System.currentTimeMillis();
                System.out.println("Solusi ditemukan dalam " + current.g + " langkah");
                System.out.println("Waktu pencarian: " + (endTime - startTime) + " ms");
                List<Node> solution = Node.reconstructPath(current);
                return solution;
            }

            String key = getBoardKey(current.board);
            if (visited.contains(key)) continue;
            visited.add(key);

            for (Node nextNode : generateNextNodes(current)) {
                String nextKey = getBoardKey(nextNode.board);
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