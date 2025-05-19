package main;

import algoritma.UCS;
import algoritma.A;

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
                    System.out.println("1. Manhattan Distance Heuristic");
                    System.out.println("2. Obstacle Heuristic");
                    System.out.println("3. Combined Heuristic");
                    System.out.print("Masukkan pilihan (1-3): ");
                    int heuristicChoice = 0;
                    while (heuristicChoice < 1 || heuristicChoice > 3) {
                        try {
                            heuristicChoice = Integer.parseInt(scanner.nextLine());
                            if (heuristicChoice < 1 || heuristicChoice > 3) {
                                System.out.println("Input tidak valid! Silakan masukkan angka antara 1-3.");
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Input tidak valid! Silakan masukkan angka antara 1-3.");
                            heuristicChoice = 0;
                        }
                    }
                    System.out.println("\nAnda memilih heuristik: ");                    switch (heuristicChoice) {
                        case 1:
                            System.out.println("Manhattan Distance Heuristic");
                            Board solution = A.solve(start, "manhattan");
                            if (solution != null) {
                                System.out.println("\nFinal board state:");
                                OutputWriter.printBoard(solution.board);
                                if (solution.path != null && !solution.path.isEmpty()) {
                                    System.out.println("\nSolution steps:");
                                    for (String step : solution.path) {
                                        System.out.println("- " + step);
                                    }
                                }
                            }
                            break;
                        case 2:
                            System.out.println("Obstacle Heuristic");
                            Board solution1 = A.solve(start, "obstacle");
                            if (solution1 != null) {
                                System.out.println("\nFinal board state:");
                                OutputWriter.printBoard(solution1.board);
                                if (solution1.path != null && !solution1.path.isEmpty()) {
                                    System.out.println("\nSolution steps:");
                                    for (String step : solution1.path) {
                                        System.out.println("- " + step);
                                    }
                                }
                            }
                            break;
                        case 3:
                            System.out.println("Combined Heuristic");
                            Board solution2 = A.solve(start, "combined");
                            if (solution2 != null) {
                                System.out.println("Kondisi AKhir papan:");
                                OutputWriter.printBoard(solution2.board);
                                if (solution2.path != null && !solution2.path.isEmpty()) {
                                    System.out.println("\nSolution steps:");
                                    for (String step : solution2.path) {
                                        System.out.println("- " + step);
                                    }
                                }
                            }
                            break;
                    }
                    break;
            }
        }
    }
}