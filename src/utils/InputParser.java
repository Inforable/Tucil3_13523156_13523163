package utils;

import java.io.*;
import java.util.*;
import model.*;

public class InputParser {
    public static Board readFromFile(String path) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF-8"))) {
            // Baca baris pertama: dimensi board
            String line = br.readLine();
            if (line != null && line.startsWith("\uFEFF")) {
                line = line.substring(1);
            }

            // Ambil ukuran board dari input (jumlah baris saja yang dipakai tetap)
            String[] dimension = line.strip().split("\\s+");
            int rows = Integer.parseInt(dimension[0]);

            // Baca jumlah piece
            int numPieces = Integer.parseInt(br.readLine().strip());

            List<String> rawLines = new ArrayList<>();
            int maxCols = 0;

            // Baca baris asli (termasuk spasi), cari panjang kolom maksimal
            while ((line = br.readLine()) != null && rawLines.size() < rows) {
                line = line.replaceAll("[^A-Za-z\\.K ]", "");
                if (!line.isEmpty()) {
                    rawLines.add(line);
                    if (line.length() > maxCols) {
                        maxCols = line.length();
                    }
                }
            }

            // Inisialisasi board sesuai realita input
            char[][] board = new char[rows][maxCols];
            int exitX = -1, exitY = -1;

            for (int i = 0; i < rows; i++) {
                String rowLine = rawLines.get(i);
                for (int j = 0; j < maxCols; j++) {
                    char c = (j < rowLine.length()) ? rowLine.charAt(j) : '.';
                    board[i][j] = c;
                    if (c == 'K') {
                        exitX = j;
                        exitY = i;
                    }
                }
            }

            // Deteksi K di luar board (baris tambahan setelah rows)
            int rowOffset = rows;
            while ((line = br.readLine()) != null) {
                line = line.replaceAll("[^A-Za-z\\.K ]", "");
                for (int j = 0; j < line.length(); j++) {
                    if (line.charAt(j) == 'K') {
                        exitX = j;
                        exitY = rowOffset;
                    }
                }
                rowOffset++;
            }

            // Deteksi pieces
            Map<Character, Piece> pieces = new HashMap<>();
            Set<Character> seen = new HashSet<>();

            for (int y = 0; y < rows; y++) {
                for (int x = 0; x < maxCols; x++) {
                    char c = board[y][x];
                    if (Character.isUpperCase(c) && c != 'K' && !seen.contains(c)) {
                        seen.add(c);
                        boolean isHorizontal = (x + 1 < maxCols && board[y][x + 1] == c);
                        boolean isPrimary = (c == 'P');
                        int length = 1;

                        if (isHorizontal) {
                            int tx = x + 1;
                            while (tx < maxCols && board[y][tx] == c) {
                                length++;
                                tx++;
                            }
                        } else {
                            int ty = y + 1;
                            while (ty < rows && board[ty][x] == c) {
                                length++;
                                ty++;
                            }
                        }

                        pieces.put(c, new Piece(c, x, y, length, isHorizontal, isPrimary));
                    }
                }
            }

            // Validasi jumlah piece
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