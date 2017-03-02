package db;
import java.util.*;
import db.G_func;

/**
 * Created by Thuy-Du on 2/26/2017.
 */
public class Table<T> {
    private boolean CrTableSel = false;
    private ArrayList<ArrayList<String>> table;
    private ArrayList<String> names;
    private ArrayList<String> types;
    private int rows;

    //Creates blank table
    public Table() {
        table = new ArrayList<>();
    }

    //Creates copy of a Table instance
    public Table(Table to_copy) {
        names = to_copy.names;
        types = to_copy.types;
        rows = to_copy.rows;
        table = to_copy.cloning();
    }

    //Creates new table
    public Table(int cols, ArrayList<String> col_names, ArrayList<String> col_types) {
        names = col_names;
        types = col_types;
        rows = 2;
        table = new ArrayList<>(col_names.size());
        for (int x = 0; x < col_names.size(); x++) {
            ArrayList<String> sets = new ArrayList<>();
            sets.add(col_names.get(x));
            sets.add(col_types.get(x));
            table.add(sets);
        }
    }
    //Creates table as select aka Join
    //Need to add cases for no shared columns and multiple shared columns
    public Table(Table[] tables) {
        Table curr = new Table(tables[0]);
        for (int t = 1; t < tables.length; t++) {
            Table next = new Table(tables[t]);
            ArrayList<Integer> A_index = new ArrayList<>(); //stores shared column index
            ArrayList<Integer> B_index = new ArrayList<>(); //stores shared column index
            Table notshared1 = new Table(curr); //creates copy
            Table notshared2 = new Table(next); //creates copy
            ArrayList<String> shared = new ArrayList<>(); //shared names
            ArrayList<String> shared_t = new ArrayList<>(); //shared types

            //For each column in A.names
            for (int x = 0; x < next.names.size(); x++) {
                if (next.names.contains(curr.names.get(x))) {
                    A_index.add(x);
                    int indexb = next.names.indexOf(next.names.get(x));
                    B_index.add(indexb);
                    shared.add((String) curr.names.get(x));
                    shared_t.add((String) curr.types.get(x));
                }
            }
            //Used removal rather than removedAll because removedAll was not mutating lists
            for (int k = A_index.size() - 1; k >= 0; k--) {
                notshared1.removal(A_index.get(k));
                notshared2.removal(B_index.get(k));
            }
            //Defining variables for col_size, names, and types
            int total_col = curr.names.size() + next.names.size() - A_index.size();
            ArrayList<String> named = G_func.combine_lists(shared, notshared1.names, notshared2.names);
            ArrayList<String> typed = G_func.combine_lists(shared_t, notshared1.types, notshared2.types);
            Table curr_new = new Table(total_col, named, typed);
            for (int x = 0; x < shared.size(); x++) {
                int colA = A_index.get(x);
                int colB = B_index.get(x);
                for (int r = 2; r < curr.rows; r++) {
                    ArrayList<String> data = new ArrayList<>();
                    if (next.gets(colB).contains(curr.gets(colA, r))) {
                        data.add(curr.gets(colA, r));
                        int rowB = next.gets(colB).indexOf(curr.gets(colA, r));
                        for (int y = 0; y < notshared1.names.size(); y++) {
                            data.add(notshared1.gets(y, r));
                        }
                        for (int y = 0; y < notshared2.names.size(); y++) {
                            data.add(notshared2.gets(y, rowB));
                        }
                        curr_new.insert(data);
                    }
                }
            }
            curr = curr_new;
        }
        table = curr.table;
        names = curr.names;
        types = curr.types;
        rows = curr.rows;
    }

    //Inserts values
    public void insert(ArrayList<T> data) {
        for (int x = 0; x < data.size(); x++) {
            ArrayList copy = table.get(x);
            String point = data.get(x).toString();
            copy.add(point);
            table.set(x, copy);
        }
        rows++;
    }

    //Prints table
    public void print() {
        if (table != null) {
            //Prints col_name and col_type together
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
        else {
            return;
        }
    }


    //Gets the ArrayList at column index
    public ArrayList<String> gets(int index) {
        return table.get(index);
        }

    //Gets the value at column col and row row
    public String gets(int col, int row) {
        return table.get(col).get(row);
    }

    //Removes column at specified index
    public void removal(int index) {
        table.remove(index);
        names.remove(index);
        types.remove(index);
    }

    //Supposed to remove all columns at the specified indexes, but not working
    public void removedAll(ArrayList<Integer> x) {
        for (int k = x.size() - 1; k >= 0; k--) {
            table.remove(x.get(k));
            names.remove(x.get(k));
            types.remove(x.get(k));
        }
    }

    //Creates clone of table
    public ArrayList<ArrayList<String>> cloning() {
        return (ArrayList<ArrayList<String>>) table.clone();
    }

    //This main method is for testing.
    public static void main(String[] args) {
        String[] first = new String[] {"x","y"};
        String[] second = new String[] {"int", "int"};
        String[] firsts = new String[] {"x", "z"};
        String[] seconds = new String[] {"int", "int"};
        String[] firsted = new String[] {"x","b"};
        Table t = new Table(3, new ArrayList<String>(Arrays.asList(first)), new ArrayList<String>(Arrays.asList(second)));
        Table s = new Table(3, new ArrayList<String>(Arrays.asList(firsts)), new ArrayList<String>(Arrays.asList(seconds)));
        Table u = new Table(3, new ArrayList<String>(Arrays.asList(firsted)), new ArrayList<String>(Arrays.asList(seconds)));
        Integer[] one = new Integer[] {2,5};
        Integer[] two = new Integer[] {8,3};
        Integer[] three = new Integer[] {13,7};
        Integer[] ones = new Integer[] {2,4};
        Integer[] twos = new Integer[] {8,9};
        Integer[] threes = new Integer[] {10,1};
        Integer[] oned = new Integer[] {2,3};
        Integer[] twod = new Integer[] {8,0};
        Integer[] threed = new Integer[] {10,1};
        t.insert(new ArrayList<Integer>(Arrays.asList(one)));
        t.insert(new ArrayList<Integer>(Arrays.asList(two)));
        t.insert(new ArrayList<Integer>(Arrays.asList(three)));
        s.insert(new ArrayList<Integer>(Arrays.asList(ones)));
        s.insert(new ArrayList<Integer>(Arrays.asList(twos)));
        s.insert(new ArrayList<Integer>(Arrays.asList(threes)));
        u.insert(new ArrayList<Integer>(Arrays.asList(oned)));
        u.insert(new ArrayList<Integer>(Arrays.asList(twod)));
        u.insert(new ArrayList<Integer>(Arrays.asList(threed)));
        Table k = new Table(new Table[] {t, s});
        Table j = new Table(new Table[] {t, s}); //Bug when calling join Table constructor
        k.print();
        j.print();
        t.print();
        s.print();
    }


}
