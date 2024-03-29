package game2048;

import java.io.Serializable;
import java.util.Formatter;
import java.util.Observable;


/** The state of a game of 2048.
 *  @author TODO: Richard
 */
public class Model extends Observable implements Serializable {
    /** Current contents of the board. */
    private Board board;
    /** Current score. */
    private int score;
    /** Maximum score so far.  Updated when game ends. */
    private int maxScore;
    /** True iff game is ended. */
    private boolean gameOver;

    /* Coordinate System: column C, row R of the board (where row 0,
     * column 0 is the lower-left corner of the board) will correspond
     * to board.tile(c, r).  Be careful! It works like (x, y) coordinates.
     */

    /** Largest piece value. */
    public static final int MAX_PIECE = 2048;

    /** A new 2048 game on a board of size SIZE with no pieces
     *  and score 0. */
    public Model(int size) {
        board = new Board(size);
        score = maxScore = 0;
        gameOver = false;
    }

    /** A new 2048 game where RAWVALUES contain the values of the tiles
     * (0 if null). VALUES is indexed by (row, col) with (0, 0) corresponding
     * to the bottom-left corner. Used for testing purposes. */
    public Model(int[][] rawValues, int score, int maxScore, boolean gameOver) {
        int size = rawValues.length;
        board = new Board(rawValues, score);
        this.score = score;
        this.maxScore = maxScore;
        this.gameOver = gameOver;
    }

    /** Return the current Tile at (COL, ROW), where 0 <= ROW < size(),
     *  0 <= COL < size(). Returns null if there is no tile there.
     *  Used for testing. Should be deprecated and removed.
     *  */
    public Tile tile(int col, int row) {
        return board.tile(col, row);
    }

    /** Return the number of squares on one side of the board.
     *  Used for testing. Should be deprecated and removed. */
    public int size() {
        return board.size();
    }

    /** Return true iff the game is over (there are no moves, or
     *  there is a tile with value 2048 on the board). */
    public boolean gameOver() {
        checkGameOver();
        if (gameOver) {
            maxScore = Math.max(score, maxScore);
        }
        return gameOver;
    }

    /** Return the current score. */
    public int score() {
        return score;
    }

    /** Return the current maximum game score (updated at end of game). */
    public int maxScore() {
        return maxScore;
    }

    /** Clear the board to empty and reset the score. */
    public void clear() {
        score = 0;
        gameOver = false;
        board.clear();
        setChanged();
    }

    /** Add TILE to the board. There must be no Tile currently at the
     *  same position. */
    public void addTile(Tile tile) {
        board.addTile(tile);
        checkGameOver();
        setChanged();
    }

    /** Tilt the board toward SIDE. Return true iff this changes the board.
     *
     * 1. If two Tile objects are adjacent in the direction of motion and have
     *    the same value, they are merged into one Tile of twice the original
     *    value and that new value is added to the score instance variable
     * 2. A tile that is the result of a merge will not merge again on that
     *    tilt. So each move, every tile will only ever be part of at most one
     *    merge (perhaps zero).
     * 3. When three adjacent tiles in the direction of motion have the same
     *    value, then the leading two tiles in the direction of motion merge,
     *    and the trailing tile does not.
     * */
    public boolean tilt(Side side) {
        boolean changed;
        changed = false;

        // TODO: Modify this.board (and perhaps this.score) to account
        // for the tilt to the Side SIDE. If the board changed, set the
        // changed local variable to true.
        /** there are basically 2 main stages into solving the puzzle;
         * for stage 1, we would have to make sure all non-null tiles are
         * on the right row level after each move and yet to be merged;
         * Stage 2: after all elements are in their position, look for
         * the adjacent with the same numbers and merge them with the move
         * function;
         * Lastly, set the board's perspective back to North from the previously
         * assigned side at the very beginning of this block;
         */
        board.setViewingPerspective(side); //board reoriented for move in all directions/
        /** stage 1: implementing a new variable position to set the conditions in order to move
         * the all pieces to the correct spots in rows;
         */
        for(int c = 0; c < size(); c++) { //add columns stay fixed/
            for(int r = size() - 1; r >= 0 ; r--) {//Note that for the row iteration that has to start from the top, otherwise bugs occur/
                Tile t = board.tile(c, r);
                if(t != null) {
                    int nexPos = 3;
                   while(nexPos >= r) {
                        if(board.tile(c, nexPos) == null) {
                            break;
                        }
                        nexPos--;
                   }
                   if(nexPos >= r) {
                       board.move(c, nexPos, t);
                       changed =  true;//setting the changeed to be true for each move/
                   }
                }
            }
            /** after the first stage, we are now for sure that all non-null
             * pieces have filled the slot after another piece or moved to
             * the very top(row 3); then we shall see all same numbers merge
             * into its double. Note that there is only 2 numbers for the maxim
             * to be merged each move;
             */
            for(int r = 3; r > 0; r--) {//shifting from the top to the bottom/
                Tile currTile = board.tile(c, r);
                int nextRow = r - 1;
                if(nextRow < 0) {//setting the boundary/
                    break;
                }
                Tile tBelow = board.tile(c, nextRow);
                if(tBelow == null || currTile == null) {//both pieces must not be null before proceed/
                    break;
                }
                if(tBelow.value() == currTile.value()){
                    board.move(c, r, tBelow);
                    score += currTile.value()  * 2;//update the score with the tile value multiplied by 2, otherwise bugs occur/
                    for(int x = nextRow - 1; x >= 0; x--) {//cases that there are more than 2 numbers in the adjacent/
                        Tile tBB = board.tile(c, x);
                        if(tBB == null) {
                            break;
                        } else
                            board.move(c, nextRow, tBB);
                                if(x == 0) //for special cases where an adjacent of 4 occurs/
                                    score *= 2;
                    }
                    changed = true;//setting the changed to be true for each move/
                }
            }
        }
        board.setViewingPerspective(Side.NORTH);//tile the board back to the side North after all operations/

        checkGameOver();
        if (changed) {
            setChanged();
        }
        return changed;
    }

