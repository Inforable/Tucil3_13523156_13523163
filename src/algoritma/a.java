package algoritma;

import model.Board;
import model.Piece;
import utils.OutputWriter;

import java.util.*;

public class a {
    public static class Move {
        Board resultState;
        String description;

        public Move(Board resultState, String description) {
            this.resultState = resultState;
            this.description = description;
        }
    }

    private static boolean isGoalState(Board state) {
        Piece primaryPiece = state.pieces.get('P');
        return primaryPiece.x == state.exitX && primaryPiece.y == state.exitY;
    }

    private static String getBoardKey(Board state) {
        StringBuilder sb = new StringBuilder();
        List<Character> sortedKeys = new ArrayList<>(state.pieces.keySet());
        Collections.sort(sortedKeys);
        for (char id : sortedKeys) {
            Piece p = state.pieces.get(id);
            sb.append(id).append(p.x).append(",").append(p.y).append(";");
        }
        return sb.toString();
    }

    private static List<Move> generateNextStates(Board state) {
        List<Move> nextStates = new ArrayList<>();
        
        for (Map.Entry<Character, Piece> entry : state.pieces.entrySet()) {
            Piece piece = entry.getValue();
            char id = entry.getKey();
            if (piece.isHorizontal) {
                for (int step = 1; canMoveLeft(state, piece, step); step++) {
                    Board newBoard = state.cloneBoard();
                    newBoard.movePiece(id, -step);
                    nextStates.add(new Move(newBoard, "Move " + id + " left " + step + " steps"));
                }
                for (int step = 1; canMoveRight(state, piece, step); step++) {
                    Board newBoard = state.cloneBoard();
                    newBoard.movePiece(id, step);
                    nextStates.add(new Move(newBoard, "Move " + id + " right " + step + " steps"));
                }
            } else {
                for (int step = 1; canMoveUp(state, piece, step); step++) {
                    Board newBoard = state.cloneBoard();
                    newBoard.movePiece(id, -step);
                    nextStates.add(new Move(newBoard, "Move " + id + " up " + step + " steps"));
                }
                for (int step = 1; canMoveDown(state, piece, step); step++) {
                    Board newBoard = state.cloneBoard();
                    newBoard.movePiece(id, step);
                    nextStates.add(new Move(newBoard, "Move " + id + " down " + step + " steps"));
                }
            }
        }
        return nextStates;
    }

    public static int calculateObstacleHeuristic(Board board, Piece primaryPiece) {
        int obstacles = 0;
        int targetX = board.exitX;
        int primaryX = primaryPiece.x;
        int primaryY = primaryPiece.y;
        
        // Count blocking pieces between primary piece and exit
        for (Piece piece : board.pieces.values()) {
            if (piece != primaryPiece && piece.isHorizontal) {
                if (piece.y == primaryY && 
                    ((primaryX < targetX && piece.x > primaryX && piece.x < targetX) ||
                     (primaryX > targetX && piece.x < primaryX && piece.x + piece.length > targetX))) {
                    obstacles++;
                }
            } else if (piece != primaryPiece && !piece.isHorizontal) {
                if (piece.x >= primaryX && piece.x <= targetX) {
                    obstacles++;
                }
            }
        }
        return obstacles * 2; 
    }

    public static int calculateManhattanHeuristic(Board board, Piece primaryPiece) {
        return Math.abs(primaryPiece.x - board.exitX);
    }

    public static int calculateCombinedHeuristic(Board board, Piece primaryPiece) {
        int obstacleCount = calculateObstacleHeuristic(board, primaryPiece);
        int manhattanDistance = calculateManhattanHeuristic(board, primaryPiece);
        return obstacleCount + manhattanDistance;
    }

    private static boolean canMoveLeft(Board board, Piece piece, int steps) {
        if (piece.x - steps < 0) return false;
        for (int i = 1; i <= steps; i++) {
            if (board.board[piece.y][piece.x - i] != '.') return false;
        }
        return true;
    }

    private static boolean canMoveRight(Board board, Piece piece, int steps) {
        if (piece.x + piece.length + steps - 1 >= board.board[0].length) return false;
        for (int i = 1; i <= steps; i++) {
            if (board.board[piece.y][piece.x + piece.length - 1 + i] != '.') return false;
        }
        return true;
    }

    private static boolean canMoveUp(Board board, Piece piece, int steps) {
        if (piece.y - steps < 0) return false;
        for (int i = 1; i <= steps; i++) {
            if (board.board[piece.y - i][piece.x] != '.') return false;
        }
        return true;
    }

    private static boolean canMoveDown(Board board, Piece piece, int steps) {
        if (piece.y + piece.length + steps - 1 >= board.board.length) return false;
        for (int i = 1; i <= steps; i++) {
            if (board.board[piece.y + piece.length - 1 + i][piece.x] != '.') return false;
        }
        return true;
    }

    public static Board solve(Board initialState, String heuristicType) {
        PriorityQueue<Board> openSet = new PriorityQueue<>((a, b) -> (a.g + a.h) - (b.g + b.h));
        Set<String> visited = new HashSet<>();
        int nodesVisited = 0;
        long startTime = System.currentTimeMillis();

        initialState.g = 0;
        initialState.path = new ArrayList<>();
        Piece primaryPiece = initialState.pieces.get('P');
          switch (heuristicType.toLowerCase()) {
            case "obstacle":
                initialState.h = calculateObstacleHeuristic(initialState, primaryPiece);
                break;
            case "combined":
                initialState.h = calculateCombinedHeuristic(initialState, primaryPiece);
                break;
            default:
                initialState.h = calculateManhattanHeuristic(initialState, primaryPiece);
                break;
        }
        
        openSet.add(initialState);

        while (!openSet.isEmpty()) {
            Board current = openSet.poll();
            nodesVisited++;
            
            if (isGoalState(current)) {
                long endTime = System.currentTimeMillis();
                System.out.println("\nSolution found!");
                System.out.println("Nodes visited: " + nodesVisited);
                System.out.println("Path length: " + current.path.size());
                System.out.println("Time taken: " + (endTime - startTime) + " ms");
                return current;
            }

            String boardKey = getBoardKey(current);
            if (visited.contains(boardKey)) continue;
            visited.add(boardKey);

            for (Move move : generateNextStates(current)) {
                Board nextBoard = move.resultState;
                String nextKey = getBoardKey(nextBoard);
                
                if (!visited.contains(nextKey)) {
                    nextBoard.g = current.g + 1;
                    nextBoard.path = new ArrayList<>(current.path);
                    nextBoard.path.add(move.description);
                      switch (heuristicType.toLowerCase()) {
                        case "obstacle":
                            nextBoard.h = calculateObstacleHeuristic(nextBoard, nextBoard.pieces.get('P'));
                            break;
                        case "combined":
                            nextBoard.h = calculateCombinedHeuristic(nextBoard, nextBoard.pieces.get('P'));
                            break;
                        default:
                            nextBoard.h = calculateManhattanHeuristic(nextBoard, nextBoard.pieces.get('P'));
                            break;
                    }
                    
                    openSet.add(nextBoard);
                }
            }
        }
        System.out.println("No solution found!");
        System.out.println("Nodes visited: " + nodesVisited);
        long endTime = System.currentTimeMillis();
        System.out.println("Time taken: " + (endTime - startTime) + " ms");
        return null;
    }
}
