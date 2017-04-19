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
            if (img.length() == 1) {
                parent = null;
            } else {
                String p = img.substring(0, img.length() - 1);
                parent = QuadTree.get(p);
            }
            if (img.length() == 1) {
                ullon = r_ullon;
                ullat = r_ullat;
                lrlon = r_lrlon;
                lrlat = r_lrlat;
            } else {
                String l_num = img.substring(img.length() - 1);
                double[][][] coords = coords(parent.ullon, parent.ullat, parent.lrlon, parent.lrlat);
                if (l_num.equals("1")) {
                    ullon = coords[0][0][0];
                    ullat = coords[0][0][1];
                    lrlat = coords[1][1][0];
                    lrlon = coords[1][1][1];
                } else if (l_num.equals("2")) {
                    ullon = coords[0][1][0];
                    ullat = coords[0][1][1];
                    lrlat = coords[1][2][0];
                    lrlon = coords[1][2][1];
                } else if (l_num.equals("3")) {
                    ullon = coords[1][0][0];
                    ullat = coords[1][0][1];
                    lrlat = coords[2][1][0];
                    lrlon = coords[2][1][1];
                } else if (l_num.equals("4")) {
                    ullon = coords[1][1][0];
                    ullat = coords[1][1][1];
                    lrlat = coords[2][2][0];
                    lrlon = coords[2][2][1];
                }
            }
            placeZoom(img, this);
            QuadTree.put(img, this);
            for (int x = 0; x < 4; x++) {
                if (img.length() == 7) {
                    sub[x] = null;
                } else {
                    sub[x] = new Node(img + num[x]);
                    sub[x].parent = this;
                }
            }


        }

        double[][][] coords(double ullon, double ullat, double lrlon, double lrlat) {
            double m_lon = (ullon + lrlon) / 2;
            double m_lat = (ullat + lrlat) / 2;
            double[] x_y = new double[2];
            double[][][] coord = new double[3][3][2];
            for (int x = 0; x < 3; x++) {
                if (x == 0) {
                    x_y[1] = ullat;
                } else if (x == 1) {
                    x_y[1] = m_lat;
                } else {
                    x_y[1] = lrlat;
                }
                for (int y = 0; y < 3; y++) {
                    if (y == 0) {
                        x_y[0] = ullon;
                    } else if (y == 1) {
                        x_y[0] = m_lon;
                    } else {
                        x_y[0] = lrlon;
                    }
                    coord[x][y] = x_y;
                }
            }
            return coord;
        }

    }

    private void placeZoom(String img, Node t) {
        int len = img.length();
        switch (len) {
            case 1:
                Zoom1.add(t);
            case 2:
                Zoom2.add(t);
            case 3:
                Zoom3.add(t);
            case 4:
                Zoom4.add(t);
            case 5:
                Zoom5.add(t);
            case 6:
                Zoom6.add(t);
            case 7:
                Zoom7.add(t);
        }
    }


    public Rasterer(String imgRoot) {
        for (int x = 0; x < 4; x++) {
            new Node(num[x]);
        }
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
        double d_londpp = LonDPP(d_lrlon, d_ullon, d_width);
        Node first;
        Node sec;
        Node third;
        Node curr = QuadTree.get("1");
        double londpp = LonDPP(curr.lrlon, curr.ullon, d_width);
        while (londpp > d_londpp) {
            curr = curr.sub[0];
            londpp = LonDPP(curr.lrlon, curr.ullon, d_width);
        }
        int len = curr.img.length();
        first = Zooms.get(len - 1).get(0);
        sec = Zooms.get(len - 1).get(0);
        third = Zooms.get(len - 1).get(0);
        for (int x = 0; x < Zooms.get(len - 1).size(); x++) {
            Node check = Zooms.get(len - 1).get(x);
            if (contained(d_ullon, d_ullat, check)) {
                first = check;
            }
            if (contained(d_ullon, d_lrlat, check)) {
                third = check;
            }
            if (contained(d_lrlon, d_ullat, check)) {
                sec = check;
            }
        }
        ArrayList<String> col = nimgs(first.img, third.img, "v");
        int n_rows = col.size();
        ArrayList<String> row = nimgs(first.img, sec.img, "h");
        int n_cols = row.size();
        imgs = new String[n_rows][n_cols];
        String[] rowed = row.toArray(new String[n_rows]);
        imgs[0] = rowed;
        for (int x = 1; x < col.size(); x++) {
            Node next = QuadTree.get(col.get(x));
            row = nimgs(next.img, sec.img, "h");
            rowed = row.toArray(new String[n_rows]);
            imgs[x] = rowed;
        }
        results.put("rendered_grid", imgs);
        results.put("raster_ul_lon", first.ullon);
        results.put("raster_ul_lat", first.ullat);
        results.put("raster_lr_lon", sec.lrlon);
        results.put("raster_lr_lat", third.lrlat);
        results.put("depth", len);
        return results;
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

    private ArrayList<String> nimgs(String img, String img2, String horv) {
        ArrayList<String> n = new ArrayList<>();
        n.add(QuadTree.get(img).img);
        while (!img.equals(img2)) {
            img = incr(img, horv);
            n.add(QuadTree.get(img).img);
        }
        return n;
    }

    private boolean contained(double dlon, double dlat, Node t) {
        if (dlon > t.ullon && dlon < t.lrlon) {
            if (dlat < t.ullat && dlat > t.lrlat) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        Rasterer t = new Rasterer("img");
        Map<String, Double> params = new HashMap<>();
        params.put("lrlon", -122.2104604264636);
        params.put("ullon", -122.30410170759153);
        params.put("w", 1085.0);
        params.put("h", 566.0);
        params.put("ullat", 37.870213571328854);
        params.put("lrlat", 37.8318576119893);
        Map<String, Object> k = t.getMapRaster(params);
        String[][] imgs = (String[][]) k.get("rendered_grid");
        //for (int x = 0; x < imgs.length; x++) {
        //   for (int y = 0; y < imgs[0].length; y++ ) {
        //        System.out.print(imgs[x][y] + "  ");
        //    }
        //    System.out.println("");
        //}

        System.out.println(t.QuadTree.get("1").ullon);
        System.out.println(t.QuadTree.get("11").ullon);
        System.out.println(t.QuadTree.get("12").ullon);
        System.out.println(t.QuadTree.get("13").ullon);
        System.out.println(t.QuadTree.get("14").ullon);


    }
}
