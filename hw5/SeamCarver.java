import edu.princeton.cs.algs4.Picture;

import java.awt.Color;

/**
 * Created by Thuy-Du on 4/26/2017.
 */
public class SeamCarver {
    private double[][] energies;
    private int height;
    private int width;
    private Picture pic;

    private class Node {
        private double sEnergy;
        private Node parent;
        private int x;
        private int y;

        Node(Node p, double energy, int x, int y) {
            parent = p;
            if (parent == null) {
                sEnergy = energy;
            } else {
                sEnergy = parent.sEnergy + energy;
            }
            this.x = x;
            this.y = y;
        }
    }

    public SeamCarver(Picture picture) {
        height = picture.height();
        width = picture.width();
        energies = new double[height][width];
        pic = new Picture(picture);
        energied();
    }

    public Picture picture() {
        return pic;
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public double energy(int x, int y) {
        if (energies[y][x] == 0) {
            int xf = addOne(x, "w");
            int xb = minusOne(x, "w");
            int yf = addOne(y, "h");
            int yb = minusOne(y, "h");
            Color wf = pic.get(xf, y);
            Color wb = pic.get(xb, y);
            Color hf = pic.get(x, yf);
            Color hb = pic.get(x, yb);

            double w = calcE(wf, wb);
            double h = calcE(hf, hb);
            energies[y][x] = w + h;
        }
        return energies[y][x];

    }

    public int[] findHorizontalSeam() {
        energies = transpose(energies);
        int[] result = findVerticalSeam();
        energies = transpose(energies);
        return result;
    }

    public int[] findVerticalSeam() {
        Node[] hs = new Node[energies[0].length];
        for (int x = 0; x < energies[0].length; x++) {
            Node n = new Node(null, energy(x, 0), x, 0);
            for (int y = 0; y < energies.length - 1; y++) {
                double f;
                double s;
                double t;
                if (x == 0) {
                    f = Double.MAX_VALUE;
                    s = energies[y + 1][x];
                    t = energies[y + 1][x + 1];
                } else if (x == energies[0].length - 1) {
                    t = Double.MAX_VALUE;
                    f = energies[y + 1][x - 1];
                    s = energies[y + 1][x];
                } else {
                    f = energies[y + 1][x - 1];
                    s = energies[y + 1][x];
                    t = energies[y + 1][x + 1];
                }
                if (f <= s && f <= t) {
                    n = new Node(n, f, x - 1, y + 1);
                } else if (s <= f && s <= t) {
                    n = new Node(n, s, x, y + 1);
                } else if (t <= s && t <= f) {
                    n = new Node(n, t, x + 1, y + 1);
                }
            }
            hs[x] = n;
        }
        int[] verts = new int[energies.length];
        int c = energies.length - 1;
        Node min = minNode(hs);
        while (min.parent != null) {
            verts[c] = min.x;
            min = min.parent;
            c--;
        }
        verts[0] = min.x;
        return verts;
    }

    public void removeHorizontalSeam(int[] seam) {

    }

    public void removeVerticalSeam(int[] seam) {
        
    }

    private Node minNode(Node[] nodes) {
        double min = nodes[0].sEnergy;
        int minNode = 0;
        for (int x = 1; x < nodes.length; x++) {
            if (nodes[x].sEnergy < min) {
                min = nodes[x].sEnergy;
                minNode = x;
            }
        }
        return nodes[minNode];
    }

    private void energied() {
        for (int x = 0; x < height; x++) {
            for (int y = 0; y < width; y++) {
                energies[x][y] = energy(y, x);
            }
        }
    }


    private int addOne(int x, String horw) {
        x++;
        if (horw.equals("h")) {
            if (x == height) {
                x = 0;
            }
        } else if (horw.equals("w")) {
            if (x == width) {
                x = 0;
            }
        }
        return x;
    }

    private int minusOne(int x, String horw) {
        x--;
        if (horw.equals("h")) {
            if (x < 0) {
                x = height - 1;
            }
        } else if (horw.equals("w")) {
            if (x < 0) {
                x = width - 1;
            }
        }
        return x;
    }

    private double calcE(Color b, Color f) {
        double dR = Math.pow(Math.abs(b.getRed() - f.getRed()), 2);
        double dB = Math.pow(Math.abs(b.getBlue() - f.getBlue()), 2);
        double dG = Math.pow(Math.abs(b.getGreen() - f.getGreen()), 2);
        return dR + dB + dG;

    }

    private static double[][] transpose(double[][] array) {
        int c = array[0].length;
        int r = array.length;
        double[][] newarray = new double[c][r];
        for (int i = 0; i < r; i++) {
            for (int j = 0; j < c; j++) {
                newarray[j][i] = array[i][j];
            }
        }
        return newarray;
    }

}
