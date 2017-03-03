package db;

/**
 * Created by Thuy-Du on 3/1/2017.
 */
import java.util.*;
import db.Table;
import java.util.StringTokenizer;

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
        Iterator z = x.iterator();
        ArrayList<Integer> index = new ArrayList<>();
        for (int k = 0; k < x.size(); k++) {
            if (z.next().equals(y)) {
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

    //Splits String into parts at every whitespace
    protected static String[] splits(String str) {
        return str.split("\\s+");
    }

    //Computes the operation
    protected static ArrayList<String> oper (Table t, String operation, String name) {
        String[] tokes = new String[] {"+", "-", "*", "/",};
        ArrayList<String> tokens = new ArrayList<>(Arrays.asList(tokes));
        String[] parts = splits(operation);
        ArrayList<String> news = new ArrayList<>();
        if (parts.length == 1) {
            int col = t.names.indexOf(parts[0]);
            news = t.gets(col);
        } else {
            String one = parts[0];
            String operand = parts[1];
            String two = parts[2];
            int ind1 = t.names.indexOf(one);
            int ind2 = t.names.indexOf(two);
            ArrayList<String> colA = t.gets(ind1);
            ArrayList<String> colB = t.gets(ind2);
            int value;
            String sentence;
            String type = type_out(t.types.get(ind1), t.types.get(ind2));
            news.add(name);
            if (type.equals("int") || type.equals("float") && tokens.contains(operand)) {
                news.add(type);
                for (int val = 2; val < t.rows; val++) {
                    if (operand.equals("+")) {
                        value = Integer.parseInt(colA.get(val)) + Integer.parseInt(colB.get(val));
                    } else if (operand.equals("-")) {
                        value = Integer.parseInt(colA.get(val)) - Integer.parseInt(colB.get(val));
                    } else if (operand.equals("*")) {
                        value = Integer.parseInt(colA.get(val)) * Integer.parseInt(colB.get(val));
                    } else {
                        value = Integer.parseInt(colA.get(val)) + Integer.parseInt(colB.get(val));
                    }
                    news.add(Integer.toString(value));
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
            return news;
    }

    //Gives rows that do not match conditions
    protected static ArrayList<Integer> cond(Table t, String conditions) {
        String val_2;
        String[] conds = conditions.split("\\s+and\\s+");
        ArrayList<Integer> rem = new ArrayList<>();
        for (int x = 2; x < t.rows; x++) {
            int failed = 0;
            for (int y = 0; y < conds.length; y++) {
                int type_n;
                String type;
                String[] parts = splits(conds[y]);
                int indx1 = t.names.indexOf(parts[0]);
                ArrayList<String> col1 = t.gets(indx1);
                ArrayList<String> col2;
                if ((type_n = check_literal(parts[2])) != -1) {
                    String col_type;
                    val_2 = lit_to_str(parts[2]);
                    if (type_n == 0) {
                        col_type = "string";
                    } else if (type_n == 1) {
                        col_type = "int";
                    } else {
                        col_type = "float";
                    }
                    String[] vals = new String[t.rows];
                    Arrays.fill(vals, val_2);
                    col2 = new ArrayList<>(Arrays.asList(vals));
                    type = type_out(t.types.get(indx1), col_type);
                } else {
                    int indx2 = t.names.indexOf(parts[2]);
                    col2 = t.gets(indx2);
                    type = type_out(t.types.get(indx1), t.types.get(indx2));
                }
                if (!sing_cond(col1.get(x), col2.get(x), parts[1], type)) {
                    failed++;
                }

            }
            if (failed > 0) {
                rem.add(x);
            }
        }
        Comparator comparator = Collections.reverseOrder();
        Collections.sort(rem,comparator);
        return rem;
    }

    //Checks values with a single condition
    protected static Boolean sing_cond(String val_1, String val_2, String bool, String type) {
        int val1;
        int val2;
        if (type != "string") {
            val1 = Integer.parseInt(val_1);
            val2 = Integer.parseInt(val_2);}
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
        return x.substring(1,x.length()-2); }
        else {
            return x;
        }
    }

    //testing
    public static void main(String[] args) {
        String[] first = new String[] {"x","y"};
        String[] second = new String[] {"int", "int"};
        String[] firsts = new String[] {"x", "z"};
        String[] seconds = new String[] {"int", "int"};
        String[] firsted = new String[] {"x","b"};
        Table t = new Table(3, new ArrayList<String>(Arrays.asList(first)), new ArrayList<String>(Arrays.asList(second)), "t");
        Table s = new Table(3, new ArrayList<String>(Arrays.asList(firsts)), new ArrayList<String>(Arrays.asList(seconds)), "s");
        Table u = new Table(3, new ArrayList<String>(Arrays.asList(firsted)), new ArrayList<String>(Arrays.asList(seconds)), "u");
        Integer[] one = new Integer[] {2,5};
        Integer[] two = new Integer[] {8,3};
        Integer[] three = new Integer[] {13,7};
        Integer[] ones = new Integer[] {2,4};
        Integer[] twos = new Integer[] {8,9};
        Integer[] threes = new Integer[] {10,1};
        Integer[] oned = new Integer[] {2,3};
        Integer[] twod = new Integer[] {8,0};
        Integer[] threed = new Integer[] {5,1};
        t.insert(new ArrayList<Integer>(Arrays.asList(one)));
        t.insert(new ArrayList<Integer>(Arrays.asList(two)));
        t.insert(new ArrayList<Integer>(Arrays.asList(three)));
        s.insert(new ArrayList<Integer>(Arrays.asList(ones)));
        s.insert(new ArrayList<Integer>(Arrays.asList(twos)));
        s.insert(new ArrayList<Integer>(Arrays.asList(threes)));
        u.insert(new ArrayList<Integer>(Arrays.asList(oned)));
        u.insert(new ArrayList<Integer>(Arrays.asList(twod)));
        u.insert(new ArrayList<Integer>(Arrays.asList(threed)));
        String conds = "x > y and x == 2";
        ArrayList<Integer> rem = cond(t,conds);
        System.out.println(rem.get(0));
        System.out.println(rem.get(1));
        System.out.println(rem.get(2));
    }


}
