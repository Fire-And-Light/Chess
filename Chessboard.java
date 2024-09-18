import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

class Chessboard {
    public static final int HEIGHT = 8;
    public static final int WIDTH = 8;
    private static final String VACANT = "__";

    // A map of all positions to their corresponding pieces or spaces on the chessboard
    private ConcurrentHashMap<Position, String> positions;

    // A map of all IDs to their corresponding pieces on the chessboard
    private ConcurrentHashMap<String, ChessPiece> pieces;

    // The set of spaces on the chessboard
    private HashSet<Position> spaces;

    // A map of all pieces that can escape a check to their corresponding moves that escape the check
    private ConcurrentHashMap<String, HashSet<Position>> checkEscapes;

    // The current Pawn vulnerable to "en passant" capture
    private Pawn enPassantPawn;

    Chessboard() {
        super();
        this.positions = new ConcurrentHashMap<Position, String>();
        this.pieces = new ConcurrentHashMap<String, ChessPiece>();
        this.spaces = new HashSet<Position>();
        this.checkEscapes = new ConcurrentHashMap<String, HashSet<Position>>();
        this.enPassantPawn = null;
    }

    void initialize() {
        this.initializePieces();
        this.initializeSpaces();
    }

    void clearBoard() {
        // this.positions does not need to be cleared since all pieces can just be overwritten
        this.pieces.clear();
        this.spaces.clear();
        this.checkEscapes.clear();
    }

    // Only call this when the piece exists
    ChessPiece getPiece(String ID) {
        return this.pieces.get(ID);
    }

    ChessPiece getPiece(Position pos) {
        return this.pieces.get(this.positions.get(pos));
    }

    // If the added piece has the same position as another, then it will overwrite it
    void spawnPiece(String ID, ChessPiece piece) {
        this.pieces.remove(this.positions.get(piece.getPos()));
        this.pieces.put(ID, piece);
        // A copy of the position need not be created here because manipulating the position will be handled in "movePiece()". Refer to the notes to understand what I'm talking about
        this.positions.put(piece.getPos(), ID);
        this.spaces.remove(piece.getPos());
    }

    // If there's no piece to remove, then nothing happens
    void removePiece(String ID) {
        this.removePiece(this.getPiece(ID).getPos());
    }

    void removePiece(Position pos) {
        // A copy of the position need not be created here because it won't be manipulated. Refer to the notes to understand what I'm talking about
        this.spaces.add(pos);
        this.pieces.remove(this.positions.get(pos));
        this.positions.put(pos, Chessboard.VACANT);
    }

    // Only call this when the destination is vacant
    void movePiece(String ID, Position dest) {
        this.movePiece(this.getPiece(ID).getPos(), dest);
    }

    void movePiece(Position start, Position dest) {
        // Refer to the notes as to why I made copies here
        Position startCopy = new Position(start.getRow(), start.getCol());
        Position destCopy = new Position(dest.getRow(), dest.getCol());
        String ID = this.getID(start); // This can't directly be inserted in line 87 for some reason
        ChessPiece piece = this.getPiece(start);

        this.spaces.add(startCopy);
        piece.getPos().setPosition(dest.getRow(), dest.getCol());
        this.positions.put(destCopy, ID);
        this.positions.put(startCopy, Chessboard.VACANT);
        this.spaces.remove(destCopy);
    }

    String getID(Position pos) {
        return this.positions.get(pos);
    }

    boolean containsPiece(String ID) {
        return this.pieces.containsKey(ID);
    }

    boolean isPiece(Position pos) {
        return this.positions.containsKey(pos) && !this.spaces.contains(pos);
    }

    boolean canMovePiece(String ID, Position dest) {
        return this.pieces.get(ID).move(this, dest);
    }

    boolean friendlyFire(ChessPlayer friendlyTeam, Position pos) {
        return this.isPiece(pos) && this.getPiece(pos).getOwner() == friendlyTeam;
    }

    boolean isEnemy(ChessPlayer friendlyTeam, Position pos) {
        return this.isPiece(pos) && this.getPiece(pos).getOwner() != friendlyTeam;
    }

