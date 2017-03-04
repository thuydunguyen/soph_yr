package db;

import java.io.*;
import java.util.*;

public class Table<T> {

    protected boolean CrTableSel = false;
    protected ArrayList<ArrayList<String>> table;
    protected ArrayList<String> names;
    protected ArrayList<String> types;
    protected int rows;
    protected String named;

    //*************************************************************************************************************//

    //Creates blank table
    protected Table() {
    }

    //Creates copy of a Table instance
    protected Table(Table to_copy) {
        names = to_copy.cloning(0);
        types = to_copy.cloning(1);
        rows = to_copy.rows;
        table = to_copy.cloning();
        named = to_copy.named;
    }

    //Creates new table
    protected Table(int cols, ArrayList<String> col_names, ArrayList<String> col_types, String n) {
        named = n;
        names = col_names;
        types = col_types;
        rows = 2;
        table = new ArrayList<>(cols);
        for (int x = 0; x < col_names.size(); x++) {
            ArrayList<String> sets = new ArrayList<>();
            sets.add(col_names.get(x));
            sets.add(col_types.get(x));
            table.add(sets);
        }
    }

    //Creates table as select aka Join
    //Need to add cases multiple shared columns
    protected Table(Table[] tables, String n) {
        Table curr = new Table(tables[0]);
        for (int t = 1; t < tables.length; t++) {
            Boolean s_columns = true;
            Table next = new Table(tables[t]);
            ArrayList<Integer> A_index = new ArrayList<>(); //stores shared column index
            ArrayList<Integer> B_index = new ArrayList<>(); //stores shared column index
            Table notshared1 = new Table(curr); //creates copy
            Table notshared2 = new Table(next); //creates copy
            ArrayList<String> shared = new ArrayList<>(); //shared names
            ArrayList<String> shared_t = new ArrayList<>(); //shared types

            //For each name in A.names: finds the indexes of shared columns
            for (int x = 0; x < curr.names.size(); x++) {
                if (next.names.contains(curr.names.get(x))) {
                    int indexb = next.names.indexOf(next.names.get(x));
                    if (next.types.get(indexb).equals(curr.types.get(x))) { //makes sure columns are same type too
                        A_index.add(x);
                        B_index.add(indexb);
                        shared.add((String) curr.names.get(x));
                        shared_t.add((String) curr.types.get(x));
                    }
                }
            }
            if (shared.size() != 0) {
                notshared1.removedAll(A_index);
                notshared2.removedAll(B_index);
            } else { //For when no columns are shared
                A_index.add(0);
                B_index.add(0);
                notshared1.removal(0);
                shared.add((String) curr.names.get(0));
                shared_t.add((String) curr.types.get(0));
                s_columns = false;
            }
            //Defining variables for col_size, names, and types
            ArrayList<String> named = G_func.combine_lists(shared, notshared1.names, notshared2.names);
            ArrayList<String> typed = G_func.combine_lists(shared_t, notshared1.types, notshared2.types);
            int total_col = named.size();
            Table curr_new = new Table(total_col, named, typed, n);
            int x = 0; //Used to be a for loop but had complications so got rid of it
            int colA = A_index.get(x); //Shared column x in A
            int colB = B_index.get(x); //Shared column x in B
            for (int r = 2; r < curr.rows; r++) { //for each value in shared column of A
                ArrayList<Integer> rowsB;
                if (s_columns) {
                    rowsB = G_func.each_index(next.gets(colB), curr.gets(colA, r));
                } else {
                    rowsB = new ArrayList<>();
                    for (int k = 2; k < curr.rows; k++) {
                        rowsB.add(k);
                    }
                }
                if (rowsB.size() > 0) { //gets shared values of shared colB
                    for (int z = 0; z < rowsB.size(); z++) { //for each index in rowsB
                        ArrayList<String> data = new ArrayList<>(); //creates row data
                        data.add(curr.gets(colA, r)); //Adds shared value
                        int rowB = rowsB.get(z); //gets the zth shared index from rowsB
                        for (int y = 0; y < notshared1.names.size(); y++) { //Adds data from left table
                            data.add(notshared1.gets(y, r));
                        }
                        for (int y = 0; y < notshared2.names.size(); y++) { //Adds data from right table
                            data.add(notshared2.gets(y, rowB));
                        }
                        curr_new.insert(data); //inserts data into joined table
                    }
                }
            }
            curr = curr_new; //New curr becomes the joined table.

        }
        table = curr.table; //Copies over data from complete joined table to construct.
        names = curr.names;
        types = curr.types;
        rows = curr.rows;
        named = curr.named;
    }

