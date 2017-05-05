import javafx.collections.transformation.SortedList;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Boggle {
    private boolean random = false;
    private int rows = 4;
    private int cols = 4;
    private String dict = "words";
    private int nwords = 1;
    private String input;
    private String[][] board;
    private HashSet diction;
    private ArrayList<String> words = new ArrayList<>();
    private Comparator<String> comp = new Compares();
    private int maxL;


    public Boggle(String[] args) {
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
            } else if (x.equals("<")) {
                input = parts[ind + 1];
            }
            ind++;
        }
        createBoard();
        createDict();
        for (int x = 0; x < rows; x++) {
            for (int y = 0; y < cols; y++) {
                generateWords(board[x][y], x, y, false);
            }
        }
        Collections.sort(words, comp);
    }

    private void createBoard() {
        if (random) {
            board = new String[rows][cols];
            for (int x = 0; x < rows; x++) {
                for (int y = 0; y < cols; y++) {
                    board[x][y] = randomLett();
                }
            }
        } else {

            try {
                List<String> lines = Files.readAllLines(Paths.get(input));
                rows = lines.size();
                cols = lines.get(0).length();
                board = new String[rows][cols];
                for (int x = 0; x < rows; x++) {
                    String line = lines.get(x);
                    for (int ind = 0; ind < cols; ind++) {
                        board[x][ind] = line.substring(ind, ind + 1);
                    }
                }
            } catch (IOException e) {
                System.out.println("Not a file");
                Scanner token = new Scanner(input);
            }
        }
    }

    private ArrayList<int[]> getNeigh(int x, int y) {
        ArrayList<int[]> neigh = new ArrayList<>();

        neigh.add(new int[]{x - 1, y - 1});
        neigh.add(new int[]{x - 1, y});
        neigh.add(new int[]{x - 1, y + 1});
        neigh.add(new int[]{x, y - 1});
        neigh.add(new int[]{x, y + 1});
        neigh.add(new int[]{x, y});
        neigh.add(new int[]{x + 1, y - 1});
        neigh.add(new int[]{x + 1, y});
        neigh.add(new int[]{x + 1, y + 1});
        return neigh;
    }

    private void createDict() {
        diction = new HashSet();
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

    private void generateWords(String curr, int x, int y, boolean prev) {
        if (diction.contains(curr)) {
            if (!words.contains(curr)) {
                words.add(curr);
            }
            prev = true;
        }
        if ((prev == true) && (!diction.contains(curr))) {
            return;
        }
        if (curr.length() >= maxL) {
            return;
        }
        ArrayList<int[]> neighbors = getNeigh(x, y);

        for (int[] coord : neighbors) {
            int xn = coord[0];
            int yn = coord[1];
            if (xn >= 0 && yn >= 0 && xn < rows && yn < cols) {
                generateWords(curr + board[xn][yn], xn, yn, prev);

            }
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


    public static void main(String[] args) {
        String[] test = new String[]{"-k", "7", "<", "testBoggle"};
        Boggle game = new Boggle(test);
        for (int x = 0; x < game.nwords; x++) {
            System.out.println(game.words.get(x));
            if (x == game.words.size()-1) {
                break;
            }
        }
    }

}