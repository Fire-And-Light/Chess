class Position {
    private int row;
    private int col;

    Position(int row, int col) {
        super();
        this.row = row;
        this.col = col;
    }

    Position() {
        this(0, 0);
    }

    int getRow() {
        return this.row;
    }

    int getCol() {
        return this.col;
    }

    void setPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }

    @Override
    public boolean equals(Object o) {
        Position pos = (Position) o;
        
        return this.row == pos.getRow() && this.col == pos.getCol();
    }

    // Override the hash function so that two positions with the same coordinates are equal for hash maps
    @Override
    public int hashCode() {
        return this.row * 10 + this.col;
    }
}