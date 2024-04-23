import java.util.Scanner;

public class RedBlueNim {
    // Constants for scores
    static int mRed = 2;
    static int mBlue = 3;

    public static void main(String[] args) {
        // Check if the correct number of arguments is provided
        if (args.length < 2) {
            System.out.println("Usage: java RedBlueNim <num-red> <num-blue> [<first-player> <depth>]");
            System.exit(1);
        }
        
        // Parse command line arguments
        int redMarbles = Integer.parseInt(args[0]);
        int blueMarbles = Integer.parseInt(args[1]);
        String first = "computer"; // Default first player
        Integer depth = null; // Depth for depth-limited search
        
        // Process optional arguments
        if (args.length > 2) {
            if (args[2].equals("human")) {
                first = "human";
            } else if (!args[2].equals("computer")) {
                System.out.println("Invalid value for <first-player>, assuming 'computer'");
            }
        }
        
        if (args.length > 3) {
            try {
                depth = Integer.parseInt(args[3]); // Parse depth
            } catch (NumberFormatException e) {
                System.out.println("Invalid value for <depth>, assuming unlimited depth");
            }
        }

        // Start the game
        redBlueNim(redMarbles, blueMarbles, first, depth);
    }

    public static void redBlueNim(int redMarbles, int blueMarbles, String first, Integer depth) {
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;
        String gamer = first; // Current player
    
        Scanner scanner = new Scanner(System.in);
    
        // Game loop
        while (redMarbles > 0 && blueMarbles > 0) {
            System.out.printf("Number of RED marbles: %d & Number of BLUE marbles: %d\n", redMarbles, blueMarbles);
    
            // Computer's turn
            if (gamer.equals("computer")) {
                String gamemove;
                int actualDepth = depth != null ? depth.intValue() : Integer.MAX_VALUE; // Default depth
                Object[] result = minmaxABPrun(redMarbles, blueMarbles, actualDepth, true, alpha, beta); // Minimax with alpha-beta pruning
                gamemove = (String) result[0]; // Best move
                if (gamemove.equals("red")) {
                    redMarbles--;
                } else {
                    blueMarbles--;
                }
                if (redMarbles == 0 || blueMarbles == 0) {
                    break;
                }
                gamer = "human"; // Switch to human player
            } else {
                // Human's turn
                if (gamer.equals("human")) {
                    String pile;
                    do {
                        System.out.print("Please select from which pile to remove RED/BLUE marble from: ");
                        pile = scanner.nextLine();
                    } while (!pile.equals("red") && !pile.equals("blue"));
    
                    if (pile.equals("red")) {
                        redMarbles--;
                    } else {
                        blueMarbles--;
                    }
                    if (redMarbles == 0 || blueMarbles == 0) {
                        break;
                    }
                    gamer = "computer"; // Switch to computer player
                }
            }
        }
    
        scanner.close();
    
        // Display final state and winner
        System.out.printf("Number of RED marbles: %d & Number of BLUE marbles: %d\n", redMarbles, blueMarbles);
        if (gamer.equals("human")) {
            int scores = blueMarbles * mBlue + redMarbles * mRed;
            System.out.printf("COMPUTER wins this game with the score: %d !!\n", scores);
        } else {
            int scores = blueMarbles * mBlue + redMarbles * mRed;
            System.out.printf("HUMAN wins this game with the score: %d !!\n", scores);
        }
    }

    // Evaluate the current state of the game
    public static int evaluate(int redMarbles, int blueMarbles) {
        if (redMarbles == 0 && blueMarbles == 0) {
            return 0;
        }
        if (redMarbles == 0) {
            return -blueMarbles * mBlue;
        }
        if (blueMarbles == 0) {
            return redMarbles * mRed;
        }
        int computerScores = redMarbles * 2 + blueMarbles * 3;
        int humanScores = (redMarbles + blueMarbles) * 3 - computerScores;
        return computerScores - humanScores;
    }

    // Minimax algorithm with alpha-beta pruning
    public static Object[] minmaxABPrun(int redMarbles, int blueMarbles, Integer depth, boolean max, int alpha, int beta) {
        if (depth == 0 || (redMarbles == 0 && blueMarbles == 0)) {
            return new Object[]{null, evaluate(redMarbles, blueMarbles)};
        }

        int topScores = max ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        String topGameMove = null;

        // Try all possible moves
        for (String gameMove : new String[]{"red", "blue"}) {
            int newRedMarbles, newBlueMarbles;

            if (gameMove.equals("red") && redMarbles > 0) {
                newRedMarbles = redMarbles - 1;
                newBlueMarbles = blueMarbles;
            } else if (gameMove.equals("blue") && blueMarbles > 0) {
                newRedMarbles = redMarbles;
                newBlueMarbles = blueMarbles - 1;
            } else {
                continue;
            }

            // Recursively call minmaxABPrun for the next move
            Object[] result = minmaxABPrun(newRedMarbles, newBlueMarbles, depth - 1, !max, alpha, beta);
            int scores = (int) result[1]; // Score of the current move

            // Update alpha-beta values
            if (max && scores > topScores) {
                topScores = scores;
                topGameMove = gameMove;
                alpha = Math.max(alpha, topScores);
            } else if (!max && scores < topScores) {
                topScores = scores;
                topGameMove = gameMove;
                beta = Math.min(beta, topScores);
            }
            if (beta <= alpha) {
                break;
            }
        }
        return new Object[]{topGameMove, topScores};
    }
}
