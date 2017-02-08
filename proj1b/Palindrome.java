/**
 * Created by Thuy-Du on 2/6/2017.
 */
public class Palindrome {
    public static Deque<Character> wordToDeque(String word) {
        Deque<Character> Char = new ArrayDequeSolution<>();
        for (int x = 0; x < word.length(); x++) {
            Char.addLast(word.charAt(x));
        }
        return Char;

    }

    public static boolean isPalindrome(String word) {
        int y = 0;
        int x = word.length() - 1;
        boolean c = true;
        while ((x > y) && c) {
            c = word.charAt(y) == word.charAt(x);
            y += 1;
            x -= 1;
        }
        return c;
    }

    public static boolean isPalindrome(String word, CharacterComparator cc) {
        int y = 0;
        int x = word.length() - 1;
        boolean c = true;
        while ((x > y) && c) {
            c = cc.equalChars(word.charAt(x), word.charAt(y));
            y++;
            x--;
        }
        return c;
    }


}
