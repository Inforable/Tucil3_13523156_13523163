package algoritma;

import model.Board;
import model.Piece;
import java.util.*;

public class Djikstra {
    public static class Move {
        Board resultState;
        String description;
        public Move(Board resultState, String description) {
            this.resultState = resultState;
            this.description = description;
        }
    }

    private static boolean isGoal(Board state) {
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

    private static String getBoardKey(Board state) {
        StringBuilder sb = new StringBuilder();
        List<Character> sortedKeys = new ArrayList<>(state.pieces.keySet());
        Collections.sort(sortedKeys);
        for (char id : sortedKeys) {
            Piece p = state.pieces.get(id);
            sb.append(id).append(p.x).append(",").append(p.y).append(";");
        }
        return sb.toString();
    }

    private static List<Move> generateNextStates(Board state) {
        List<Move> nextStates = new ArrayList<>();
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
                    nextStates.add(new Move(newBoard, "Geser " + id + " ke kiri " + step));
                }
                // Geser ke kanan
                for (int step = 1; piece.x + piece.length - 1 + step < state.board[0].length; step++) {
                    int checkX = piece.x + piece.length - 1 + step;
                    int checkY = piece.y;
                    if (checkX >= state.board[0].length) break;
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
                    if (!(checkX == state.exitX && checkY == state.exitY) && state.board[checkY][checkX] != '.') break;
                    Board newBoard = state.cloneBoard();
                    newBoard.movePiece(id, step);
                    nextStates.add(new Move(newBoard, "Geser " + id + " ke bawah " + step));
                }
            }
        }
        return nextStates;
    }

    public static Board solve(Board initialState) {
        PriorityQueue<Board> openSet = new PriorityQueue<>(Comparator.comparingInt(a -> a.g));
        Set<String> visited = new HashSet<>();
        long startTime = System.currentTimeMillis();

        initialState.g = 0;
        initialState.h = 0;
        initialState.path = new ArrayList<>();
        openSet.add(initialState);

        while (!openSet.isEmpty()) {
            Board current = openSet.poll();
            if (isGoal(current)) {
                long endTime = System.currentTimeMillis();
                System.out.println("Solusi ditemukan dalam " + current.path.size() + " langkah");
                System.out.println("Waktu pencarian: " + (endTime - startTime) + " ms");
                return current;
            }
            String boardKey = getBoardKey(current);
            if (visited.contains(boardKey)) continue;
            visited.add(boardKey);
            for (Move move : generateNextStates(current)) {
                Board nextBoard = move.resultState;
                String nextKey = getBoardKey(nextBoard);
                if (!visited.contains(nextKey)) {
                    nextBoard.g = current.g + 1;
                    nextBoard.h = 0;
                    nextBoard.path = new ArrayList<>(current.path);
                    nextBoard.path.add(move.description);
                    openSet.add(nextBoard);
                }
            }
        }
        System.out.println("Tidak ada solusi");
        System.out.println("Tidak ada gerakan yang ditemukan");
        long endTime = System.currentTimeMillis();
        System.out.println("Waktu pencarian: " + (endTime - startTime) + " ms");
        return null;
    }
}
