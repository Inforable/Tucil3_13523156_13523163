package model;

public class Piece {
    public char id;
    public int x, y; // Ini koordinat dari head
    public int length;
    public boolean isHorizontal;
    public boolean isPrimary;

    // Ctor
    public Piece(char id, int x, int y, int length, boolean isHorizontal, boolean isPrimary) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.length = length;
        this.isHorizontal = isHorizontal;
        this.isPrimary = isPrimary;
    }
}