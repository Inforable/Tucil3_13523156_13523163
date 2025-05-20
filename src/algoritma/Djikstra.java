package algoritma;

import java.util.*;
import model.*;

public class Djikstra {
    public static List<Node> solve(Board initialBoard) {
        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingInt(n -> n.g));
        Set<String> visited = new HashSet<>();
        long startTime = System.currentTimeMillis();

        Node startNode = new Node(initialBoard, null, null, 0, 0);
        openSet.add(startNode);

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();

            if (current.board.isGoal()) {
                long endTime = System.currentTimeMillis();
                System.out.println("Solusi ditemukan dalam " + current.g + " langkah");
                System.out.println("Waktu pencarian: " + (endTime - startTime) + " ms");
                List<Node> solution = Node.reconstructPath(current);
                return solution;
            }

            String key = current.board.getBoardKey();
            if (visited.contains(key)) continue;
            visited.add(key);

            List<Node> nextNodes = Node.generateNextNodes(current);
            for (Node nextNode : nextNodes) {
                String nextKey = nextNode.board.getBoardKey();
                if (!visited.contains(nextKey)) {
                    openSet.add(nextNode);
                }
            }
        }

        System.out.println("Tidak ada solusi");
        long endTime = System.currentTimeMillis();
        System.out.println("Waktu pencarian: " + (endTime - startTime) + " ms");
        return null;
    }
}
