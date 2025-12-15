
import java.util.Random;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Titouan HETMANIAK
 */
public class GameModel {
    


    public static final int SIZE = 4;

    public enum Dir { UP, DOWN, LEFT, RIGHT }

    private final int[][] grid = new int[SIZE][SIZE];
    private final Random rng = new Random();

    private int moves = 0;
    private boolean gameOver = false;

    public GameModel() {
        reset();
    }

    public void reset() {
        for (int r = 0; r < SIZE; r++)
            for (int c = 0; c < SIZE; c++)
                grid[r][c] = 0;

        moves = 0;
        gameOver = false;

        // Démarrage : 2 tuiles sur des bords
        spawnOnBorder();
        spawnOnBorder();
        updateGameOver();
    }

    public int[][] getGridCopy() {
        int[][] copy = new int[SIZE][SIZE];
        for (int r = 0; r < SIZE; r++)
            System.arraycopy(grid[r], 0, copy[r], 0, SIZE);
        return copy;
    }

    public int getMoves() { return moves; }
    public boolean isGameOver() { return gameOver; }

    public boolean move(Dir dir) {
        if (gameOver) return false;

        int[][] before = getGridCopy();

        switch (dir) {
            case LEFT -> moveLeft();
            case RIGHT -> moveRight();
            case UP -> moveUp();
            case DOWN -> moveDown();
        }

        boolean changed = !sameGrid(before, grid);

        if (changed) {
            moves++;
            spawnOnBorder();
            updateGameOver();
        }

        return changed;
    }

    private void moveLeft() {
        for (int r = 0; r < SIZE; r++) {
            int[] line = new int[SIZE];
            for (int c = 0; c < SIZE; c++) line[c] = grid[r][c];
            int[] merged = compressAndMerge(line);
            for (int c = 0; c < SIZE; c++) grid[r][c] = merged[c];
        }
    }

    private void moveRight() {
        for (int r = 0; r < SIZE; r++) {
            int[] line = new int[SIZE];
            for (int c = 0; c < SIZE; c++) line[c] = grid[r][SIZE - 1 - c];
            int[] merged = compressAndMerge(line);
            for (int c = 0; c < SIZE; c++) grid[r][SIZE - 1 - c] = merged[c];
        }
    }

    private void moveUp() {
        for (int c = 0; c < SIZE; c++) {
            int[] line = new int[SIZE];
            for (int r = 0; r < SIZE; r++) line[r] = grid[r][c];
            int[] merged = compressAndMerge(line);
            for (int r = 0; r < SIZE; r++) grid[r][c] = merged[r];
        }
    }

    private void moveDown() {
        for (int c = 0; c < SIZE; c++) {
            int[] line = new int[SIZE];
            for (int r = 0; r < SIZE; r++) line[r] = grid[SIZE - 1 - r][c];
            int[] merged = compressAndMerge(line);
            for (int r = 0; r < SIZE; r++) grid[SIZE - 1 - r][c] = merged[r];
        }
    }

    /**
     * Prend une ligne orientée vers la gauche (index 0 = côté "poussé"),
     * - compresse (enlève les 0),
     * - fusionne suivant règles :
     *   - a == b => 2a
     *   - {1,2} => 3
     * - puis recompresse et remplit de 0.
     */
    private int[] compressAndMerge(int[] line) {
        List<Integer> compact = new ArrayList<>();
        for (int v : line) if (v != 0) compact.add(v);

        List<Integer> out = new ArrayList<>();
        int i = 0;
        while (i < compact.size()) {
            int a = compact.get(i);

            if (i + 1 < compact.size()) {
                int b = compact.get(i + 1);

                if (canMerge(a, b)) {
                    out.add(mergeResult(a, b));
                    i += 2;
                    continue;
                }
            }

            out.add(a);
            i++;
        }

        int[] res = new int[SIZE];
        for (int k = 0; k < out.size() && k < SIZE; k++) res[k] = out.get(k);
        return res;
    }

    private boolean canMerge(int a, int b) {
        if (a == 0 || b == 0) return false;
        if (a == b) return true;
        // 1 + 2 = 3 dans les deux sens
        return (a == 1 && b == 2) || (a == 2 && b == 1);
    }

    private int mergeResult(int a, int b) {
        if ((a == 1 && b == 2) || (a == 2 && b == 1)) return 3;
        return a * 2; // a==b
    }

    private void spawnOnBorder() {
        List<int[]> empties = new ArrayList<>();

        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                boolean isBorder = (r == 0 || r == SIZE - 1 || c == 0 || c == SIZE - 1);
                if (isBorder && grid[r][c] == 0) empties.add(new int[]{r, c});
            }
        }

        if (empties.isEmpty()) return; // aucun bord libre

        int[] pos = empties.get(rng.nextInt(empties.size()));
        grid[pos[0]][pos[1]] = rng.nextBoolean() ? 1 : 2;
    }

    private void updateGameOver() {
        // Si une case vide existe => pas game over
        for (int r = 0; r < SIZE; r++)
            for (int c = 0; c < SIZE; c++)
                if (grid[r][c] == 0) {
                    gameOver = false;
                    return;
                }

        // Sinon, vérifier fusions possibles adjacentes
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                int v = grid[r][c];
                if (r + 1 < SIZE && canMerge(v, grid[r + 1][c])) { gameOver = false; return; }
                if (c + 1 < SIZE && canMerge(v, grid[r][c + 1])) { gameOver = false; return; }
            }
        }

        gameOver = true;
    }

    private boolean sameGrid(int[][] a, int[][] b) {
        for (int r = 0; r < SIZE; r++)
            for (int c = 0; c < SIZE; c++)
                if (a[r][c] != b[r][c]) return false;
        return true;
    }
}

    

