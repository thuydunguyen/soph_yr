package db;
import java.util.regex.*;
import db.Parser;
import java.util.ArrayList;

public class Database {
    public ArrayList<Table> storage;

    public Database() {
        storage = new ArrayList<>();
    }

    public String transact(String query) {
        return Parser.eval(query);
    }
}
