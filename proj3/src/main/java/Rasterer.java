import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Hashtable;

/**
 * This class provides all code necessary to take a query box and produce
 * a query result. The getMapRaster method must return a Map containing all
 * seven of the required fields, otherwise the front end code will probably
 * not draw the output correctly.
 */
public class Rasterer {
    private String[][] imgs;
    private String[] num = new String[]{"1", "2", "3", "4"};
    private Map<String, Node> QuadTree = new Hashtable<>();
    private ArrayList<ArrayList<Node>> Zooms = new ArrayList<>();
    private ArrayList<Node> Zoom1 = new ArrayList<>();
    private ArrayList<Node> Zoom2 = new ArrayList<>();
    private ArrayList<Node> Zoom3 = new ArrayList<>();
    private ArrayList<Node> Zoom4 = new ArrayList<>();
    private ArrayList<Node> Zoom5 = new ArrayList<>();
    private ArrayList<Node> Zoom6 = new ArrayList<>();
    private ArrayList<Node> Zoom7 = new ArrayList<>();


    // Recommended: QuadTree instance variable. You'll need to make
    //              your own QuadTree since there is no built-in quadtree in Java.

    /**
     * imgRoot is the name of the directory containing the images.
     * You may not actually need this for your class.
     */
    private class Node {
        Node parent;
        Node[] sub = new Node[4];
        String img;
        double ullon;
        double lrlon;
        double lrlat;
        double ullat;

        Node(String img) {
            double r_ullat = MapServer.ROOT_ULLAT;
            double r_lrlat = MapServer.ROOT_LRLAT;
            double r_ullon = MapServer.ROOT_ULLON;
            double r_lrlon = MapServer.ROOT_LRLON;
            this.img = img;
            if (img.equals("root")) {
                parent = this;
                ullon = r_ullon;
                ullat = r_ullat;
                lrlon = r_lrlon;
                lrlat = r_lrlat;
            } else if (img.length() == 1) {
                parent = QuadTree.get("root");
            } else {
                String p = img.substring(0, img.length() - 1);
                parent = QuadTree.get(p);
            }
            if (!img.equals("root")) {
                String l_num = img.substring(img.length() - 1);
                double[][][] coords = coords(parent.ullon, parent.ullat, parent.lrlon, parent.lrlat);
                if (l_num.equals("1")) {
                    ullon = coords[0][0][0];
                    ullat = coords[0][0][1];
                    lrlat = coords[1][1][1];
                    lrlon = coords[1][1][0];
                } else if (l_num.equals("2")) {
                    ullon = coords[0][1][0];
                    ullat = coords[0][1][1];
                    lrlat = coords[1][2][1];
                    lrlon = coords[1][2][0];
                } else if (l_num.equals("3")) {
                    ullon = coords[1][0][0];
                    ullat = coords[1][0][1];
                    lrlat = coords[2][1][1];
                    lrlon = coords[2][1][0];
                } else if (l_num.equals("4")) {
                    ullon = coords[1][1][0];
                    ullat = coords[1][1][1];
                    lrlat = coords[2][2][1];
                    lrlon = coords[2][2][0];
                }
            }
            placeZoom(img, this);
            QuadTree.put(img, this);
            if (img.equals("root")) {
                img = "";
            }
            for (int x = 0; x < 4; x++) {
                if (img.length() == 7) {
                    sub[x] = null;
                } else {
                    sub[x] = new Node(img + num[x]);
                }
            }


        }


        double[][][] coords(double ullon, double ullat, double lrlon, double lrlat) {
            double m_lon = (ullon + lrlon) / 2;
            double m_lat = (ullat + lrlat) / 2;
            double[] longs = new double[]{ullon, m_lon, lrlon};
            double[] lats = new double[]{ullat, m_lat, lrlat};
            double[][][] coord = new double[3][3][2];
            for (int x = 0; x < 3; x++) {
                for (int y = 0; y < 3; y++) {
                    coord[x][y][0] = longs[y];
                    coord[x][y][1] = lats[x];
                }
            }
            return coord;

        }
    }

    private void placeZoom(String img, Node t) {
        int len = img.length();
        if (img.equals("root")) {
        } else if (len == 1) {
            Zoom1.add(t);
        } else if (len == 2) {
            Zoom2.add(t);
        } else if (len == 3) {
            Zoom3.add(t);
        } else if (len == 4) {
            Zoom4.add(t);
        } else if (len == 5) {
            Zoom5.add(t);
        } else if (len == 6) {
            Zoom6.add(t);
        } else if (len == 7) {
            Zoom7.add(t);
        }
    }


    public Rasterer(String imgRoot) {
        new Node("root");
        Zooms.add(Zoom1);
        Zooms.add(Zoom2);
        Zooms.add(Zoom3);
        Zooms.add(Zoom4);
        Zooms.add(Zoom5);
        Zooms.add(Zoom6);
        Zooms.add(Zoom7);
    }

