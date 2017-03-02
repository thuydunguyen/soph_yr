package db;

/**
 * Created by Thuy-Du on 3/1/2017.
 */
import java.util.*;
public class G_func {

    protected static ArrayList<String> combine_lists(ArrayList<String> list1, ArrayList<String> list2, ArrayList<String> list3) {
        ArrayList combination = new ArrayList<String>(list1);
        combination.addAll(list2);
        combination.addAll(list3);
        return combination;
    }

    protected static ArrayList<String> to_list(String[] x) {
        return new ArrayList<>(Arrays.asList(x));
    }

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
    public static void main(String[] args) {
        ArrayList<String> x =  new ArrayList<>();
        x.add("a");
        x.add("b");
        x.add("b");
        x.add("c");
        x.add("d");
        x.add("b");
        String y = "b";
        ArrayList<Integer> k = each_index(x, y);
        System.out.print(k.get(0));
        System.out.print(k.get(1));
        System.out.print(k.get(2));
    }


}
