package db;
import java.util.regex.*;
import db.Parser;

public class Database {
    public Database() {
        // YOUR CODE HERE
    }

    public String transact(String query) {
        return Parser.eval(query);
    }

}
