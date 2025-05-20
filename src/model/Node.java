package model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Node {
    public Board board;
    public Node parent;
    public String moveDesc; 
    public int g;   
    public int h;      

    public Node(Board board, Node parent, String moveDesc, int g, int h) {
        this.board = board;
        this.parent = parent;
        this.moveDesc = moveDesc;
        this.g = g;
        this.h = h;
    }

    public static List<Node> reconstructPath(Node goalNode) {
        LinkedList<Node> path = new LinkedList<>();
        Node current = goalNode;
        while (current != null) {
            path.addFirst(current);
            current = current.parent;
        }
        return path;
    }

    public static List<Node> generateNextNodes(Node currentNode) {
        Board state = currentNode.board;
        List<Node> nextNodes = new ArrayList<>();
        for (Map.Entry<Character, Piece> entry : state.pieces.entrySet()) {
            Piece piece = entry.getValue();
            char id = entry.getKey();
            if (piece.isHorizontal) {
                // Geser ke kiri
                for (int step = 1; piece.x - step >= 0; step++) {
                    int checkX = piece.x - step;
                    int checkY = piece.y;
                    if (checkX < 0) break;
                    if (!(checkX == state.exitX && checkY == state.exitY) && state.board[checkY][checkX] != '.') break;
                    Board newBoard = state.cloneBoard();
                    newBoard.movePiece(id, -step);
                    String desc = "Geser " + id + " ke kiri " + step;
                    Node nextNode = new Node(newBoard, currentNode, desc, currentNode.g + 1, 0);
                    nextNodes.add(nextNode);
                }
                // Geser ke kanan
                for (int step = 1; piece.x + piece.length - 1 + step < state.board[0].length; step++) {
                    int checkX = piece.x + piece.length - 1 + step;
                    int checkY = piece.y;
                    if (checkX >= state.board[0].length) break;
                    if (!(checkX == state.exitX && checkY == state.exitY) && state.board[checkY][checkX] != '.') break;
                    Board newBoard = state.cloneBoard();
                    newBoard.movePiece(id, step);
                    String desc = "Geser " + id + " ke kanan " + step;
                    Node nextNode = new Node(newBoard, currentNode, desc, currentNode.g + 1, 0);
                    nextNodes.add(nextNode);
                }
            } else {
                // Geser ke atas
                for (int step = 1; piece.y - step >= 0; step++) {
                    int checkX = piece.x;
                    int checkY = piece.y - step;
                    if (checkY < 0) break;
                    if (!(checkX == state.exitX && checkY == state.exitY) && state.board[checkY][checkX] != '.') break;
                    Board newBoard = state.cloneBoard();
                    newBoard.movePiece(id, -step);
                    String desc = "Geser " + id + " ke atas " + step;
                    Node nextNode = new Node(newBoard, currentNode, desc, currentNode.g + 1, 0);
                    nextNodes.add(nextNode);
                }
                // Geser ke bawah
                for (int step = 1; piece.y + piece.length - 1 + step < state.board.length; step++) {
                    int checkX = piece.x;
                    int checkY = piece.y + piece.length - 1 + step;
                    if (checkY >= state.board.length) break;
                    if (!(checkX == state.exitX && checkY == state.exitY) && state.board[checkY][checkX] != '.') break;
                    Board newBoard = state.cloneBoard();
                    newBoard.movePiece(id, step);
                    String desc = "Geser " + id + " ke bawah " + step;
                    Node nextNode = new Node(newBoard, currentNode, desc, currentNode.g + 1, 0);
                    nextNodes.add(nextNode);
                }
            }
        }
        return nextNodes;
    }
}