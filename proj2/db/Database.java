package db;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.ArrayList;
import java.util.regex.Pattern;


public class Database {
    private ArrayList<Table> storage;

    public Database() {
        storage = new ArrayList<>();
    }

    public String transact(String query) {
        return eval(query);
    }

    //**********************PARSING_PATTERNS*************************//

    // Various common constructs, simplifies parsing.
    protected static final String REST = "\\s*(.*)\\s*",
            COMMA = "\\s*,\\s*",
            AND = "\\s+and\\s+";

    // Stage 1 syntax, contains the command name.
    protected static final Pattern CREATE_CMD = Pattern.compile("create table " + REST),
            LOAD_CMD = Pattern.compile("load " + REST),
            STORE_CMD = Pattern.compile("store " + REST),
            DROP_CMD = Pattern.compile("drop table " + REST),
            INSERT_CMD = Pattern.compile("insert into " + REST),
            PRINT_CMD = Pattern.compile("print " + REST),
            SELECT_CMD = Pattern.compile("select " + REST);

    // Stage 2 syntax, contains the clauses of commands.
    protected static final Pattern CREATE_NEW = Pattern.compile("(\\S+)\\s+\\((\\S+\\s+\\S+\\s*"
            + "(?:,\\s*\\S+\\s+\\S+\\s*)*)\\)"),
            SELECT_CLS = Pattern.compile("([^,]+?(?:,[^,]+?)*)\\s+from\\s+"
                    + "(\\S+\\s*(?:,\\s*\\S+\\s*)*)(?:\\s+where\\s+"
                    + "([\\w\\s+\\-*/'<>=!]+?(?:\\s+and\\s+"
                    + "[\\w\\s+\\-*/'<>=!]+?)*))?"),
            CREATE_SEL = Pattern.compile("(\\S+)\\s+as select\\s+"
                    + SELECT_CLS.pattern()),
            INSERT_CLS = Pattern.compile("(\\S+)\\s+values\\s+(.+?"
                    + "\\s*(?:,\\s*.+?\\s*)*)");


    //******************EVALUATOR**********************************//

    public String eval(String query) {
        Matcher m;
        if ((m = CREATE_CMD.matcher(query)).matches()) {
            return createTable(m.group(1));
        } else if ((m = DROP_CMD.matcher(query)).matches()) {
            return dropTable(m.group(1));
        } else if ((m = PRINT_CMD.matcher(query)).matches()) {
            return printTable(m.group(1));
        } else if ((m = SELECT_CMD.matcher(query)).matches()) {
            return select(m.group(1));
        } else if ((m = INSERT_CMD.matcher(query)).matches()) {
            return insertRow(m.group(1));
        } else if ((m = STORE_CMD.matcher(query)).matches()) {
            return storeTable(m.group(1));
        } else if ((m = LOAD_CMD.matcher(query)).matches()) {
            return loadTable(m.group(1));
        }
        return "ERROR: Invalid Command";
    }


    //**********************COMMANDS***********************//

    protected String createTable(String expr) {
        Matcher m;
        if ((m = CREATE_NEW.matcher(expr)).matches()) {
            return createNewTable(m.group(1), m.group(2).split(COMMA));
        } else if ((m = CREATE_SEL.matcher(expr)).matches()) {
            return createSelectedTable(m.group(1), m.group(2), m.group(3), m.group(4));
        } else {
            return "ERROR: Cannot make table";
        }
    }

    protected String createNewTable(String name, String[] cols) {
        String[] named = new String[cols.length];
        String[] types = new String[cols.length];
        StringJoiner joiner = new StringJoiner(" ");
        for (int i = 0; i < cols.length; i++) {
            joiner.add(cols[i]);
            String[] parts = cols[i].split("\\s+");
            named[i] = parts[0];
            types[i] = parts[1];

        }
        Table t = new Table(G_func.to_list(named), G_func.to_list(types), name);
        if (t.error == 1) {
            return "ERROR: Table is BadTable";
        }
        storage.add(t);
        return "";
    }

    protected String createSelectedTable(String name, String exprs, String tables, String conds) {
        String[] columns = exprs.split("\\s*,\\s*");
        String[] table = tables.split("\\s*,\\s*");
        String[] cond;
        try {
            cond = conds.split("\\s+and\\s+");
        } catch (NullPointerException e) {
            cond = new String[0];
        }
        try {
            Table[] t = retrieve(table);
            Table joined = new Table(t, name);
            Table joined2 = G_func.oper(joined, columns, name);
            if (joined2.error == 1) {
                joined.error = 0;
                return "ERROR: Column name for arithmetic needed";
            }
            if (joined2.error == 2) {
                joined.error = 0;
                return "ERROR: Invalid arithmetic";
            }
            if (joined2.error == 3) {
                joined.error = 0;
                return "ERROR: Column not found";
            }
            if (cond.length > 0) {
                ArrayList<Integer> rem = G_func.condition(joined2, cond);
                for (int x = 0; x < rem.size(); x++) {
                    if (rem.get(0) == -1) {
                        return "ERROR: Invalid conditional statement";
                    } else if (rem.get(0) == -2) {
                        return "ERROR: Column not found for condition";
                    } else if (rem.get(0) == -3) {
                        return "ERROR: Incompatible types compared";
                    }
                    joined2.removal_r(rem.get(x));
                }
            }
            storage.add(joined2);
            return "";
        } catch (NullPointerException e) {
            return "ERROR: One or more tables do not exist";
        }
    }

