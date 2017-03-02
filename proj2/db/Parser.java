package db;

import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import db.Table;
import db.G_func;


/**
 * Created by Thuy-Du on 2/26/2017.
 */
public class Parser {

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

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Expected a single query argument");
            return;
        }

        eval(args[0]);
    }

    protected static String eval(String query) {
        Matcher m;
        if ((m = CREATE_CMD.matcher(query)).matches()) {
            return createTable(m.group(1));
        } else if ((m = LOAD_CMD.matcher(query)).matches()) {
            return loadTable(m.group(1));
        } else if ((m = STORE_CMD.matcher(query)).matches()) {
            return storeTable(m.group(1));
        } else if ((m = DROP_CMD.matcher(query)).matches()) {
            return dropTable(m.group(1));
        } else if ((m = INSERT_CMD.matcher(query)).matches()) {
            return insertRow(m.group(1));
        } else if ((m = PRINT_CMD.matcher(query)).matches()) {
            return printTable(m.group(1));
        } else if ((m = SELECT_CMD.matcher(query)).matches()) {
            return select(m.group(1));
        } else {
            System.err.printf("Malformed query: " + query);
            return "";
        }
    }

    private static String createTable(String expr) {
        Matcher m;
        if ((m = CREATE_NEW.matcher(expr)).matches()) {
            return createNewTable(m.group(1), m.group(2).split(COMMA));
        } else if ((m = CREATE_SEL.matcher(expr)).matches()) {
            return createSelectedTable(m.group(1), m.group(2), m.group(3), m.group(4));
        } else {
            System.err.printf("Malformed create: " + expr);
            return "";
        }
    }

    //Added statements to get col_names and col_types.
    private static String createNewTable(String name, String[] cols) {
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
        Table t = new Table(named.length, G_func.to_list(named) , G_func.to_list(types));
        t.print();
        return colSentence;
    }

    private static String createSelectedTable(String name, String exprs, String tables, String conds) {
        return "You are trying to create a table named " + name + " by selecting these expressions: " +
                 exprs + " from the join of these tables: " + tables + ", filtered by these conditions: " + conds;
    }

    private static String loadTable(String name) {
        return "You are trying to load the table named " + name;
    }

    private static String storeTable(String name) {
        return "You are trying to store the table named " + name;
    }

    private static String dropTable(String name) {
        return "You are trying to drop the table named " + name;
    }

    private static String insertRow(String expr) {
        Matcher m = INSERT_CLS.matcher(expr);
        if (!m.matches()) {
            System.err.printf("Malformed insert: " + expr);
            return "";
        }

        return "You are trying to insert the row" +  m.group(2) + " into the table " + m.group(1);
    }

    private static String printTable(String name) {
        return "You are trying to print the table named " + name;
    }

    private static String select(String expr) {
        Matcher m = SELECT_CLS.matcher(expr);
        if (!m.matches()) {
            System.err.printf("Malformed select: " + expr);
            return "";
        }

        return select(m.group(1), m.group(2), m.group(3));
    }

    private static String select(String exprs, String tables, String conds) {
        return "You are trying to select these expressions: " +
                exprs + " from the join of these tables: " + tables + ", filtered by these conditions: " + conds;
    }

}


