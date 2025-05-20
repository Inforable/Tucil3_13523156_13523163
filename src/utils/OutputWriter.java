package utils;

import java.awt.Point;
import java.io.*;
import java.util.*;
import model.*;

public class OutputWriter {
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_WHITE_BG = "\u001B[47m";
    private static final String ANSI_BLACK = "\u001B[30m";
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_YELLOW_BG = "\u001B[43m";

    // Print board biasa
    public static void printBoard(char[][] board) {
        for (char[] row : board) {
            for (char cell : row) {
                switch (cell) {
                    case 'P' -> System.out.print(ANSI_RED + cell + ANSI_RESET);
                    case 'K' -> System.out.print(ANSI_WHITE_BG + ANSI_BLACK + cell + ANSI_RESET);
                    default -> System.out.print(cell);
                }
            }
            System.out.println();
        }
    }

    // Mengambil posisi dari semua piece
    public static Map<Character, Set<Point>> getPiecePositions(char[][] board) {
        Map<Character, Set<Point>> positions = new HashMap<>();
        for (int y = 0; y < board.length; y++) {
            for (int x = 0; x < board[y].length; x++) {
                char c = board[y][x];
                if (c != '.') {
                    positions.computeIfAbsent(c, k -> new HashSet<>()).add(new Point(x, y));
                }
            }
        }
        return positions;
    }

    // Print board ketika ada perubahan
    public static void printStepBoard(char[][] boardNow, char[][] boardPrev) {
        Set<Point> changedPositions = new HashSet<>();

        if (boardPrev != null) {
            Map<Character, Set<Point>> prevPositions = getPiecePositions(boardPrev);
            Map<Character, Set<Point>> nowPositions = getPiecePositions(boardNow);

            // Bandingkan posisi piece per karakter untuk cari perubahan posisi
            for (char pieceId : nowPositions.keySet()) {
                Set<Point> oldSet = prevPositions.getOrDefault(pieceId, Collections.emptySet());
                Set<Point> newSet = nowPositions.get(pieceId);

                for (Point p : oldSet) {
                    if (!newSet.contains(p)) changedPositions.add(p);
                }

                for (Point p : newSet) {
                    if (!oldSet.contains(p)) changedPositions.add(p);
                }
            }
        }

        // Mencetak dengan highlight background kuning untuk posisi yang berubah
        for (int y = 0; y < boardNow.length; y++) {
            for (int x = 0; x < boardNow[y].length; x++) {
                char nowCell = boardNow[y][x];
                Point pos = new Point(x, y);

                boolean changed = changedPositions.contains(pos);

                if (changed) {
                    switch (nowCell) {
                        case 'P' -> System.out.print(ANSI_YELLOW_BG + ANSI_RED + nowCell + ANSI_RESET);
                        case 'K' -> System.out.print(ANSI_YELLOW_BG + ANSI_BLACK + nowCell + ANSI_RESET);
                        default -> System.out.print(ANSI_YELLOW_BG + nowCell + ANSI_RESET);
                    }
                } else {
                    switch (nowCell) {
                        case 'P' ->  System.out.print(ANSI_RED + nowCell + ANSI_RESET);
                        case 'K' ->  System.out.print(ANSI_WHITE_BG + ANSI_BLACK + nowCell + ANSI_RESET);
                        default ->  System.out.print(nowCell);
                    }
                }
            }
            System.out.println();
        }
    }

    // Mengubah board menjadi string yang bisa disimpan di file
    public static String boardToString(char[][] board) {
        StringBuilder sb = new StringBuilder();
        for (char[] row : board) {
            for (char cell : row) {
                sb.append(cell);
            }
            sb.append(System.lineSeparator());
        }
        return sb.toString();
    }

    // Menyimpan list of lines ke file txt
    public static void saveLinesToFile(String filepath, List<String> lines) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filepath))) {
            for (String line : lines) {
                writer.println(line);
            }
            System.out.println("Output berhasil disimpan ke " + filepath);
        } catch (IOException e) {
            System.err.println("Gagal menyimpan file: " + e.getMessage());
        }
    }

    // Menyimpan board ke file
    public static void saveBoardToFile(String filepath, char[][] board) {
        String boardString = boardToString(board);
        try (PrintWriter writer = new PrintWriter(new FileWriter(filepath))) {
            writer.print(boardString);
            System.out.println("Board berhasil disimpan ke " + filepath);
        } catch (IOException e) {
            System.err.println("Gagal menyimpan board ke file: " + e.getMessage());
        }
    }

    public static void printSolutionPath(List<Node> path) {
        if (path == null) {
            System.out.println("Tidak ditemukan solusi");
            return;
        }

        System.out.println("Papan awal:");
        printBoard(path.get(0).board.board);

        for (int i = 1; i < path.size(); i++) {
            Node node = path.get(i);
            System.out.println("\nLangkah " + i + ": " + node.moveDesc);
            printStepBoard(node.board.board, path.get(i - 1).board.board);
        }
    }

    public static void saveSolutionPath(List<Node> path, String filePath) {
        if (path == null) {
            System.err.println("Tidak ada solusi untuk disimpan.");
            return;
        }

        List<String> outputLines = new ArrayList<>();
        outputLines.add("Solusi ditemukan dalam " + (path.size() - 1) + " langkah:");
        outputLines.add("Papan awal:");
        outputLines.add(boardToString(path.get(0).board.board));

        for (int i = 1; i < path.size(); i++) {
            Node node = path.get(i);
            outputLines.add("");
            outputLines.add("Langkah " + i + ": " + node.moveDesc);
            outputLines.add(boardToString(node.board.board));
        }

        saveLinesToFile(filePath, outputLines);
    }
}