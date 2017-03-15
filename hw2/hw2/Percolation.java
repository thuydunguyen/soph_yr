package hw2;

import edu.princeton.cs.algs4.WeightedQuickUnionUF;

import java.util.ArrayList;

public class Percolation {
    private int[][] perc;
    private int size;
    private ArrayList<Integer> opened;
    private WeightedQuickUnionUF unite;

    public Percolation(int N) {
        perc = new int[N][N];
        for (int x = 0; x < N; x++) {
            for (int y = 0; y < N; y++) {
                perc[x][y] = 0;
            }
        }
        size = N;
        unite = new WeightedQuickUnionUF(N * N);
        opened = new ArrayList<>();
    }

    public void open(int row, int col) {
        if (row >= size || row < 0 || col >= size || col < 0) {
            throw new IndexOutOfBoundsException("Desired site is out of bounds");
        }
        perc[row][col] = 1;
        int D = xyto1D(row, col);
        opened.add(D);
        for (int z = 0; z < opened.size(); z++) {
            if (connects(opened.get(z), D)) {
                unite.union(opened.get(z), D);
            }
        }

    }

    public boolean isOpen(int row, int col) {
        if (row >= size || row < 0 || col >= size || col < 0) {
            throw new IndexOutOfBoundsException("Desired site is out of bounds");
        }
        return perc[row][col] == 1;
    }

    public boolean isFull(int row, int col) {
        if (row >= size || row < 0 || col >= size || col < 0) {
            throw new IndexOutOfBoundsException("Desired site is out of bounds");
        }
        int D = xyto1D(row, col);
        if (D < size - 1) {
            return true;
        }
        for (int x = 0; x < size; x++) {
            if (opened.contains(x)) {
                if (unite.connected(x, D)) {
                    return true;
                }
            }
        }
        return false;
    }

    public int numberOfOpenSites() {
        return opened.size();
    }

    public boolean percolates() {
        for (int x = size * (size - 1); x < size * size; x++) {
            if (opened.contains(x)) {
                for (int y = 0; y < size; y++) {
                    if (unite.connected(x, y)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private int xyto1D(int row, int col) {
        return col + row * size;
    }

    private boolean connects(int n1, int n2) {
        int diff = Math.abs(n1 - n2);
        return ((diff == size) || (diff == 1));
    }

    public static void main(String[] args) {
        Percolation p = new Percolation(5);
        p.open(3, 4);
        p.open(2, 4);
        p.open(2, 2);
        p.open(2, 3);
        p.open(0, 2);
        p.open(1, 2);
        p.open(4, 4);
        System.out.println(p.percolates());
    }

}                       
