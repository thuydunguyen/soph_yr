package db;

import java.io.*;
import java.util.*;

//NEED TO ADD IN SCENARIOS FOR NaN AND NOVALUE//

public class G_func {

    //*******************ARITHMETIC*********************//

    //Computes arithmetic
    protected static Table oper(Table t, String[] columns, String named) {
        if (columns[0].equals("*")) {
            Table Q = new Table(t);
            Q.named = named;
            return Q;
        }
        ArrayList<String>[] new_col = new ArrayList[columns.length];
        for (int cols = 0; cols < columns.length; cols++) { //for each arithmetic wanted
            String[] check = columns[cols].split("\\s+as\\s+");
            check[0] = check[0].replaceAll("\\s+", "");
            String[] parts = tokens(check[0]);
            ArrayList<String> news = new ArrayList<>();
            if (parts.length == 1) { //Checks if arithmetic is a single column name
                int col = t.names.indexOf(parts[0]);
                if (col < 0) { //Checks if column exists
                    return new Table();
                }
                news = t.gets(col);
                if (check.length > 1) { //Checks if col is renamed
                    check[1] = check[1].replaceAll("\\s+", "");
                    news.set(0, check[1]);
                }
            } else {
                ArrayList<String> colB;
                String one = parts[0];
                String operand = parts[1];
                String two = parts[2];
                int ind1 = t.names.indexOf(one);
                int ind2 = t.names.indexOf(two);
                if (ind1 == -1) {
                    Table e = new Table();
                    e.error = 3;
                    return e;
                }
                ArrayList<String> colA = t.gets(ind1);
                Object typeA = t.types.get(ind1);
                Object typeB;
                if (ind2 >= 0) {
                    colB = t.gets(ind2);
                    typeB = t.types.get(ind2);
                } else {
                    colB = new ArrayList<>();
                    for (int d = 0; d < colA.size(); d++) {
                        colB.add(parts[2]);
                    }
                    typeB = check_literal(parts[2]);
                    if (typeB.equals("none")) {
                        Table e = new Table();
                        e.error = 3;
                        return e;
                    }
                }
                String type = type_out(typeA, typeB);
                if (type.equals("BAD")) {
                    return new Table();
                }
                if (check.length == 1) {
                    System.out.println("Column name needed for arithmetic");
                    return new Table();
                }
                check[1] = check[1].replaceAll("\\s+", "");
                news.add(check[1]);
                news.add(type);
                for (int val = 2; val < t.rows; val++) {
                    String valid = valid_oper(operand, type);
                    String str = compute(colA.get(val), colB.get(val), operand, type);
                    if (valid.equals("false")) {
                        Table e = new Table();
                        e.error = 2;
                        return e;
                    }
                    news.add(str);
                }
            }
            new_col[cols] = news;
        }
        return new Table(new_col, named);
    }

    //Splits String into tokens (splits at every nonalphanumeric character;
    protected static String[] tokens(String str) {
        String[] result = str.split("(?<=[^\\w]+)|(?=[^\\w]+)");
        return result;
    }

    //Compares types and outputs resulting type
    protected static String type_out(Object a, Object b) {
        if (a.equals("special") && b.equals("special")) {
            return "";
        } else if (a.equals("special")) {
            return (String) b;
        } else if (b.equals("special")) {
            return (String) a;
        }
        ArrayList<String> nums = new ArrayList<String>(Arrays.asList(new String[]{"int", "float"}));
        ArrayList<String> words = new ArrayList<String>(Arrays.asList(new String[]{"string"}));
        if (nums.contains(a) && nums.contains(b)) {
            int x = nums.indexOf(a);
            int y = nums.indexOf(b);
            return nums.get(Math.max(x, y));
        } else if (words.contains(a) && words.contains(b)) {
            return "string";
        } else {
            System.err.printf("Cannot perform operation with types: " + a + " and " + b + "\n");
            return "BAD";
        }
    }

