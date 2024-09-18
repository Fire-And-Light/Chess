import java.util.HashSet;

class Knight extends ChessPiece {
    Knight(ChessPlayer owner, Position start) {
        super(owner, start);
    }

    @Override
    boolean move(Chessboard chessboard, Position dest) {
        int destRow = dest.getRow();
        int destCol = dest.getCol();
        int thisRow = this.getPos().getRow();
        int thisCol = this.getPos().getCol();
        boolean samePosition;
        boolean outOfBounds;
        boolean validMove;

        // If the player is in check, then check if the move escapes it
        if (this.getOwner().inCheck()) {
            if (!chessboard.escapesCheck(this, dest)) {
                return false;

            } else {
                this.getOwner().unCheck();
                chessboard.clearEscapes();
            }
        }

        samePosition = this.getPos().equals(dest);

        if (!samePosition) {
            outOfBounds = destRow < 1 || destRow > 8 || destCol < 1 || destCol > 8;

            if (!outOfBounds) {
                validMove = (destRow == thisRow + 2 && destCol == thisCol - 1)
                    || (destRow == thisRow + 1 && destCol == thisCol - 2)
                    || (destRow == thisRow - 1 && destCol == thisCol - 2)
                    || (destRow == thisRow - 2 && destCol == thisCol - 1)
                    || (destRow == thisRow - 2 && destCol == thisCol + 1)
                    || (destRow == thisRow - 1 && destCol == thisCol + 2)
                    || (destRow == thisRow + 1 && destCol == thisCol + 2)
                    || (destRow == thisRow + 2 && destCol == thisCol + 1);

                if (validMove) {
                    if (chessboard.friendlyFire(this.getOwner(), dest)) {
                        return false;
                    }
                    
                    // If the move results in a self-check, then deny it
                    if (chessboard.resultsInCheck(this, dest)) {
                        return false;
                    }

                    chessboard.removePiece(dest);
                    chessboard.movePiece(this.getPos(), dest);

                    if (this.targetsKing(chessboard)) {
                        if (this.getOwner() == Chess.PLAYER1) {
                            Chess.PLAYER2.checked();

                        } else {
                            Chess.PLAYER1.checked();
                        }
                    }

                } else {
                    return false;
                }

            } else {
                return false;
            }

        } else {
            return false;
        }

        return true;
    }

    @Override
    boolean targetsKing(Chessboard chessboard) {
        int thisRow = this.getPos().getRow();
        int thisCol = this.getPos().getCol();
        int kingRow;
        int kingCol;

        if (this.getOwner() == Chess.PLAYER1) {
            kingRow = chessboard.getPiece("ki").getPos().getRow();
            kingCol = chessboard.getPiece("ki").getPos().getCol();

        } else {
            kingRow = chessboard.getPiece("Ki").getPos().getRow();
            kingCol = chessboard.getPiece("Ki").getPos().getCol();
        }

        return (kingRow == thisRow + 2 && kingCol == thisCol - 1)
            || (kingRow == thisRow + 1 && kingCol == thisCol - 2)
            || (kingRow == thisRow - 1 && kingCol == thisCol - 2)
            || (kingRow == thisRow - 2 && kingCol == thisCol - 1)
            || (kingRow == thisRow - 2 && kingCol == thisCol + 1)
            || (kingRow == thisRow - 1 && kingCol == thisCol + 2)
            || (kingRow == thisRow + 1 && kingCol == thisCol + 2)
            || (kingRow == thisRow + 2 && kingCol == thisCol + 1);
    }

    @Override
    HashSet<Position> possibleMoves(Chessboard chessboard) {
        HashSet<Position> moves = new HashSet<Position>();
        int thisRow = this.getPos().getRow();
        int thisCol = this.getPos().getCol();
        Position pos = new Position(thisRow + 2, thisCol - 1);

        if (!chessboard.friendlyFire(this.getOwner(), pos) && chessboard.onBoard(pos)) {
            moves.add(new Position(pos.getRow(), pos.getCol()));
        }

        pos.setPosition(thisRow + 1, thisCol - 2);

        if (!chessboard.friendlyFire(this.getOwner(), pos) && chessboard.onBoard(pos)) {
            moves.add(new Position(pos.getRow(), pos.getCol()));
        }

        pos.setPosition(thisRow - 1, thisCol - 2);

        if (!chessboard.friendlyFire(this.getOwner(), pos) && chessboard.onBoard(pos)) {
            moves.add(new Position(pos.getRow(), pos.getCol()));
        }

        pos.setPosition(thisRow - 2, thisCol - 1);

        if (!chessboard.friendlyFire(this.getOwner(), pos) && chessboard.onBoard(pos)) {
            moves.add(new Position(pos.getRow(), pos.getCol()));
        }

        pos.setPosition(thisRow - 2, thisCol + 1);

        if (!chessboard.friendlyFire(this.getOwner(), pos) && chessboard.onBoard(pos)) {
            moves.add(new Position(pos.getRow(), pos.getCol()));
        }

        pos.setPosition(thisRow - 1, thisCol + 2);

        if (!chessboard.friendlyFire(this.getOwner(), pos) && chessboard.onBoard(pos)) {
            moves.add(new Position(pos.getRow(), pos.getCol()));
        }

        pos.setPosition(thisRow + 1, thisCol + 2);

        if (!chessboard.friendlyFire(this.getOwner(), pos) && chessboard.onBoard(pos)) {
            moves.add(new Position(pos.getRow(), pos.getCol()));
        }

        pos.setPosition(thisRow + 2, thisCol + 1);

        if (!chessboard.friendlyFire(this.getOwner(), pos) && chessboard.onBoard(pos)) {
            moves.add(new Position(pos.getRow(), pos.getCol()));
        }

        return moves;
    }
}