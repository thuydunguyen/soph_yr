package hw3.puzzle;

/**
 * Created by Thuy-Du on 3/21/2017.
 */
public class Solver {

    private int move = 0;
    private Iterable<WorldState> solved;

    public Solver(WorldState initial) {
        String curr = initial.toString();
        move = initial.estimatedDistanceToGoal();
        solved = initial.neighbors();
    }

    public int moves() {
        return move;
    }

    public Iterable<WorldState> solution() {
        return solved;
    }
}
