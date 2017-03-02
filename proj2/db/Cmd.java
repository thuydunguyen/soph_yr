package db;

import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


//Need to make cases for the rest of the commands

/**
 * Created by Thuy-Du on 3/1/2017.
 */
public class Cmd {
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
        eval(args[0]);
    }

    public static Table eval(String query) {
        Matcher m;
        if ((m = CREATE_CMD.matcher(query)).matches()) {
            return createTable(m.group(1));
        } else {
            return new Table();
        }
    }

    private static Table createTable(String expr) {
        Matcher m;
        if ((m = CREATE_NEW.matcher(expr)).matches()) {
            return createNewTable(m.group(1), m.group(2).split(COMMA));
        }
        else {
            return new Table();
        }
    }

    //Added statements to get col_names and col_types.
    private static Table createNewTable(String name, String[] cols) {
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
        Table t = new Table(named.length, G_func.to_list(named), G_func.to_list(types));
        t.print();
        return t;
    }

}
