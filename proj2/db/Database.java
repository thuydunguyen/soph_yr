package db;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Iterator;
import java.util.StringJoiner;
import java.util.regex.*;
import db.Parser;
import java.util.ArrayList;
import db.G_func;


public class Database {
    public ArrayList<Table> storage;

    public Database() {
        storage = new ArrayList<>();
    }

    public String transact(String query) {
        return Parser.eval(query);
    }

    // Various common constructs, simplifies parsing.
    private static final String REST = "\\s*(.*)\\s*",
            COMMA = "\\s*,\\s*",
            AND = "\\s+and\\s+";

    // Stage 1 syntax, contains the command name.
    private static final Pattern CREATE_CMD = Pattern.compile("create table " + REST),
            LOAD_CMD = Pattern.compile("load " + REST),
            STORE_CMD = Pattern.compile("store " + REST),
            DROP_CMD = Pattern.compile("drop table " + REST),
            INSERT_CMD = Pattern.compile("insert into " + REST),
            PRINT_CMD = Pattern.compile("print " + REST),
            SELECT_CMD = Pattern.compile("select " + REST);

    // Stage 2 syntax, contains the clauses of commands.
    private static final Pattern CREATE_NEW = Pattern.compile("(\\S+)\\s+\\((\\S+\\s+\\S+\\s*" +
            "(?:,\\s*\\S+\\s+\\S+\\s*)*)\\)"),
            SELECT_CLS = Pattern.compile("([^,]+?(?:,[^,]+?)*)\\s+from\\s+" +
                    "(\\S+\\s*(?:,\\s*\\S+\\s*)*)(?:\\s+where\\s+" +
                    "([\\w\\s+\\-*/'<>=!]+?(?:\\s+and\\s+" +
                    "[\\w\\s+\\-*/'<>=!]+?)*))?"),
            CREATE_SEL = Pattern.compile("(\\S+)\\s+as select\\s+" +
                    SELECT_CLS.pattern()),
            INSERT_CLS = Pattern.compile("(\\S+)\\s+values\\s+(.+?" +
                    "\\s*(?:,\\s*.+?\\s*)*)");


    public void eval(String query) {
        Matcher m;
        if ((m = CREATE_CMD.matcher(query)).matches()) {
            createTable(m.group(1));
        } else if ((m = DROP_CMD.matcher(query)).matches()) {
            dropTable(m.group(1));
        } else if ((m = PRINT_CMD.matcher(query)).matches()) {
            printTable(m.group(1));
        } else if ((m = SELECT_CMD.matcher(query)).matches()) {
            select(m.group(1));
        } else if ((m = INSERT_CMD.matcher(query)).matches()) {
            insertRow(m.group(1));}

    }

    public void createTable(String expr) {
        Matcher m;
        if ((m = CREATE_NEW.matcher(expr)).matches()) {
            createNewTable(m.group(1), m.group(2).split(COMMA));
        }
        else if ((m = CREATE_SEL.matcher(expr)).matches()) {
            createSelectedTable(m.group(1), m.group(2), m.group(3), m.group(4));
        }
    }

    //Added statements to get col_names and col_types.
    public void createNewTable(String name, String[] cols) {
        String[] named = new String[cols.length];
        String[] types = new String[cols.length];
        StringJoiner joiner = new StringJoiner(" ");
        for (int i = 0; i < cols.length; i++) {
            joiner.add(cols[i]);
            String[] parts = cols[i].split("\\s+");
            named[i] = parts[0];
            types[i] = parts[1];

        }
        String colSentence = joiner.toString();
        Table t = new Table(named.length, G_func.to_list(named), G_func.to_list(types), name);
        storage.add(t);
    }

    //Need to work on combining Table constructor with oper and cond from G_func
    //Figure out how to separate exprs with "as" statement in it
    public void createSelectedTable(String name, String exprs, String tables, String conds) {
        String[] columns = exprs.split("\\s*,\\s*");
        String[] table = tables.split("\\s*,\\s*");
        String[] cond;
        try {
            cond = conds.split("\\s+and\\s+");
        } catch (NullPointerException e) {
            cond = new String[0];
        }
        Table[] t = retrieve(table);
        Table joined = new Table(t, name);
        Table joined2 = G_func.oper(joined, columns, name);
        if (cond.length > 0) {
            ArrayList<Integer> rem = G_func.condition(joined2, cond);
            for (int x = 0; x < rem.size(); x++) {
                joined2.removal_r(rem.get(x));
            }
        }
        storage.add(joined2);
    }

    private void dropTable(String name) {
       int indx = index(name);
       storage.remove(indx);
    }

    private void printTable(String name) {
        Table t = retrieve(name);
        t.print();
    }

    private void select(String expr) {
        Matcher m = SELECT_CLS.matcher(expr);
        if (!m.matches()) {
            System.err.printf("Malformed select: %s\n", expr);
            return;
        }
        select(m.group(1), m.group(2), m.group(3));
    }

    private void select(String exprs, String tables, String conds) {
        String[] columns = exprs.split("\\s*,\\s*");
        String[] table = tables.split("\\s*,\\s*");
        String[] cond;
        try {
            cond = conds.split("\\s+and\\s+");
        } catch (NullPointerException e) {
            cond = new String[0];
        }
        Table[] t = retrieve(table);
        Table joined = new Table(t, "temp");
        Table joined2 = G_func.oper(joined, columns, "temp");
        if (cond.length > 0) {
            ArrayList<Integer> rem = G_func.condition(joined2, cond);
            for (int x = 0; x < rem.size(); x++) {
                joined2.removal_r(rem.get(x));
            }
        }
        joined2.print();
    }

    private void insertRow(String expr) {
        Matcher m = INSERT_CLS.matcher(expr);

        if (!m.matches()) {
            System.err.printf("Malformed insert: %s\n", expr);
        }

        String name = m.group(1);
        String data = m.group(2);

        String[] vals = data.split("\\s*,\\s*");
        ArrayList<String> values = new ArrayList<>(Arrays.asList(vals));
        int indx = index(name);
        Table t = retrieve(name);
        t.insert(values);
        storage.set(indx, t);

    }



    //Gets tables corresponding to desired names
    public Table[] retrieve(String[] name) {
        Table[] t = new Table[name.length];
        for (int n = 0; n < name.length; n++) {
            for (int x = 0; x < storage.size(); x++) {
                if (storage.get(x).named.equals(name[n])) {
                    t[n] = storage.get(x);
                }
            }
        }
        return t;
    }

    public Table retrieve(String name) {
            int indx = index(name);
            if (indx >= 0) {
                return storage.get(indx);
            }
        return new Table();
    }

    public int index(String name) {
        for (int x = 0; x < storage.size(); x++) {
            if (storage.get(x).named.equals(name)) {
                return x;
            }
        }
        return -1;
    }

    //Need to make commands for:
    //Load
    //Insert into
    //Store
}

