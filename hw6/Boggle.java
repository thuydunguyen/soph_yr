import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;

public class Boggle {
    private boolean random = false;
    private int rows = 4;
    private int cols = 4;
    private String dict = "words";
    private int nwords = 1;
    private String input;
    private ArrayList<Connect> board;

    public Boggle(String args) {
        String splitter = "[-<]";
        String[] parts = args.split(splitter);

        for (String x : parts) {
            String[] line = x.split("\\s");
            if (line[0].equals("r")) {
                random = true;
            } else if (line[0].equals("n")) {
                try {
                    rows = Integer.valueOf(line[1]);
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("n dim must be a pos integer.");

                }
            } else if (line[0].equals("m")) {
                try {
                    cols = Integer.valueOf(line[1]);
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("m dim must be a pos integer.");

                }
            } else if (line[0].equals("d")) {
                dict = line[1];
            } else if (line[0].equals("k")) {
                nwords = Integer.valueOf(line[1]);
            } else {
                input = line[0];
            }
        }
    }

    private class Connect {
        private HashSet adj;

        Connect(String[] conn) {
            adj = new HashSet<String>(Arrays.asList(conn));
        }

        private void addAdj(String letter) {
            adj.add(letter);
        }
    }

    private void createBoard() {

        Scanner sc = new Scanner(input);

    }


    public static void main(String[] args) {
        System.out.println("longitudes");
        System.out.println("omniscient");
    }

}