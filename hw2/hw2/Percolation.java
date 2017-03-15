package hw2;

import edu.princeton.cs.algs4.WeightedQuickUnionUF;

import java.util.ArrayList;

public class Percolation {
    private int size;
    private ArrayList<Integer> opened;
    private WeightedQuickUnionUF unite;
    private ArrayList<Integer> neighbors = new ArrayList<>();

    public Percolation(int N) {
        if (N <= 0) {
            throw new IllegalArgumentException("N must be greater than ZERO");
        }
        size = N;
        unite = new WeightedQuickUnionUF(N * N);
        opened = new ArrayList<>();
        neighbors.add(1);
        neighbors.add(-1);
    }

    public void open(int row, int col) {
        if (row >= size || row < 0 || col >= size || col < 0) {
            throw new IndexOutOfBoundsException("Desired site is out of bounds");
        }
        int D = xyto1D(row, col);
        opened.add(D);
        for (int z = 0; z < 2; z++) {
            int cols = col + neighbors.get(z);
            int rows = row + neighbors.get(z);
            if (rows >= 0 && rows < size) {
                if (isOpen(rows, col)) {
                    int d = xyto1D(rows, col);
                    unite.union(d, D);
                }
            }
            if (cols >= 0 && cols < size) {
                if (isOpen(row, cols)) {
                    int d = xyto1D(row, cols);
                    unite.union(d, D);
                }
            }
        }

    }

    public boolean isOpen(int row, int col) {
        if (row >= size || row < 0 || col >= size || col < 0) {
            throw new IndexOutOfBoundsException("Desired site is out of bounds");
        }
        int D = xyto1D(row, col);
        return opened.contains(D);
    }

    public boolean isFull(int row, int col) {
        if (row >= size || row < 0 || col >= size || col < 0) {
            throw new IndexOutOfBoundsException("Desired site is out of bounds");
        }
        int D = xyto1D(row, col);
        if (D < size - 1) {
            return isOpen(row, col);
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
                    if (opened.contains(y)) {
                        if (unite.connected(y, x)) {
                            return true;
                        }
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
