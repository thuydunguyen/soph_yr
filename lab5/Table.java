public class Table {
    private int[][] table;
    private int rowsize;
    private int colsize;
    private int nextr;


    public Table() {
        rowsize = 0;
        colsize = 0;
        nextr = 0;

    }

    public Table(int r, int c) {
        rowsize = r;
        colsize = c;
        nextr = 0;
        table = new int[r][c];
    }

    public void addRow(int[] x) {
        if (isEmpty()) {
            colsize = x.length;
        }
        if (x.length != colsize) {
            System.out.println("input length: " + x.length + " and column size: " + colsize + " do not match");
        }
        else {
            if (nextr > rowsize - 1) {
                int[][] ext = new int[nextr + 1][colsize];
                for (int i = 0; i < rowsize; i++) {
                    ext[i] = table[i];
                }
                table = ext;
                rowsize++;
            }

            table[nextr] = x;
            nextr++;
        }

    }

    public boolean isEmpty() {
        return colsize == 0;
    }

    public void print() {
        for (int i = 0; i < rowsize; i++) {
            for (int j = 0; j < colsize; j++) {
                System.out.print(table[i][j] + " ");
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        Table T1 = new Table();
        T1.addRow(new int[]{2, 4, 8});
        T1.addRow(new int[]{3, 9});
        T1.addRow(new int[]{10, 12, 13});
        T1.print();
    }
}
