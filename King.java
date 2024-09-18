import java.util.HashSet;

class King extends ChessPiece {
    private boolean canCastle;

    King(ChessPlayer owner, Position start) {
        super(owner, start);
        this.canCastle = true;
    }

    boolean canCastle() {
        return this.canCastle;
    }

    @Override
    boolean move(Chessboard chessboard, Position dest) {
        int destRow = dest.getRow();
        int destCol = dest.getCol();
        int thisRow = this.getPos().getRow();
        int thisCol = this.getPos().getCol();
        boolean samePosition;
        boolean outOfBounds;
        boolean canCastleP1R1;
        boolean canCastleP1R2;
        boolean canCastleP2R1;
        boolean canCastleP2R2;
        boolean canCastle;
        boolean validMove;
        Position KiPos;
        Position RPos;

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
                canCastleP1R1 = this.getOwner() == Chess.PLAYER1
                    && chessboard.containsPiece("R1")
                    && ((Rook) chessboard.getPiece("R1")).canCastle()
                    && this.canCastle
                    && destRow == 1
                    && destCol == 3;
                canCastleP1R2 = this.getOwner() == Chess.PLAYER1
                    && chessboard.containsPiece("R2")
                    && ((Rook) chessboard.getPiece("R2")).canCastle()
                    && this.canCastle
                    && destRow == 1
                    && destCol == 7;
                canCastleP2R1 = this.getOwner() == Chess.PLAYER2
                    && chessboard.containsPiece("r1")
                    && ((Rook) chessboard.getPiece("r1")).canCastle()
                    && this.canCastle
                    && destRow == 8
                    && destCol == 3;
                canCastleP2R2 = this.getOwner() == Chess.PLAYER2
                    && chessboard.containsPiece("r2")
                    && ((Rook) chessboard.getPiece("r2")).canCastle()
                    && this.canCastle
                    && destRow == 8
                    && destCol == 7;
                canCastle = canCastleP1R1 || canCastleP1R2 || canCastleP2R1 || canCastleP2R2;   
                validMove = (destRow == thisRow + 1 && destCol == thisCol)
                    || (destRow == thisRow + 1 && destCol == thisCol - 1)
                    || (destRow == thisRow + 1 && destCol == thisCol + 1)
                    || (destRow == thisRow && destCol == thisCol - 1)
                    || (destRow == thisRow && destCol == thisCol + 1)
                    || (destRow == thisRow - 1 && destCol == thisCol)
                    || (destRow == thisRow - 1 && destCol == thisCol - 1)
                    || (destRow == thisRow - 1 && destCol == thisCol + 1)
                    || canCastle;

                if (validMove) {
                    if (canCastle) {
                        if (canCastleP1R1) {
                            KiPos = new Position(1, 3);
                            RPos = new Position(1, 4);

                            if (!chessboard.vacantSpot(RPos)
                                || !chessboard.vacantSpot(KiPos)
                                || !chessboard.vacantSpot(new Position(1, 2))) {
                                return false;
                            }

                            // If the move results in a self-check, then deny it
                            if (chessboard.resultsInCheck(this, dest)) {
                                return false;
                            }

                            chessboard.movePiece("Ki", KiPos);
                            chessboard.movePiece("R1", RPos);
                            this.canCastle = false;
                
                        } else if (canCastleP1R2) {
                            KiPos = new Position(1, 7);
                            RPos = new Position(1, 6);

                            if (!chessboard.vacantSpot(RPos)
                                || !chessboard.vacantSpot(KiPos)) {
                                return false;
                            }

                            // If the move results in a self-check, then deny it
                            if (chessboard.resultsInCheck(this, dest)) {
                                return false;
                            }

                            chessboard.movePiece("Ki", KiPos);
                            chessboard.movePiece("R2", RPos);
                            this.canCastle = false;

                        } else if (canCastleP2R1) {
                            KiPos = new Position(8, 3);
                            RPos = new Position(8, 4);

                            if (!chessboard.vacantSpot(RPos)
                                || !chessboard.vacantSpot(KiPos)
                                || !chessboard.vacantSpot(new Position(8, 2))) {
                                return false;
                            }

                            // If the move results in a self-check, then deny it
                            if (chessboard.resultsInCheck(this, dest)) {
                                return false;
                            }

                            chessboard.movePiece("ki", KiPos);
                            chessboard.movePiece("r1", RPos);
                            this.canCastle = false;

                        } else if (canCastleP2R2) {
                            KiPos = new Position(8, 7);
                            RPos = new Position(8, 6);

                            if (!chessboard.vacantSpot(RPos)
                                || !chessboard.vacantSpot(KiPos)) {
                                return false;
                            }

                            // If the move results in a self-check, then deny it
                            if (chessboard.resultsInCheck(this, dest)) {
                                return false;
                            }

                            chessboard.movePiece("ki", KiPos);
                            chessboard.movePiece("r2", RPos);
                            this.canCastle = false;
                        }

                    // If the move is not a castle move
                    } else {
                        if (chessboard.friendlyFire(this.getOwner(), dest)) {
                            return false;
                        }
                        
                        // If the move results in a self-check, then deny it
                        if (chessboard.resultsInCheck(this, dest)) {
                            return false;
                        }

                        chessboard.removePiece(dest);
                        chessboard.movePiece(this.getPos(), dest);
                        this.canCastle = false;

                        if (this.targetsKing(chessboard)) {
                            if (this.getOwner() == Chess.PLAYER1) {
                                Chess.PLAYER2.checked();
    
                            } else {
                                Chess.PLAYER1.checked();
                            }
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
               
        return (kingRow == thisRow + 1 && kingCol == thisCol)
            || (kingRow == thisRow + 1 && kingCol == thisCol - 1)
            || (kingRow == thisRow + 1 && kingCol == thisCol + 1)
            || (kingRow == thisRow && kingCol == thisCol - 1)
            || (kingRow == thisRow && kingCol == thisCol + 1)
            || (kingRow == thisRow - 1 && kingCol == thisCol)
            || (kingRow == thisRow - 1 && kingCol == thisCol - 1)
            || (kingRow == thisRow - 1 && kingCol == thisCol + 1);
    }

    @Override
    HashSet<Position> possibleMoves(Chessboard chessboard) {
        HashSet<Position> moves = new HashSet<Position>();
        int thisRow = this.getPos().getRow();
        int thisCol = this.getPos().getCol();
        Position pos = new Position(thisRow + 1, thisCol);
        boolean canCastleR1;
        boolean canCastleR2;

        if (!chessboard.friendlyFire(this.getOwner(), pos) && chessboard.onBoard(pos)) {
            moves.add(new Position(pos.getRow(), pos.getCol()));
        }

        pos.setPosition(thisRow + 1, thisCol - 1);

        if (!chessboard.friendlyFire(this.getOwner(), pos) && chessboard.onBoard(pos)) {
            moves.add(new Position(pos.getRow(), pos.getCol()));
        }

        pos.setPosition(thisRow + 1, thisCol + 1);

        if (!chessboard.friendlyFire(this.getOwner(), pos) && chessboard.onBoard(pos)) {
            moves.add(new Position(pos.getRow(), pos.getCol()));
        }

        pos.setPosition(thisRow, thisCol - 1);

        if (!chessboard.friendlyFire(this.getOwner(), pos) && chessboard.onBoard(pos)) {
            moves.add(new Position(pos.getRow(), pos.getCol()));
        }

        pos.setPosition(thisRow, thisCol + 1);

        if (!chessboard.friendlyFire(this.getOwner(), pos) && chessboard.onBoard(pos)) {
            moves.add(new Position(pos.getRow(), pos.getCol()));
        }

        pos.setPosition(thisRow - 1, thisCol);

        if (!chessboard.friendlyFire(this.getOwner(), pos) && chessboard.onBoard(pos)) {
            moves.add(new Position(pos.getRow(), pos.getCol()));
        }

        pos.setPosition(thisRow - 1, thisCol - 1);

        if (!chessboard.friendlyFire(this.getOwner(), pos) && chessboard.onBoard(pos)) {
            moves.add(new Position(pos.getRow(), pos.getCol()));
        }

        pos.setPosition(thisRow - 1, thisCol + 1);

        if (!chessboard.friendlyFire(this.getOwner(), pos) && chessboard.onBoard(pos)) {
            moves.add(new Position(pos.getRow(), pos.getCol()));
        }

        if (this.getOwner() == Chess.PLAYER1) {
            canCastleR1 = chessboard.containsPiece("R1")
                && ((Rook) chessboard.getPiece("R1")).canCastle()
                && this.canCastle;

            pos.setPosition(thisRow, thisCol - 1);

            canCastleR1 = canCastleR1 && chessboard.vacantSpot(pos);

            pos.setPosition(thisRow, thisCol - 2);

            canCastleR1 = canCastleR1
                && chessboard.vacantSpot(pos)
                && chessboard.vacantSpot(new Position(thisRow, thisCol - 3));

            if (canCastleR1) {
                moves.add(new Position(pos.getRow(), pos.getCol()));
            }

            canCastleR2 = chessboard.containsPiece("R2")
                && ((Rook) chessboard.getPiece("R2")).canCastle()
                && this.canCastle;

            pos.setPosition(thisRow, thisCol + 1);
            
            canCastleR2 = canCastleR2 && chessboard.vacantSpot(pos);

            pos.setPosition(thisRow, thisCol + 2);

            canCastleR2 = canCastleR2 && chessboard.vacantSpot(pos);

            if (canCastleR2) {
                moves.add(new Position(pos.getRow(), pos.getCol()));
            }

        } else {
            canCastleR1 = chessboard.containsPiece("r1")
                && ((Rook) chessboard.getPiece("r1")).canCastle()
                && this.canCastle;

            pos.setPosition(thisRow, thisCol - 1);

            canCastleR1 = canCastleR1 && chessboard.vacantSpot(pos);

            pos.setPosition(thisRow, thisCol - 2);

            canCastleR1 = canCastleR1
                && chessboard.vacantSpot(pos)
                && chessboard.vacantSpot(new Position(thisRow, thisCol - 3));

            if (canCastleR1) {
                moves.add(new Position(pos.getRow(), pos.getCol()));
            }

            canCastleR2 = chessboard.containsPiece("r2")
                && ((Rook) chessboard.getPiece("r2")).canCastle()
                && this.canCastle;

            pos.setPosition(thisRow, thisCol + 1);
            
            canCastleR2 = canCastleR2 && chessboard.vacantSpot(pos);
            
            pos.setPosition(thisRow, thisCol + 2);

            canCastleR2 = canCastleR2 && chessboard.vacantSpot(pos);

            if (canCastleR2) {
                moves.add(new Position(pos.getRow(), pos.getCol()));
            }
        }

        return moves;
    }
}