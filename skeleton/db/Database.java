package db;
import java.util.regex.*;

public class Database {
    public Database() {
        // YOUR CODE HERE
    }
    private static final String REST  = "\\s*(.*)\\s*",
            COMMA = "\\s*,\\s*",
            AND   = "\\s+and\\s+";
    // Stage 1 syntax, contains the command name.
    private static final Pattern CREATE_CMD = Pattern.compile("create table " + REST),
            LOAD_CMD   = Pattern.compile("load " + REST),
            STORE_CMD  = Pattern.compile("store " + REST),
            DROP_CMD   = Pattern.compile("drop table " + REST),
            INSERT_CMD = Pattern.compile("insert into " + REST),
            PRINT_CMD  = Pattern.compile("print " + REST),
            SELECT_CMD = Pattern.compile("select " + REST);

    // Stage 2 syntax, contains the clauses of commands.
    private static final Pattern CREATE_NEW  = Pattern.compile("(\\S+)\\s+\\((\\S+\\s+\\S+\\s*" +
            "(?:,\\s*\\S+\\s+\\S+\\s*)*)\\)"),
            SELECT_CLS  = Pattern.compile("([^,]+?(?:,[^,]+?)*)\\s+from\\s+" +
                    "(\\S+\\s*(?:,\\s*\\S+\\s*)*)(?:\\s+where\\s+" +
                    "([\\w\\s+\\-*/'<>=!]+?(?:\\s+and\\s+" +
                    "[\\w\\s+\\-*/'<>=!]+?)*))?"),
            CREATE_SEL  = Pattern.compile("(\\S+)\\s+as select\\s+" +
                    SELECT_CLS.pattern()),
            INSERT_CLS  = Pattern.compile("(\\S+)\\s+values\\s+(.+?" +
                    "\\s*(?:,\\s*.+?\\s*)*)");

    public String transact(String query) {
        Matcher m;
        if ((m = CREATE_CMD.matcher(query)).matches()) {
            return "";
        } else if ((m = LOAD_CMD.matcher(query)).matches()) {
            return "";
        } else if ((m = STORE_CMD.matcher(query)).matches()) {
            return "";
        } else if ((m = DROP_CMD.matcher(query)).matches()) {
            return "";
        } else if ((m = INSERT_CMD.matcher(query)).matches()) {
            return "";
        } else if ((m = PRINT_CMD.matcher(query)).matches()) {
            return "printing";
        } else if ((m = SELECT_CMD.matcher(query)).matches()) {
            return "select";
        } else {
            System.err.printf("Malformed query: %s\n", query);
            return "";
        }
    }
}

