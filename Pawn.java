import java.util.HashSet;

class Pawn extends ChessPiece {
    private boolean hasNotMoved;
    private boolean canBeEnPassant;

    Pawn(ChessPlayer owner, Position start) {
        super(owner, start);
        this.hasNotMoved = true;
        this.canBeEnPassant = false;
    }

    // Returns "true" if the "en passant" capture is possible
    boolean canBeEnPassant() {
        return this.canBeEnPassant;
    }

    void enPassantSwitch() {
        this.canBeEnPassant = !this.canBeEnPassant;
    }

    @Override
    boolean move(Chessboard chessboard, Position dest) {
        int destRow = dest.getRow();
        int destCol = dest.getCol();
        int thisRow = this.getPos().getRow();
        int thisCol = this.getPos().getCol();
        boolean samePosition;
        boolean outOfBounds;
        boolean attackMove;
        boolean forwardMove;
        boolean forward2Move;
        boolean validMove;
        boolean canEnPassant;
        Position belowDest;

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
                if (this.getOwner() == Chess.PLAYER1) {
                    attackMove = (destRow == thisRow + 1 && destCol == thisCol - 1)
                        || (destRow == thisRow + 1 && destCol == thisCol + 1);
                    forwardMove = destRow == thisRow + 1
                        && destCol == thisCol;
                    forward2Move = destRow == thisRow + 2
                        && destCol == thisCol
                        && this.hasNotMoved;
                    belowDest = new Position(destRow - 1, destCol);

                } else {
                    attackMove = (destRow == thisRow - 1 && destCol == thisCol - 1)
                        || (destRow == thisRow - 1 && destCol == thisCol + 1);
                    forwardMove = destRow == thisRow - 1
                        && destCol == thisCol;
                    forward2Move = destRow == thisRow - 2
                        && destCol == thisCol
                        && this.hasNotMoved;
                    belowDest = new Position(destRow + 1, destCol);
                }

                validMove = attackMove || forwardMove || forward2Move;

                if (validMove) {
                    if (attackMove) {
                        if (chessboard.vacantSpot(dest)) {
                            canEnPassant = chessboard.isEnemy(this.getOwner(), belowDest)
                                && chessboard.getPiece(belowDest).getClass() == this.getClass()
                                && ((Pawn) chessboard.getPiece(belowDest)).canBeEnPassant();
    
                            if (canEnPassant) {
                                // If the move results in a self-check, then deny it
                                if (chessboard.resultsInCheck(this, dest)) {
                                    return false;
                                }

                                chessboard.removePiece(belowDest);
                                chessboard.movePiece(this.getPos(), dest);
                
                                this.hasNotMoved = false;
            
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
    
                        } else if (chessboard.friendlyFire(this.getOwner(), dest)) {
                            return false;

                        } else {
                            // If the move results in a self-check, then deny it
                            if (chessboard.resultsInCheck(this, dest)) {
                                return false;
                            }

                            chessboard.removePiece(dest);
                            chessboard.movePiece(this.getPos(), dest);
            
                            this.hasNotMoved = false;
        
                            if (this.targetsKing(chessboard)) {
                                if (this.getOwner() == Chess.PLAYER1) {
                                    Chess.PLAYER2.checked();
        
                                } else {
                                    Chess.PLAYER1.checked();
                                }
                            }
                        }
    
                    } else if (forwardMove) {
                        if (!chessboard.vacantSpot(dest)) {
                            return false;
                        }

                        // If the move results in a self-check, then deny it
                        if (chessboard.resultsInCheck(this, dest)) {
                            return false;
                        }

                        chessboard.removePiece(dest);
                        chessboard.movePiece(this.getPos(), dest);
        
                        this.hasNotMoved = false;
    
                        if (this.targetsKing(chessboard)) {
                            if (this.getOwner() == Chess.PLAYER1) {
                                Chess.PLAYER2.checked();
    
                            } else {
                                Chess.PLAYER1.checked();
                            }
                        }
    
                    } else if (forward2Move) {
                        if (!chessboard.vacantSpot(dest) || !chessboard.vacantSpot(belowDest)) {
                            return false;
                        }

                        // If the move results in a self-check, then deny it
                        if (chessboard.resultsInCheck(this, dest)) {
                            return false;
                        }

                        chessboard.removePiece(dest);
                        chessboard.movePiece(this.getPos(), dest);
        
                        this.hasNotMoved = false;
    
                        if (this.targetsKing(chessboard)) {
                            if (this.getOwner() == Chess.PLAYER1) {
                                Chess.PLAYER2.checked();
    
                            } else {
                                Chess.PLAYER1.checked();
                            }
                        }
    
                        chessboard.setEnPassantPawn(this);
                        this.enPassantSwitch();
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

        if (this.getOwner() == Chess.PLAYER1) {
            if (chessboard.getPiece("ki").getPos().equals(new Position(thisRow + 1, thisCol - 1))
                || chessboard.getPiece("ki").getPos().equals(new Position(thisRow + 1, thisCol + 1))) {
                return true;
            }

        } else {
            if (chessboard.getPiece("Ki").getPos().equals(new Position(thisRow - 1, thisCol - 1))
                || chessboard.getPiece("Ki").getPos().equals(new Position(thisRow - 1, thisCol + 1))) {
                return true;
            }
        }

        return false;
    }

    @Override
    HashSet<Position> possibleMoves(Chessboard chessboard) {
        HashSet<Position> moves = new HashSet<Position>();
        int thisRow = this.getPos().getRow();
        int thisCol = this.getPos().getCol();
        Position oneUp;
        Position twoUp;
        Position left;
        Position right;
        Position upLeft;
        Position upRight;
        boolean canEnPassantLeft;
        boolean canEnPassantRight;

        if (this.getOwner() == Chess.PLAYER1) {
            oneUp = new Position(thisRow + 1, thisCol);
            twoUp = new Position(thisRow + 2, thisCol);
            left = new Position(thisRow, thisCol - 1);
            right = new Position(thisRow, thisCol + 1);
            upLeft = new Position(thisRow + 1, thisCol - 1);
            upRight = new Position(thisRow + 1, thisCol + 1);
            canEnPassantLeft = chessboard.isEnemy(this.getOwner(), left)
                && chessboard.getPiece(left).getClass() == this.getClass()
                && ((Pawn) chessboard.getPiece(left)).canBeEnPassant();
            canEnPassantRight = chessboard.isEnemy(this.getOwner(), right)
                && chessboard.getPiece(right).getClass() == this.getClass()
                && ((Pawn) chessboard.getPiece(right)).canBeEnPassant();

            if (chessboard.vacantSpot(oneUp) && chessboard.onBoard(oneUp)) {
                if (chessboard.vacantSpot(twoUp) && chessboard.onBoard(twoUp) && this.hasNotMoved) {
                    moves.add(oneUp);
                    moves.add(twoUp);

                } else {
                    moves.add(oneUp);
                }
            }

            if (chessboard.isEnemy(Chess.PLAYER1, upLeft) || canEnPassantLeft) {
                moves.add(upLeft);
            }

            if (chessboard.isEnemy(Chess.PLAYER1, upRight) || canEnPassantRight) {
                moves.add(upRight);
            }

        } else {
            oneUp = new Position(thisRow - 1, thisCol);
            twoUp = new Position(thisRow - 2, thisCol);
            left = new Position(thisRow, thisCol + 1);
            right = new Position(thisRow, thisCol - 1);
            upLeft = new Position(thisRow - 1, thisCol + 1);
            upRight = new Position(thisRow - 1, thisCol - 1);
            canEnPassantLeft = chessboard.isEnemy(this.getOwner(), left)
                && chessboard.getPiece(left).getClass() == this.getClass()
                && ((Pawn) chessboard.getPiece(left)).canBeEnPassant();
            canEnPassantRight = chessboard.isEnemy(this.getOwner(), right)
                && chessboard.getPiece(right).getClass() == this.getClass()
                && ((Pawn) chessboard.getPiece(right)).canBeEnPassant();

            if (chessboard.vacantSpot(oneUp) && chessboard.onBoard(oneUp)) {
                if (chessboard.vacantSpot(twoUp) && chessboard.onBoard(twoUp) && this.hasNotMoved) {
                    moves.add(oneUp);
                    moves.add(twoUp);

                } else {
                    moves.add(oneUp);
                }
            }

            if (chessboard.isEnemy(Chess.PLAYER2, upLeft) || canEnPassantLeft) {
                moves.add(upLeft);
            }

            if (chessboard.isEnemy(Chess.PLAYER2, upRight) || canEnPassantRight) {
                moves.add(upRight);
            }
        }

        return moves;
    }
}