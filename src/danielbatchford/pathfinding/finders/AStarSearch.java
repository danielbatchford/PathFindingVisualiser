package danielbatchford.pathfinding.finders;

import danielbatchford.pathfinding.Grid;
import danielbatchford.pathfinding.util.Options;

import java.util.List;

public class AStarSearch extends PriorityQueueSearch {


    @Override
    public List<int[]> findPath(int[] startCord, int[] endCord, Grid grid, Options options) {
        return super.findPath(startCord, endCord, grid, options, true);
    }

}
