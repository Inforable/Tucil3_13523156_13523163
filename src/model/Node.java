package model;

import java.util.LinkedList;
import java.util.List;

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
}