package model;

import java.util.*;

public class Board {
    public char[][] board;
    public Map<Character, Piece> pieces;
    public int exitX, exitY;

    // Ctor
    public Board(char[][] board, Map<Character, Piece> pieces, int exitX, int exitY) {
        this.board = board;
        this.pieces = (pieces != null) ? pieces : new HashMap<>();
        this.exitX = exitX;
        this.exitY = exitY;
    }

    // Mengecek state sudah mencapai exit atau belum
    public boolean isGoal() {
        Piece p = this.pieces.get('P');
        for (int i = 0; i < p.length; i++) {
            int x = p.x + (p.isHorizontal ? i : 0);
            int y = p.y + (p.isHorizontal ? 0 : i);
            if (this.exitX == x && this.exitY == y) {
                return true;
            }
        }
        return false;
    }

    // Menghasilkan string unik
    public String getBoardKey() {
        StringBuilder sb = new StringBuilder();
        List<Character> sortedKeys = new ArrayList<>(this.pieces.keySet());
        Collections.sort(sortedKeys);
        for (char id : sortedKeys) {
            Piece p = this.pieces.get(id);
            sb.append(id).append(p.x).append(",").append(p.y).append(";");
        }
        return sb.toString();
    }

    // deep copy
    public Board cloneBoard() {
        int rows = board.length;
        int cols = board[0].length;

        char[][] newBoard = new char[rows][cols];
        for (int i = 0; i < rows; i++) {
            newBoard[i] = Arrays.copyOf(board[i], cols);
        }

        Map<Character, Piece> newPieces = new HashMap<>();
        if (pieces == null) {
            throw new IllegalStateException("Pieces ini bernilai null");
        }
        for (Map.Entry<Character, Piece> entry : pieces.entrySet()) {
            Piece p = entry.getValue();
            newPieces.put(entry.getKey(), new Piece(p.id, p.x, p.y, p.length, p.isHorizontal, p.isPrimary));
        }
        return new Board(newBoard, newPieces, exitX, exitY);
    }

    // Menggeser piece dengan id tertentu
    public void movePiece(char id, int delta) {
        Piece piece = pieces.get(id);
        if (piece == null) return;

        int newX = piece.x + (piece.isHorizontal ? delta : 0);
        int newY = piece.y + (piece.isHorizontal? 0 : delta);

        // validasi posisi terhadap board
        if (newX < 0 || newY < 0 || piece.isHorizontal && newX + piece.length - 1 >= board[0].length || (!piece.isHorizontal && newY + piece.length - 1 >= board.length)) {
            return;
        }

        // Hapus dari posisi lama
        if (piece.isHorizontal) {
            for (int i = 0; i < piece.length; i++) {
                board[piece.y][piece.x + i] = '.';
            }
            // Update posisi
            piece.x += delta;
            for (int i = 0; i < piece.length; i++) {
                board[piece.y][piece.x + i] = piece.id;
            }
        } else {
            for (int i = 0; i < piece.length; i++) {
                board[piece.y + i][piece.x] = '.';
            }
            piece.y += delta;
            for (int i = 0; i < piece.length; i++) {
                board[piece.y + i][piece.x] = piece.id;
            }
        }
    }
}