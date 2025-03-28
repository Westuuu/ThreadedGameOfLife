package io;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class InputHandler {
    private int height;
    private int width;
    private int iterations;
    private final List<int[]> liveCellCoordinates;

    public InputHandler() {
        this.liveCellCoordinates = new ArrayList<>();
    }

    public void readFile(String filePath) throws IOException {
        if (filePath.startsWith("resources/")) {
            readFromResources(filePath.substring("resources/".length()));
        } else {
            try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
                parseBufferedReader(br);
            }
        }
    }

    private void readFromResources(String filePath) throws IOException {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(filePath)) {
            if (inputStream == null){
                throw new IOException("File not found: " + filePath);
            }
            try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
                parseBufferedReader(br);
            }
        }
    }

    private void parseBufferedReader(BufferedReader br) throws IOException {
        try {
            String heightStr = br.readLine();
            if (heightStr == null) throw new IllegalArgumentException("Missing height value");
            this.height = Integer.parseInt(heightStr.trim());
            
            String widthStr = br.readLine();
            if (widthStr == null) throw new IllegalArgumentException("Missing width value");
            this.width = Integer.parseInt(widthStr.trim());
            
            String iterationsStr = br.readLine();
            if (iterationsStr == null) throw new IllegalArgumentException("Missing iterations value");
            this.iterations = Integer.parseInt(iterationsStr.trim());
            
            String cellCountStr = br.readLine();
            if (cellCountStr == null) throw new IllegalArgumentException("Missing live cell count");
            int numberOfLiveCells = Integer.parseInt(cellCountStr.trim());

            int cellsRead = 0;
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.trim().split("\\s+");
                if (parts.length != 2) {
                    throw new IllegalArgumentException(
                        "Invalid coordinate format at line " + (cellsRead + 5) + ". Expected: 'row col'"
                    );
                }
                try {
                    int row = Integer.parseInt(parts[0]);
                    int col = Integer.parseInt(parts[1]);
                    validateLiveCoordinates(row, col);
                    liveCellCoordinates.add(new int[]{row, col});
                    cellsRead++;
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException(
                        "Invalid number format at line " + (cellsRead + 5) + ". Expected integers"
                    );
                }
            }

            if (cellsRead != numberOfLiveCells) {
                throw new IllegalArgumentException(
                    "Number of coordinates (" + cellsRead + ") doesn't match specified count (" + numberOfLiveCells + ")"
                );
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number format. Expected integers");
        }
    }

    private void validateLiveCoordinates(int row, int col) throws IllegalArgumentException {
        if (row >= height || col >= width || row < 0 || col < 0) {
            throw new IllegalArgumentException(
                    String.format("Invalid coordinates (%d,%d). Grid size is %dx%d",
                            row, col, height, width)
            );
        }

    }
    public int getHeight() {
        return height;
    }
    public int getWidth() {
        return width;
    }
    public int getIterations() {
        return iterations;
    }
    public List<int[]> getLiveCellCoordinates() {
        return liveCellCoordinates;
    }
}
