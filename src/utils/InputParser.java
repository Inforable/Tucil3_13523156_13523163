// ===== File: InputParser.java =====
package utils;

import java.io.*;
import java.util.*;
import model.*;

public class InputParser {
    public static Board readFromFile(String path) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF-8"))) {
            // Membaca dimensi
            String line = br.readLine();
            if (line != null && line.startsWith("\uFEFF")) {
                line = line.substring(1);
            }
            if (line == null) {
                System.out.println("Input invalid");
                return null;
            }
            line = line.startsWith("\uFEFF") ? line.substring(1) : line;
            String[] dimension = line.strip().split("\\s+");
            int rows = Integer.parseInt(dimension[0]); // rows dari board
            int cols = Integer.parseInt(dimension[1]); // cols dari board

            // Membaca jumlah dari pieces
            int numPieces = Integer.parseInt(br.readLine().strip());

            List<String> lines = new ArrayList<>();
            while ((line = br.readLine()) != null) {
                line = line.replaceAll("[^A-Za-z\\.K ]", ""); // Filter karakter yang valid
                lines.add(line);
            }

            int rawRows = lines.size();
            int maxCol = 0;
            int exitX = -1, exitY = -1; // Posisi K

            // Hitung kolom maksimum berdasarkan karakter piece (bukan K)
            for (int i = 0; i < rawRows; i++) {
                String row = lines.get(i);
                for (int j = 0; j < row.length(); j++) {
                    char c = row.charAt(j);
                    if (Character.isUpperCase(c) && c != 'K') {
                        maxCol = Math.max(maxCol, j);
                    }
                    if (c == 'K') {
                        exitX = j;
                        exitY = i;
                    }
                }
            }

            // Ukuran board awal
            int boardRows = Math.max(rows, rawRows);
            int boardCols = Math.max(cols, maxCol + 1);

            // Periksa apakah perlu ekspansi ke bawah/kanan
            boolean expandBottom = (exitY >= rows && exitY >= rawRows);
            boolean expandRight = (exitX >= cols && exitX >= maxCol + 1);

            int offsetY = 0;
            int offsetX = 0;

            int finalRows = boardRows + (expandBottom ? 1 : 0);
            int finalCols = boardCols + (expandRight ? 1 : 0);

            char[][] board = new char[finalRows][finalCols]; // Inisialisasi board
            for (char[] row : board) Arrays.fill(row, '.');

            // Menyalin karakter dari input ke board, kecuali K
            for (int i = 0; i < rawRows; i++) {
                String row = lines.get(i);
                for (int j = 0; j < row.length(); j++) {
                    char c = row.charAt(j);
                    if (c != 'K') {
                        int y = i + offsetY;
                        int x = j + offsetX;
                        if (y < finalRows && x < finalCols) {
                            board[y][x] = c;
                        }
                    }
                }
            }

            // Menempatkan K
            if (exitX != -1 && exitY != -1) {
                int kRow = exitY + offsetY;
                int kCol = exitX + offsetX;
                if (kRow < finalRows && kCol < finalCols) {
                    board[kRow][kCol] = 'K';
                    exitY = kRow;
                    exitX = kCol;

                    for (int i = 0; i < finalRows; i++) {
                        boolean diLuarBoard = (i >= rows || kCol >= cols);
                        boolean sisaBarisInput = (i - offsetY < lines.size() && kCol >= lines.get(i - offsetY).length());
                        if (board[i][kCol] == '.' && (diLuarBoard || sisaBarisInput)) {
                            board[i][kCol] = ' ';
                        }
                    }
                    for (int j = 0; j < finalCols; j++) {
                        boolean diLuarBoard = (j >= cols);
                        boolean sisaKolomInput = (kRow - offsetY < lines.size() && j >= lines.get(kRow - offsetY).length());
                        if (board[kRow][j] == '.' && (diLuarBoard || sisaKolomInput)) {
                            board[kRow][j] = ' ';
                        }
                    }
                }
            }

            Map<Character, Piece> pieces = new HashMap<>();
            Set<Character> seen = new HashSet<>();

            // Deteksi dan konstruksi setiap piece
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

            return new Board(board, pieces, exitX, exitY); // Kembalikan hasil akhir board

        } catch (IOException | NumberFormatException e) {
            System.out.println("Gagal membaca file input " + e.getMessage());
            return null;
        }
    }
}