package algoritma;

import java.util.*;
import model.*;

public class GBFS {
    // Class move untuk menyimpan state hasil
    public static class Move {
        Board resultState;
        String description;

        public Move(Board resultState, String description) {
            this.resultState = resultState;
            this.description = description;
        }
    }

    public static List<String> solve(Board initialState, String heuristicType) {
        PriorityQueue<Board> prioQueue = new PriorityQueue<>(Comparator.comparingInt(b -> b.h)); // prioQueue based on dengan nilai heuristik
        Set<String> visited = new HashSet<>(); // Menyimpan konfigurasi board yang telah dikunjungi

        initialState.h = calculateHeuristic(initialState, heuristicType);
        prioQueue.add(initialState); // Initial state

        while (!prioQueue.isEmpty()) {
            Board current = prioQueue.poll(); // Mengambil node dengan nilai heuristik terkecil

            if (isGoal(current)) {
                return current.path;
            }

            String key = getBoardKey(current);
            // Skip kalo uda pernah dikunjungi
            if (visited.contains(key)) {
                continue;
            }
            visited.add(key);

            // Generate semua pergerakan
            for (Move move : generateNextStates(current)) {
                Board newState = move.resultState;
                newState.h = calculateHeuristic(newState, heuristicType);
                newState.path = new ArrayList<>(current.path);
                newState.path.add(move.description);
                prioQueue.add(newState);
            }
        }

        // Jika tidak ditemukan solusi
        return null;
    }

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