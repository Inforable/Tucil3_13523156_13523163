package utils;

public class OutputWriter {
    public static void printBoard(char[][] board) {
        for (char[] row : board) {
            for (char cell : row) {
                System.out.print(cell);
            }
            System.out.println();
        }
    }
}