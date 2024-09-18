import java.util.HashSet;

abstract class ChessPiece {
    private final ChessPlayer owner;
    private Position pos;

    ChessPiece(ChessPlayer owner, Position start) {
        super();
        this.owner = owner;
        this.pos = start;
    }

    ChessPlayer getOwner() {
        return this.owner;
    }

    Position getPos() {
        return this.pos;
    }

    // Returns "true" if the move was successful
    abstract boolean move(Chessboard chessboard, Position dest);

    // Returns "true" if the piece can attack the king
    abstract boolean targetsKing(Chessboard chessboard);

    // Returns the set of possible moves by the piece
    abstract HashSet<Position> possibleMoves(Chessboard chessboard);
}