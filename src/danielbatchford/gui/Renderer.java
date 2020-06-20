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
import java.util.Arrays;
import java.util.Random;
import java.util.Set;

public class Renderer extends PApplet {

    int[] dim;
    int[] div;
    int[] boxWidth;

    int divCount;
    boolean eraseMode;

    Grid grid;

    ArrayList<int[]> path;
    Options options;
    PathFinder finder;
    String searchType;
    StateLogger logger;

    ArrayList<Box> openList;
    Set<Box> closedList;
    int[] start;
    int[] end;

    int textSize;
    boolean renderSearch;
    boolean showMenu;

    Random random;

    //CHECK START IS WALKABLE
    public static void main(String[] args) {
        PApplet.main(new String[]{"--present", Renderer.class.getName()});
    }

    @Override
    public void draw() {
        if (renderSearch) {
            strokeWeight(0);
            State state = logger.getStates().get(0);
            if (logger.getStates().size() > 1)
                logger.getStates().remove(0);
            openList = (ArrayList<Box>) state.openList;
            closedList = state.closedList;

            fill(188, 191, 200);
            for (Box b : closedList) {
                drawBox(b.getCord());
            }

            fill(116, 180, 190);
            for (Box b : openList) {
                drawBox(b.getCord());
            }

            if (logger.getStates().size() == 1 && path != null) {
                fill(240, 45, 58);
                for (int[] cords : path) {
                    drawBox(cords);
                }
                renderSearch = false;
            }
        }

        drawBox(end, new int[]{240, 45, 58});
        drawBox(start, new int[]{240, 45, 58});
        drawText(searchType);
    }

    @Override
    public void mousePressed() {
        Box box = grid.getBoxes()[mouseX * div[0] / dim[0]][mouseY * div[1] / dim[1]];

        if (Arrays.equals(start,box.getCord()) || Arrays.equals(end,box.getCord())) {

        } else {
            eraseMode = !box.isWalkable();
            box.setWalkable(!box.isWalkable());
            drawBox(box.getCord(), box.isWalkable() ? new int[]{208, 208, 208} : new int[]{83, 94, 121});
            refreshSearch();
            renderSearch = false;
        }
    }

    @Override
    public void mouseDragged() {
        Box box = grid.getBoxes()[mouseX / boxWidth[0]][mouseY / boxWidth[1]];
        if (Arrays.equals(start,box.getCord()) || Arrays.equals(end,box.getCord())) {
            System.out.println("AA");
        } else {
            box.setWalkable(eraseMode);
            drawBox(box.getCord(), box.isWalkable() ? new int[]{208, 208, 208} : new int[]{83, 94, 121});
        }
    }

    public void mouseReleased() {
        refreshSearch();
        renderSearch = true;
        drawGrid();
        drawBox(start, new int[]{240, 45, 58});
        drawBox(end, new int[]{240, 45, 58});

    }

    @Override
    public void keyReleased() {
        switch (key) {
            case '1':
                finder = new AStarSearch();
                searchType = "A* Search";
                break;
            case '2':
                finder = new DjikstraSearch();
                searchType = "Djikstra Search";

                break;
            case '3':
                finder = new BreadthFirstSearch();
                searchType = "Breadth First Search";
                break;
            case '4':
                finder = new DepthFirstSearch();
                searchType = "Depth First Search";
                break;
            case 'c':
                for (int x = 0; x < div[0]; x++) {
                    for (int y = 0; y < div[1]; y++) {
                        grid.getBoxes()[x][y].setWalkable(true);
                    }
                }
                break;
            case 'r':
                refreshStartAndEnd();
                break;
            case 'm':
                showMenu = !showMenu;
                return;
            default:
                return;
        }
        drawGrid();
        refreshSearch();
        drawBox(start, new int[]{240, 45, 58});
        drawBox(end, new int[]{240, 45, 58});

        renderSearch = true;
    }


    @Override
    public void setup() {

        frameRate(60);
        stroke(39, 48, 67);

        textSize = 30;
        //textSize(textSize);
        textFont(createFont("Arial", textSize), textSize);

        divCount = 20;
        setDivFactor();
        boxWidth = new int[]{dim[0] / div[0], dim[1] / div[1]};

        grid = new Grid(div);
        finder = new BreadthFirstSearch();
        searchType = "Breadth First Search";
        options = new Options('m', false, true);

        renderSearch = true;
        showMenu = false;

        random = new Random();
        refreshStartAndEnd();
        refreshSearch();
        drawGrid();
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

    void drawBox(int[] cord, int[] color) {
        fill(color[0], color[1], color[2]);
        drawBox(cord);

    }

    void drawGrid() {
        for (int x = 0; x < div[0]; x++) {
            for (int y = 0; y < div[1]; y++) {
                Box box = grid.getBoxes()[x][y];
                drawBox(box.getCord(), box.isWalkable() ? new int[]{246, 246, 246} : new int[]{39, 48, 67});
            }
        }
    }

    void drawText(String s) {

        if(showMenu){
            fill(255,255,255,100);
            rect(0,dim[1]-220,600,220);
            fill(0, 0, 0);
            text("Use the mouse to place obstacles", 15, dim[1]-55);
            text("Press R to randomise start and end", 15, dim[1]-95);
            text("Press C to clear board", 15, dim[1]-135);
            text("Use 1-4 to select searching algorithms", 15,dim[1]-175);
            text("Currently using "+ s, 15, dim[1] - 15);
        }
        else{
            text("Press M", 15, dim[1]-55);
        }
    }

    void refreshSearch() {
        path = (ArrayList<int[]>) finder.findPath(start, end, grid, options);
        logger = finder.getStateLogger();
    }

    void refreshStartAndEnd() {
        start = new int[]{random.nextInt(div[0]), random.nextInt(div[1])};
        end = new int[]{random.nextInt(div[0]), random.nextInt(div[1])};

        while (!grid.getBoxes()[start[0]][start[1]].isWalkable()) {
            start = new int[]{random.nextInt(div[0]), random.nextInt(div[1])};
        }

        while (!grid.getBoxes()[end[0]][end[1]].isWalkable() && !Arrays.equals(start, end)) {
            end = new int[]{random.nextInt(div[0]), random.nextInt(div[1])};
        }
    }

    void setDivFactor() {
        div = new int[2];

        if (dim[0] >= dim[1]) {
            div[0] = (int) ((float) divCount * ((float) dim[0] / (float) dim[1]));
            div[1] = divCount;
        } else {
            div[0] = divCount;
            div[1] = (int) ((float) divCount * ((float) dim[0] / (float) dim[1]));
        }
    }
}
