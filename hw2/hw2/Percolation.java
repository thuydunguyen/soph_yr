package hw2;

import edu.princeton.cs.algs4.WeightedQuickUnionUF;

import java.util.ArrayList;
import java.util.HashSet;

public class Percolation {
    private int size;
    private HashSet<Integer> opened;
    private WeightedQuickUnionUF unite;
    private ArrayList<Integer> neighbors = new ArrayList<>();
    private ArrayList<Integer> top = new ArrayList<>();
    private ArrayList<Integer> bottom = new ArrayList<>();

    public Percolation(int N) {
        if (N <= 0) {
            throw new IllegalArgumentException("N must be greater than ZERO");
        }
        size = N;
        unite = new WeightedQuickUnionUF(N * N);
        opened = new HashSet();
        neighbors.add(1);
        neighbors.add(-1);
    }

    public void open(int row, int col) {
        if (row >= size || row < 0 || col >= size || col < 0) {
            throw new IndexOutOfBoundsException("Desired site is out of bounds");
        }
        int D = xyto1D(row, col);
        if (!opened.contains(D)) {
            opened.add(D);
            if (D < size) {
                top.add(D);
            }
            if (D >= size * (size - 1)) {
                bottom.add(D);
            }
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
        if (D < size) {
            return isOpen(row, col);
        }
        for (int x = 0; x < top.size(); x++) {
            if (unite.connected(top.get(x), D)) {
                return true;
            }
        }
        return false;
    }

    public int numberOfOpenSites() {
        return opened.size();
    }

    public boolean percolates() {
        for (int x = 0; x < bottom.size(); x++) {
            for (int y = 0; y < top.size(); y++) {
                if (unite.connected(top.get(y), bottom.get(x))) {
                    return true;
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
        System.out.println(p.isFull(2, 3));
        System.out.println(p.unite.connected(13, 2));

    }

}