package model;

import java.util.*;

public class Board {
    public char[][] board;
    public Map<Character, Piece> pieces;
    public int exitX, exitY;
    public List<String> path; // Placeholder
    public int g, h;

    // Ctor
    public Board(char[][] board, Map<Character, Piece> pieces, int exitX, int exitY) {
        this.board = board;
        this.pieces = pieces;
        this.exitX = exitX;
        this.exitY = exitY;
        this.path = new ArrayList<>();
        this.g = 0;
        this.h = 0;
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

        // Hapus dari posisi lama
        if (piece.isHorizontal) {
            for (int i = 0; i < piece.length; i++) {
                board[piece.y][piece.x + i] = ' ';
            }
            // Update posisi
            piece.x += delta;
            for (int i = 0; i < piece.length; i++) {
                board[piece.y][piece.x + i] = piece.id;
            }
        } else {
            for (int i = 0; i < piece.length; i++) {
                board[piece.y + i][piece.x] = ' ';
            }
            piece.y += delta;
            for (int i = 0; i < piece.length; i++) {
                board[piece.y + i][piece.x] = piece.id;
            }
        }
    }
}