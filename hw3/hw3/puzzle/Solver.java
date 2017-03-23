package hw3.puzzle;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import edu.princeton.cs.algs4.MinPQ;

/**
 * Created by Thuy-Du on 3/21/2017.
 */
public class Solver {

    private int move = 0;
    private ArrayList<WorldState> solved = new ArrayList<>();

    private class SearchNode {
        private SearchNode prev;
        private WorldState curr;
        private int moves;
        private int dist;


        public SearchNode(SearchNode prev, WorldState curr, int moves) {
            this.prev = prev;
            this.curr = curr;
            this.moves = moves;
            dist = curr.estimatedDistanceToGoal();
        }
    }

    public Solver(WorldState initial) {
        SearchNode check = new SearchNode(new SearchNode(null, initial, 0), initial, 0);
        Comparator<SearchNode> comp = new Compares();
        MinPQ<SearchNode> store = new MinPQ<>(comp);
        store.insert(check);
        while (!store.isEmpty()) {
            SearchNode next = store.delMin();
            check = next;
            if (check.curr.isGoal()) {
                break;
            }
            Iterator<WorldState> neighbors = check.curr.neighbors().iterator();
            while (neighbors.hasNext()) {
                WorldState neigh = neighbors.next();
                if (!neigh.equals(check.prev.curr)) {
                    store.insert(new SearchNode(check, neigh, check.moves + 1));
                }
            }
        }

        move = check.moves;
        while (check.prev != null) {
            solved.add(check.curr);
            check = check.prev;
        }
        Collections.reverse(solved);
    }

    public int moves() {
        return move;
    }

    public Iterable<WorldState> solution() {
        return solved;
    }

    private class Compares implements Comparator<SearchNode> {
        public int compare(SearchNode a, SearchNode b) {
            int disa = a.dist + a.moves;
            int disb = b.dist + b.moves;
            return disa - disb;
        }
    }


}
