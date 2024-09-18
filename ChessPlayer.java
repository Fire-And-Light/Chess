class ChessPlayer {
    private final String ID;
    private int numQueens;
    private int numRooks;
    private int numKnights;
    private int numBishops;
    private boolean inCheck;

    ChessPlayer(String ID) {
        super();
        this.ID = ID;
        this.numQueens = 1;
        this.numRooks = 2;
        this.numKnights = 2;
        this.numBishops = 2;
        this.inCheck = false;
    }

    String getID() {
        return this.ID;
    }

    int getNumQueens() {
        return this.numQueens;
    }

    int getNumRooks() {
        return this.numRooks;
    }

    int getNumKnights() {
        return this.numKnights;
    }

    int getNumBishops() {
        return this.numBishops;
    }

    void incrementQueens() {
        this.numQueens++;
    }

    void incrementRooks() {
        this.numRooks++;
    }

    void incrementKnights() {
        this.numKnights++;
    }

    void incrementBishops() {
        this.numBishops++;
    }

    void checked() {
        this.inCheck = true;
    }

    void unCheck() {
        this.inCheck = false;
    }

    boolean inCheck() {
        return this.inCheck;
    }

    void reset() {
        this.numQueens = 1;
        this.numRooks = 2;
        this.numKnights = 2;
        this.numBishops = 2;
        this.inCheck = false;
    }
}