import java.util.Scanner;

public class MineSweeper {

    public static final char MINE = 'X';
    public static final char GROUND = '.';
    public static final double HARD = 0.60; // ~40% Mines
    public static final double MEDIUM = 0.80; // ~20% Mines
    public static final double EASY = 0.90; // ~10% Mines
    public static final double SUPEREASY = 0.97; // ~3% Mines

    // Initialise the map with ground cover
    public static void initMap(char[][] map) {
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                map[i][j] = GROUND;
            }
        }
    }

    // Randomly place mines in the mine matrix
    // return the number of mines planted
    public static int plantMines(int[][] grid, double difficulty) {
        int totalMines = 0;
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if (Math.random() > difficulty) {
                    grid[i][j] = 1; // Mine
                    totalMines++;
                } else grid[i][j] = 0; // No mine
            }
        }
        return totalMines;
    }

    // Display the current map showing moves and mines
    public static void showMap(char[][] map) {
        System.out.print("  ");

        for (int i = 0; i < 10; i++) System.out.printf("%2d", i);

        System.out.println();
        for (int i = 0; i < map.length; i++) {
            System.out.printf("%2d", i);
            for (int j = 0; j < map[0].length; j++) {
                System.out.printf("%2c", map[i][j]);
            }
            System.out.println();
        }
    }

    // Show the map and reveal the location of the mines
    public static void revealMines(int[][] grid, char[][] map) {
        System.out.print("  ");
        for (int i = 0; i < 10; i++) System.out.printf("%2d", i);
        System.out.println();
        for (int i = 0; i < grid.length; i++) {
            System.out.printf("%2d", i);
            for (int j = 0; j < grid[0].length; j++) {
                if (grid[i][j] == 1) System.out.printf("%2c", MINE);
                else System.out.printf("%2c", map[i][j]);
            }
            System.out.println();
        }
    }

    // Take the players move and sweep area for mines
    /*public static int checkForMines(int row, int col, int[][] grid){
        // Need to check the neighbouring cells
        int mineCount = 0;
        if (row < 9)
            if (grid[row+1][col] == 1)
                mineCount++;

        if (row > 0)
            if (grid[row-1][col] == 1)
                mineCount++;

        if (col > 0)
            if (grid[row][col-1] == 1)
                mineCount++;

        if (col < 9)
            if (grid[row][col+1] == 1)
                mineCount++;

        if (row > 0 && col > 0)
            if (grid[row-1][col-1] == 1)
                mineCount++;

        if (row < 9 && col > 0)
            if (grid[row+1][col-1] == 1)
                mineCount++;

        if (row > 0 && col < 9)
            if (grid[row-1][col+1] == 1)
                mineCount++;

        if (row < 9 && col < 9)
            if (grid[row+1][col+1] == 1)
                mineCount++;

        return mineCount;
    }*/

    public static int checkForMines(int row, int col, int[][] grid) {
        /* For example lets say the user entered 5,5 for row and col. Then we would need to check the
           neighbouring cells around 5,5 as shown below

            [4,4][4,5][4,6]
            [5,4][5,5][5,6]
            [6,4][6,5][6,6]
            OR
            (row-1,col-1)    (row-1,col)    (row-1,col+1)
            (row, col-1)    (row,col)    (row,col+1)
            (row+1,col-1)    (row+1,col)    (row+1,col+1)

            But what about edge cases where we are on the boundary of the mine map?
            Checking all neighbours will result in an ArrayIndexOutofBounds runtime error.
            So we will need to catch such an error and handle it.
        */
        int mineCount = 0;
        // Setup the 8 transforms for row and col (row_tx, col_tx)
        int[][] tx = {
            { -1, -1 },
            { -1, 0 },
            { -1, +1 },
            { 0, -1 },
            { 0, +1 },
            { +1, -1 },
            { +1, 0 },
            { +1, +1 },
        };

        // Loop through the 8 transforms and count the neighbouring mines
        for (int i = 0; i < tx.length; i++) {
            try {
                // Check for a mine at the applied transform to row col
                if (grid[row + tx[i][0]][col + tx[i][1]] == 1) mineCount++;
            } catch (ArrayIndexOutOfBoundsException e) {
                // Expecting exceptions at edge cases
                // Do nothing
            }
        }
        return mineCount;
    }

    public static void showBanner(int lives, int score, int numMines) {
        System.out.println();
        System.out.println(" **************************");
        System.out.println(" *    Java Minesweeper    *");
        System.out.println(" **************************");
        System.out.printf(
            "Lives = %d Score = %d Mines = %d\n",
            lives,
            score,
            numMines
        );
        System.out.printf("Ctrl-c to quit\n\n");
    }

    public static void main(String[] args) {
        int[][] gameState = new int[10][10];
        char[][] gameMap = new char[10][10];
        int row, col;
        int lives = 3,
            score = 0,
            numMines = 0;
        boolean win = false;

        Scanner in = new Scanner(System.in);

        initMap(gameMap);
        numMines = plantMines(gameState, MEDIUM);
        // Cheat to test
        // revealMines(gameState, gameMap);
        while (win == false && lives > 0) {
            showBanner(lives, score, numMines);
            showMap(gameMap);
            System.out.println();
            System.out.print("Enter row number (0-9): ");
            row = in.nextInt();
            System.out.print("Enter column number (0-9): ");
            col = in.nextInt();

            // Nasty stuff - really need a better way to handle invalid input
            // I will return to this later.
            // Would it be better to loop on each individual input?
            // This would mean the grid goes off the screen. So, we need to redisplay
            // the grid on an invalid input.
            if ((row < 0 || row > 9) || (col < 0 || col > 9)) {
                System.out.println("Invalid input. Please try again.");
                continue;
            }

            // Player has hit a mine
            if (gameState[row][col] == 1) {
                gameMap[row][col] = MINE;
                lives--;
            }
            // Player has dodged a mine
            else {
                // Count the number of neighbouring mines
                int neighbouringMines = checkForMines(row, col, gameState);
                // Show the number of neighbouring mines on the map
                // Need to convert from integer to character digit
                gameMap[row][col] = (char) (neighbouringMines + '0');
                score++;
                // 100 - numMines will equal score when the player has cleared the map
                if (score + numMines == 100) win = true;
            }
        }
        showBanner(lives, score, numMines);
        revealMines(gameState, gameMap);
        if (win) System.out.println("Congratulations you win!");
        else System.out.println("Sorry, you loose!");
        in.close();
    }
}