    boolean vacantSpot(Position pos) {
        return this.spaces.contains(pos);
    }

    boolean onBoard(Position pos) {
        return this.positions.containsKey(pos);
    }

    void setEnPassantPawn(Pawn pawn) {
        this.enPassantPawn = pawn;
    }

    void enPassantSwitch(ChessPlayer player) {
        if (this.enPassantPawn != null && this.enPassantPawn.getOwner() == player) {
            this.enPassantPawn.enPassantSwitch();
            this.enPassantPawn = null;
        }
    }

    boolean canPromote(String ID) {
        ChessPiece piece = this.getPiece(ID);
        ChessPlayer player = piece.getOwner();

        if (piece.getClass() == Pawn.class) {
            Pawn pawn = (Pawn) piece;

            if (player == Chess.PLAYER1) {
                return pawn.getPos().getRow() == 8;
    
            } else {
                return pawn.getPos().getRow() == 1;
            }

        } else {
            return false;
        }
    }

    // Once a pawn reaches the top it must be promoted to a Queen, Rook, Bishop or Knight
    void promote(String ID, String choice ) {
        Pawn pawn = (Pawn) this.getPiece(ID);
        ChessPlayer currentPlayer = pawn.getOwner();
        ChessPiece newPiece = null;
        String Q;
        String R;
        String K;
        String B;

        if (currentPlayer == Chess.PLAYER1) {
            Q = "Q";
            R = "R";
            K = "K";
            B = "B";

        } else {
            Q = "q";
            R = "r";
            K = "k";
            B = "b";
        }

        if (choice.equals("Queen")) {
            newPiece = new Queen(currentPlayer, new Position(pawn.getPos().getRow(), pawn.getPos().getCol()));
            currentPlayer.incrementQueens();
            this.spawnPiece(Q + currentPlayer.getNumQueens(), newPiece);

        } else if (choice.equals("Rook")) {
            newPiece = new Rook(currentPlayer, new Position(pawn.getPos().getRow(), pawn.getPos().getCol()));
            currentPlayer.incrementRooks();
            this.spawnPiece(R +currentPlayer.getNumRooks(), newPiece);

        } else if (choice.equals("Knight")) {
            newPiece = new Knight(currentPlayer, new Position(pawn.getPos().getRow(), pawn.getPos().getCol()));
            currentPlayer.incrementKnights();;
            this.spawnPiece(K + currentPlayer.getNumKnights(), newPiece);

        } else if (choice.equals("Bishop")) {
            newPiece = new Bishop(currentPlayer, new Position(pawn.getPos().getRow(), pawn.getPos().getCol()));
            currentPlayer.incrementBishops();;
            this.spawnPiece(B + currentPlayer.getNumBishops(), newPiece);
        }

        if (newPiece.targetsKing(this)) {
            if (currentPlayer == Chess.PLAYER1) {
                Chess.PLAYER2.checked();

            } else {
                Chess.PLAYER1.checked();
            }
        }
    }

    boolean kingTargeted(ChessPlayer opponent) {
        for (ChessPiece piece : this.pieces.values()) {
            if (piece.getOwner() == opponent) {
                if (piece.targetsKing(this)) {
                    return true;
                }
            }
        }

        return false;
    }

    boolean escapesCheck(ChessPiece piece, Position dest) {
        String ID = this.getID(piece.getPos());

        return this.checkEscapes.containsKey(ID) && this.checkEscapes.get(ID).contains(dest);
    }

    void clearEscapes() {
        this.checkEscapes.clear();
    }

