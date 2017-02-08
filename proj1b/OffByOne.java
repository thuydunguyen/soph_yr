/**
 * Created by Thuy-Du on 2/6/2017.
 */
public class OffByOne implements CharacterComparator {
    @Override
    public boolean equalChars(char x, char y) {
        int diff = Math.abs(x - y);
        return diff == 1;
    }
}
