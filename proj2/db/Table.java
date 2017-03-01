package db;
import java.util.*;

/**
 * Created by Thuy-Du on 2/26/2017.
 */
public class Table<T> {
    private boolean CrTableSel = false;
    private ArrayList<ArrayList<String>> table;

    public Table() {
        table = new ArrayList<>();
    }

    public Table(int cols, String[] col_names, String[] col_types) {
        table = new ArrayList<>(col_names.length);
        for (int x = 0; x < col_names.length; x++) {
            ArrayList<String> sets = new ArrayList<>();
            sets.add(col_names[x]);
            sets.add(col_types[x]);
            table.add(sets);
        }
    }

    public void insert(T[] data) {
        for (int x = 0; x < table.size(); x++) {
            ArrayList copy = table.get(x);
            String point = data[x].toString();
            copy.add(point);
            table.set(x, copy);
        }
    }

    public void print() {
        //Prints col_name and col_type
        String comma;
        for (int x = 0; x < table.size(); x++) {
            if (x == table.size() - 1) {
                comma = "";
                System.out.println(table.get(x).get(0) + " " + table.get(x).get(1) + comma);

            } else {
                comma = ",";
                System.out.print(table.get(x).get(0) + " " + table.get(x).get(1) + comma);
            }
        }
        //Prints rest of the data
        for (int y = 2; y < table.get(0).size(); y++) {
            for (int x = 0; x < table.size(); x++) {
                if (x == table.size() - 1) {
                    comma = "";
                    System.out.println(table.get(x).get(y) + comma);

                } else {
                    comma = ",";
                    System.out.print(table.get(x).get(y) + comma);
                }
            }
        }
    }
    //This main method is for testing.
    public static void main(String[] args) {
        Table t = new Table(3, new String[] {"a", "b", "c"}, new String[] {"String", "String", "String"});
        t.insert(new Integer[] {1,2,3});
        t.insert(new Integer[] {4,5,6});
        t.print();
    }
}
