package algoritma;

import model.Board;
import model.Piece;
import utils.OutputWriter;

import java.util.*;

public class a {
    public static int calculateObstacleHeuristic(Board board, Piece primaryPiece) {
        int obstacles = 0;
        int primaryX = primaryPiece.x;
        int primaryY = primaryPiece.y;
        
        for (Piece piece : board.pieces.values()) {
            if (piece != primaryPiece) {
                if (piece.isHorizontal) {
                    if (piece.y >= primaryY && piece.x <= primaryX + primaryPiece.length) {
                        obstacles++;
                    }
                } else {
                    if (piece.x >= primaryX && piece.x <= board.exitX) {
                        obstacles++;
                    }
                }
            }
        }
        return obstacles;
    }

    public static int calculateManhattanHeuristic(Board board, Piece primaryPiece) {
        int primaryX = primaryPiece.x;
        int primaryY = primaryPiece.y;
        return Math.abs(primaryX - board.exitX) + Math.abs(primaryY - board.exitY);
    }

    public static int calculateCombinedHeuristic(Board board, Piece primaryPiece) {
        int obstacleCount = calculateObstacleHeuristic(board, primaryPiece);
        int manhattanDistance = calculateManhattanHeuristic(board, primaryPiece);
        
        double obstacleWeight = 0.6;
        double manhattanWeight = 0.4;
        
        return (int)(obstacleCount * obstacleWeight + manhattanDistance * manhattanWeight);
    }    public static Board solveWithObstacleHeuristic(Board initialBoard) {
        PriorityQueue<Board> openSet = new PriorityQueue<>((a, b) -> (a.g + a.h) - (b.g + b.h));
        Set<String> closedSet = new HashSet<>();
        int nodesVisited = 0;
        long startTime = System.currentTimeMillis();

        initialBoard.h = calculateObstacleHeuristic(initialBoard, initialBoard.pieces.get('P'));
        openSet.add(initialBoard);

        while (!openSet.isEmpty()) {
            Board current = openSet.poll();
            nodesVisited++;
            
            Piece primaryPiece = current.pieces.get('P');
            if (primaryPiece.x == current.exitX && primaryPiece.y == current.exitY) {
                long endTime = System.currentTimeMillis();
                System.out.println("\nGerakan " + nodesVisited + ": ");
                OutputWriter.printBoard(current.board);
                System.out.println("\nSolution found!");
                System.out.println("Nodes visited: " + nodesVisited);
                System.out.println("Time taken: " + (endTime - startTime) + " ms");
                return current;
            }

            String boardKey = getBoardKey(current);
            if (closedSet.contains(boardKey)) continue;
            closedSet.add(boardKey);

            List<Board> nextMoves = generatePossibleMoves(current);
            for (Board nextBoard : nextMoves) {
                String nextKey = getBoardKey(nextBoard);
                if (!closedSet.contains(nextKey)) {
                    nextBoard.g = current.g + 1;
                    nextBoard.h = calculateObstacleHeuristic(nextBoard, nextBoard.pieces.get('P'));
                    openSet.add(nextBoard);
                }
            }
        }
        return null;
    }    public static Board solveWithManhattanHeuristic(Board initialBoard) {
        PriorityQueue<Board> openSet = new PriorityQueue<>((a, b) -> (a.g + a.h) - (b.g + b.h));
        Set<String> closedSet = new HashSet<>();
        int nodesVisited = 0;
        long startTime = System.currentTimeMillis();

        initialBoard.h = calculateManhattanHeuristic(initialBoard, initialBoard.pieces.get('P'));
        openSet.add(initialBoard);

        while (!openSet.isEmpty()) {
            Board current = openSet.poll();
            nodesVisited++;
            
            Piece primaryPiece = current.pieces.get('P');
            if (primaryPiece.x == current.exitX && primaryPiece.y == current.exitY) {
                long endTime = System.currentTimeMillis();
                System.out.println("\nNode #" + nodesVisited);
                OutputWriter.printBoard(current.board);
                System.out.println("\nSolution found!");
                System.out.println("Nodes visited: " + nodesVisited);
                System.out.println("Time taken: " + (endTime - startTime) + " ms");
                return current;
            }

            String boardKey = getBoardKey(current);
            if (closedSet.contains(boardKey)) continue;
            closedSet.add(boardKey);

            List<Board> nextMoves = generatePossibleMoves(current);
            for (Board nextBoard : nextMoves) {
                String nextKey = getBoardKey(nextBoard);
                if (!closedSet.contains(nextKey)) {
                    nextBoard.g = current.g + 1;
                    nextBoard.h = calculateManhattanHeuristic(nextBoard, nextBoard.pieces.get('P'));
                    openSet.add(nextBoard);
                }
            }
        }
        return null;
    }

