package hw2;

import edu.princeton.cs.introcs.StdRandom;
import edu.princeton.cs.algs4.StdStats;


public class PercolationStats {
    private double[] results;
    private int tests;

    public PercolationStats(int N, int T) {
        if (N <= 0 || T <= 0) {
            throw new IllegalArgumentException("N and T cannot be less than or equal to ZERO");
        }
        results = new double[T];
        for (int x = 0; x < T; x++) {
            Percolation sample = new Percolation(N);
            double n = 0;
            while (!sample.percolates()) {
                int r = StdRandom.uniform(N);
                int c = StdRandom.uniform(N);
                sample.open(r, c);
                n++;
            }
            results[x] = (n / (N * N));
        }
        tests = T;
    }

    public double mean() {
        return StdStats.mean(results);
    }

    public double stddev() {
        return StdStats.stddev(results);
    }

    public double confidenceLow() {
        double m = mean();
        double std = stddev();
        double sqrt = Math.pow(tests, 0.5);
        return (m - (1.96 * std / sqrt));
    }

    public double confidenceHigh() {
        double m = mean();
        double std = stddev();
        double sqrt = Math.pow(tests, 0.5);
        return (m + (1.96 * std / sqrt));
    }

    public static void main(String[] args) {
        PercolationStats test = new PercolationStats(200, 30);
        System.out.println(test.mean());
        System.out.println("Done");
    }

}                       