    //Computes the operation
    protected static String compute(String val1, String val2, String oper, String type) {
        if (val1.equals("NaN") || val2.equals("NaN")) {
            return "NaN";
        }
        if (val1.equals("NOVALUE") && val2.equals("NOVALUE")) {
            return "NOVALUE";
        }
        if (type.equals("string")) {
            val1 = val1.substring(1, val1.length() - 1);
            val2 = val2.substring(1, val2.length() - 1);
            if (val1.equals("NOVALUE")) {
                val1 = "";
            }
            if (val2.equals("NOVALUE")) {
                val2 = "";
            }
            return "'" + val1 + val2 + "'";
        } else {
            float vals1;
            float vals2;
            float result;
            String str;
            if (val1.equals("NOVALUE")) {
                vals1 = 0.0f;
                vals2 = Float.parseFloat(val2);
            } else if (val2.equals("NOVALUE")) {
                vals2 = 0.0f;
                vals1 = Float.parseFloat(val1);
            } else {
                vals1 = Float.parseFloat(val1);
                vals2 = Float.parseFloat(val2);
            }
            if (oper.equals("+")) {
                result = vals1 + vals2;
            } else if (oper.equals("-")) {
                result = vals1 - vals2;
            } else if (oper.equals("*")) {
                result = vals1 * vals2;
            } else if (oper.equals("/")) {
                if (Math.signum(vals2) == 0) {
                    return "NaN";
                }
                result = vals1 / vals2;
            } else {
                return "";
            }
            if (type.equals("int")) {
                str = Integer.toString((int) Math.floor(result));
            } else {
                str = f_to_str(result);
            }
            return str;
        }
    }

    //Checks if arithmetic is valid
    protected static String valid_oper(String oper, String type) {
        ArrayList<String> operations = new ArrayList<>(Arrays.asList(new String[]{"+", "-", "*", "/"}));
        String valid = "true";
        if (type.equals("string")) {
            if (!oper.equals("+")) {
                valid = "false";
            }
        } else {
            if (!operations.contains(oper)) {
                valid = "false";
            }
        }
        return valid;
    }


    //***********************CONDITIONS******************************//

