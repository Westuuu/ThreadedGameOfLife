package parallel;


import core.Grid;

import java.util.concurrent.CountDownLatch;

public class ThreadProcessor {
    int numberOfThreads;
    private CountDownLatch latch;
    Grid grid;

    public ThreadProcessor(Grid grid) {
        this.grid = grid;
        calculateThreads();
    }

    public void executeTasks(int totalRows, Task task) throws InterruptedException {
        this.latch = new CountDownLatch(numberOfThreads);
        Thread[] threads = new Thread[numberOfThreads];

        int baseRowsPerThread = totalRows / numberOfThreads;
        int leftoverRows = totalRows % numberOfThreads;

        int startRow = 0;

        for (int threadID = 0; threadID < numberOfThreads; threadID++) {
            int rowsForThisThread = baseRowsPerThread + (threadID < leftoverRows ? 1 : 0);
            int endRow = startRow + rowsForThisThread;

            final int threadStartRow = startRow;
            final int threadEndRow = endRow;

            System.out.printf("tid: %d: rows: %d:%d (%d) cols: %d:%d (%d)%n",
                    threadID, threadStartRow, threadEndRow - 1, threadEndRow - threadStartRow,
                    0, grid.getHeight() - 1, grid.getHeight());

            threads[threadID] = new Thread(() -> {
                try {
                    task.execute(threadStartRow, threadEndRow);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
            threads[threadID].start();

            startRow = endRow;
        }

        latch.await();
    }

    private void calculateThreads(){
        this.numberOfThreads = Math.min(grid.getHeight(), Runtime.getRuntime().availableProcessors());
    }
}
