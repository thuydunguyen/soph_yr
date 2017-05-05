import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;

public class Boggle {
    private boolean random = false;
    private int rows = 4;
    private int cols = 4;
    private String dict = "words";
    private int nwords = 1;
    private Scanner input;
    private ArrayList<ArrayList<String>> board;
    private DictHashSet diction;
    private ArrayList<String> words = new ArrayList<>();
    private Comparator<String> comp = new Compares();
    private int maxL;


    public Boggle(String[] args, Scanner file) {
        String[] parts = args;
        int ind = 0;
        for (String x : parts) {
            if (x.equals("-r")) {
                random = true;
            } else if (x.equals("-n")) {
                try {
                    rows = Integer.valueOf(parts[ind + 1]);
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("n dim must be a pos integer.");

                }
            } else if (x.equals("-m")) {
                try {
                    cols = Integer.valueOf(parts[ind + 1]);
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("m dim must be a pos integer.");

                }
            } else if (x.equals("-d")) {
                dict = parts[ind + 1];
            } else if (x.equals("-k")) {
                nwords = Integer.valueOf(parts[ind + 1]);
            }
            ind++;
        }
        input = file;
        createBoard();
        createDict();
        for (int x = 0; x < rows; x++) {
            for (int y = 0; y < cols; y++) {
                generateWords(board.get(x).get(y), x, y, new CoordHashSet());
            }
        }
        Collections.sort(words, comp);
    }

    private void createBoard() {
        board = new ArrayList<>();
        if (random) {
            for (int x = 0; x < rows; x++) {
                ArrayList<String> part = new ArrayList<>();
                for (int y = 0; y < cols; y++) {
                    part.add(randomLett());
                }
                board.add(part);
            }
        } else {
            while (input.hasNext()) {
                String line = input.next();
                cols = line.length();
                ArrayList<String> part = new ArrayList<>();
                for (int x = 0; x < cols; x++) {
                    part.add(line.substring(x, x + 1));
                }
                board.add(part);
            }
            rows = board.size();
        }
    }

    private ArrayList<int[]> getNeigh(int x, int y) {
        ArrayList<int[]> neigh = new ArrayList<>();

        neigh.add(new int[]{x - 1, y - 1});
        neigh.add(new int[]{x - 1, y});
        neigh.add(new int[]{x - 1, y + 1});
        neigh.add(new int[]{x, y - 1});
        neigh.add(new int[]{x, y + 1});
        neigh.add(new int[]{x + 1, y - 1});
        neigh.add(new int[]{x + 1, y});
        neigh.add(new int[]{x + 1, y + 1});
        return neigh;
    }

    private void createDict() {
        diction = new DictHashSet();
        maxL = 0;
        try {
            List<String> lines = Files.readAllLines(Paths.get(dict));
            for (String x : lines) {
                if (maxL < x.length()) {
                    maxL = x.length();
                }
                diction.add(x);
            }
        } catch (IOException f) {
            System.out.printf("No such file exists");
        }
    }

    private void generateWords(String curr, int x, int y, CoordHashSet used) {
        if (diction.contains(curr)) {
            if (!words.contains(curr)) {
                words.add(curr);
            }
        }
        if (diction.partMatch(curr)) {
            ArrayList<int[]> neighbors = getNeigh(x, y);
            used.add(new int[]{x, y});
            for (int[] coord : neighbors) {
                int xn = coord[0];
                int yn = coord[1];
                CoordHashSet nused = new CoordHashSet();
                for (int[] check : used) {
                    nused.add(check);
                }
                if (xn >= 0 && yn >= 0 && xn < rows && yn < cols && !nused.containsCoord(coord)) {
                    generateWords(curr + board.get(xn).get(yn), xn, yn, nused);

                }
            }
        } else {
            return;
        }

    }


    private String randomLett() {
        Random r = new Random();
        String c = (r.nextInt(26) + "a");
        return c;
    }

    private class Compares implements Comparator<String> {
        public int compare(String x, String y) {
            int xl = x.length();
            int yl = y.length();
            if (xl > yl) {
                return -1;
            } else if (xl < yl) {
                return 1;
            } else {
                return x.compareTo(y);
            }
        }
    }

    private class CoordHashSet extends HashSet<int[]> {
        public boolean containsCoord(int[] Coord) {
            for (int[] coords : this) {
                if (coords[0] == Coord[0] && coords[1] == Coord[1]) {
                    return true;
                }
            }
            return false;
        }
    }

    private class DictHashSet extends HashSet<String> {
        public boolean partMatch(String match) {
            for (String x : this) {
                if (x.startsWith(match)) {
                    return true;
                }
            }
            return false;
        }
    }


    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        Boggle game = new Boggle(args, scan);
        for (int x = 0; x < game.nwords; x++) {
            System.out.println(game.words.get(x));
            if (x == game.words.size() - 1) {
                break;
            }
        }

    }

}