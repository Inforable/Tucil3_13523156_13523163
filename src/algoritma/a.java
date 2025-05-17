package algoritma;

import model.Board;
import model.Piece;
import java.util.*;

public class a {
    private static int BOARD_SIZE;
    private static int nodesCreated = 0;
    
    private static void setBoardSize(Board board) {
        BOARD_SIZE = board.board.length;
    }
    private static int calculateHeuristic(Board board) {
        Piece primary = null;
        int blockingPieces = 0;
        
        for (Piece piece : board.pieces.values()) {
            if (piece.isPrimary) {
                primary = piece;
                break;
            }
        }
        
        if (primary == null) return Integer.MAX_VALUE;
        
        int endX = Math.min(BOARD_SIZE, primary.x + primary.length);
        for (int x = endX; x < BOARD_SIZE; x++) {
            if (board.board[primary.y][x] != '.') {
                blockingPieces++;
            }
        }
        
        int distanceToExit = BOARD_SIZE - (primary.x + primary.length);
        
        return blockingPieces * 2 + distanceToExit;
    }

    private static List<Board> generateMoves(Board currentBoard) {
        List<Board> possibleMoves = new ArrayList<>();
        
        for (Piece piece : currentBoard.pieces.values()) {
            if (piece.isHorizontal) {
                // Cek kiri
                for (int newX = piece.x - 1; newX >= 0; newX--) {
                    if (newX >= 0 && currentBoard.board[piece.y][newX] == '.') {
                        Board newBoard = createNewBoardState(currentBoard, piece, newX, piece.y);
                        if (newBoard != null) {
                            newBoard.g = currentBoard.g + 1;
                            newBoard.h = calculateHeuristic(newBoard);
                            newBoard.path = new ArrayList<>(currentBoard.path);
                            newBoard.path.add(piece.id + " left");
                            possibleMoves.add(newBoard);
                        }
                    } else break;
                }
                
                // Cek kanan
                for (int newX = piece.x + 1; newX + piece.length <= BOARD_SIZE; newX++) {
                    if (newX + piece.length - 1 < BOARD_SIZE && 
                        currentBoard.board[piece.y][newX + piece.length - 1] == '.') {
                        Board newBoard = createNewBoardState(currentBoard, piece, newX, piece.y);
                        if (newBoard != null) {
                            newBoard.g = currentBoard.g + 1;
                            newBoard.h = calculateHeuristic(newBoard);
                            newBoard.path = new ArrayList<>(currentBoard.path);
                            newBoard.path.add(piece.id + " right");
                            possibleMoves.add(newBoard);
                        }
                    } else break;
                }
            } else {
                // Cek atas
                for (int newY = piece.y - 1; newY >= 0; newY--) {
                    if (newY >= 0 && currentBoard.board[newY][piece.x] == '.') {
                        Board newBoard = createNewBoardState(currentBoard, piece, piece.x, newY);
                        if (newBoard != null) {
                            newBoard.g = currentBoard.g + 1;
                            newBoard.h = calculateHeuristic(newBoard);
                            newBoard.path = new ArrayList<>(currentBoard.path);
                            newBoard.path.add(piece.id + " up");
                            possibleMoves.add(newBoard);
                        }
                    } else break;
                }
                
                // Cek bawah
                for (int newY = piece.y + 1; newY + piece.length <= BOARD_SIZE; newY++) {
                    if (newY + piece.length - 1 < BOARD_SIZE && 
                        currentBoard.board[newY + piece.length - 1][piece.x] == '.') {
                        Board newBoard = createNewBoardState(currentBoard, piece, piece.x, newY);
                        if (newBoard != null) {
                            newBoard.g = currentBoard.g + 1;
                            newBoard.h = calculateHeuristic(newBoard);
                            newBoard.path = new ArrayList<>(currentBoard.path);
                            newBoard.path.add(piece.id + " down");
                            possibleMoves.add(newBoard);
                        }
                    } else break;
                }
            }
        }
        
        return possibleMoves;
    }

    private static Board createNewBoardState(Board currentBoard, Piece piece, int newX, int newY) {
        char[][] newBoardArray = new char[BOARD_SIZE][BOARD_SIZE];
        Map<Character, Piece> newPieces = new HashMap<>();
        
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                newBoardArray[i][j] = currentBoard.board[i][j];
            }
        }
        
        for (int i = 0; i < piece.length; i++) {
            if (piece.isHorizontal) {
                newBoardArray[piece.y][piece.x + i] = '.';
            } else {
                newBoardArray[piece.y + i][piece.x] = '.';
            }
        }
        
        for (int i = 0; i < piece.length; i++) {
            if (piece.isHorizontal) {
                if (newBoardArray[newY][newX + i] != '.') return null;
                newBoardArray[newY][newX + i] = piece.id;
            } else {
                if (newBoardArray[newY + i][newX] != '.') return null;
                newBoardArray[newY + i][newX] = piece.id;
            }
        }
        
        for (Map.Entry<Character, Piece> entry : currentBoard.pieces.entrySet()) {
            Piece p = entry.getValue();
            if (p.id == piece.id) {
                newPieces.put(p.id, new Piece(p.id, newX, newY, p.length, p.isHorizontal, p.isPrimary));
            } else {
                newPieces.put(p.id, new Piece(p.id, p.x, p.y, p.length, p.isHorizontal, p.isPrimary));
            }
        }
        
        return new Board(newBoardArray, newPieces);
    }

    private static boolean isSolved(Board board) {
        Piece primary = board.pieces.values().stream()
                                  .filter(p -> p.isPrimary)
                                  .findFirst()
                                  .orElse(null);
        
        return primary != null && primary.x + primary.length == BOARD_SIZE;
    }    public static Board solve(Board startBoard) {
        nodesCreated = 0;
        int steps = 0;
        setBoardSize(startBoard);  
        PriorityQueue<Board> openSet = new PriorityQueue<>((a, b) -> 
            (a.g + a.h) - (b.g + b.h));
        Set<String> closedSet = new HashSet<>();
        
        startBoard.h = calculateHeuristic(startBoard);
        openSet.add(startBoard);
        
        System.out.println("Initial state:");
        utils.OutputWriter.printBoard(startBoard.board);
        System.out.println();
        
        while (!openSet.isEmpty()) {
            Board current = openSet.poll();
            nodesCreated++;
            
            String boardState = Arrays.deepToString(current.board);
            if (closedSet.contains(boardState)) {
                continue;
            }
            
            steps++;
            System.out.println("Step " + steps + ":");
            utils.OutputWriter.printBoard(current.board);
            System.out.println();
            
            if (isSolved(current)) {
                System.out.println("Solution found!");
                System.out.println("Total steps: " + steps);
                System.out.println("Total nodes created: " + nodesCreated);
                return current;
            }
            
            closedSet.add(boardState);
            
            List<Board> nextMoves = generateMoves(current);
            for (Board nextBoard : nextMoves) {
                String nextBoardState = Arrays.deepToString(nextBoard.board);
                if (!closedSet.contains(nextBoardState)) {
                    openSet.add(nextBoard);
                }
            }
        }
        
        return null; 
    }
}
