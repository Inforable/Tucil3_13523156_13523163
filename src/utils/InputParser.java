package utils;

import java.io.*;
import java.util.*;
import model.*;

public class InputParser {
    public static Board readFromFile(String path) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF-8"))) {
            // Membaca dimensi board
            String line = br.readLine();
            if (line == null) {
                System.out.println("Input invalid, file kosong");
                return null;
            }
            String[] dimension = line.strip().split("\\s+");
            int rawRows = Integer.parseInt(dimension[0]);
            int rawCols = Integer.parseInt(dimension[1]);

            // Membaca jumlah pieces
            String pieceLine = br.readLine();
            int numPieces;
            try {
                numPieces = Integer.parseInt(pieceLine.strip());
            } catch (NumberFormatException e) {
                System.out.println("Jumlah piece harus berupa integer");
                return null;
            }

            // Membaca konfigurasi pieces
            List<String> lines = new ArrayList<>();
            int maxLineLength = 0;
            while ((line = br.readLine()) != null) {
                if (!line.matches("[A-Za-z\\.K ]*")) {
                    System.out.println("Konfigurasi Piece ada yang tidak valid");
                    return null;
                }
                lines.add(line);
                maxLineLength = Math.max(maxLineLength, line.length());
            }

            int exitX = -1, exitY = -1; // Posisi default K
            for (int i = 0; i < lines.size(); i++) {
                String row = lines.get(i);
                for (int j = 0; j < row.length(); j++) {
                    if (row.charAt(j) == 'K') {
                        exitX = j;
                        exitY = i;
                    }
                }
            }

            if (exitX == -1 || exitY == -1) {
                System.out.println("Tidak ditemukan jalur keluar (K)");
                return null;
            }

            int finalRows = Math.max(rawRows, lines.size());
            int finalCols = Math.max(rawCols, maxLineLength);

            char[][] board = new char[finalRows][finalCols];
            for (int i = 0; i < finalRows; i++) {
                for (int j = 0; j < finalCols; j++) {
                    board[i][j] = ' '; // Yang sejajar dengan K dijadiin space aja
                }
            }

            for (int i = 0; i < lines.size(); i++) {
                String row = lines.get(i);
                for (int j = 0; j < row.length(); j++) {
                    board[i][j] = row.charAt(j);
                }
            }


            Map<Character, Piece> pieces = new HashMap<>();
            Set<Character> seen = new HashSet<>();

            for (int y = 0; y < finalRows; y++) {
                for (int x = 0; x < finalCols; x++) {
                    char c = board[y][x];
                    if (Character.isUpperCase(c) && c != 'K' && !seen.contains(c)) {
                        seen.add(c);
                        boolean isHorizontal = (x + 1 < finalCols && board[y][x + 1] == c);
                        boolean isPrimary = (c == 'P');
                        int length = 1;

                        if (isHorizontal) {
                            int tx = x + 1;
                            while (tx < finalCols && board[y][tx] == c) {
                                length++;
                                tx++;
                            }
                        } else {
                            int ty = y + 1;
                            while (ty < finalRows && board[ty][x] == c) {
                                length++;
                                ty++;
                            }
                        }
                        pieces.put(c, new Piece(c, x, y, length, isHorizontal, isPrimary));
                    }
                }
            }

            int count = 0;
            for (char c : pieces.keySet()) {
                if (c != 'P') count++;
            }
            if (count != numPieces) {
                System.out.println("Jumlah piece tidak sesuai dengan input");
                return null;
            }

            return new Board(board, pieces, exitX, exitY);

        } catch (IOException | NumberFormatException e) {
            System.out.println("Gagal membaca file input " + e.getMessage());
            return null;
        }
    }
}