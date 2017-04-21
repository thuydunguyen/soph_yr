/**
 * Class for doing Radix sort
 *
 * @author Akhil Batra
 * @version 1.4 - April 14, 2016
 **/
public class RadixSort {

    /**
     * Does Radix sort on the passed in array with the following restrictions:
     * The array can only have ASCII Strings (sequence of 1 byte characters)
     * The sorting is stable and non-destructive
     * The Strings can be variable length (all Strings are not constrained to 1 length)
     *
     * @param asciis String[] that needs to be sorted
     * @return String[] the sorted array
     **/
    public static String[] sort(String[] asciis) {
        sortHelper(asciis, 0, asciis.length, 0);
        return asciis;
    }

    /**
     * Radix sort helper function that recursively calls itself to achieve the sorted array
     * destructive method that changes the passed in array, asciis
     *
     * @param asciis String[] to be sorted
     * @param start  int for where to start sorting in this method (includes String at start)
     * @param end    int for where to end sorting in this method (does not include String at end)
     * @param index  the index of the character the method is currently sorting on
     **/
    private static void sortHelper(String[] asciis, int start, int end, int index) {
        int[] counts = new int[256];
        String[] sort = new String[asciis.length];
        for (String str : asciis) {
            char last = str.charAt(str.length() - index - 1);
            int num = (int) last;
            counts[num] += 1;
        }

        int[] place = new int[256];
        int x = 0;
        int tally = 0;
        for (int c : counts) {
            if (c == 0) {
                place[x] = 0;
            } else {
                tally = tally + c;
                place[x] = tally;
            }
            x++;
        }

        for (int z = asciis.length - 1; z > -1; z--) {
            String str = asciis[z];
            char last = str.charAt(str.length() - index - 1);
            int num = (int) last;
            int ind = place[num];
            place[num] = ind - 1;
            sort[ind - 1] = str;
        }

        String[] copy = asciis;
        int k = 0;
        for (String str : sort) {
            copy[k] = str;
            k++;
        }
        if (index != 3) {
            sortHelper(asciis, start, end, index + 1);
        }

    }


    public static void main(String[] args) {
        String[] list = new String[]{"able", "cave", "arts", "book", "acts"};
        String[] uns = new String[]{"?????", "??", "???-", "???", "????A", "1", "?", "3?4", "?@", "??", "?b", "?cp???", "?s??k", "???", "q?", ">M", "???O]", "?A?", "???+?", "P?", "? ?", "??I?", "^???"};
        sortHelper(list, 0, 4, 0);
        for (String str : uns) {
            System.out.println(str);
        }
    }


}
