package stonemoving.state;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * Set the state of the game.
 */
@Data
@Slf4j
public class StoneState implements Cloneable {

    /**
     * The 8&#xd7;8 array configures the initial state of
     * the stone-path matrix.
     */
    public static final int[][] INITIAL = {
            {0, 1, 2, 1, 1, 2, 1, 1},
            {1, 1, 1, 1, 2, 1, 1, 2},
            {1, 1, 1, 2, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1},
            {2, 1, 1, 1, 1, 1, 1, 1},
            {1, 2, 1, 1, 1, 1, 1, 2},
            {1, 1, 1, 2, 1, 1, 1, 1},
            {1, 2, 1, 2, 1, 1, 2, 1}
    };
    /**
     * The 8&#xd7;8 array stores the default state for
     * swapping the variables.
     */
    public static final int[][] CURRENT = {
            {1, 1, 2, 1, 1, 2, 1, 1},
            {1, 1, 1, 1, 2, 1, 1, 2},
            {1, 1, 1, 2, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1},
            {2, 1, 1, 1, 1, 1, 1, 1},
            {1, 2, 1, 1, 1, 1, 1, 2},
            {1, 1, 1, 2, 1, 1, 1, 1},
            {1, 2, 1, 2, 1, 1, 2, 1}
    };

    public static final int[][] BOARD = {
            {4, 3, 4, 7, 3, 2, 6, 1},
            {2, 5, 3, 1, 2, 3, 2, 2},
            {2, 3, 2, 4, 2, 2, 2, 5},
            {4, 4, 5, 4, 4, 4, 3, 4},
            {4, 3, 2, 4, 2, 4, 2, 4},
            {3, 3, 2, 2, 5, 2, 5, 2},
            {4, 5, 5, 2, 2, 5, 6, 1},
            {3, 2, 2, 3, 5, 1, 3, 1}
    };

    @Setter(AccessLevel.NONE)
    private Board[][] matrix;

    @Setter(AccessLevel.NONE)
    private int stoneRow;

    @Setter(AccessLevel.NONE)
    private int stoneCol;

    /**
     * Creates a {@code RollingCubesState} object representing
     * the initial state of the stone-path.
     */
    public StoneState() {
        this(INITIAL);
    }

    /**
     * Initialize state by the specified array.
     *
     * @param a is a 8&#xd7;8 array indicates the initial state.
     * @throws IllegalArgumentException if the array does not represent a valid
     *                                  configuration of the tray.
     */
    public StoneState(int[][] a) {
        if (!isValidMatrix(a)) {
            throw new IllegalArgumentException();
        }
        gameInitial(a);
    }

    private boolean isValidMatrix(int[][] a) {
        if (a == null || a.length != 8) {
            return false;
        }
        boolean foundText = false;
        for (int[] row : a) {
            if (row == null || row.length != 8) {
                return false;
            }
            for (int space : row) {
                if (space < 0 || space >= Board.values().length) {
                    return false;
                }
                if (space == Board.STONE.getValue()) {
                    if (foundText) {
                        return false;
                    }
                    foundText = true;
                }
            }
        }
        return foundText;
    }

    private void gameInitial(int[][] a) {
        this.matrix = new Board[8][8];
        for (int i = 0; i < 8; ++i) {
            for (int j = 0; j < 8; ++j) {
                if ((this.matrix[i][j] = Board.of(a[i][j])) == Board.STONE) {
                    stoneRow = i;
                    stoneCol = j;
                }
            }
        }
    }

    /**
     * Check whether the game is solved.
     *
     * @return {@code true} if the puzzle is solved,
     * {@code false} otherwise.
     */
    public boolean isSolved() {
        if (matrix[7][7] == Board.STONE)
            return true;
        return false;
    }