    //Computes conditional statement
    protected static ArrayList<Integer> condition(Table t, String[] conds) {
        String val_2;
        ArrayList<Integer> rem = new ArrayList<>();
        Set<Integer> rm_rem = new HashSet<>();
        for (int x = 2; x < t.rows; x++) { //For each row
            int failed = 0; //Number of conditions the row failed
            for (int y = 0; y < conds.length; y++) { //For each condition
                String type_n; //type produced from check_literal
                String type; //column type
                String[] parts = splits(conds[y]); //splits the condition into tokens
                int indx1 = t.names.indexOf(parts[0]); //Gets column1
                ArrayList<String> col1 = t.gets(indx1);
                ArrayList<String> col2; //Initializes column2
                if ((type_n = check_literal(parts[2])) != "none") { //Checks if  second arg is literal
                    String col_type; //Sets col_type of second arg based on type_n
                    val_2 = parts[2];
                    col_type = type_n;
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
        Collections.sort(rem, comparator);
        return rem;
    }

    //Splits String into conditional parts
    protected static String[] splits(String str) {
        str = str.replaceAll("\\s+", "");
        String[] result = str.split("(?<=[<=>])|(?=[<=>])");
        String[] fin = new String[3];
        if (result.length == 4) {
            fin[1] = result[1] + result[2];
            fin[0] = result[0];
            fin[2] = result[3];
        } else {
            fin = result;
        }
        return fin;

    }

    //Checks values with a single condition
    protected static Boolean sing_cond(String val_1, String val_2, String bool, String type) {
        float val1;
        float val2;
        if ((val_1.equals("NOVALUE")) || val_2.equals("NOVALUE")) {
            return false;
        } else if ((val_1.equals("NaN") && val_2.equals("NaN"))) {
            return true;
        } else if (type != "string") {
            if (val_1.equals("NaN")) {
                switch (bool) {
                    case "==":
                        return false;
                    case ">":
                        return true;
                    case "<":
                        return false;
                    case ">=":
                        return true;
                    case "<=":
                        return false;
                    case "!=":
                        return true;
                }
            } else if (val_2.equals("NaN")) {
                switch (bool) {
                    case "==":
                        return false;
                    case ">":
                        return false;
                    case "<":
                        return true;
                    case ">=":
                        return false;
                    case "<=":
                        return true;
                    case "!=":
                        return true;
                }
            }
            val1 = Float.parseFloat(val_1);
            val2 = Float.parseFloat(val_2);
            if (type == "int") {
                val1 = (float) Math.floor(val1);
                val2 = (float) Math.floor(val2);
            }
        } else {
            val1 = val_1.compareTo(val_2);
            val2 = 0;
        }
        switch (bool) {
            case "==":
                return val1 == val2;
            case "!=":
                return val1 != val2;
            case ">":
                return val1 > val2;
            case ">=":
                return val1 >= val2;
            case "<":
                return val1 < val2;
            case "<=":
                return val1 <= val2;
        }
        return false;
    }


    //**********************GENERAL****************************//

    //Checks if the string contains a literal
    protected static String check_literal(String x) {
        Boolean begin = x.startsWith("'");
        Boolean end = x.endsWith("'");
        if (x.equals("NOVALUE")) {
            return "special";
        } else if (x.equals("NaN")) {
            return "special";
        } else if (begin && end) {
            return "string";
        } else {
            try {
                int y = Integer.parseInt(x);
                return "int";
            } catch (NumberFormatException e) {
                try {
                    float z = Float.parseFloat(x);
                    return "float";
                } catch (NumberFormatException f) {
                    return "none";
                }
            }
        }
    }

    //Changes float to string at three decimals
    protected static String f_to_str(float x) {
        return String.format("%.3f", x);
    }

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

    //Gets all indexes of a value in an arraylist
    protected static ArrayList<Integer> each_index(ArrayList<String> x, String y) {
        ArrayList<Integer> index = new ArrayList<>();
        for (int k = 2; k < x.size(); k++) {
            String val = x.get(k);
            if (val.equals(y)) {
                index.add(k);
            }
        }
        return index;
    }


    //****************LOAD_TABLE****************************//

    //Creates table from a path aka load
    protected static Table loadComp(String path, String name) {
        ArrayList<String> names = new ArrayList<>();
        ArrayList<String> types = new ArrayList<>();
        try {
            FileReader fr = new FileReader(path);
            BufferedReader textR = new BufferedReader(fr);
            int nlines = n_lines(path);
            String[] lines = new String[nlines];
            for (int x = 0; x < nlines; x++) {
                lines[x] = textR.readLine();
            }
            String[] n_t = lines[0].split("\\s*,\\s*");
            for (int y = 0; y < n_t.length; y++) {
                String[] parts = n_t[y].split("\\s+");
                names.add(parts[0]);
                types.add(parts[1]);
            }
            Table t = new Table(names, types, name);
            for (int z = 1; z < nlines; z++) {
                ArrayList<String> values = new ArrayList<>();
                String[] vals = lines[z].split("\\s*,\\s*");
                for (int v = 0; v < vals.length; v++) {
                    values.add(vals[v]);
                }
                t.insert(values);
            }
            return t;

        } catch (FileNotFoundException e) {
            System.out.println("File does not exist");
        } catch (IOException f) {
            System.out.println("No more lines to read");
        }
        return new Table();

    }

    //Gets number of lines in a file
    protected static int n_lines(String path) {
        try {
            FileReader fr = new FileReader(path);
            BufferedReader bf = new BufferedReader(fr);
            String line_n;
            int n = 0;
            while ((line_n = bf.readLine()) != null) {
                n++;
            }
            bf.close();
            return n;

        } catch (IOException f) {
            System.out.println("");
            return -1;
        }
    }

    //Creates path
    protected static String pathway(String name) {
        String fileName = name + ".tbl";
        String directory = System.getProperty("user.dir");
        String path = directory + File.separator + fileName;
        return path;
    }

    public static void main(String[] args) {
        String t = "Yes";
        String s = "1";
        String val = t + s;
        System.out.println("'" + val + "'");
    }


}
