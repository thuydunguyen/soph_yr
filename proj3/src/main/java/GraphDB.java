import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashSet;


/**
 * Graph for storing all of the intersection (vertex) and road (edge) information.
 * Uses your GraphBuildingHandler to convert the XML files into a graph. Your
 * code must include the vertices, adjacent, distance, closest, lat, and lon
 * methods. You'll also need to include instance variables and methods for
 * modifying the graph (e.g. addNode and addEdge).
 *
 * @author Alan Yao, Josh Hug
 */
public class GraphDB {
    /** Your instance variables for storing the graph. You should consider
     * creating helper classes, e.g. Node, Edge, etc. */

    /**
     * Example constructor shows how to create and start an XML parser.
     * You do not need to modify this constructor, but you're welcome to do so.
     *
     * @param dbPath Path to the XML file to be parsed.
     */

    private final Map<Long, Node> nodes = new LinkedHashMap<>();
    private final Map<String, Edge> edges = new LinkedHashMap<>();

    public GraphDB(String dbPath) {
        try {
            File inputFile = new File(dbPath);
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            GraphBuildingHandler gbh = new GraphBuildingHandler(this);
            saxParser.parse(inputFile, gbh);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        clean();
    }

    static class Node {
        private String name;
        private Long id;
        private String lon;
        private String lat;
        private Set<Edge> edge;
        private Set<Long> adj;
        private Node parent;
        private double dist;
        private double prior;

        Node(Long id, String lon, String lat) {
            this.name = null;
            this.id = id;
            this.lon = lon;
            this.lat = lat;
            edge = new HashSet<>();
            adj = new HashSet<>();
        }


        void setName(String name) {
            this.name = name;
        }

        void addEdge(Edge edged) {
            this.edge.add(edged);
        }

        void addAdj(Node n) {
            this.adj.add(n.id);
        }

        Long getId() {
            return id;
        }

        double distance(double tlon, double tlat) {
            double x1 = Double.parseDouble(lon);
            double y1 = Double.parseDouble(lat);
            double first = Math.pow((tlon - x1), 2);
            double second = Math.pow((tlat - y1), 2);
            return Math.pow((first + second), .5);
        }

        void setRest(Node parented, double endlon, double endlat) {
            this.parent = parented;
            if (parent == null) {
                this.dist = 0;
            } else {
                double plon = Double.valueOf(parent.lon);
                double plat = Double.valueOf(parent.lat);
                this.dist = parent.dist + distance(plon, plat);
            }
            this.prior = this.dist + distance(endlon, endlat);
        }

        double getPrior() {
            return prior;
        }

        Node getParent() {
            return parent;
        }

        double getDist() {
            return dist;
        }

        double getLon() {
            return Double.parseDouble(lon);
        }

        double getLat() {
            return Double.parseDouble(lat);
        }


    }

    static class Edge {
        private String id;
        private ArrayList<Node> ref;
        private boolean valid;


        Edge(String id) {
            this.id = id;
            this.ref = new ArrayList<>();
            valid = false;
        }


        void setValidity(boolean tf) {
            this.valid = tf;
        }

        void addRef(Node node) {
            this.ref.add(node);
        }

        ArrayList<Node> getrefs() {
            return this.ref;
        }

        boolean isValid() {
            return valid;
        }
    }

    /**
     * Helper to process strings into their "cleaned" form, ignoring punctuation and capitalization.
     *
     * @param s Input string.
     * @return Cleaned string.
     */
    static String cleanString(String s) {
        return s.replaceAll("[^a-zA-Z ]", "").toLowerCase();
    }

    /**
     * Remove nodes with no connections from the graph.
     * While this does not guarantee that any two nodes in the remaining graph are connected,
     * we can reasonably assume this since typically roads are connected.
     */
    private void clean() {
        Iterable itera = vertices();
        Iterator<Long> iter = itera.iterator();
        while (iter.hasNext()) {
            Long id = iter.next();
            Node node = getNode(id);
            if (node.edge.isEmpty()) {
                iter.remove();
            }

        }
    }

//////////////////////////////////***HELPERS***/////////////////////////////////////////

    void addNode(Node node) {
        nodes.put(node.id, node);
    }

    void addEdge(Edge edge) {
        edges.put(edge.id, edge);
    }

    Node getNode(Long id) {
        return nodes.get(id);
    }


    /////////////////////////////////////////////////////////////////////////////////////

    /**
     * Returns an iterable of all vertex IDs in the graph.
     */
    Iterable<Long> vertices() {
        return nodes.keySet();
    }

    /**
     * Returns ids of all vertices adjacent to v.
     */
    Iterable<Long> adjacent(long v) {
        return nodes.get(v).adj;
    }

    /**
     * Returns the Euclidean distance between vertices v and w, where Euclidean distance
     * is defined as sqrt( (lonV - lonV)^2 + (latV - latV)^2 ).
     */
    double distance(long v, long w) {
        Node a = nodes.get(v);
        Node b = nodes.get(w);
        double x1 = Double.parseDouble(a.lon);
        double y1 = Double.parseDouble(a.lat);
        double x2 = Double.parseDouble(b.lon);
        double y2 = Double.parseDouble(b.lat);

        double first = Math.pow((x1 - x2), 2);
        double second = Math.pow((y1 - y2), 2);

        return Math.pow((first + second), .5);


    }

    /**
     * Returns the vertex id closest to the given longitude and latitude.
     */
    long closest(double lon, double lat) {
        Iterator<Long> iter = vertices().iterator();
        double min = 0;
        int start = 0;
        Long currid = Long.valueOf("0");
        while (iter.hasNext()) {
            Long id = iter.next();
            Node a = nodes.get(id);
            double x1 = Double.parseDouble(a.lon);
            double y1 = Double.parseDouble(a.lat);
            double first = Math.pow((x1 - lon), 2);
            double second = Math.pow((y1 - lat), 2);
            double dist = Math.pow((first + second), .5);

            if (min > dist || start == 0) {
                min = dist;
                currid = id;
            }
            start++;
        }
        return currid;

    }

    /**
     * Longitude of vertex v.
     */
    double lon(long v) {
        Node n = nodes.get(v);
        return Double.parseDouble(n.lon);
    }

    /**
     * Latitude of vertex v.
     */
    double lat(long v) {
        Node n = nodes.get(v);
        return Double.parseDouble(n.lat);
    }

}