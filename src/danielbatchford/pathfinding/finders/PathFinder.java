package danielbatchford.pathfinding.finders;


import danielbatchford.pathfinding.Box;
import danielbatchford.pathfinding.Grid;
import danielbatchford.pathfinding.statelogging.StateLogger;
import danielbatchford.pathfinding.util.Options;

import java.util.*;

public class PathFinder {

    protected Options options;
    protected Box start;
    protected Box end;
    protected Set<Box> closedList;
    protected StateLogger stateLogger;
    List<Box> neighbors;

    protected PathFinder() {

    }

    public List<int[]> findPath(int[] startCord, int[] endCord, Grid grid, Options options) {
        this.options = options;

        if (options.attachStateLogger) {
            stateLogger = new StateLogger();
        }
        int[] dim = grid.getDimensions();
        start = grid.getBoxes()[startCord[0]][startCord[1]];
        end = grid.getBoxes()[endCord[0]][endCord[1]];


        start.setParent(null);

        closedList = new HashSet<>();
        closedList.add(start);

        return null;
    }

    protected float getDistance(Box a, Box b) {
        int[] aC = a.getCord();
        int[] bC = b.getCord();
        switch (options.distanceMetric) {
            case 'm':
                return Math.abs(aC[0] - bC[0]) + Math.abs(aC[1] - bC[1]);
            case 'e':
                return (float) Math.sqrt(Math.pow(aC[0] - bC[0], 2) + Math.pow(aC[1] - bC[1], 2));
            default:
                return 100;
        }
    }

    protected List<int[]> backTrace(Box box) {

        if (box == null) return null;

        List<int[]> route = new ArrayList<>();

        route.add(box.getCord());
        while (box.getParent() != null) {
            box = box.getParent();
            route.add(box.getCord());
        }
        Collections.reverse(route);
        return route;
    }

    public StateLogger getStateLogger() {
        return stateLogger;
    }
}