    /**
     * Check whether the stone can be moved to the specified location.
     *
     * @param row is the row of the specified position
     * @param col is the col of the specified position
     * @return {@code true} if stone at its position can be move to the specified location,
     * {@code false} otherwise.
     */
    public boolean canBeMoved(int row, int col) {
        int m = 0, x = 0;
        if (CURRENT[stoneRow][stoneCol] == Integer.parseInt(String.valueOf(Board.FRAMED))) {
            {
                if ((BOARD[stoneRow][stoneCol] + stoneRow == row) &&  (BOARD[stoneRow][stoneCol] + stoneCol == col))
                    x = 1;
                else if ((stoneRow + BOARD[stoneRow][stoneCol] == row) && (stoneCol - BOARD[stoneRow][stoneCol] == col))
                    x = 1;
                else if ((stoneRow - BOARD[stoneRow][stoneCol] == row) && (BOARD[stoneRow][stoneCol] + stoneCol == col))
                    x = 1;
                else if (((stoneRow - BOARD[stoneRow][stoneCol]) == row) && ((stoneCol - BOARD[stoneRow][stoneCol]) == col))
                    x = 1;
                else ;
            }
            return x == 1 && 0 <= row && row <= 7 && 0 <= col && col <= 7;

        } else if (CURRENT[stoneRow][stoneCol] == Integer.parseInt(String.valueOf(Board.UNFRAMED))) {
            if (((BOARD[stoneRow][stoneCol] + stoneRow) == row) && (stoneCol == col)) m = 1;

            else if (((stoneRow - BOARD[stoneRow][stoneCol]) == row) && (stoneCol == col)) m = 1;

            else if (((BOARD[stoneRow][stoneCol] + stoneCol) == col) && (stoneRow == row)) m = 1;

            else if (((stoneCol - BOARD[stoneRow][stoneCol]) == col) && (stoneRow == row)) m = 1;
            else m = 0;
        }

        return m == 1 && 0 <= row && row <= 7 && 0 <= col && col <= 7;

    }

    /**
     * Set the state of the specified place to be the stone's location,
     * set the previous place stone has stayed back to normal path.
     *
     * @param row is the row of the path where the stone wants to go
     * @param col is the column of the path where the stone wants to go
     */
    public void moveToNext(int row, int col) {
        try {
            if (matrix[row][col] != Board.STONE) {
                matrix[row][col] = Board.STONE;
                stoneRow = row;
                stoneCol = col;
            }
            for (int i = 0; i <= 7; i++)
                for (int j = 0; j <= 7; j++) {
                    if (row == i && col == j) ;
                    else if (matrix[row][col] == Board.STONE && matrix[i][j] == Board.STONE) {
                        matrix[i][j] = Board.of(CURRENT[i][j]);
                        break;
                    }
                }
        } catch (Exception e) {
            throw new ArrayIndexOutOfBoundsException();
        }
    }

    public void changeDirec(){
        for (int i = 0; i <= 7; i++)
            for (int j = 0; j <= 7; j++) {
                if(matrix[i][j] == Board.DIREC)
                    matrix[i][j] = Board.of(CURRENT[i][j]);
            }
    }
    /**
     * Copy the matrix.
     *
     * @return the copy one the the matrix
     */
    public StoneState clone() {
        StoneState copy = null;
        try {
            copy = (StoneState) super.clone();
        } catch (CloneNotSupportedException e) {
        }
        copy.matrix = new Board[matrix.length][];
        for (int i = 0; i < matrix.length; ++i) {
            copy.matrix[i] = matrix[i].clone();
        }
        return copy;
    }

    /**
     * The content of the matrix, consists of STONE,FRAMED,UNFRAMED.
     *
     * @return the content string of the matrix
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Board[] row : matrix) {
            for (Board format : row) {
                sb.append(format).append(' ');
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    /**
     * For testing if the state sets well.
     *
     * @param args arguments
     */
    public static void main(String[] args) {
        StoneState state = new StoneState();
        System.out.println(state);
        state.moveToNext(1, 7);
        System.out.println(state);
        System.out.println(state.canBeMoved(3, 5));
//        state.moveToNext(2, 0);
//        System.out.println(state);
//        state.moveToNext(4, 4);
//        System.out.println(state);
//        System.out.println(state.canBeMoved(2, 4));
//        state.moveToNext(2, 4);
//        System.out.println(state.canBeMoved(2, 6));
//        state.moveToNext(2, 6);
//        System.out.println(state);
    }
}
