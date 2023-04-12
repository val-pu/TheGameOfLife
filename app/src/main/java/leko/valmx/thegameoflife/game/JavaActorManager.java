package leko.valmx.thegameoflife.game;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Objects;

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


    public JavaActorManager(Context context) {
//        for (int i = 0; i < 20000; i++) {
//            int x = Random.Default.nextInt(mapSizeX);
//            int y = Random.Default.nextInt(mapSizeY);
//            if ((getCell(x, y) & ALIVE_NEXT_GEN_BITMASK) == 0)
//                changeCellState(x, y);
//        }
        fetchNextGeneration();

        // Loading user gamerule settings.
        GameRuleHelper gameRuleHelper = new GameRuleHelper(context);
        gameRuleHelper.loadRules();
    }

    private GameRuleHelper.RuleSet ruleSet = new GameRuleHelper.RuleSet(0b000011000000100);

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

    public static final int mapSizeX = 500;
    public static final int mapSizeY = 500;

    private short[][] cells = new short[mapSizeX][mapSizeY];

    private ArrayList<Cell> aliveCells = new ArrayList<>();
    private ArrayList<Cell> aliveCellsNextGeneration = new ArrayList<>();

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

        if (!kill) {
            aliveCellsNextGeneration.add(new Cell(mod(x, mapSizeX), mod(y, mapSizeY)));
        }

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
        }

    }

    /**
     * Changes the state of thegiven cell to alive or dead and vice versa
     */
    public void switchCurrentState(int x, int y) {
        if (isCellAlive(x, y)) {
            setCurrentlyDead(x, y);
            return;
        }

        setCurrentlyAlive(x, y);

    }

    public void setCurrentlyAlive(int x, int y) {
        if (isCellAlive(x, y)) return;
        short currentCell = getCell(x, y);

        currentCell ^= ALIVE_NEXT_GEN_BITMASK;

        updateNeighboursOfCell(x, y, 1);

        setCell(x, y, currentCell);
        fetchCell(x, y);
        aliveCells.add(new Cell(mod(x, mapSizeX), mod(y, mapSizeY)));
    }

    public void setCurrentlyDead(int x, int y) {
        if (!isCellAlive(x, y)) return;

        short currentCell = getCell(x, y);

        currentCell &= ~ALIVE_NEXT_GEN_BITMASK;

        updateNeighboursOfCell(x, y, -1);

        setCell(x, y, currentCell);
        fetchCell(x, y);
        aliveCells.remove(new Cell(mod(x, mapSizeX), mod(y, mapSizeY)));
    }

    private void updateNeighboursOfCell(int x, int y, int count) {
        for (int[] offset : offsets) {
            int currentX = x + offset[0];
            int currentY = y + offset[1];
            int currentCell = (getCell(currentX, currentY));


            int currentNeighbours = (currentCell & NEXT_GEN_NEIGHBOURS_BITMASK);
            currentNeighbours += count;

            setCell(currentX, currentY, (short) ((currentCell & (~NEXT_GEN_NEIGHBOURS_BITMASK)) + currentNeighbours));
            fetchCell(currentX, currentY);
        }
    }

    /**
     * Shifts all cell shorts << 5
     */
    private void fetchNextGeneration() {
        for (int x = 0; x < cells.length; x++) {
            for (int y = 0; y < cells[0].length; y++) {
                fetchCell(x, y);
            }
        }
        aliveCells = aliveCellsNextGeneration;
        aliveCellsNextGeneration = new ArrayList<>();
    }

    private void fetchCell(int x, int y) {
        if (x < 0 || y < 0) return;
        cells[x][y] = (short) ((cells[x][y] & 0b11111) | (cells[x][y] << 5));
    }

    public boolean isCellAlive(int x, int y) {
        return (getCell(x, y) & ALIVE_CURRENT_GEN_BITMASK) != 0;
    }

    // Getting cells with side scrolling (x%width)
    public short getCell(int x, int y) {
        return cells[mod(x, mapSizeX)][mod(y, mapSizeY)];
    }

    public void setCell(int x, int y, short newCell) {
        cells[mod(x, mapSizeX)][mod(y, mapSizeY)] = newCell;
    }

    private HashSet<Cell> lookedUpNeighbours;

    public void calculateNextGeneration(GameRuleHelper.RuleSet ruleSet) {
        lookedUpNeighbours = new HashSet<>();

        for (Cell currentCell : aliveCells) {
            if (!lookedUpNeighbours.contains(currentCell)) {
                calculateNextGenerationForCell(currentCell.x, currentCell.y);
            }
        }

        /*for (int x = 0; x < cells.length; x++) {
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
        }*/
    }

    private void calculateNextGenerationForCell(int x, int y) {

        if (lookedUpNeighbours.contains(new Cell(mod(x, mapSizeX), mod(y, mapSizeY)))) return;
        lookedUpNeighbours.add(new Cell(mod(x, mapSizeX), mod(y, mapSizeY)));

        short currentCell = getCell(x, y);
        int neighbours = (currentCell & CURRENT_GEN_NEIGHBOURS_BITMASK) >>> 5;
        if (neighbours >= 8) neighbours = 7;
        // Checking

        if (!ruleSet.willSurvive(neighbours))
            changeCellState(x, y);
        else aliveCellsNextGeneration.add(new Cell(mod(x, mapSizeX), mod(y, mapSizeY)));

        for (int[] offset : offsets) {

            int currX = x + offset[0];
            int currY = y + offset[1];
            Cell c = new Cell(mod(currX,mapSizeX), mod(currY,mapSizeY));

            if (lookedUpNeighbours.contains(c)) continue;
            if (isCellAlive(currX, currY)) continue;

            lookedUpNeighbours.add(c);

            if (ruleSet.willBeBorn(getCurrentNeighboursOfCellCurrentGeneration(currX, currY)))
                changeCellState(currX, currY);


        }

    }

    public ArrayList<Cell> getAliveCells() {
        return aliveCells;
    }

    private int getCurrentNeighboursOfCellCurrentGeneration(int x, int y) {
        short cell = getCell(x, y);
        return (cell & CURRENT_GEN_NEIGHBOURS_BITMASK) >>> 5;
    }

    public void calculateNextGenAsync() {
        if(!ASyncNextGenerationTask.PROCESS_RUNNING) {
            ASyncNextGenerationTask aSyncNextGenerationTask = new ASyncNextGenerationTask();
            aSyncNextGenerationTask.execute(this);
        }
    }

    public void clearCells() {
        cells = new short[mapSizeX][mapSizeY];
    }

    private static class ASyncNextGenerationTask extends AsyncTask<JavaActorManager, Void, Void> {

        public static boolean PROCESS_RUNNING = false;

        @Override
        protected void onPreExecute() {
            PROCESS_RUNNING = true;
        }

        @Override
        protected void onPostExecute(Void unused) {
            PROCESS_RUNNING = false;
        }

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

    public void setRuleSet(GameRuleHelper.RuleSet ruleSet) {
        this.ruleSet = ruleSet;
    }

    public GameRuleHelper.RuleSet getRuleSet() {
        return ruleSet;
    }

    public static class Cell {
        int x;
        int y;

        public Cell(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(@Nullable Object obj) {
            if (!(obj instanceof Cell)) return false;

            Cell o = (Cell) obj;

            return o.x == x && o.y == y;

        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }
}
