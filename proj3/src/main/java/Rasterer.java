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
    private String[] num = new String[]{"1", "2", "3", "4"};
    private Map<String, Node> quadtree = new Hashtable<>();
    private ArrayList<ArrayList<Node>> zooms = new ArrayList<>();
    private ArrayList<Node> zoom1 = new ArrayList<>();
    private ArrayList<Node> zoom2 = new ArrayList<>();
    private ArrayList<Node> zoom3 = new ArrayList<>();
    private ArrayList<Node> zoom4 = new ArrayList<>();
    private ArrayList<Node> zoom5 = new ArrayList<>();
    private ArrayList<Node> zoom6 = new ArrayList<>();
    private ArrayList<Node> zoom7 = new ArrayList<>();
    private double rullat = MapServer.ROOT_ULLAT;
    private double rlrlat = MapServer.ROOT_LRLAT;
    private double rullon = MapServer.ROOT_ULLON;
    private double rlrlon = MapServer.ROOT_LRLON;


    // Recommended: quadtree instance variable. You'll need to make
    //              your own quadtree since there is no built-in quadtree in Java.

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
            this.img = img;
            if (img.equals("root")) {
                parent = this;
                ullon = rullon;
                ullat = rullat;
                lrlon = rlrlon;
                lrlat = rlrlat;
            } else if (img.length() == 1) {
                parent = quadtree.get("root");
            } else {
                String p = img.substring(0, img.length() - 1);
                parent = quadtree.get(p);
            }
            if (!img.equals("root")) {
                String lnum = img.substring(img.length() - 1);
                double mlon = (parent.ullon + parent.lrlon) / 2;
                double mlat = (parent.ullat + parent.lrlat) / 2;
                if (lnum.equals("1")) {
                    ullon = parent.ullon;
                    ullat = parent.ullat;
                    lrlat = mlat;
                    lrlon = mlon;
                } else if (lnum.equals("2")) {
                    ullon = mlon;
                    ullat = parent.ullat;
                    lrlat = mlat;
                    lrlon = parent.lrlon;
                } else if (lnum.equals("3")) {
                    ullon = parent.ullon;
                    ullat = mlat;
                    lrlat = parent.lrlat;
                    lrlon = mlon;
                } else if (lnum.equals("4")) {
                    ullon = mlon;
                    ullat = mlat;
                    lrlat = parent.lrlat;
                    lrlon = parent.lrlon;
                }
            }
            placezoom(img, this);
            quadtree.put(img, this);
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
    }

    private void placezoom(String img, Node t) {
        int len = img.length();
        if (img.equals("root")) {
            return;
        } else if (len == 1) {
            zoom1.add(t);
        } else if (len == 2) {
            zoom2.add(t);
        } else if (len == 3) {
            zoom3.add(t);
        } else if (len == 4) {
            zoom4.add(t);
        } else if (len == 5) {
            zoom5.add(t);
        } else if (len == 6) {
            zoom6.add(t);
        } else if (len == 7) {
            zoom7.add(t);
        }
    }


    public Rasterer(String imgRoot) {
        new Node("root");
        zooms.add(zoom1);
        zooms.add(zoom2);
        zooms.add(zoom3);
        zooms.add(zoom4);
        zooms.add(zoom5);
        zooms.add(zoom6);
        zooms.add(zoom7);
    }

    private static double londp(double dlrlon, double dullon, double width) {
        return (dlrlon - dullon) / width;
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
        Double dullon = params.get("ullon");
        Double dlrlon = params.get("lrlon");
        Double dwidth = params.get("w");
        Double dullat = params.get("ullat");
        Double dlrlat = params.get("lrlat");
        if (!valid(dullon, dlrlon, dullat, dlrlat)) {
            results.put("query_succes", false);
        } else {
            dlrlon = adjust(dlrlon, "lrlon");
            dlrlat = adjust(dlrlat, "lrlat");
            dullon = adjust(dullon, "ullon");
            dullat = adjust(dullat, "ullat");
            double dlondpp = londp(dlrlon, dullon, dwidth);
            Node first;
            Node sec;
            Node third;
            Node curr = quadtree.get("1");
            double londpp = londp(curr.lrlon, curr.ullon, 256);
            while (londpp > dlondpp && !curr.img.equals("1111111")) {
                curr = curr.sub[0];
                londpp = londp(curr.lrlon, curr.ullon, 256);
            }
            int len = curr.img.length();
            int k = 0;
            Node check = zooms.get(len - 1).get(k);
            while (!contained(dullon, dullat, check)) {
                k++;
                check = zooms.get(len - 1).get(k);
            }
            first = check;

            ArrayList<String> col = nimgs(first.img, first.lrlat, dlrlat, dlrlon, "v");
            int nrows = col.size();
            ArrayList<String> row = nimgs(first.img, first.lrlon, dlrlat, dlrlon, "h");
            int ncols = row.size();
            imgs = new String[nrows][ncols];
            imgs = fill(imgs, row, 0);
            for (int x = 1; x < nrows; x++) {
                Node next = quadtree.get(col.get(x));
                row = imgrows(next.img, ncols);
                imgs = fill(imgs, row, x);
            }
            sec = quadtree.get(imgs[0][ncols - 1].replaceAll("\\D+", ""));
            third = quadtree.get(imgs[nrows - 1][0].replaceAll("\\D+", ""));

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

    private String[][] fill(String[][] imgs, ArrayList<String> img, int row) {
        for (int x = 0; x < img.size(); x++) {
            imgs[row][x] = img.get(x);
        }
        return imgs;
    }

    private double adjust(double d, String point) {
        if (point.equals("ullon")) {
            if (d < rullon) {
                return rullon;
            }
        } else if (point.equals("ullat")) {
            if (d > rullat) {
                return rullat;
            }
        } else if (point.equals("lrlon")) {
            if (d > rlrlon) {
                return rlrlon;
            }
        } else if (point.equals("lrlat")) {
            if (d < rlrlat) {
                return rlrlat;
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
        String lchar = img.substring(img.length() - 1);
        String x;
        if (horv.equals("h")) {
            if (lchar.equals("1") || lchar.equals("3")) {
                int num = Integer.parseInt(img) + 1;
                x = Integer.toString(num);
            } else {
                if (lchar.equals("4")) {
                    lchar = "3";
                } else if (lchar.equals("2")) {
                    lchar = "1";
                }
                x = incr(img.substring(0, img.length() - 1), horv) + lchar;
            }
        } else {
            if (lchar.equals("1") || lchar.equals("2")) {
                int num = Integer.parseInt(img) + 2;
                x = Integer.toString(num);
            } else {
                if (lchar.equals("3")) {
                    lchar = "1";
                } else if (lchar.equals("4")) {
                    lchar = "2";
                }
                x = incr(img.substring(0, img.length() - 1), horv) + lchar;
            }
        }
        return x;
    }

    private ArrayList<String> nimgs(String img, double imgstart,
                                    double dlrlat, double dlrlon,
                                    String horv) {
        String imgstr = "img/";
        String endstr = ".png";
        ArrayList<String> row = new ArrayList<>();
        ArrayList<String> col = new ArrayList<>();
        String comp2 = "\\b[2,4]+\\b";
        String comp3 = "\\b[3,4]+\\b";
        col.add(img);
        row.add(imgstr + img + endstr);
        double increment1 = (rlrlon - rullon) / Math.pow(2, img.length());
        double increment2 = (rullat - rlrlat) / Math.pow(2, img.length());
        if (horv.equals("h")) {
            while (imgstart < dlrlon && !img.matches(comp2)) {
                img = incr(img, horv);
                imgstart = imgstart + increment1;
                row.add(imgstr + img + endstr);
            }
            return row;
        } else {
            while (imgstart > dlrlat && !img.matches(comp3)) {
                imgstart = imgstart - increment2;
                img = incr(img, horv);
                col.add(img);
            }
            return col;
        }

    }

    private ArrayList<String> imgrows(String img, int times) {
        String imgstr = "img/";
        String endstr = ".png";
        ArrayList<String> row = new ArrayList<>();
        String curr = img;
        row.add(imgstr + curr + endstr);
        for (int x = 0; x < times - 1; x++) {
            curr = incr(curr, "h");
            row.add(imgstr + curr + endstr);
        }
        return row;
    }

    private boolean valid(double ullon, double lrlon, double ullat, double lrlat) {
        if (ullon > lrlon || lrlat > ullat) {
            return false;
        }
        return (!(rullon > ullon && rlrlon < lrlon && rullat < ullat && rlrlat > lrlat));

    }

}
