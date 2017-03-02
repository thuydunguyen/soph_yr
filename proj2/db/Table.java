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
    public Table(Table A, Table B) {
        ArrayList<Integer> A_index = new ArrayList<>(); //stores shared column index
        ArrayList<Integer> B_index = new ArrayList<>(); //stores shared column index
        Table notshared1 = new Table(A); //creates copy
        Table notshared2 = new Table(B); //creates copy
        ArrayList<String> shared = new ArrayList<>(); //shared names
        ArrayList<String> shared_t = new ArrayList<>(); //shared types

        //For each column in A.names
        for (int x = 0; x < A.names.size(); x++) {
            if (B.names.contains(A.names.get(x))) {
                A_index.add(x);
                int indexb = B.names.indexOf(A.names.get(x));
                B_index.add(indexb);
                shared.add((String) A.names.get(x));
                shared_t.add((String) A.types.get(x));
            }
        }
        //Used removal rather than removedAll because removedAll was not mutating lists
        for (int k = A_index.size() - 1; k >= 0; k--) {
            notshared1.removal(A_index.get(k));
            notshared2.removal(B_index.get(k));
        }
        //Defining variables for col_size, names, and types
        int total_col = A.names.size() + B.names.size() - A_index.size();
        ArrayList<String> named = G_func.combine_lists(shared, notshared1.names, notshared2.names);
        ArrayList<String> typed = G_func.combine_lists(shared_t, notshared1.types, notshared2.types);

        names = named;
        types = typed;
        table = new ArrayList<>(total_col);
        
        //Fill table with names and types
        for (int x = 0; x < named.size(); x++) {
            ArrayList<String> sets = new ArrayList<>();
            sets.add(named.get(x));
            sets.add(typed.get(x));
            table.add(sets);
        }
        
        //Fill table with data
        for (int i = 0; i < shared.size(); i++) { //For each shared column
            for (int y = 2; y < A.rows; y++) { //Run through each row
                int colB = B_index.get(i); //Gets the shared column index
                int colA = A_index.get(i);
                if (B.gets(colB).contains(A.gets(colA, y))) { //Checks if row y, colA of A  is in colB of B
                    ArrayList copy = table.get(0); //Sets first column to be the shared column
                    copy.add(A.gets(colA, y));
                    table.set(i, copy);
                    for (int k = 0; k < notshared1.names.size(); k++) {
                        copy = table.get(k + 1);
                        copy.add(notshared1.gets(k, y));
                        table.set(k + 1, copy);
                    }
                    for (int j = 0; j < notshared2.names.size(); j++) {
                        int ind = j + notshared1.names.size() + 1;
                        copy = table.get(ind);
                        copy.add(notshared2.gets(j, y));
                        table.set(ind, copy);
                    }
                }
            }
        }
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
        Table t = new Table(3, new ArrayList<String>(Arrays.asList(first)), new ArrayList<String>(Arrays.asList(second)));
        Table s = new Table(3, new ArrayList<String>(Arrays.asList(firsts)), new ArrayList<String>(Arrays.asList(seconds)));
        Integer[] one = new Integer[] {2,5};
        Integer[] two = new Integer[] {8,3};
        Integer[] three = new Integer[] {13,7};
        Integer[] ones = new Integer[] {2,4};
        Integer[] twos = new Integer[] {8,9};
        Integer[] threes = new Integer[] {10,1};
        ArrayList<Integer> A_index= new ArrayList<>();
        A_index.add(0);
        A_index.add(1);
        t.insert(new ArrayList<Integer>(Arrays.asList(one)));
        t.insert(new ArrayList<Integer>(Arrays.asList(two)));
        t.insert(new ArrayList<Integer>(Arrays.asList(three)));
        s.insert(new ArrayList<Integer>(Arrays.asList(ones)));
        s.insert(new ArrayList<Integer>(Arrays.asList(twos)));
        s.insert(new ArrayList<Integer>(Arrays.asList(threes)));
        Table j = new Table(t,s);
        t.print();
        s.print();
        j.print();
    }
}
