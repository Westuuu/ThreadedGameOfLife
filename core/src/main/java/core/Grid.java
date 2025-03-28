package core;


import parallel.ThreadProcessor;

import java.util.List;

public class Grid {
    public static final int MIN_NEIGHBORS_SURVIVAL = 2;
    public static final int MAX_NEIGHBORS_SURVIVAL = 3;
    public static final int NEIGHBORS_FOR_BIRTH = 3;

    private final int width;
    private final int height;
    private CellState[][] currentGrid;
    private CellState[][] nextGrid;
    private final ThreadProcessor threadProcessor;

    public Grid(int width, int height) {
        this.width = width;
        this.height = height;
        this.currentGrid = new CellState[height][width];
        this.nextGrid = new CellState[height][width];

        this.threadProcessor = new ThreadProcessor(this);

        initializeGrids();
    }

    public void initializeGrids() {
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                currentGrid[row][col] = CellState.DEAD;
                nextGrid[row][col] = CellState.DEAD;
            }
        }
    }

    public CellState getCurrentGridCellState(int row, int col) {
        row = Math.floorMod(row, height);
        col = Math.floorMod(col, width);
        return currentGrid[row][col];
    }

    public void setCurrentGridCellState(List<int[]> liveCellCoordinates) {
        for(int[] cellCoordinates : liveCellCoordinates) {
            currentGrid[cellCoordinates[0]][cellCoordinates[1]] = CellState.ALIVE;
        }
    }

    public void setNextGridCellState(int row, int col, CellState state) {
        row = Math.floorMod(row, height);
        col = Math.floorMod(col, width);
        nextGrid[row][col] = state;
    }


    public int countLiveNeighbors(int row, int col) {
        int count = 0;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) continue;
                if (getCurrentGridCellState(row + i, col + j) == CellState.ALIVE) {
                    count++;
                }
            }
        }
        return count;
    }

    public void calculateNextGridThreaded(int startRow, int endRow) {
        for (int row = startRow; row < endRow; row++) {
            for (int col = 0; col < width; col++) {
                int liveNeighbors = countLiveNeighbors(row, col);
                CellState currentState = getCurrentGridCellState(row, col);

                if (currentState == CellState.ALIVE) {
                    if (liveNeighbors >= MIN_NEIGHBORS_SURVIVAL &&
                            liveNeighbors <= MAX_NEIGHBORS_SURVIVAL) {
                        setNextGridCellState(row, col, CellState.ALIVE);
                    } else {
                        setNextGridCellState(row, col, CellState.DEAD);
                    }
                } else {
                    if (liveNeighbors == NEIGHBORS_FOR_BIRTH) {
                        setNextGridCellState(row, col, CellState.ALIVE);
                    } else {
                        setNextGridCellState(row, col, CellState.DEAD);
                    }
                }
            }
        }
    }

    public void overwriteCurrentGrid(){
        this.currentGrid = this.nextGrid;
    }

    public void calculateNextGrid() throws InterruptedException {
        threadProcessor.executeTasks(height, this::calculateNextGridThreaded);
        overwriteCurrentGrid();
        this.nextGrid = new CellState[height][width];
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                sb.append(currentGrid[row][col] == CellState.ALIVE ? "■ " : "□ ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public int getHeight() {
        return height;
    }
}