    //Creates table from a list of ArrayLists<String>
    protected Table(ArrayList<String>[] columns, String n) {
        table = new ArrayList<ArrayList<String>>();
        names = new ArrayList<String>();
        types = new ArrayList<String>();
        for (int x = 0; x < columns.length; x++) {
            table.add(columns[x]);
            names.add(columns[x].get(0));
            types.add(columns[x].get(1));
        }
        rows = columns[0].size();
        named = n;
    }


    //**************************************************************************************************************//

    //Inserts values
    protected void insert(ArrayList<T> data) {
        for (int x = 0; x < data.size(); x++) {
            ArrayList copy = table.get(x);
            String point = data.get(x).toString();
            copy.add(point);
            table.set(x, copy);
        }
        rows++;
    }

    //Prints table
    protected void print() {
        if (table != null) {
            //Prints col_name and col_type together
            String comma;
            for (int x = 0; x < names.size(); x++) {
                if (x == names.size() - 1) {
                    comma = "";
                    System.out.println(table.get(x).get(0) + " " + table.get(x).get(1) + comma);

                } else {
                    comma = ",";
                    System.out.print(table.get(x).get(0) + " " + table.get(x).get(1) + comma);
                }
            }
            //Prints rest of the data
            for (int y = 2; y < table.get(0).size(); y++) {
                for (int x = 0; x < names.size(); x++) {
                    if (x == names.size() - 1) {
                        comma = "";
                        System.out.println(table.get(x).get(y) + comma);

                    } else {
                        comma = ",";
                        System.out.print(table.get(x).get(y) + comma);
                    }
                }
            }
        } else {
            System.out.println("No such table exists");
        }
    }

    //Stores table
    protected void createFile() {
        String path = G_func.pathway(named);
        File fl = new File(path);
        if (fl.exists()) {
            fl.delete();
        }
        try (FileWriter fw = new FileWriter(fl)) {
            if (table != null) {
                //Prints col_name and col_type together
                String comma;
                for (int x = 0; x < names.size(); x++) {
                    if (x == names.size() - 1) {
                        comma = "";
                        fw.write(table.get(x).get(0) + " " + table.get(x).get(1) + comma);
                        fw.write(System.lineSeparator());

                    } else {
                        comma = ",";
                        fw.write(table.get(x).get(0) + " " + table.get(x).get(1) + comma);
                    }
                }
                //Prints rest of the data
                for (int y = 2; y < table.get(0).size(); y++) {
                    for (int x = 0; x < names.size(); x++) {
                        if (x == names.size() - 1) {
                            comma = "";
                            fw.write(table.get(x).get(y) + comma);
                            fw.write(System.lineSeparator());

                        } else {
                            comma = ",";
                            fw.write(table.get(x).get(y) + comma);
                        }
                    }
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    //**************************************************************************************************************//

    //Gets the ArrayList at column index
    protected ArrayList<String> gets(int index) {
        return table.get(index);
    }

    //Gets the value at column col and row row
    protected String gets(int col, int row) {
        return table.get(col).get(row);
    }

    //Removes column at specified index
    protected void removal(int index) {
        table.remove(index);
        names.remove(index);
        types.remove(index);
    }

    //Removes row
    protected void removal_r(int row) {
        for (int x = 0; x < names.size(); x++) {
            ArrayList<String> copy = table.get(x);
            copy.remove(row);
            table.set(x, copy);
        }
        rows--;
    }

    //Removes all Columns in ArrayList x
    protected void removedAll(ArrayList<Integer> x) {
        for (int k = x.size() - 1; k >= 0; k--) {
            int indx = x.get(k);
            table.remove(indx);
            names.remove(indx);
            types.remove(indx);
        }
    }

    //Creates clone of table
    protected ArrayList<ArrayList<String>> cloning() {
        return (ArrayList<ArrayList<String>>) table.clone();
    }

    //Clones Table's names or types
    protected ArrayList<String> cloning(int n) {
        if (n == 0) {
            return (ArrayList<String>) names.clone();
        } else {
            return (ArrayList<String>) types.clone();
        }
    }


}