    private static double LonDPP(double lrlon, double ullon, double width) {
        return (lrlon - ullon) / width;
    }


    /**
     * Takes a user query and finds the grid of images that best matches the query. These
     * images will be combined into one big image (rastered) by the front end. <br>
     * <p>
     * The grid of images must obey the following properties, where image in the
     * grid is referred to as a "tile".
     * <ul>
     * <li>The tiles collected must cover the most longitudinal distance per pixel
     * (LonDPP) possible, while still covering less than or equal to the amount of
     * longitudinal distance per pixel in the query box for the user viewport size. </li>
     * <li>Contains all tiles that intersect the query bounding box that fulfill the
     * above condition.</li>
     * <li>The tiles must be arranged in-order to reconstruct the full image.</li>
     * </ul>
     * </p>
     *
     * @param params Map of the HTTP GET request's query parameters - the query box and
     *               the user viewport width and height.
     * @return A map of results for the front end as specified:
     * "render_grid"   -> String[][], the files to display
     * "raster_ul_lon" -> Number, the bounding upper left longitude of the rastered image <br>
     * "raster_ul_lat" -> Number, the bounding upper left latitude of the rastered image <br>
     * "raster_lr_lon" -> Number, the bounding lower right longitude of the rastered image <br>
     * "raster_lr_lat" -> Number, the bounding lower right latitude of the rastered image <br>
     * "depth"         -> Number, the 1-indexed quadtree depth of the nodes of the rastered image.
     * Can also be interpreted as the length of the numbers in the image
     * string. <br>
     * "query_success" -> Boolean, whether the query was able to successfully complete. Don't
     * forget to set this to true! <br>
     * @see #REQUIRED_RASTER_REQUEST_PARAMS
     */

    /**
     * params: lrlon, ullon, w, h, ullat, lrlat
     */

    public Map<String, Object> getMapRaster(Map<String, Double> params) {
        // System.out.println(params);
        String[][] imgs;
        Map<String, Object> results = new HashMap<>();
        Double d_ullon = params.get("ullon");
        Double d_lrlon = params.get("lrlon");
        Double d_width = params.get("w");
        Double d_ullat = params.get("ullat");
        Double d_lrlat = params.get("lrlat");
        if (!valid(d_ullon, d_lrlon, d_ullat, d_lrlat)) {
            results.put("query_succes", false);
        } else {
            double d_londpp = LonDPP(d_lrlon, d_ullon, d_width);
            d_lrlon = adjust(d_lrlon, "lrlon");
            d_lrlat = adjust(d_lrlat, "lrlat");
            d_ullon = adjust(d_ullon, "ullon");
            d_ullat = adjust(d_ullat, "ullat");
            Node first;
            Node sec;
            Node third;
            Node curr = QuadTree.get("1");
            double londpp = LonDPP(curr.lrlon, curr.ullon, 256);
            while (londpp > d_londpp) {
                curr = curr.sub[0];
                londpp = LonDPP(curr.lrlon, curr.ullon, 256);
            }
            int len = curr.img.length();
            int k = 0;
            Node check = Zooms.get(len - 1).get(k);
            while (!contained(d_ullon, d_ullat, check)) {
                k++;
                check = Zooms.get(len - 1).get(k);
            }
            first = check;

            ArrayList<String> col = nimgs(first.img, d_lrlat, d_lrlon, "v");
            int n_rows = col.size();
            ArrayList<String> row = nimgs(first.img, d_lrlat, d_lrlon, "h");
            int n_cols = row.size();
            imgs = new String[n_rows][n_cols];
            String[] rowed = row.toArray(new String[n_rows]);
            imgs[0] = rowed;
            for (int x = 1; x < col.size(); x++) {
                Node next = QuadTree.get(col.get(x));
                row = imgrows(next.img, n_cols);
                rowed = row.toArray(new String[n_rows]);
                imgs[x] = rowed;
            }
            sec = QuadTree.get(imgs[0][n_cols - 1].replaceAll("\\D+", ""));
            third = QuadTree.get(imgs[n_rows - 1][0].replaceAll("\\D+", ""));

            results.put("render_grid", imgs);
            results.put("raster_ul_lon", first.ullon);
            results.put("raster_ul_lat", first.ullat);
            results.put("raster_lr_lon", sec.lrlon);
            results.put("raster_lr_lat", third.lrlat);
            results.put("depth", len);
            results.put("query_success", true);
        }
        return results;
    }

    private double adjust(double d, String point) {
        double r_ullat = MapServer.ROOT_ULLAT;
        double r_lrlat = MapServer.ROOT_LRLAT;
        double r_ullon = MapServer.ROOT_ULLON;
        double r_lrlon = MapServer.ROOT_LRLON;
        if (point.equals("ullon")) {
            if (d < r_ullon) {
                return r_ullon;
            }
        } else if (point.equals("ullat")) {
            if (d > r_ullat) {
                return r_ullat;
            }
        } else if (point.equals("lrlon")) {
            if (d > r_lrlon) {
                return r_lrlon;
            }
        } else if (point.equals("lrlat")) {
            if (d < r_lrlat) {
                return r_lrlat;
            }
        }
        return d;
    }

