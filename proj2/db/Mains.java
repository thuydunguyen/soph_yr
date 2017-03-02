package db;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import db.Database;
import db.Cmd;

public class Mains {
    private static final String EXIT   = "exit";
    private static final String PROMPT = "> ";


    public static void main(String[] args) throws IOException {

        String REST = "\\s*(.*)\\s*";
        Pattern malfrm = Pattern.compile("Malformed " + REST);
        Pattern expect = Pattern.compile("Expected a single query argument");

        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        Database db = new Database();
        System.out.print(PROMPT);

        String line = "";
        while ((line = in.readLine()) != null) {
            if (EXIT.equals(line)) {
                break;
            }

            if (!line.trim().isEmpty()) {
                String result = db.transact(line);
                if (result.length() > 0) {
                    System.out.println(result);
                    if (!(malfrm.matcher(result)).matches() || !(expect.matcher(result)).matches()) {
                        db.storage.add(Cmd.eval(result));
                    }
                }
            }

            System.out.print(PROMPT);
        }

        in.close();
    }
}