    boolean resultsInCheck(ChessPiece piece, Position dest) {
        boolean check = true;
        String deadID = null;
        ChessPiece deadPiece = null;
        Position pastPos = new Position(piece.getPos().getRow(), piece.getPos().getCol());
        ChessPlayer opponent;

        if (piece.getOwner() == Chess.PLAYER1) {
            opponent = Chess.PLAYER2;

        } else {
            opponent = Chess.PLAYER1;
        }

        // Attempt move on a vacant destination
        if (this.vacantSpot(dest)) {
            this.movePiece(piece.getPos(), dest);

            // If castling
            if (piece.getClass() == King.class) {
                if (this.getID(piece.getPos()).equals("Ki")) {
                    if (dest.equals(new Position(1, 7)) && ((King) piece).canCastle()) {
                        this.movePiece(new Position(1, 8), new Position(1, 6));
    
                    } else if (dest.equals(new Position(1, 3)) && ((King) piece).canCastle()) {
                        this.movePiece(new Position(1, 1), new Position(1, 4));
                    }
    
                } else {
                    if (dest.equals(new Position(8, 7)) && ((King) piece).canCastle()) {
                        this.movePiece(new Position(8, 8), new Position(8, 6));
    
                    } else if (dest.equals(new Position(8, 3)) && ((King) piece).canCastle()) {
                        this.movePiece(new Position(8, 1), new Position(8, 4));
                    }
                }
                
            // If an "en passant" capture
            } else if (piece.getClass() == Pawn.class) {
                if (piece.getOwner() == Chess.PLAYER1) {
                    if (dest.equals(new Position(piece.getPos().getRow() + 1, piece.getPos().getCol() - 1))) {
                        Position left = new Position(piece.getPos().getRow(), piece.getPos().getCol() - 1);
                        deadID = this.getID(left);
                        deadPiece = this.getPiece(left);
    
                        if (this.onBoard(left) && deadPiece.getClass() == Pawn.class && ((Pawn) deadPiece).canBeEnPassant()) {
                            this.removePiece(left);
                        }
    
                    } else if (dest.equals(new Position(piece.getPos().getRow() + 1, piece.getPos().getCol() + 1))) {
                        Position right = new Position(piece.getPos().getRow(), piece.getPos().getCol() + 1);
                        deadID = this.getID(right);
                        deadPiece = this.getPiece(right);
    
                        if (this.onBoard(right) && deadPiece.getClass() == Pawn.class && ((Pawn) deadPiece).canBeEnPassant()) {
                            this.removePiece(right);
                        }
                    }
    
                } else {
                    if (dest.equals(new Position(piece.getPos().getRow() - 1, piece.getPos().getCol() + 1))) {
                        Position left = new Position(piece.getPos().getRow(), piece.getPos().getCol() + 1);
                        deadID = this.getID(left);
                        deadPiece = this.getPiece(left);
    
                        if (this.onBoard(left) && deadPiece.getClass() == Pawn.class && ((Pawn) deadPiece).canBeEnPassant()) {
                            this.removePiece(left);
                        }
    
                    } else if (dest.equals(new Position(piece.getPos().getRow() - 1, piece.getPos().getCol() - 1))) {
                        Position right = new Position(piece.getPos().getRow(), piece.getPos().getCol() - 1);
                        deadID = this.getID(right);
                        deadPiece = this.getPiece(right);
    
                        if (this.onBoard(right) && deadPiece.getClass() == Pawn.class && ((Pawn) deadPiece).canBeEnPassant()) {
                            this.removePiece(right);
                        }
                    }
                }
            }

            // If the resulting move clears the check, then there's no checkmate
            if (!this.kingTargeted(opponent)) {
                check = false;
            }

            // Revert move
            this.movePiece(dest, pastPos);

            if (piece.getClass() == King.class) {
                if (this.getID(piece.getPos()).equals("Ki")) {
                    if (dest.equals(new Position(1, 7)) && ((King) piece).canCastle()) {
                        this.movePiece(new Position(1, 6), new Position(1, 8));
    
                    } else if (dest.equals(new Position(1, 3)) && ((King) piece).canCastle()) {
                        this.movePiece(new Position(1, 4), new Position(1, 1));
                    }
    
                } else {
                    if (dest.equals(new Position(8, 7)) && ((King) piece).canCastle()) {
                        this.movePiece(new Position(8, 6), new Position(8, 8));
    
                    } else if (dest.equals(new Position(8, 3)) && ((King) piece).canCastle()) {
                        this.movePiece(new Position(8, 4), new Position(8, 1));
                    }
                }

            } else if (piece.getClass() == Pawn.class) {
                if (deadPiece != null) {
                    if (piece.getOwner() == Chess.PLAYER1) {
                        if (dest.equals(new Position(piece.getPos().getRow() + 1, piece.getPos().getCol() - 1))) {             
                            if (deadPiece.getClass() == Pawn.class && ((Pawn) deadPiece).canBeEnPassant()) {
                                this.spawnPiece(deadID, deadPiece);
                            }
        
                        } else if (dest.equals(new Position(piece.getPos().getRow() + 1, piece.getPos().getCol() + 1))) {
                            if (deadPiece.getClass() == Pawn.class && ((Pawn) deadPiece).canBeEnPassant()) {
                                this.spawnPiece(deadID, deadPiece);
                            }
                        }
    
                    } else {
                        if (dest.equals(new Position(piece.getPos().getRow() - 1, piece.getPos().getCol() + 1))) {            
                            if (deadPiece.getClass() == Pawn.class && ((Pawn) deadPiece).canBeEnPassant()) {
                                this.spawnPiece(deadID, deadPiece);
                            }
        
                        } else if (dest.equals(new Position(piece.getPos().getRow() - 1, piece.getPos().getCol() - 1))) {       
                            if (deadPiece.getClass() == Pawn.class && ((Pawn) deadPiece).canBeEnPassant()) {
                                this.spawnPiece(deadID, deadPiece);
                            }
                        }
                    }
                }
            }

        // Attempt move on an enemy piece
        } else {
            deadID = this.getID(dest);
            deadPiece = this.getPiece(dest);

            this.removePiece(dest);
            this.movePiece(piece.getPos(), dest);

            if (!this.kingTargeted(opponent)) {
                check = false;
            }

            this.movePiece(dest, pastPos);
            this.spawnPiece(deadID, deadPiece);
        }

        return check;
    }

