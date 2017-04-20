import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Thuy-Du on 4/18/2017.
 */
public class test {
    private final Map<String, Node> nodes = new LinkedHashMap<>();

    static class Node{
        private String name;
        private boolean yes;

        public Node(String name) {
            this.name = name;
            yes = false;
        }

        void setYes(boolean t) {
            this.yes = t;
        }

    }

    test() {

    }
    void addNode(Node node) {
        nodes.put(node.name, node);
    }




    public static void main(String[] args) {
        test t = new test();
        Node n1 = new Node("n1");
        t.addNode(n1);
        n1.setYes(true);
        System.out.println(n1.yes);
        System.out.print(t.nodes.get("n1").yes);



    }
}
