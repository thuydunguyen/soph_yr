package db;

/**
 * Created by Thuy-Du on 3/1/2017.
 */
import java.util.*;
import db.Table;
import java.util.StringTokenizer;
import db.Database;

public class G_func {

    //Combines 3 ArrayLists into one
    protected static ArrayList<String> combine_lists(ArrayList<String> list1, ArrayList<String> list2, ArrayList<String> list3) {
        ArrayList combination = new ArrayList<String>(list1);
        combination.addAll(list2);
        combination.addAll(list3);
        return combination;
    }

    //Changes a String array into and ArrayList
    protected static ArrayList<String> to_list(String[] x) {
        return new ArrayList<>(Arrays.asList(x));
    }

    //Gets all indexes of a value in and arraylist
    protected static ArrayList<Integer> each_index(ArrayList<String> x, String y) {
        ArrayList<Integer> index = new ArrayList<>();
        for (int k = 2; k < x.size(); k++) {
            String val = x.get(k);
            if (val.equals(y)){
                index.add(k);
            }
        }
        return index;
    }

    //Compares types and outputs resulting type
    protected static String type_out(Object a, Object b) {
        ArrayList<String> nums = new ArrayList<String>(Arrays.asList(new String[] {"int", "float"}));
        ArrayList<String> words = new ArrayList<String>(Arrays.asList(new String[] {"string"}));
        if (nums.contains(a) && nums.contains(b)) {
            int x = nums.indexOf(a);
            int y = nums.indexOf(b);
            return nums.get(Math.max(x,y));
        }
        else if (words.contains(a) && words.contains(b)) {
            return "string";
        }
        else {
            System.err.printf("Cannot perform operation with class: " + a + " and " + b);
            return "";
        }
    }

    //Splits String into conditional parts
    protected static String[] splits(String str) {
        str = str.replaceAll("\\s+","");
        String[] result = str.split("(?<=[<=>])|(?=[<=>])");
        String[] fin = new String[3];
        if (result.length == 4) {
            fin[1] = result[1]+result[2];
            fin[0] = result[0];
            fin[2] = result[3];}
        else {
            fin = result;
        }
        return fin;

    }

    //Splits String into tokens;
    protected static String[] tokens(String str) {
        String[] result = str.split("(?<=[-+*/])|(?=[-+*/])");
        return result;
    }

    //Computes the operation
    protected static Table oper (Table t, String[] columns, String named) {
        ArrayList<String>[] new_col = new ArrayList[columns.length];
        String[] tokes = new String[]{"+", "-", "*", "/",};
        ArrayList<String> tokens = new ArrayList<>(Arrays.asList(tokes));
        for (int cols = 0; cols < columns.length; cols++) {
            String[] check = columns[cols].split("\\s+as\\s+");
            check[0] = check[0].replaceAll("\\s+", "");
            String[] parts = tokens(check[0]);
            ArrayList<String> news = new ArrayList<>();
            String name;
            if (parts.length == 1) {
                if (parts[0].equals("*")) {
                    return new Table(t);
                }
                int col = t.names.indexOf(parts[0]);
                news = t.gets(col);
                if (check.length > 1) {
                    check[1] = check[1].replaceAll("\\s+", "");
                    news.set(0,check[1]);
                }
            } else {
                String one = parts[0];
                String operand = parts[1];
                String two = parts[2];
                int ind1 = t.names.indexOf(one);
                int ind2 = t.names.indexOf(two);
                ArrayList<String> colA = t.gets(ind1);
                ArrayList<String> colB = t.gets(ind2);
                int value;
                float floater;
                String sentence;
                String type = type_out(t.types.get(ind1), t.types.get(ind2));
                check[1] = check[1].replaceAll("\\s+", "");
                news.add(check[1]);
                if (type.equals("int") || type.equals("float") && tokens.contains(operand)) {
                    news.add(type);
                    if (type.equals("int")) {
                        for (int val = 2; val < t.rows; val++) {
                            if (operand.equals("+")) {
                                value = Integer.parseInt(colA.get(val)) + Integer.parseInt(colB.get(val));
                            } else if (operand.equals("-")) {
                                value = Integer.parseInt(colA.get(val)) - Integer.parseInt(colB.get(val));
                            } else if (operand.equals("*")) {
                                value = Integer.parseInt(colA.get(val)) * Integer.parseInt(colB.get(val));
                            } else {
                                value = Integer.parseInt(colA.get(val)) / Integer.parseInt(colB.get(val));
                            }
                            news.add(Integer.toString(value));
                        }
                    }
                    else if (type.equals("float")) {
                        for (int val = 2; val < t.rows; val++) {
                            if (operand.equals("+")) {
                                floater = Float.parseFloat(colA.get(val)) + Float.parseFloat(colB.get(val));
                            } else if (operand.equals("-")) {
                                floater = Float.parseFloat(colA.get(val)) - Float.parseFloat(colB.get(val));
                            } else if (operand.equals("*")) {
                                floater = Float.parseFloat(colA.get(val)) * Float.parseFloat(colB.get(val));
                            } else {
                                floater = Float.parseFloat(colA.get(val)) / Float.parseFloat(colB.get(val));
                            }
                            news.add(Float.toString(floater));
                        }
                    }
                } else if (type.equals("string") && operand.equals("+")) {
                    news.add(type);
                    for (int val = 2; val < t.rows; val++) {
                        sentence = colA.get(val) + colB.get(val);
                        news.add(sentence);
                    }
                } else {
                    System.err.printf("Operand: " + operand + " cannot be performed");
                }
            }
            new_col[cols] = news;
        }
        return new Table(new_col, named);
    }

