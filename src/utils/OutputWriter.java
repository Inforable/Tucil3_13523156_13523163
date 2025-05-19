package utils;

public class OutputWriter {
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_WHITE_BG = "\u001B[47m";
    private static final String ANSI_BLACK = "\u001B[30m";
    private static final String ANSI_RESET = "\u001B[0m";
    public static void printBoard(char[][] board) {
        for (char[] row : board) {
            for (char cell : row) {
                if (cell == 'P') System.out.print(ANSI_RED + cell + ANSI_RESET); 
                else if (cell == 'K') System.out.print(ANSI_WHITE_BG + ANSI_BLACK + cell + ANSI_RESET); 
                else System.out.print(cell);
            }
            System.out.println();
        }
    }
}