    private boolean contained(double lon, double lat, Node t) {
        if (t.ullon <= lon && t.lrlon >= lon) {
            if (t.ullat >= lat && t.lrlat <= lat) {
                return true;
            }
        }
        return false;
    }

    private static String incr(String img, String horv) {
        String l_char = img.substring(img.length() - 1);
        String x;
        if (horv.equals("h")) {
            if (l_char.equals("1") || l_char.equals("3")) {
                int num = Integer.parseInt(img) + 1;
                x = Integer.toString(num);
            } else {
                if (l_char.equals("4")) {
                    l_char = "3";
                } else if (l_char.equals("2")) {
                    l_char = "1";
                }
                x = incr(img.substring(0, img.length() - 1), horv) + l_char;
            }
        } else {
            if (l_char.equals("1") || l_char.equals("2")) {
                int num = Integer.parseInt(img) + 2;
                x = Integer.toString(num);
            } else {
                if (l_char.equals("3")) {
                    l_char = "1";
                } else if (l_char.equals("4")) {
                    l_char = "2";
                }
                x = incr(img.substring(0, img.length() - 1), horv) + l_char;
            }
        }
        return x;
    }

    private ArrayList<String> nimgs(String img, double d_lrlat, double d_lrlon, String horv) {
        String imgstr = "img/";
        String endstr = ".png";
        ArrayList<String> row = new ArrayList<>();
        ArrayList<String> col = new ArrayList<>();
        Node curr = QuadTree.get(img);
        String comp2 = "\\b[2,4]+\\b";
        String comp3 = "\\b[3,4]+\\b";
        col.add(curr.img);
        row.add(imgstr + curr.img + endstr);
        if (horv.equals("h")) {
            while (curr.lrlon < d_lrlon && !curr.img.matches(comp2)) {
                curr = QuadTree.get(incr(curr.img, "h"));
                row.add(imgstr + curr.img + endstr);
            }
            return row;
        } else {
            while (curr.lrlat > d_lrlat && !curr.img.matches(comp3)) {
                curr = QuadTree.get(incr(curr.img, "v"));
                col.add(curr.img);
            }
            return col;
        }

    }

    private ArrayList<String> imgrows(String img, int times) {
        String imgstr = "img/";
        String endstr = ".png";
        ArrayList<String> row = new ArrayList<>();
        Node node = QuadTree.get(img);
        String curr = node.img;
        row.add(imgstr + curr + endstr);
        for (int x = 0; x < times; x++) {
            curr = incr(curr, "h");
            row.add(imgstr + curr + endstr);
        }
        return row;
    }

    private boolean valid(double ullon, double lrlon, double ullat, double lrlat) {
        double r_ullat = MapServer.ROOT_ULLAT;
        double r_lrlat = MapServer.ROOT_LRLAT;
        double r_ullon = MapServer.ROOT_ULLON;
        double r_lrlon = MapServer.ROOT_LRLON;
        if (ullon > lrlon || lrlat > ullat) {
            return false;
        } else if (r_ullon > ullon && r_lrlon < lrlon && r_ullat < ullat && r_lrlat > lrlat) {
            return false;
        } else {
            return true;
        }
    }

    public static void main(String[] args) {
        Rasterer t = new Rasterer("img");
        Map<String, Double> params = new HashMap<>();
        params.put("lrlon", -122.22275132672245);
        params.put("ullon", -122.23995662778569);
        params.put("w", 613.0);
        params.put("h", 676.0);
        params.put("ullat", 37.877266154010954);
        params.put("lrlat", 37.85829260830337);
        Map<String, Object> k = t.getMapRaster(params);
        String[][] imgs = (String[][]) k.get("render_grid");
        for (int x = 0; x < imgs.length; x++) {
            for (int y = 0; y < imgs[0].length; y++) {
                System.out.print(imgs[x][y] + "  ");
            }
            System.out.println("");
        }
        System.out.println(Array.getLength(imgs));


        double r_lrlon = -122.2104604264636;
        double r_ullon = -122.30410170759153;
        double r_ullat = 37.870213571328854;
        double r_lrlat = 37.8318576119893;
        r_lrlon = t.adjust(r_lrlon, "lrlon");
        r_lrlat = t.adjust(r_lrlat, "lrlat");
        r_ullon = t.adjust(r_ullon, "ullon");
        r_ullat = t.adjust(r_ullat, "ullat");


        /**double lrlon = t.QuadTree.get("11").lrlon;
         double ullon = t.QuadTree.get("11").ullon;
         double ullat = t.QuadTree.get("11").ullat;
         double lrlat = t.QuadTree.get("11").lrlat;
         */


    }
}
