import java.util.Hashtable;
import java.util.Map;

/**
 * Created by Thuy-Du on 4/18/2017.
 */
public class test {

    public static void main(String[] args) {
        Map<String, Integer> t = new Hashtable<>();
        t.put("A", 1);
        t.put("B", 2);
        System.out.println(t.get("A"));
        System.out.println(t.get("F"));


    }
}
