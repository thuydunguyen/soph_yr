/**
 * Created by Thuy-Du on 2/6/2017.
 */
public class OffByN implements CharacterComparator {
    int n;

    public OffByN(int N) {
        this.n = N;
    }

    @Override
    public boolean equalChars(char x, char y) {
        int diff = Math.abs(x - y);
        return diff == this.n;
    }


}


