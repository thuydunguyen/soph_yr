package db;

/**
 * Created by Thuy-Du on 3/1/2017.
 */
import java.util.*;
public class G_func {

    protected static String[] every_other(String[] T, int start) {
        String[] t = new String[T.length];
        int index = 0;
        for (int x = start; x < T.length; x += 2) {
            t[index] = T[x];
            index++;
        }
        return t;
    }

    protected static ArrayList<String> combine_lists(ArrayList<String> list1, ArrayList<String> list2, ArrayList<String> list3) {
        ArrayList combination = new ArrayList<String>(list1);
        combination.addAll(list2);
        combination.addAll(list3);
        return combination;
    }
}
