package db;
import java.util.ArrayList;

/**
 * Created by Thuy-Du on 2/26/2017.
 */
public class Table<T> {
    private boolean CrTableSel = false;
    private ArrayList<ArrayList> col;

    public Table() {
        col = new ArrayList<>();
    }

    public Table(int cols, String[] col_names, String[] col_types) {
        col = new ArrayList<>(col_names.length);
        for (int x = 0; x < col_names.length; x++) {
            ArrayList<String> sets = new ArrayList<String>();
            sets.add(0, col_names[x]);
            sets.add(1, col_types[x]);
            col.set(x, sets);
        }
    }

    public void insert(T[] data) {
        for (int x = 0; x < col.size() + 1; x++) {
            ArrayList copy = col.get(x);
            copy.add(data[x]);
            col.set(x, copy);
        }
    }

    public void select_join(String[] col_names) {

    }
}
