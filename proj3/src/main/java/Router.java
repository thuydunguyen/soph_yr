import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Iterator;

/**
 * This class provides a shortestPath method for finding routes between two points
 * on the map. Start by using Dijkstra's, and if your code isn't fast enough for your
 * satisfaction (or the autograder), upgrade your implementation by switching it to A*.
 * Your code will probably not be fast enough to pass the autograder unless you use A*.
 * The difference between A* and Dijkstra's is only a couple of lines of code, and boils
 * down to the priority you use to order your vertices.
 */
public class Router {
    /**
     * Return a LinkedList of <code>Long</code>s representing the shortest path from st to dest,
     * where the longs are node IDs.
     */


    private Comparator<GraphDB.Node> comp;


    private Router() {
        comp = new Compares();
    }


    public static LinkedList<Long> shortestPath
            (GraphDB g, double stlon, double stlat, double destlon, double destlat) {
        PriorityQueue<GraphDB.Node> store = new PriorityQueue<>(new Router().comp);
        HashSet<Long> checked = new HashSet<>();
        LinkedList solved = new LinkedList();
        Long goal = g.closest(destlon, destlat);
        Long start = g.closest(stlon, stlat);
        GraphDB.Node check = g.getNode(start);
        check.setRest(null, destlon, destlat);
        store.add(check);
        while (!store.isEmpty()) {
            GraphDB.Node next = store.peek();
            checked.add(next.getId());
            store.remove(next);
            check = next;
            if (check.getId().equals(goal)) {
                break;
            }
            Iterator<Long> adj = g.adjacent(check.getId()).iterator();
            while (adj.hasNext()) {
                Long neigh = adj.next();
                GraphDB.Node pot = g.getNode(neigh);
                double potPrior = checkPrior(check.getDist(),
                        check.getLon(), check.getLat(), destlon, destlat,
                        pot.getLon(), pot.getLat());
                if (checked.contains(neigh)) {
                    if (check.getPrior() > potPrior) {
                        pot.setRest(check, destlon, destlat);
                        store.add(pot);
                    }
                } else {
                    pot.setRest(check, destlon, destlat);
                    store.add(pot);
                }
            }
        }

        while (check.getParent() != null) {
            solved.addFirst(check.getId());
            check = check.getParent();
        }
        solved.addFirst(start);

        return solved;
    }

    private static double dist(double lon, double lat, double lon2, double lat2) {
        double first = Math.pow((lon - lon2), 2);
        double second = Math.pow((lat - lat2), 2);
        return Math.pow((first + second), 0.5);
    }

    private static double checkPrior
            (double pdist, double plon, double plat,
             double endlon, double endlat, double lon, double lat) {
        double dist = pdist + dist(lon, lat, plon, plat);
        return dist + dist(lon, lat, endlon, endlat);
    }


    private class Compares implements Comparator<GraphDB.Node> {
        public int compare(GraphDB.Node a, GraphDB.Node b) {
            if (a.getPrior() > b.getPrior()) {
                return 1;
            } else if (a.getPrior() < b.getPrior()) {
                return -1;
            } else {
                return 0;
            }
        }
    }

    public static void main(String[] args) {

    }

}
