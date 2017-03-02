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
}
