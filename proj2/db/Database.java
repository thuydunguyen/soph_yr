package db;
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
        }
    }

    public void createTable(String expr) {
        Matcher m;
        if ((m = CREATE_NEW.matcher(expr)).matches()) {
            createNewTable(m.group(1), m.group(2).split(COMMA));
        }
        else {
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
        String[] columns = exprs.split(",");
        String[] table = tables.split(",");
        try {
        String[] cond = conds.split("\\s+and\\s+");}
        catch (NullPointerException e) {

        }
        Table[] t = retrieve(table);
        Table joined = new Table(t, name);
        ArrayList<String>[] select_col = new ArrayList[columns.length];
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
            for (int x = 0; x < storage.size(); x++) {
                if (storage.get(x).named.equals(name)) {
                     return storage.get(x);
                }
            }
        return new Table();
    }
}

