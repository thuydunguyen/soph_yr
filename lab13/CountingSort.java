/**
 * Class with 2 ways of doing Counting sort, one naive way and one "better" way
 *
 * @author Akhil Batra
 * @version 1.1 - April 16, 2016
 **/
public class CountingSort {

    /**
     * Counting sort on the given int array. Returns a sorted version of the array.
     * does not touch original array (non-destructive method)
     * DISCLAIMER: this method does not always work, find a case where it fails
     *
     * @param arr int array that will be sorted
     * @return the sorted array
     **/
    public static int[] naiveCountingSort(int[] arr) {
        // find max
        int max = Integer.MIN_VALUE;
        for (int i : arr) {
            if (i > max) {
                max = i;
            }
        }

        // gather all the counts for each value
        int[] counts = new int[max + 1];
        for (int i : arr) {
            counts[i] += 1;
        }

        // put the value count times into a new array
        int[] sorted = new int[arr.length];
        int k = 0;
        for (int i = 0; i < counts.length; i += 1) {
            for (int j = 0; j < counts[i]; j += 1, k += 1) {
                sorted[k] = i;
            }
        }

        // return the sorted array
        return sorted;
    }

    /**
     * Counting sort on the given int array, must work even with negative numbers.
     * Note, this code does not need to work for ranges of numbers greater
     * than 2 billion.
     * does not touch original array (non-destructive method)
     *
     * @param toSort int array that will be sorted
     **/
    public static int[] betterCountingSort(int[] toSort) {
        int max = 0;
        int min = 0;

        for (int x : toSort) {
            if (x < 0) {
                int xp = Math.abs(x);
                if (xp > min) {
                    min = xp;
                }
            } else {
                if (x > max) {
                    max = x;
                }
            }
        }
        int[] counts = new int[max + min + 1];
        for (int x : toSort) {
            if (x < 0) {
                counts[x + min] += 1;
            } else {
                counts[min + x] += 1;
            }
        }

        int[] sort = new int[toSort.length];
        min = -1 * min;
        int place = 0;
        for (int c : counts) {
            for (int n = 0; n < c; n++) {
                sort[n + place] = min;
            }
            min++;
            place = place + c;
        }
        return sort;
    }

    public static void main(String[] args) {
        int[] arr = new int[]{9, 5, -4, 2, 1, -2, 5, 3, 0, -2, 3, 1, 1};
        int[] arr2 = new int[]{9, 2, 4, 1, 5, 2, 10, 2};
        int[] sort = betterCountingSort(arr);
        int[] sort2 = naiveCountingSort(arr2);
        for (int x : sort) {
            System.out.print(x + " ");
        }
    }
}
