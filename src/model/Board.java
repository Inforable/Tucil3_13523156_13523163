package model;

import java.util.*;

public class Board {
    public char[][] board;
    public Map<Character, Piece> pieces;
    public List<String> path; // Placeholder
    public int g, h;

    // Ctor
    public Board(char[][] board, Map<Character, Piece> pieces) {
        this.board = board;
        this.pieces = pieces;
        this.path = new ArrayList<>();
        this.g = 0;
        this.h = 0;
    }
}