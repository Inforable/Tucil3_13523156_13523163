package algoritma;

import java.util.*;
import model.*;

public class UCS {
    public static class Node implements Comparable<Node> {
        Board state;
        int cost;
        List<String> path; // Daftar langkah yang ditempuh sejauh ini
        String lastMove;

        public Node(Board state, int cost, List<String> path, String lastMove) {
            this.state = state;
            this.cost = cost;
            this.path = new ArrayList<>(path);
            this.lastMove = lastMove;
        }

        @Override
        // Override compareTo bawaan dengan membandingkan node
        public int compareTo(Node other) {
            return Integer.compare(this.cost, other.cost);
        }
    }

    public static class Move {
        Board resultState;
        String description;

        public Move(Board resultState, String description) {
            this.resultState = resultState;
            this.description = description;
        }
    }

    public static List<String> solve(Board initialState) {
        PriorityQueue<Node> prioQueue = new PriorityQueue<>(); // Menyimpan Queue berdasarkan cost
        Set<String> visited = new HashSet<>(); // Menyimpan konfigurasi board yang telah dikunjungi
        
        prioQueue.add(new Node(initialState, 0, new ArrayList<>(), "")); // Initial state

        while (!prioQueue.isEmpty()) {
            Node current = prioQueue.poll(); // Mengambil node dengan cost terkecil
            Board state = current.state;

            if (isGoal(state)) {
                return current.path;
            }

            String key = getBoardKey(state);
            // Skip kalo uda pernah dikunjungi
            if (visited.contains(key)) {
                continue;
            }
            visited.add(key);

            // Generate semua pergerakan
            for (Move move : generateNextStates(state)) {
                List<String> newPath = new ArrayList<>(current.path);
                newPath.add(move.description);
                prioQueue.add(new Node(move.resultState, current.cost + 1, newPath, move.description));
            }
        }
        // Jika tidak ditemukan solusi
        return null;
    }

    // Mengecek state sudah mencapai exit atau belum
    public static boolean isGoal(Board state) {
        Piece p = state.pieces.get('P');
        for (int i = 0; i < p.length; i++) {
            int x = p.x + (p.isHorizontal ? i : 0);
            int y = p.y + (p.isHorizontal ? 0 : i);
            if (state.exitX == x && state.exitY == y) {
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

    // Menghasilkan semua kemungkinan gerakan
    public static List<Move> generateNextStates(Board state) {
        List<Move> nextStates = new ArrayList<>();

        for (Map.Entry<Character, Piece> entry : state.pieces.entrySet()) {
            Piece piece = entry.getValue();
            char id = entry.getKey();

            // Gerak horizontal
           if (piece.isHorizontal) {
                // ke kiri
                for (int step = 1; piece.x - step >= 0; step++) {
                    int checkX = piece.x - step;
                    int checkY = piece.y;
                    if (checkX < 0) break;

                    // Jika cell bukan '.' dan bukan exit, berhenti
                    if (!(checkX == state.exitX && checkY == state.exitY) && state.board[checkY][checkX] != '.') break;

                    Board newBoard = state.cloneBoard();
                    newBoard.movePiece(id, -step);
                    nextStates.add(new Move(newBoard, "Geser " + id + " ke kiri " + step));
                }

                // Geser ke kanan
                for (int step = 1; piece.x + piece.length - 1 + step < state.board[0].length; step++) {
                    int checkX = piece.x + piece.length - 1 + step;
                    int checkY = piece.y;
                    if (checkX >= state.board[0].length) break;

                    // Jika cell bukan '.' dan bukan exit, berhenti
                    if (!(checkX == state.exitX && checkY == state.exitY) && state.board[checkY][checkX] != '.') break;

                    Board newBoard = state.cloneBoard();
                    newBoard.movePiece(id, step);
                    nextStates.add(new Move(newBoard, "Geser " + id + " ke kanan " + step));
                }
            } else {
                // Geser ke atas
                for (int step = 1; piece.y - step >= 0; step++) {
                    int checkX = piece.x;
                    int checkY = piece.y - step;
                    if (checkY < 0) break;

                    // Jika cell bukan '.' dan bukan exit, berhenti
                    if (!(checkX == state.exitX && checkY == state.exitY) && state.board[checkY][checkX] != '.') break;

                    Board newBoard = state.cloneBoard();
                    newBoard.movePiece(id, -step);
                    nextStates.add(new Move(newBoard, "Geser " + id + " ke atas " + step));
                }

                // Geser ke bawah
                for (int step = 1; piece.y + piece.length - 1 + step < state.board.length; step++) {
                    int checkX = piece.x;
                    int checkY = piece.y + piece.length - 1 + step;
                    if (checkY >= state.board.length) break;

                    // Jika cell bukan '.' dan bukan exit, berhenti
                    if (!(checkX == state.exitX && checkY == state.exitY) && state.board[checkY][checkX] != '.') break;

                    Board newBoard = state.cloneBoard();
                    newBoard.movePiece(id, step);
                    nextStates.add(new Move(newBoard, "Geser " + id + " ke bawah " + step));
                }
            }
        }
        return nextStates;
    }
}