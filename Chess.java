import java.util.Scanner;

public class Chess {
    static final ChessPlayer PLAYER1 = new ChessPlayer("Player 1");
    static final ChessPlayer PLAYER2 = new ChessPlayer("Player 2");
    private Chessboard chessboard;
    private ChessPlayer currentPlayer;
    private boolean gameOver;
    private Scanner input;

    public static void main(String args[]) {
        Chess game = new Chess();
        game.initialize();
    }

    public void initialize() {
        this.chessboard = new Chessboard();
        this.chessboard.initialize();
        this.input = new Scanner(System.in);

        this.printMenu();

        while (true) {
            String choice = this.input.nextLine();

            if (choice.equals("q")) {
                this.endGame();

            } else if (choice.equals("p")) {
                this.playGame();

            } else {
                System.out.println("Enter a valid command");
            }
        }
    }

    void playGame() {
        this.currentPlayer = Chess.PLAYER1;
        boolean inGameScreen = true;
        boolean nextPlayersChoice;
        String choice;

        while (inGameScreen) {
            this.printOppositeBoards();
            this.printGameOptions();
            this.printPlayerState();
            chessboard.enPassantSwitch(this.currentPlayer);
            nextPlayersChoice = false;

            while (!nextPlayersChoice) {
                choice = this.input.nextLine();

                if (choice.equals("m")) {
                    nextPlayersChoice = true;
                    inGameScreen = false;
                    Chess.PLAYER1.unCheck();
                    Chess.PLAYER2.unCheck();
                    this.chessboard.clearBoard();
                    this.chessboard.initialize();
                    this.currentPlayer = Chess.PLAYER1;
                    this.printMenu();
                    break;

                } else if (choice.equals("q")) {
                    nextPlayersChoice = true;
                    this.endGame();

                } else if (choice.equals("r")) {
                    nextPlayersChoice = true;
                    Chess.PLAYER1.reset();;
                    Chess.PLAYER2.reset();;
                    this.chessboard.clearBoard();
                    this.chessboard.initialize();
                    this.currentPlayer = Chess.PLAYER1;
                    this.gameOver = false;

                } else if (this.validInput(choice)) {
                    nextPlayersChoice = true;

                    if (Chess.PLAYER1.inCheck() || Chess.PLAYER2.inCheck()) {
                        if (this.chessboard.checkmate(this.currentPlayer)) {
                            this.printOppositeBoards();
                            this.printGameOptions();
                            System.out.println("CHECKMATE! " + this.currentPlayer.getID().toUpperCase() + " WINS!");
                            Chess.PLAYER1.reset();;
                            Chess.PLAYER2.reset();;
                            this.gameOver = true;
                            nextPlayersChoice = false;
                        }
                    }

                    this.currentPlayer = this.switchPlayer();
                }
            }
        }
    }

    void endGame() {
        this.input.close();
        System.exit(0);
    }

    ChessPlayer switchPlayer() {
        if (this.currentPlayer == Chess.PLAYER1) {
            return Chess.PLAYER2;

        } else {
            return Chess.PLAYER1;
        }
    }
    
    boolean isPlayersPiece(String ID) {
        return this.chessboard.getPiece(ID).getOwner() == this.currentPlayer;
    }

    boolean validInput(String input) {
        if (this.gameOver) {
            System.out.println("The game is over! Enter 'm', 'r' or 'q'");
            return false;
        }
        
        String ID;
        int row;
        int col;
        Position dest;
        boolean correctFormat;
        boolean correctPiece;
        
        try {
            ID = input.substring(0, 2);
            row = Integer.parseInt(input.substring(3, 4));
            col = Integer.parseInt(input.substring(5));
            dest = new Position(row, col);
            correctFormat = input.substring(2, 3).equals(" ") && input.substring(4, 5).equals(" ")
                && input.length() == 6;
            correctPiece = this.chessboard.containsPiece(ID)
                && this.isPlayersPiece(ID);

        } catch (NullPointerException e) {
            System.out.println("Enter a valid command!");
            return false;

        } catch (IndexOutOfBoundsException e) {
            System.out.println("Enter a valid command!");
            return false;

        } catch (NumberFormatException e) {
            System.out.println("Enter a valid command!");
            return false;
        }

        if (!correctFormat) {
            System.out.println("Enter a valid command!");
            return false;
        }
        
        if (!correctPiece) {
            System.out.println("Enter a piece on your team!");
            return false;
        }

        if (!this.chessboard.canMovePiece(ID, dest)) {
            System.out.println("Enter a valid move!");
            return false;
        }

        if (this.chessboard.canPromote(ID)) {
            this.printPromotion();

            String choice = this.input.nextLine();
            boolean validChoice = choice.equals("Queen")
                || choice.equals("Rook")
                || choice.equals("Knight")
                || choice.equals("Bishop");

            while (!validChoice) {
                System.out.println("You must enter a valid piece!");

                choice = this.input.nextLine();
                validChoice = choice.equals("Queen")
                    || choice.equals("Rook")
                    || choice.equals("Knight")
                    || choice.equals("Bishop");

            }

            this.chessboard.promote(ID, choice);
        }

        return true;
    }

    void printOppositeBoards() {
        for (int row = Chessboard.HEIGHT; row >= 1; row--) {
            System.out.print(row + " |");

            for (int col = 1; col <= Chessboard.WIDTH; col++) {
                System.out.print(this.chessboard.getID(new Position(row, col)) + "|");
            }

            System.out.print("                  " + (Chessboard.HEIGHT - row + 1) + " |");

            for (int col = Chessboard.WIDTH; col >= 1; col--) {
                System.out.print(this.chessboard.getID(new Position(Chessboard.HEIGHT - row + 1, col)) + "|");
            }

            System.out.println();
        }

        System.out.println("   1  2  3  4  5  6  7  8                       8  7  6  5  4  3  2  1");
    }

    void printGameOptions() {
        System.out.println("To make a move, enter the name, destination row number, and destination column number separated by spaces");
        System.out.println("For example, 'P4 3 4' moves P4 from (2, 4) up one space to (3, 4)");
        System.out.println("Enter 'm' to return to the main menu");
        System.out.println("Enter 'q' to quit the game");
        System.out.println("Enter 'r' to restart the game");
    }

    void printPlayerState() {
        if (Chess.PLAYER1.inCheck() || Chess.PLAYER2.inCheck()) {
            System.out.println("CHECK!");
        }

        System.out.println(this.currentPlayer.getID() + "'s turn!");
    }

    void printMenu() {
        // Menu banner
        System.out.println(" #####################################################################");
        System.out.println("     ###########      ### ##### ###       ###        ###        ########");
        System.out.println("        ######  ######### ##### ### ######### ########## ###################");
        System.out.println(" ###########  ########### ##### ### ######### ########## ###########");
        System.out.println("   ########  ############       ###       ###        ###        ##########");
        System.out.println("       ####  ############ ##### ### ################ ########## ###");
        System.out.println("############  ########### ##### ### ################ ########## #######");
        System.out.println("     ########   ######### ##### ### ################ ########## #############");
        System.out.println("  #############       ### ##### ###       ###        ###        #####");
        System.out.println("        ####################################################################");

        // Menu options
        System.out.println();
        System.out.println("                          Enter 'p' to play the game");
        System.out.println();
        System.out.println("                          Enter 'q' to quit the game");
        System.out.println();
        System.out.println();
    }

    void printPromotion() {
        System.out.println(this.currentPlayer.getID() + "'s Pawn must be promoted!");
        System.out.println("Enter 'Queen', 'Rook', 'Knight' or 'Bishop'");
    }
}