package main;

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
        }
    }
}