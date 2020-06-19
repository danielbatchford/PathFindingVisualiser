package danielbatchford.gui;

import danielbatchford.pathfinding.Box;
import danielbatchford.pathfinding.Grid;
import danielbatchford.pathfinding.finders.*;
import danielbatchford.pathfinding.statelogging.State;
import danielbatchford.pathfinding.statelogging.StateLogger;
import danielbatchford.pathfinding.util.Options;
import processing.core.PApplet;

import java.awt.*;
import java.util.ArrayList;
import java.util.Set;

public class Renderer extends PApplet {

    int[] dim;
    int[] div;
    int[] boxWidth;

    boolean eraseMode;

    Grid grid;
    ArrayList<int[]> path;
    Options options;
    PathFinder finder;
    StateLogger logger;

    int[] start;
    int[] end;

    //CHECK START IS WALKABLE
    public static void main(String[] args) {
        PApplet.main(new String[]{"--present", Renderer.class.getName()});
    }

    @Override
    public void draw() {

        fill(255, 255, 234);
        strokeWeight(2);
        for (int x = 0; x < div[0]; x++) {
            for (int y = 0; y < div[1]; y++) {
                Box box = grid.getBoxes()[x][y];
                if ((box.isWalkable())) {
                    drawBox(box.getCord());
                }
            }
        }
        strokeWeight(0);

        State state = logger.getStates().get(0);
        if (logger.getStates().size() > 1)
            logger.getStates().remove(0);
        ArrayList<Box> openList = (ArrayList<Box>) state.openList;
        Set<Box> closedList = state.closedList;

        fill(204, 480, 0);
        for (Box b : closedList) {
            drawBox(b.getCord());
        }

        fill(255, 237, 102);
        for (Box b : openList) {
            drawBox(b.getCord());

        }


        fill(255, 84, 91);
        if (logger.getStates().size() == 1 && path != null) {
            for (int[] cords : path) {
                drawBox(cords);
            }
        }

        fill(92, 92, 92);
        for (int x = 0; x < div[0]; x++) {
            for (int y = 0; y < div[1]; y++) {
                Box box = grid.getBoxes()[x][y];
                if (!(box.isWalkable())) {
                    drawBox(box.getCord());
                }
            }
        }

        fill(255, 84, 91);
        drawBox(start);
        drawBox(end);
    }

    @Override
    public void mousePressed() {
        Box box = grid.getBoxes()[mouseX * div[0] / dim[0]][mouseY * div[1] / dim[1]];

        //the below order matters
        this.eraseMode = !box.isWalkable();
        box.setWalkable(!box.isWalkable());

        refreshSearch();
    }

    @Override
    public void mouseDragged() {
        grid.getBoxes()[mouseX / boxWidth[0]][mouseY / boxWidth[1]].setWalkable(eraseMode);
    }

    @Override
    public void keyReleased() {
        switch (key) {
            case '1':
                finder = new AStarSearch();
                break;
            case '2':
                finder = new DjikstraSearch();
                break;
            case '3':
                finder = new BreadthFirstSearch();
                break;
            case '4':
                finder = new DepthFirstSearch();
                break;
            default:
                return;
        }
        refreshSearch();
    }


    @Override
    public void setup() {
        frameRate(120);
        stroke(92, 92, 92);


        div = new int[]{30, 20};
        boxWidth = new int[]{dim[0] / div[0], dim[1] / div[1]};
        grid = new Grid(div);
        finder = new BreadthFirstSearch();
        options = new Options('m', false, true);
        start = new int[]{2, 2};
        end = new int[]{20, 15};

        refreshSearch();


    }

    @Override
    public void settings() {
        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        dim = new int[]{size.width, size.height};
        size(dim[0], dim[1]);
        fullScreen();
        noSmooth();
    }

    void drawBox(int[] cord) {
        rect(cord[0] * boxWidth[0], cord[1] * boxWidth[1], boxWidth[0], boxWidth[1]);
    }

    void refreshSearch() {
        path = (ArrayList<int[]>) finder.findPath(start, end, grid, options);
        logger = finder.getStateLogger();
    }


}