    //Gives rows that do not match conditions
    protected static ArrayList<Integer> condition(Table t, String[] conds) {
        String val_2;
        ArrayList<Integer> rem = new ArrayList<>();
        Set<Integer> rm_rem = new HashSet<>();
        for (int x = 2; x < t.rows; x++) { //For each row
            int failed = 0; //Number of conditions the row failed
            for (int y = 0; y < conds.length; y++) { //For each condition
                int type_n; //type number: -1 to 2 from check_literal
                String type; //column type
                String[] parts = splits(conds[y]); //splits the condition into tokens
                int indx1 = t.names.indexOf(parts[0]); //Gets column1
                ArrayList<String> col1 = t.gets(indx1);
                ArrayList<String> col2; //Initializes column2
                if ((type_n = check_literal(parts[2])) != -1) { //Checks if  second arg is literal
                    String col_type; //Sets col_type of second arg based on type_n
                    val_2 = lit_to_str(parts[2]); //Changes literal to string
                    if (type_n == 0) {
                        col_type = "string";
                    } else if (type_n == 1) {
                        col_type = "int";
                    } else {
                        col_type = "float";
                    }
                    String[] vals = new String[t.rows]; //makes a string with same size as rows
                    Arrays.fill(vals, val_2); //Filled with second arg
                    col2 = new ArrayList<>(Arrays.asList(vals));
                    type = type_out(t.types.get(indx1), col_type); //Gets resulting type
                } else { //if not a literal
                    int indx2 = t.names.indexOf(parts[2]); //gets a column as second arg
                    col2 = t.gets(indx2);
                    type = type_out(t.types.get(indx1), t.types.get(indx2)); //gets resulting type
                }
                if (!sing_cond(col1.get(x), col2.get(x), parts[1], type)) {
                    failed++;
                }

            }
            if (failed > 0) {
                rem.add(x);
            }
        }
        rm_rem.addAll(rem);
        rem.clear();
        rem.addAll(rm_rem);
        Comparator comparator = Collections.reverseOrder();
        Collections.sort(rem,comparator);
        return rem;
    }

    //Checks values with a single condition
    protected static Boolean sing_cond(String val_1, String val_2, String bool, String type) {
        float val1;
        float val2;
        if (type != "string") {
            val1 = Float.parseFloat(val_1);
            val2 = Float.parseFloat(val_2);
            if (type == "int") {
                val1 = (float) Math.floor(val1);
                val2 = (float) Math.floor(val2);
            }
        }
        else {
            val1 = val_1.compareTo(val_2);
            val2 = 0;
        }
        if (bool.equals("==")) {
            return val1 == val2;
        }
        else if (bool.equals("!=")) {
            return val1 != val2;
        }
        else if (bool.equals(">")) {
            return val1 > val2;
        }
        else if (bool.equals(">=")) {
            return val1 >= val2;
        }
        else if (bool.equals("<")) {
            return val1 < val2;
        }
        else if (bool.equals("<=")) {
            return val1 <= val2;
        }
        return false;
    }

    //Checks if the string contains a literal
    protected static int check_literal(String x) {
        Boolean begin = x.startsWith("'");
        Boolean end = x.endsWith("'");
        if (begin && end) {
            return 0;
        }
        else {
            try {
                int y = Integer.parseInt(x);
                return 1;
            } catch (NumberFormatException e) {
                try {
                    float z = Float.parseFloat(x);
                    return 2;
                } catch (NumberFormatException f) {
                    return -1;
                }
            }
        }
    }

    //Converts literal to string
    protected static String lit_to_str(String x) {
        int y = check_literal(x);
        if (y == 0) {
        return x.substring(1,x.length()-1); }
        else {
            return x;
        }
    }

    //testing
    public static void main(String[] args) {
        String[] first = new String[]{"x", "y"};
        String[] second = new String[]{"int", "int"};
        String[] firsts = new String[]{"x", "z"};
        String[] seconds = new String[]{"int", "int"};
        Table t = new Table(3, new ArrayList<String>(Arrays.asList(first)), new ArrayList<String>(Arrays.asList(second)), "t");
        Table s = new Table(3, new ArrayList<String>(Arrays.asList(firsts)), new ArrayList<String>(Arrays.asList(seconds)), "s");
        Integer[] one = new Integer[]{2, 5};
        Integer[] two = new Integer[]{8, 3};
        Integer[] three = new Integer[]{13, 7};
        Integer[] ones = new Integer[]{2, 4};
        Integer[] twos = new Integer[]{8, 9};
        Integer[] threes = new Integer[]{10, 1};
        t.insert(new ArrayList<Integer>(Arrays.asList(one)));
        t.insert(new ArrayList<Integer>(Arrays.asList(two)));
        t.insert(new ArrayList<Integer>(Arrays.asList(three)));
        s.insert(new ArrayList<Integer>(Arrays.asList(ones)));
        s.insert(new ArrayList<Integer>(Arrays.asList(twos)));
        s.insert(new ArrayList<Integer>(Arrays.asList(threes)));
        String tables = "t,s";
        String operand = "'*'";
        String[] table = tables.split("\\s*,\\s*");
        Database db = new Database();
        db.storage.add(t);
        db.storage.add(s);
        Table[] ts =  db.retrieve(table);
        Table k  = new Table(ts, "k");
        k.print();
        String[] columns = operand.split("\\s*,\\s*");
        for (int x = 0; x < columns.length; x++) {
            System.out.println(columns[x]);
        }
        Table j = oper(k, columns, "j");
        j.print();
    }

}
