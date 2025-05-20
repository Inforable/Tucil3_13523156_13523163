package main;

import algoritma.*;
import java.util.*;
import model.*;
import utils.*;

public class Main {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Masukkan nama file input (contohnya tc1.txt): ");
            String filename = scanner.nextLine();
            String inputPath = "test/tc/" + filename;

            Board start = (Board) InputParser.readFromFile(inputPath);
            if (start == null) {
                System.out.println("Gagal memuat papan.");
                return;
            }

            System.out.println("Papan berhasil dibaca:");

            int choice = 0;
            while (choice < 1 || choice > 4) {
                System.out.println("\nPilih algoritma pencarian:");
                System.out.println("1. Greedy Best First Search");
                System.out.println("2. Uniform Cost Search");
                System.out.println("3. A* Search");
                System.out.println("4. Djikstra");
                System.out.print("Masukkan pilihan (1-4): ");

                try {
                    choice = Integer.parseInt(scanner.nextLine());
                    if (choice < 1 || choice > 4) {
                        System.out.println("Input tidak valid! Silakan masukkan angka antara 1-4.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Input tidak valid! Silakan masukkan angka antara 1-4.");
                    choice = 0;
                }
            }

            List<Node> path = null;
            String heuristic;

            switch (choice) {
                case 1 -> {
                    System.out.println("GBFS");
                    System.out.println("Pilih heuristik:");
                    System.out.println("1. Jarak terdekat ke exit (Manhattan)");
                    System.out.println("2. Banyak pieces penghalang (Blocking Cars)");
                    System.out.print("Masukkan pilihan (1-2): ");
                    int type = scanner.nextInt();
                    scanner.nextLine();

                    heuristic = (type == 2) ? "blockingcars" : "manhattan";
                    path = GBFS.solve(start, heuristic);
                }

                case 2 -> {
                    System.out.println("Uniform Cost Search");
                    path = UCS.solve(start);
                }

                case 3 -> {
                    System.out.println("A* Search");
                    System.out.println("Pilih heuristik:");
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
                    heuristic = switch (heuristicChoice) {
                        case 2 -> "obstacle";
                        case 3 -> "combined";
                        default -> "manhattan";
                    };
                    path = A.solve(start, heuristic);
                }

                case 4 -> {
                    System.out.println("Djikstra");
                    path = Djikstra.solve(start);
                }
            }

            OutputWriter.printSolutionPath(path);

            if (path != null) {
                System.out.print("\nApakah ingin menyimpan hasil ke file? (y/n): ");
                String saveChoice = scanner.nextLine().trim().toLowerCase();
                if (saveChoice.equals("y")) {
                    String outputFile = "test/result/solusi.txt";
                    OutputWriter.saveSolutionPath(path, outputFile);
                }
            }
        }
    }
}