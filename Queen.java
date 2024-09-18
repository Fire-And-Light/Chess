import java.util.HashSet;

class Queen extends ChessPiece {
    Queen(ChessPlayer owner, Position start) {
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
        boolean traverseUp;
        boolean traverseRight;
        boolean traverseDiagPosSlope;
        boolean traverseDiagNegSlope;
        boolean traverseRow;
        boolean traverseRowRight;
        boolean traverseCol;
        boolean traverseColUp;
        boolean validBishopMove;
        boolean validRookMove;
        boolean validMove;
        Position pos;

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
                traverseUp = destRow > thisRow;
                traverseRight = destCol > thisCol;
                traverseDiagPosSlope = destRow - thisRow == destCol - thisCol;
                traverseDiagNegSlope = destRow - thisRow == -(destCol - thisCol);
                traverseRow = destRow == thisRow;
                traverseRowRight = traverseRow && destCol > thisCol;
                traverseCol = destCol == thisCol;
                traverseColUp = traverseCol && destRow > thisRow;
                validBishopMove = traverseDiagPosSlope || traverseDiagNegSlope;
                validRookMove = traverseRow || traverseCol;
                validMove = validRookMove || validBishopMove;
                pos = new Position();

                if (validMove) {
                    if (validBishopMove) {
                        if (traverseUp) {
                            if (traverseRight) {
                                for (int curr = 1; curr < destCol - thisCol; curr++) {
                                    pos.setPosition(thisRow + curr, thisCol + curr);
    
                                    if (!chessboard.vacantSpot(pos)) {
                                        return false;
                                    }
                                }
    
                            } else {
                                for (int curr = 1; curr < thisCol - destCol; curr++) {
                                    pos.setPosition(thisRow + curr, thisCol - curr);
    
                                    if (!chessboard.vacantSpot(pos)) {
                                        return false;
                                    }
                                }
                            }
                            
                        } else {
                            if (traverseRight) {
                                for (int curr = 1; curr < destCol - thisCol; curr++) {
                                    pos.setPosition(thisRow - curr, thisCol + curr);
    
                                    if (!chessboard.vacantSpot(pos)) {
                                        return false;
                                    }
                                }
    
                            } else {
                                for (int curr = 1; curr < thisCol - destCol; curr++) {
                                    pos.setPosition(thisRow - curr, thisCol - curr);
    
                                    if (!chessboard.vacantSpot(pos)) {
                                        return false;
                                    }
                                }
                            }
                        }

                    } else {
                        if (traverseRow) {
                            if (traverseRowRight) {
                                for (int curr = 1; curr < destCol - thisCol; curr++) {
                                    pos.setPosition(thisRow, thisCol + curr);
                                    if (!chessboard.vacantSpot(pos)) {
                                        return false;
                                    }
                                }
        
                            } else {
                                for (int curr = 1; curr < thisCol - destCol; curr++) {
                                    pos.setPosition(thisRow, thisCol - curr);
                                    if (!chessboard.vacantSpot(pos)) {
                                        return false;
                                    }
                                }
                            }
                            
                        } else {
                            if (traverseColUp) {
                                for (int curr = 1; curr < destRow - thisRow; curr++) {
                                    pos.setPosition(thisRow + curr, thisCol);
                                    if (!chessboard.vacantSpot(pos)) {
                                        return false;
                                    }
                                }
        
                            } else {
                                for (int curr = 1; curr < thisRow - destRow; curr++) {
                                    pos.setPosition(thisRow - curr, thisCol);
                                    if (!chessboard.vacantSpot(pos)) {
                                        return false;
                                    }
                                }
                            }
                        }
                    }

                    if (chessboard.friendlyFire(this.getOwner(), dest)) {
                        return false;

                    } else {
                        // If the move results in a self-check, then deny it
                        if (chessboard.resultsInCheck(this, dest)) {
                            return false;
                        }

                        chessboard.removePiece(dest);
                        chessboard.movePiece(this.getPos(), dest);
                    }

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
        Position pos = new Position(thisRow, thisCol);
        String king;

        if (this.getOwner() == Chess.PLAYER1) {
            king = "ki";

        } else {
            king = "Ki";
        }

        for (int up = 1; thisRow + up <= 8; up++) {
            pos.setPosition(thisRow + up, thisCol);
            
            if (!chessboard.vacantSpot(pos)) {
                break;
            }
        }

        if (chessboard.isPiece(pos) && chessboard.getID(pos).equals(king)) {
            return true;
        }

        pos.setPosition(thisRow, thisCol);

        for (int down = 1; thisRow - down >= 1; down++) {
            pos.setPosition(thisRow - down, thisCol);

            if (!chessboard.vacantSpot(pos)) {
                break;
            }
        }

        if (chessboard.isPiece(pos) && chessboard.getID(pos).equals(king)) {
            return true;
        }

        pos.setPosition(thisRow, thisCol);

        for (int left = 1; thisCol - left >= 1; left++) {
            pos.setPosition(thisRow, thisCol - left);

            if (!chessboard.vacantSpot(pos)) {
                break;
            }
        }

        if (chessboard.isPiece(pos) && chessboard.getID(pos).equals(king)) {
            return true;
        }

        pos.setPosition(thisRow, thisCol);

        for (int right = 1; thisCol + right <= 8; right++) {
            pos.setPosition(thisRow, thisCol + right);

            if (!chessboard.vacantSpot(pos)) {
                break;
            }
        }

        if (chessboard.isPiece(pos) && chessboard.getID(pos).equals(king)) {
            return true;
        }

        pos.setPosition(thisRow, thisCol);

        for (int upRight = 1; thisRow + upRight <= 8 && thisCol + upRight <= 8; upRight++) {
            pos.setPosition(thisRow + upRight, thisCol + upRight);

            if (!chessboard.vacantSpot(pos)) {
                break;
            }
        }

        if (chessboard.isPiece(pos) && chessboard.getID(pos).equals(king)) {
            return true;
        }

        pos.setPosition(thisRow, thisCol);

        for (int upLeft = 1; thisRow + upLeft <= 8 && thisCol - upLeft >= 1; upLeft++) {
            pos.setPosition(thisRow + upLeft, thisCol - upLeft);

            if (!chessboard.vacantSpot(pos)) {
                break;
            }
        }

        if (chessboard.isPiece(pos) && chessboard.getID(pos).equals(king)) {
            return true;
        }

        pos.setPosition(thisRow, thisCol);

        for (int downRight = 1; thisRow - downRight >= 1 && thisCol + downRight <= 8; downRight++) {
            pos.setPosition(thisRow - downRight, thisCol + downRight);

            if (!chessboard.vacantSpot(pos)) {
                break;
            }
        }

        if (chessboard.isPiece(pos) && chessboard.getID(pos).equals(king)) {
            return true;
        }

        pos.setPosition(thisRow, thisCol);

        for (int downLeft = 1; thisRow - downLeft >= 1 && thisCol - downLeft >= 1; downLeft++) {
            pos.setPosition(thisRow - downLeft, thisCol - downLeft);

            if (!chessboard.vacantSpot(pos)) {
                break;
            }
        }

        if (chessboard.isPiece(pos) && chessboard.getID(pos).equals(king)) {
            return true;
        }

        return false;
    }

    @Override
    HashSet<Position> possibleMoves(Chessboard chessboard) {
        HashSet<Position> moves = new HashSet<Position>();
        int thisRow = this.getPos().getRow();
        int thisCol = this.getPos().getCol();
        Position pos = new Position(thisRow, thisCol);

        for (int up = 1; thisRow + up <= 8; up++) {
            pos.setPosition(thisRow + up, thisCol);

            if (!chessboard.vacantSpot(pos)) {
                if (chessboard.isEnemy(this.getOwner(), pos)) {
                    moves.add(new Position(thisRow + up, thisCol));
                }

                break;

            } else {
                moves.add(new Position(thisRow + up, thisCol));
            }
        }

        pos.setPosition(thisRow, thisCol);

        for (int down = 1; thisRow - down >= 1; down++) {
            pos.setPosition(thisRow - down, thisCol);

            if (!chessboard.vacantSpot(pos)) {
                if (chessboard.isEnemy(this.getOwner(), pos)) {
                    moves.add(new Position(thisRow - down, thisCol));
                }

                break;

            } else {
                moves.add(new Position(thisRow - down, thisCol));
            }
        }

        pos.setPosition(thisRow, thisCol);

        for (int left = 1; thisCol - left >= 1; left++) {
            pos.setPosition(thisRow, thisCol - left);

            if (!chessboard.vacantSpot(pos)) {
                if (chessboard.isEnemy(this.getOwner(), pos)) {
                    moves.add(new Position(thisRow, thisCol - left));
                }

                break;

            } else {
                moves.add(new Position(thisRow, thisCol - left));
            }    
        }

        pos.setPosition(thisRow, thisCol);

        for (int right = 1; thisCol + right <= 8; right++) {
            pos.setPosition(thisRow, thisCol + right);

            if (!chessboard.vacantSpot(pos)) {
                if (chessboard.isEnemy(this.getOwner(), pos)) {
                    moves.add(new Position(thisRow, thisCol + right));
                }

                break;

            } else {
                moves.add(new Position(thisRow, thisCol + right));
            }
        }

        pos.setPosition(thisRow, thisCol);

        for (int upRight = 1; thisRow + upRight <= 8 && thisCol + upRight <= 8; upRight++) {
            pos.setPosition(thisRow + upRight, thisCol + upRight);

            if (!chessboard.vacantSpot(pos)) {
                if (chessboard.isEnemy(this.getOwner(), pos)) {
                    moves.add(new Position(thisRow + upRight, thisCol + upRight));
                }

                break;

            } else {
                moves.add(new Position(thisRow + upRight, thisCol + upRight));
            }
        }

        pos.setPosition(thisRow, thisCol);

        for (int upLeft = 1; thisRow + upLeft <= 8 && thisCol - upLeft >= 1; upLeft++) {
            pos.setPosition(thisRow + upLeft, thisCol - upLeft);

            if (!chessboard.vacantSpot(pos)) {
                if (chessboard.isEnemy(this.getOwner(), pos)) {
                    moves.add(new Position(thisRow + upLeft, thisCol - upLeft));
                }

                break;

            } else {
                moves.add(new Position(thisRow + upLeft, thisCol - upLeft));
            }
        }

        pos.setPosition(thisRow, thisCol);

        for (int downRight = 1; thisRow - downRight >= 1 && thisCol + downRight <= 8; downRight++) {
            pos.setPosition(thisRow - downRight, thisCol + downRight);

            if (!chessboard.vacantSpot(pos)) {
                if (chessboard.isEnemy(this.getOwner(), pos)) {
                    moves.add(new Position(thisRow - downRight, thisCol + downRight));
                }

                break;

            } else {
                moves.add(new Position(thisRow - downRight, thisCol + downRight));
            }
        }

        pos.setPosition(thisRow, thisCol);

        for (int downLeft = 1; thisRow - downLeft >= 1 && thisCol - downLeft >= 1; downLeft++) {
            pos.setPosition(thisRow - downLeft, thisCol - downLeft);
            
            if (!chessboard.vacantSpot(pos)) {
                if (chessboard.isEnemy(this.getOwner(), pos)) {
                    moves.add(new Position(thisRow - downLeft, thisCol - downLeft));
                }

                break;

            } else {
                moves.add(new Position(thisRow - downLeft, thisCol - downLeft));
            }
        }

        return moves;
    }
}