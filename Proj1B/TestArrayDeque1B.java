/**
 * Created by Thuy-Du on 2/6/2017.
 */


import org.junit.Test;

import static org.junit.Assert.*;

import java.util.ArrayDeque;

public class TestArrayDeque1B {

    @Test
    public void Phase1_1() {
        StudentArrayDeque<Integer> t = new StudentArrayDeque<>();
        ArrayDequeSolution<Integer> s = new ArrayDequeSolution<>();
        boolean x = true;
        OperationSequence fail = new OperationSequence();
        Integer s1 = 1;
        Integer t1 = 1;
        DequeOperation op = new DequeOperation("initialize");

        while (x) {
            int func_call = StdRandom.uniform(150);
            int num = StdRandom.uniform(50);
            if (func_call < 25) {
                op = new DequeOperation("addFirst", num);
                s.addFirst(num);
                t.addFirst(num);
                s1 = s.get(0);
                t1 = t.get(0);
                x = s1 == t1;
            } else if (func_call < 50) {
                s1 = s.removeFirst();
                t1 = t.removeFirst();
                if ((s.size() == 0) || (s.size() != t.size())) {
                    x = t.size() == s.size();
                    op = new DequeOperation("size");
                    s1 = s.size();
                    t1 = t.size();
                } else {
                    op = new DequeOperation("removeFirst");
                    x = s1 == t1;
                }

            } else if (func_call < 75) {
                op = new DequeOperation("addLast", num);
                s.addLast(num);
                t.addLast(num);
                s1 = s.get(s.size() - 1);
                t1 = t.get(t.size() - 1);
                x = s1 == t1;
            } else if (func_call < 100) {
                s1 = s.removeLast();
                t1 = t.removeLast();
                if ((s.size() == 0) || (s.size() != t.size())) {
                    x = t.size() == s.size();
                    op = new DequeOperation("size");
                    s1 = s.size();
                    t1 = t.size();
                } else {
                    op = new DequeOperation("removeLast");
                    x = s1 == t1;
                }
            } else if (func_call < 125) {
                op = new DequeOperation("size");
                s1 = s.size();
                t1 = t.size();
                x = s1 == t1;
            } else if (func_call < 150) {
                if ((s.size() == 0) || (s.size() != t.size())) {
                    x = t.size() == s.size();
                    op = new DequeOperation("size");
                    s1 = s.size();
                    t1 = t.size();
                } else {
                    num = StdRandom.uniform(s.size());
                    op = new DequeOperation("get", num);
                    s1 = s.get(num);
                    t1 = t.get(num);
                    x = s1 == t1;
                }

            }
            fail.addOperation(op);
        }
        assertEquals(fail.toString(), s1, t1);
    }
}
