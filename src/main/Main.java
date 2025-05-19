package main;

import algoritma.UCS;
import java.util.*;
import model.Board;
import utils.InputParser;
import utils.OutputWriter;

public class Main {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Masukkan nama file input (contohnya tc1.txt): ");
            String filename = scanner.nextLine();
            String inputPath = "test/tc/" + filename;

            Board start = InputParser.readFromFile(inputPath);
            if (start == null) {
                System.out.println("Gagal memuat papan.");
                return;
            }

            System.out.println("Papan berhasil dibaca:");
            OutputWriter.printBoard(start.board);

            int choice = 0;
            while (choice < 1 || choice > 3) {
                System.out.println("\nPilih algoritma pencarian:");
                System.out.println("1. Greedy Best First Search");
                System.out.println("2. Uniform Cost Search");
                System.out.println("3. A* Search");
                System.out.print("Masukkan pilihan (1-3): ");
                
                try {
                    choice = Integer.parseInt(scanner.nextLine());
                    if (choice < 1 || choice > 3) {
                        System.out.println("Input tidak valid! Silakan masukkan angka antara 1-3.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Input tidak valid! Silakan masukkan angka antara 1-3.");
                    choice = 0;
                }
            }

            System.out.println("\nAnda memilih algoritma: ");
            switch (choice) {
                case 1:
                    System.out.println("Greedy Best First Search");
                    // TODO: Implementasi algoritma Greedy Best First Search
                    break;
                case 2:
                    System.out.println("Uniform Cost Search");
                    List<String> solve = UCS.solve(start);
                    if (solve != null) {
                        System.out.println("Solusi ditemukan dalam " + solve.size() + " langkah:");
                        for (String langkah : solve) {
                            System.out.println("- " + langkah);
                        }
                    } else {
                        System.out.println("Tidak ditemukan solusi");
                    }
                    break;
                case 3:
                    System.out.println("A* Search");
                    // a.solve(start);
                    // break;
            }
        }
    }
}