package leko.valmx.thegameoflife.game;

import android.os.AsyncTask;
import android.util.Log;

import kotlin.random.Random;
import leko.valmx.thegameoflife.game.utils.GameRuleHelper;

public class JavaActorManager {
    /**
     * Cleaner implementation using bit magic with shorts
     * 0 | Alive or not
     * 1 2 3 | Neighbours
     * 4 | New alive or not for next generation
     * 5 6 7 | Neighbours next generation
     */

    public JavaActorManager() {
        for (int i = 0; i < 19000; i++) {
            int x = Random.Default.nextInt(mapSize);
            int y = Random.Default.nextInt(mapSize);
            if ((getCell(x,y) & ALIVE_NEXT_GEN_BITMASK) == 0)
                changeCellState(x, y);
        }
        fetchNextGeneration();
    }

    public static short NEXT_GEN_NEIGHBOURS_BITMASK = 0b000001111;
    public static short CURRENT_GEN_NEIGHBOURS_BITMASK = 0b0111100000;
    public static short ALIVE_CURRENT_GEN_BITMASK = 0b1000000000;
    public static short ALIVE_NEXT_GEN_BITMASK = 0b000010000;

    private static final int[][] offsets = {
            {1, 0},
            {1, 1},
            {0, 1},
            {-1, 0},
            {0, -1},
            {-1, -1},
            {-1, 1},
            {1, -1}};

    private final int mapSize = 500;

    private final short[][] cells = new short[mapSize][mapSize];

    /**
     * Kills/ Births the cell and updates the neighbour count of all adjacent cells
     * The method will check if the cell is alive or not before updating.
     * USED FOR NEXT GEN
     */

    private void changeCellState(int x, int y) {
        // How much will be added to the neighbour count of adjacent cells
        boolean kill;

        // If the cell is alive it will die and the neighbours neighbour count will be decreased.
        short cell = (short) (getCell(x, y) ^ ALIVE_NEXT_GEN_BITMASK);
        kill = isCellAlive(x, y);

        setCell(x, y, cell);
        for (int[] offset : offsets) {
            int currentX = x + offset[0];
            int currentY = y + offset[1];
            int currentCell = (getCell(currentX, currentY));

            int currentNeighbours = currentCell & NEXT_GEN_NEIGHBOURS_BITMASK;


            if (kill) {
                currentNeighbours--;
            } else {
                currentNeighbours++;
            }
            setCell(currentX, currentY, (short) ((currentCell & (~NEXT_GEN_NEIGHBOURS_BITMASK)) + currentNeighbours));
//            Log.i("Changed", "FROM " + Integer.toBinaryString(currentCell) + " to " + Integer.toBinaryString((currentCell & (~NEXT_GEN_NEIGHBOURS_BITMASK)) + currentNeighbours));
        }

    }

    /**
     * Shifts all cell shorts << 5
     */
    private void fetchNextGeneration() {
        for (int x = 0; x < cells.length; x++) {
            for (int y = 0; y < cells[0].length; y++) {
                cells[x][y] = (short) ((cells[x][y] & 0b11111) | (cells[x][y] << 5));
            }
        }
    }

    public boolean isCellAlive(int x, int y) {
        return (getCell(x, y) & ALIVE_CURRENT_GEN_BITMASK) != 0;
    }

    // Getting cells with side scrolling (x%width)
    public short getCell(int x, int y) {
        return cells[mod(x, mapSize)][mod(y, mapSize)];
    }

    public void setCell(int x, int y, short newCell) {
        cells[mod(x, mapSize)][mod(y, mapSize)] = newCell;
    }

    public void calculateNextGeneration(GameRuleHelper.RuleSet ruleSet) {

        for (int x = 0; x < cells.length; x++) {
            for (int y = 0; y < cells[0].length; y++) {
                short currentCell = cells[x][y];
                boolean alive = isCellAlive(x, y);

                int neighbours = (currentCell & CURRENT_GEN_NEIGHBOURS_BITMASK) >>> 5;

                if (alive && !ruleSet.willSurvive(neighbours)) {
                    changeCellState(x, y);
                } else if (!alive && ruleSet.willBeBorn(neighbours)) {
                    changeCellState(x, y);
                }
            }
        }

    }

    public void calculateNextGenAsync() {
        ASyncNextGenerationTask aSyncNextGenerationTask = new ASyncNextGenerationTask();
        aSyncNextGenerationTask.execute(this);
    }

    private class ASyncNextGenerationTask extends AsyncTask<JavaActorManager, Void, Void> {

        @Override
        protected Void doInBackground(JavaActorManager... javaActorManagers) {
            JavaActorManager javaActorManager = javaActorManagers[0];

            javaActorManager.calculateNextGeneration(new GameRuleHelper.RuleSet(0b000011000000100));
            javaActorManager.fetchNextGeneration();
            return null;
        }
    }

    // Calculates m mod n mathematically

    private int mod(int m, int n) {
        return ((m % n) + n) % n;
    }

    public short[][] getCells() {
        return cells;
    }
}
