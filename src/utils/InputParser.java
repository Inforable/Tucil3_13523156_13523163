package utils;

import java.io.*;
import java.util.*;
import model.*;

public class InputParser {
    public static Board readFromFile(String path) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF-8"))) {
            String line = br.readLine();
            if (line != null && line.startsWith("\uFEFF")) {
                line = line.substring(1); 
            }

            String[] dimensi = line.strip().split("\\s+");
            int rows = Integer.parseInt(dimensi[0]);
            int cols = Integer.parseInt(dimensi[1]);

            int numPieces = Integer.parseInt(br.readLine().strip());

            char[][] board = new char[rows][cols];
            for (int i = 0; i < rows; i++) {
                line = br.readLine();
                if (line == null) line = "";
                line = line.replaceAll("[^A-Za-z\\.K]", "");

                // Jika panjang = cols + 1 dan mengandung K
                if (line.length() == cols + 1 && line.contains("K")) {
                    line = line.substring(0, cols); // ambil kolom pertama saja
                }

                // Jika panjang kurang dari cols, isi dengan titik
                if (line.length() < cols) {
                    line = String.format("%-" + cols + "s", line).replace(' ', '.');
                }

                char[] row = new char[cols];
                for (int j = 0; j < cols; j++) {
                    row[j] = (j < line.length()) ? line.charAt(j) : '.';
                }

                board[i] = row;
            }

            Map<Character, Piece> pieces = new HashMap<>();
            Set<Character> seen = new HashSet<>();

            for (int y = 0; y < rows; y++) {
                for (int x = 0; x < cols; x++) {
                    char ch = board[y][x];
                    if (ch != '.' && ch != 'K' && !seen.contains(ch)) {
                        seen.add(ch);
                        boolean isHorizontal = (x + 1 < cols && board[y][x + 1] == ch);
                        boolean isPrimary = (ch == 'P');
                        int length = 1;

                        if (isHorizontal) {
                            int tx = x + 1;
                            while (tx < cols && board[y][tx] == ch) {
                                length++;
                                tx++;
                            }
                        } else {
                            int ty = y + 1;
                            while (ty < rows && board[ty][x] == ch) {
                                length++;
                                ty++;
                            }
                        }

                        pieces.put(ch, new Piece(ch, x, y, length, isHorizontal, isPrimary));
                    }
                }
            }

            return new Board(board, pieces);

        } catch (IOException | NumberFormatException e) {
            System.out.println("Gagal membaca file input: " + e.getMessage());
            return null;
        }
    }
}