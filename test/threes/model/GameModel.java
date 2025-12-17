

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package threes.model;
import java.util.*;

/**
 *
 * @author Titouan HETMANIAK
 */
public class GameModel {
    
    public static final int SIZE = 4;

    private final int[][] grid = new int[SIZE][SIZE];
    private final Random rng = new Random();

    private int movesCount = 0;

    public int getMovesCount() { return movesCount; }
    public int[][] getGrid() { return grid; }

    public void reset() {
        for (int r = 0; r < SIZE; r++) Arrays.fill(grid[r], 0);
        movesCount = 0;

        // Départ: 2 tuiles
        spawnRandomAnywhere();
        spawnRandomAnywhere();
    }

    private void spawnRandomAnywhere() {
        List<int[]> empties = emptyCells();
        if (empties.isEmpty()) return;
        int[] pos = empties.get(rng.nextInt(empties.size()));
        grid[pos[0]][pos[1]] = rng.nextBoolean() ? 1 : 2;
    }

    // Spawn sur un bord correspondant à la direction (règle demandée)
    public boolean spawnOnBorder(Direction dir) {
        List<int[]> candidates = new ArrayList<>();
        switch (dir) {
            case LEFT:
                for (int r = 0; r < SIZE; r++) if (grid[r][SIZE-1] == 0) candidates.add(new int[]{r, SIZE-1});
                break;
            case RIGHT:
                for (int r = 0; r < SIZE; r++) if (grid[r][0] == 0) candidates.add(new int[]{r, 0});
                break;
            case UP:
                for (int c = 0; c < SIZE; c++) if (grid[SIZE-1][c] == 0) candidates.add(new int[]{SIZE-1, c});
                break;
            case DOWN:
                for (int c = 0; c < SIZE; c++) if (grid[0][c] == 0) candidates.add(new int[]{0, c});
                break;
        }
        if (candidates.isEmpty()) return false;

        int[] pos = candidates.get(rng.nextInt(candidates.size()));
        grid[pos[0]][pos[1]] = rng.nextBoolean() ? 1 : 2;
        return true;
    }

    private List<int[]> emptyCells() {
        List<int[]> res = new ArrayList<>();
        for (int r = 0; r < SIZE; r++)
            for (int c = 0; c < SIZE; c++)
                if (grid[r][c] == 0) res.add(new int[]{r,c});
        return res;
    }

    public boolean isGameOver() {
        if (!emptyCells().isEmpty()) return false;

        // Si une fusion possible, pas fini
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                int v = grid[r][c];
                if (c+1 < SIZE && canMerge(v, grid[r][c+1])) return false;
                if (r+1 < SIZE && canMerge(v, grid[r+1][c])) return false;
            }
        }
        return true;
    }

    public MoveResult move(Direction dir) {
        boolean movedAny = false;
        int merges = 0;

        // On traite ligne par ligne / colonne par colonne selon la direction
        // Le principe: extraire une ligne, la "compresser" avec fusions, puis la remettre.
        if (dir == Direction.LEFT) {
            for (int r = 0; r < SIZE; r++) {
                int[] line = new int[SIZE];
                for (int c = 0; c < SIZE; c++) line[c] = grid[r][c];
                LineResult lr = compressAndMerge(line);
                movedAny |= lr.changed;
                merges += lr.merges;
                for (int c = 0; c < SIZE; c++) grid[r][c] = lr.out[c];
            }
        } else if (dir == Direction.RIGHT) {
            for (int r = 0; r < SIZE; r++) {
                int[] line = new int[SIZE];
                for (int c = 0; c < SIZE; c++) line[c] = grid[r][SIZE-1-c];
                LineResult lr = compressAndMerge(line);
                movedAny |= lr.changed;
                merges += lr.merges;
                for (int c = 0; c < SIZE; c++) grid[r][SIZE-1-c] = lr.out[c];
            }
        } else if (dir == Direction.UP) {
            for (int c = 0; c < SIZE; c++) {
                int[] line = new int[SIZE];
                for (int r = 0; r < SIZE; r++) line[r] = grid[r][c];
                LineResult lr = compressAndMerge(line);
                movedAny |= lr.changed;
                merges += lr.merges;
                for (int r = 0; r < SIZE; r++) grid[r][c] = lr.out[r];
            }
        } else if (dir == Direction.DOWN) {
            for (int c = 0; c < SIZE; c++) {
                int[] line = new int[SIZE];
                for (int r = 0; r < SIZE; r++) line[r] = grid[SIZE-1-r][c];
                LineResult lr = compressAndMerge(line);
                movedAny |= lr.changed;
                merges += lr.merges;
                for (int r = 0; r < SIZE; r++) grid[SIZE-1-r][c] = lr.out[r];
            }
        }

        if (movedAny) movesCount++;
        return new MoveResult(movedAny, merges);
    }

    private static class LineResult {
        int[] out;
        boolean changed;
        int merges;
        LineResult(int[] out, boolean changed, int merges){
            this.out = out; this.changed = changed; this.merges = merges;
        }
    }

    private LineResult compressAndMerge(int[] line) {
        int[] original = Arrays.copyOf(line, SIZE);

        // 1) compresser (enlever les 0)
        List<Integer> vals = new ArrayList<>();
        for (int v : line) if (v != 0) vals.add(v);

        // 2) fusionner (une seule fusion par paire, de gauche à droite)
        List<Integer> merged = new ArrayList<>();
        int merges = 0;
        int i = 0;
        while (i < vals.size()) {
            if (i + 1 < vals.size() && canMerge(vals.get(i), vals.get(i+1))) {
                int a = vals.get(i), b = vals.get(i+1);
                merged.add(mergeValue(a, b));
                merges++;
                i += 2;
            } else {
                merged.add(vals.get(i));
                i += 1;
            }
        }

        // 3) re-remplir à taille 4
        int[] out = new int[SIZE];
        for (int k = 0; k < merged.size() && k < SIZE; k++) out[k] = merged.get(k);

        boolean changed = !Arrays.equals(original, out);
        return new LineResult(out, changed, merges);
    }

    private boolean canMerge(int a, int b) {
        if (a == 0 || b == 0) return false;
        // 1+2 =>3 (dans les 2 sens)
        if ((a == 1 && b == 2) || (a == 2 && b == 1)) return true;
        // x+x seulement si x>=3
        if (a >= 3 && a == b) return true;
        return false;
    }

    private int mergeValue(int a, int b) {
        if ((a == 1 && b == 2) || (a == 2 && b == 1)) return 3;
        return a * 2; // (a==b>=3)
    }
}

    