    boolean checkmate(ChessPlayer opponent) {
        boolean checkmate = true;

        // Check all possible moves from all enemy pieces to see if the enemy can escape the check
        for (ChessPiece piece : this.pieces.values()) {
            if (piece.getOwner() != opponent) {
                for (Position dest : piece.possibleMoves(this)) {
                    if (!this.resultsInCheck(piece, dest)) {
                        if (!this.checkEscapes.containsKey(this.getID(piece.getPos()))) {
                            this.checkEscapes.put(this.getID(piece.getPos()), new HashSet<Position>());
                        }

                        this.checkEscapes.get(this.getID(piece.getPos())).add(dest);
                        checkmate = false;
                    }
                }
            }
        }

        return checkmate;
    }

    void initializePieces() {
        // Player 1's pieces
        Position pos = new Position(1, 5);
        this.pieces.put("Ki", new King(Chess.PLAYER1, pos));
        // A copy of the position need not be created here because manipulating the position will be handled in "movePiece()". Refer to the notes to understand what I'm talking about
        this.positions.put(pos, "Ki");

        pos = new Position(1, 4);
        this.pieces.put("Qu", new Queen(Chess.PLAYER1, pos));
        this.positions.put(pos, "Qu");

        pos = new Position(1, 1);
        this.pieces.put("R1", new Rook(Chess.PLAYER1, pos));
        this.positions.put(pos, "R1");

        pos = new Position(1, 8);
        this.pieces.put("R2", new Rook(Chess.PLAYER1, pos));
        this.positions.put(pos, "R2");

        pos = new Position(1, 2);
        this.pieces.put("K1", new Knight(Chess.PLAYER1, pos));
        this.positions.put(pos, "K1");

        pos = new Position(1, 7);
        this.pieces.put("K2", new Knight(Chess.PLAYER1, pos));
        this.positions.put(pos, "K2");

        pos = new Position(1, 3);
        this.pieces.put("B1", new Bishop(Chess.PLAYER1, pos));
        this.positions.put(pos, "B1");

        pos = new Position(1, 6);
        this.pieces.put("B2", new Bishop(Chess.PLAYER1, pos));
        this.positions.put(pos, "B2");

        pos = new Position(2, 1);
        this.pieces.put("P1", new Pawn(Chess.PLAYER1, pos));
        this.positions.put(pos, "P1");

        pos = new Position(2, 2);
        this.pieces.put("P2", new Pawn(Chess.PLAYER1, pos));
        this.positions.put(pos, "P2");
        
        pos = new Position(2, 3);
        this.pieces.put("P3", new Pawn(Chess.PLAYER1, pos));
        this.positions.put(pos, "P3");

        pos = new Position(2, 4);
        this.pieces.put("P4", new Pawn(Chess.PLAYER1, pos));
        this.positions.put(pos, "P4");

        pos = new Position(2, 5);
        this.pieces.put("P5", new Pawn(Chess.PLAYER1, pos));
        this.positions.put(pos, "P5");

        pos = new Position(2, 6);
        this.pieces.put("P6", new Pawn(Chess.PLAYER1, pos));
        this.positions.put(pos, "P6");

        pos = new Position(2, 7);
        this.pieces.put("P7", new Pawn(Chess.PLAYER1, pos));
        this.positions.put(pos, "P7");

        pos = new Position(2, 8);
        this.pieces.put("P8", new Pawn(Chess.PLAYER1, pos));
        this.positions.put(pos, "P8");

        // Player 2's pieces
        pos = new Position(8, 5);
        this.pieces.put("ki", new King(Chess.PLAYER2, pos));
        this.positions.put(pos, "ki");

        pos = new Position(8, 4);
        this.pieces.put("qu", new Queen(Chess.PLAYER2, pos));
        this.positions.put(pos, "qu");

        pos = new Position(8, 1);
        this.pieces.put("r1", new Rook(Chess.PLAYER2, pos));
        this.positions.put(pos, "r1");

        pos = new Position(8, 8);
        this.pieces.put("r2", new Rook(Chess.PLAYER2, pos));
        this.positions.put(pos, "r2");

        pos = new Position(8, 2);
        this.pieces.put("k1", new Knight(Chess.PLAYER2, pos));
        this.positions.put(pos, "k1");

        pos = new Position(8, 7);
        this.pieces.put("k2", new Knight(Chess.PLAYER2, pos));
        this.positions.put(pos, "k2");

        pos = new Position(8, 3);
        this.pieces.put("b1", new Bishop(Chess.PLAYER2, pos));
        this.positions.put(pos, "b1");

        pos = new Position(8, 6);
        this.pieces.put("b2", new Bishop(Chess.PLAYER2, pos));
        this.positions.put(pos, "b2");

        pos = new Position(7, 1);
        this.pieces.put("p1", new Pawn(Chess.PLAYER2, pos));
        this.positions.put(pos, "p1");

        pos = new Position(7, 2);
        this.pieces.put("p2", new Pawn(Chess.PLAYER2, pos));
        this.positions.put(pos, "p2");

        pos = new Position(7, 3);
        this.pieces.put("p3", new Pawn(Chess.PLAYER2, pos));
        this.positions.put(pos, "p3");

        pos = new Position(7, 4);
        this.pieces.put("p4", new Pawn(Chess.PLAYER2, pos));
        this.positions.put(pos, "p4");

        pos = new Position(7, 5);
        this.pieces.put("p5", new Pawn(Chess.PLAYER2, pos));
        this.positions.put(pos, "p5");

        pos = new Position(7, 6);
        this.pieces.put("p6", new Pawn(Chess.PLAYER2, pos));
        this.positions.put(pos, "p6");

        pos = new Position(7, 7);
        this.pieces.put("p7", new Pawn(Chess.PLAYER2, pos));
        this.positions.put(pos, "p7");

        pos = new Position(7, 8);
        this.pieces.put("p8", new Pawn(Chess.PLAYER2, pos));
        this.positions.put(pos, "p8");
    }

    void initializeSpaces() {
        for (int row = 3; row <= 6; row++) {
            for (int col = 1; col <= Chessboard.WIDTH; col++) {
                Position space = new Position(row, col);
                this.spaces.add(space);
                this.positions.put(space, Chessboard.VACANT);
            }
        }
    }
}