    public static Board solveWithCombinedHeuristic(Board initialBoard) {
        PriorityQueue<Board> openSet = new PriorityQueue<>((a, b) -> (a.g + a.h) - (b.g + b.h));
        Set<String> closedSet = new HashSet<>();
        int moveCounter = 0;

        initialBoard.h = calculateCombinedHeuristic(initialBoard, initialBoard.pieces.get('P'));
        openSet.add(initialBoard);
        while (!openSet.isEmpty()) {
            Board current = openSet.poll();
            moveCounter++;
            
            System.out.println("\nMove #" + moveCounter);
            OutputWriter.printBoard(current.board);

            Piece primaryPiece = current.pieces.get('P');
            if (primaryPiece.x == current.exitX && primaryPiece.y == current.exitY) {
                System.out.println("Total moves: " + moveCounter);
                return current;
            }

            String boardKey = getBoardKey(current);
            if (closedSet.contains(boardKey)) continue;
            closedSet.add(boardKey);

            List<Board> nextMoves = generatePossibleMoves(current);
            for (Board nextBoard : nextMoves) {
                String nextKey = getBoardKey(nextBoard);
                if (!closedSet.contains(nextKey)) {
                    nextBoard.g = current.g + 1;
                    nextBoard.h = calculateCombinedHeuristic(nextBoard, nextBoard.pieces.get('P'));
                    openSet.add(nextBoard);
                }
            }
        }
        return null;
    }

    private static String getBoardKey(Board board) {
        StringBuilder key = new StringBuilder();
        for (char[] row : board.board) {
            key.append(new String(row));
        }
        return key.toString();
    }

    private static List<Board> generatePossibleMoves(Board current) {
        List<Board> moves = new ArrayList<>();
        
        for (Piece piece : current.pieces.values()) {
            if (piece.isHorizontal) {
                if (canMoveLeft(current, piece)) {
                    moves.add(createNewBoardState(current, piece, -1, 0));
                }
                if (canMoveRight(current, piece)) {
                    moves.add(createNewBoardState(current, piece, 1, 0));
                }
            } else {
                if (canMoveUp(current, piece)) {
                    moves.add(createNewBoardState(current, piece, 0, -1));
                }
                if (canMoveDown(current, piece)) {
                    moves.add(createNewBoardState(current, piece, 0, 1));
                }
            }
        }
        return moves;
    }

    private static boolean canMoveLeft(Board board, Piece piece) {
        return piece.x > 0 && board.board[piece.y][piece.x - 1] == '.';
    }

    private static boolean canMoveRight(Board board, Piece piece) {
        return piece.x + piece.length < board.board[0].length && 
               board.board[piece.y][piece.x + piece.length] == '.';
    }

    private static boolean canMoveUp(Board board, Piece piece) {
        return piece.y > 0 && board.board[piece.y - 1][piece.x] == '.';
    }

    private static boolean canMoveDown(Board board, Piece piece) {
        return piece.y + piece.length < board.board.length && 
               board.board[piece.y + piece.length][piece.x] == '.';
    }

    private static Board createNewBoardState(Board current, Piece piece, int dx, int dy) {
        char[][] newBoard = new char[current.board.length][current.board[0].length];
        for (int i = 0; i < current.board.length; i++) {
            newBoard[i] = current.board[i].clone();
        }
        
        Map<Character, Piece> newPieces = new HashMap<>();
        for (Map.Entry<Character, Piece> entry : current.pieces.entrySet()) {
            Piece oldPiece = entry.getValue();
            if (oldPiece == piece) {
                newPieces.put(entry.getKey(), new Piece(
                    oldPiece.id, oldPiece.x + dx, oldPiece.y + dy,
                    oldPiece.length, oldPiece.isHorizontal, oldPiece.isPrimary
                ));
            } else {
                newPieces.put(entry.getKey(), new Piece(
                    oldPiece.id, oldPiece.x, oldPiece.y,
                    oldPiece.length, oldPiece.isHorizontal, oldPiece.isPrimary
                ));
            }
        }

        updateBoardState(newBoard, piece, dx, dy);
        
        return new Board(newBoard, newPieces, current.exitX, current.exitY);
    }

    private static void updateBoardState(char[][] board, Piece piece, int dx, int dy) {
        for (int i = 0; i < piece.length; i++) {
            if (piece.isHorizontal) {
                board[piece.y][piece.x + i] = '.';
            } else {
                board[piece.y + i][piece.x] = '.';
            }
        }
        
        for (int i = 0; i < piece.length; i++) {
            if (piece.isHorizontal) {
                board[piece.y + dy][piece.x + dx + i] = piece.id;
            } else {
                board[piece.y + dy + i][piece.x + dx] = piece.id;
            }
        }
    }
}