    /** Checks if the game is over and sets the gameOver variable
     *  appropriately.
     */
    private void checkGameOver() {
        gameOver = checkGameOver(board);
    }

    /** Determine whether game is over. */
    private static boolean checkGameOver(Board b) {
        return maxTileExists(b) || !atLeastOneMoveExists(b);
    }

    /** Returns true if at least one space on the Board is empty.
     *  Empty spaces are stored as null.
     * */
    public static boolean emptySpaceExists(Board b) {
        // TODO: Fill in this function.
        /** iterating over all tiles using the nested
         * for loops to pluck out any nulls
         */
        for(int i = 0; i < b.size(); i++) {
            for(int j = 0; j < b.size(); j++){
                if(b.tile(i, j) == null)
                    return true;
            }
        }
        return false;
    }

    /**
     * Returns true if any tile is equal to the maximum valid value.
     * Maximum valid value is given by MAX_PIECE. Note that
     * given a Tile object t, we get its value with t.value().
     */
    public static boolean maxTileExists(Board b) {
        // TODO: Fill in this function.
        /** applying the same method to search for the
         * Max_Piece which is 2048
         */
        for(int i = 0; i < b.size(); i++) {
            for(int j = 0; j < b.size(); j++){
                if (b.tile(i, j) == null) /** skip any nulls to avoid disruption errors */
                    continue;
                if(b.tile(i, j).value() == MAX_PIECE) /** using the variables to set the conditions instead of the number */
                    return true;
            }
        }
        return false;
    }

    /**
     * Returns true if there are any valid moves on the board.
     * There are two ways that there can be valid moves:
     * 1. There is at least one empty space on the board.
     * 2. There are two adjacent tiles with the same value.
     */
    public static boolean atLeastOneMoveExists(Board b) {
        // TODO: Fill in this function.
        /** step 1: make sure there is no null values on the board:
         * we can actually use the predefined "empty space exists" method
         * to make our code more concise;
         */
        if(emptySpaceExists(b))
            return true;
        /** step 2: since there is no null values and we have to go over
         * any adjacent values that are the same numbers:
         * we would need to use the direction from the SIDE class to implement
         * a vertical and a horizontal coordinate with board reoriented North as
         * default so far;
         */
        int[] dx = {0, -1, 0, 1}; /** vertical checkup */
        int[] dy = {-1, 0, 1, 0}; /** horizontal checkup */

        for(int col = 0; col < b.size(); col++) {
            for(int row = 0; row < b.size(); row++) {
                int currTileValue = b.tile(col, row).value();
                for(int move = 0; move < 4; move++) {
                    int newTileCol = col + dx[move];
                    int newTileRow = row + dy[move];
                    //the move would have to be in the boundary/
                    if(newTileCol>0 && newTileCol<b.size() && newTileRow>0 && newTileRow < b.size()) {
                        Tile newTile = b.tile(newTileCol, newTileRow);
                        if(newTile.value() == currTileValue)
                            return true;
                    }
                }
            }
        }
        return false;
    }


    @Override
     /** Returns the model as a string, used for debugging. */
    public String toString() {
        Formatter out = new Formatter();
        out.format("%n[%n");
        for (int row = size() - 1; row >= 0; row -= 1) {
            for (int col = 0; col < size(); col += 1) {
                if (tile(col, row) == null) {
                    out.format("|    ");
                } else {
                    out.format("|%4d", tile(col, row).value());
                }
            }
            out.format("|%n");
        }
        String over = gameOver() ? "over" : "not over";
        out.format("] %d (max: %d) (game is %s) %n", score(), maxScore(), over);
        return out.toString();
    }

    @Override
    /** Returns whether two models are equal. */
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        } else if (getClass() != o.getClass()) {
            return false;
        } else {
            return toString().equals(o.toString());
        }
    }

    @Override
    /** Returns hash code of Model’s string. */
    public int hashCode() {
        return toString().hashCode();
    }
}