    protected String dropTable(String name) {
        int indx = index(name);
        if (indx >= 0) {
            storage.remove(indx);
            return "";
        } else {
            return "ERROR: Table does not exist to be dropped";
        }
    }

    protected String printTable(String name) {
        if (index(name) >= 0) {
            Table t = retrieve(name);
            String printed = t.print();
            return printed;
        } else {
            return "ERROR: Table does not exist";
        }
    }

    protected String select(String expr) {
        Matcher m = SELECT_CLS.matcher(expr);
        if (!m.matches()) {
            System.err.printf("Malformed select: %s\n", expr);
            return "ERROR: Invalid command. Fix: select <*> from <table> <where...> ";
        }
        return select(m.group(1), m.group(2), m.group(3));
    }

    protected String select(String exprs, String tables, String conds) {
        String[] columns = exprs.split("\\s*,\\s*");
        String[] table = tables.split("\\s*,\\s*");
        String[] cond;
        try {
            cond = conds.split("\\s+and\\s+");
        } catch (NullPointerException e) {
            cond = new String[0];
        }
        try {
            Table[] t = retrieve(table);
            Table joined = new Table(t, "temp");
            Table joined2 = G_func.oper(joined, columns, "temp");
            if (joined2.error == 1) {
                joined.error = 0;
                return "ERROR: Column name for arithmetic needed";
            }
            if (joined2.error == 2) {
                joined.error = 0;
                return "ERROR: Invalid arithmetic";
            }
            if (joined2.error == 3) {
                joined.error = 0;
                return "ERROR: Column not found";
            }
            if (cond.length > 0) {
                ArrayList<Integer> rem = G_func.condition(joined2, cond);
                for (int x = 0; x < rem.size(); x++) {
                    if (rem.get(0) == -1) {
                        return "ERROR: Invalid conditional statement";
                    } else if (rem.get(0) == -2) {
                        return "ERROR: Column not found for condition";
                    } else if (rem.get(0) == -3) {
                        return "ERROR: Incompatible types compared";
                    }
                    joined2.removal_r(rem.get(x));
                }
            }
            return joined2.print();
        } catch (NullPointerException e) {
            return "ERROR: One or more tables do not exist";
        }
    }

    protected String insertRow(String expr) {
        Matcher m = INSERT_CLS.matcher(expr);

        if (!m.matches()) {
            System.err.printf("Malformed insert: %s\n", expr);
            return "ERROR: Invalid command. Fix: insert into <table> values <vals>";
        } else {

            String name = m.group(1);
            String data = m.group(2);


            String[] vals = data.split("\\s*,\\s*");
            ArrayList<String> values = new ArrayList<>(Arrays.asList(vals));
            int indx = index(name);
            Table t = retrieve(name);
            t.insert(values);
            if (t.error == 1) {
                return "ERROR: Incorrect number of values";
            } else if (t.error == 2) {
                return "ERROR: Value type != column type";
            }
            storage.set(indx, t);
            return "";
        }
    }

    protected String storeTable(String name) {
        if (index(name) >= 0) {
            Table t = retrieve(name);
            t.createFile();
            return "";
        } else {
            return "ERROR: Table does not exist";
        }
    }

    protected String loadTable(String name) {
        try {
            int indx = index(name);
            String path = G_func.pathway(name);
            FileReader fr = new FileReader(path);
            Table t = G_func.loadComp(path, name);
            if (t.error == 1) {
                t.error = 0;
                return "ERROR: Malformed table";
            }
            else if (t.error == 2) {
                t.error = 0;
                return "ERROR: Blank file";
            }
            if (indx != -1) {
                storage.set(indx, t);
            } else {
                storage.add(t);
            }
            return "";
        } catch (FileNotFoundException e) {
            return "ERROR: File does not exist";
        }
    }

    //*****************GENERAL********************//

    //Gets tables corresponding to desired names
    protected Table[] retrieve(String[] name) {
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

    protected Table retrieve(String name) {
        int indx = index(name);
        if (indx >= 0) {
            return storage.get(indx);
        }
        return new Table();
    }

    protected int index(String name) {
        for (int x = 0; x < storage.size(); x++) {
            if (storage.get(x).named.equals(name)) {
                return x;
            }
        }
        return -1;
    }

}
