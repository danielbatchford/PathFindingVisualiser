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
import java.util.Set;

public class Renderer extends PApplet implements Constants{

    //Dimensions of screen, number of divisions and pixel box width respectively
    int[] dim;
    int[] div;
    int[] boxWidth;

    //Grid to carry out searches
    Grid grid;

    //Calculated path
    ArrayList<int[]> path;

    //Options object used in pathfinding library
    Options options;

    //Finder used in pathfinding library
    PathFinder finder;

    //Holds information about the open and closed lists at each step in a search
    StateLogger logger;

    //Whether to allow diagonal movement
    boolean diagonal = false;

    //Open and closed lists retrieved from StateLogger
    ArrayList<Box> openList;
    Set<Box> closedList;

    //Nodes to search between
    int[] start;
    int[] end;

    //Display menu variables
    String searchType;
    String[] textData;
    int textBoxHeight;

    //Whether to draw or erase when "painting" walls
    boolean eraseMode;

    //Whether to render a search
    boolean renderSearch = true;

    boolean showMenu = false;
    boolean paused = false;

    //Dictate if the mouse is moving start or end boxes
    boolean holdingStart = false;
    boolean holdingEnd = false;




    //Called on Processing PApplet initialisation
    @Override
    public void setup() {

        frameRate(FRAME_RATE);

        //Despite stroke weight being 0, a small stroke is still displayed which is intended
        strokeWeight(0);

        //Set rectangle stroke colour
        stroke(WALL_COL[0],WALL_COL[1],WALL_COL[2]);

        //Initialise a font
        textFont(createFont(TEXT_FONT, TEXT_SIZE), TEXT_SIZE);

        textData = new String[]{
                "Press M",
                "Currently Using",
                "Press D to toggle diagonal",
                "Press P To Toggle Pause",
                "Press R To Randomise Start & End Positions",
                "Press C To Clear The Board",
                "Use The Mouse To Place Obstacles / Move Start & End Positions",
                "Use Keys 1-4 To Select Searching Algorithms"};

        textBoxHeight = textData.length*TEXT_SPACING + 3* TEXT_PADDING;

        //Default search
        searchType = "Breadth First Search";

        //Initialise the number of subdivisions
        setDivFactor();
        boxWidth = new int[]{dim[0] / div[0], dim[1] / div[1]};

        //Initialise searching library objects
        grid = new Grid(div);
        finder = new BreadthFirstSearch();
        options = new Options('m', diagonal, true);

        //Initialise start and end in center of screen, +- an offset
        start = new int[]{div[0] / 2 - 3, div[1] / 2};
        end = new int[]{div[0] / 2 + 3, div[1] / 2};

        //Initialise a new search
        refreshSearch();

        //draw the grid for first display
        drawGrid();
    }

    //Called once per frame
    @Override
    public void draw() {

        if (renderSearch && !paused) {

            //Retrieve a state for this frame from the front of the list
            State state = logger.getStates().get(0);

            //Remove this state from the list, unless it is the last state (As this needs to be displayed indefinitely)
            if (logger.getStates().size() > 1)
                logger.getStates().remove(0);

            //Assign open list and closed lists for this state
            openList = (ArrayList<Box>) state.openList;
            closedList = state.closedList;

            //Draw open and closed lists
            drawClosedList();
            drawOpenList();

            //If a path is ready to be displayed
            if (logger.getStates().size() == 1 && path != null) {
                drawPath();
                renderSearch = false;
            }
        }

        //Draw start and end boxes over the open and closed list boxes
        drawBox(start, START_COL);
        drawBox(end, END_COL);

        //Draw menu text over all other elements
        drawMenu();
    }

    //Called once per mouse press
    @Override
    public void mousePressed() {

        //Get the box at mouse co-ordinates
        Box selectedBox = grid.getBoxes()[mouseX * div[0] / dim[0]][mouseY * div[1] / dim[1]];

        holdingStart = Arrays.equals(start,selectedBox.getCord());
        holdingEnd = Arrays.equals(end,selectedBox.getCord());

        //If mouse selected a start or end box
        if (holdingStart || holdingEnd) {
            drawGrid();

        }
        //Enter "Painting" mode
        else {
            //Whether to draw or erase
            eraseMode = !selectedBox.isWalkable();

            //Invert a box (walkable = !walkable)
            selectedBox.setWalkable(!selectedBox.isWalkable());

            //Draw updated box to the screen
            drawBox(selectedBox.getCord(), selectedBox.isWalkable() ? PEN_WALKABLE_COL : PEN_WALL_COL);

        }
        //Disable updating to the screen while mouse is pressed
        renderSearch = false;
    }

    //Called every time mouse moves while pressed
    @Override
    public void mouseDragged() {

        //Get the box at mouse co-ordinates
        Box selectedBox = grid.getBoxes()[mouseX / boxWidth[0]][mouseY / boxWidth[1]];


        if (holdingStart) {
            //Draw previous position of dragged box as a normal box
            drawBox(start, grid.getBoxes()[start[0]][start[1]].isWalkable() ? WALKABLE_COL : WALL_COL);

            //Update start position and draw
            start = selectedBox.getCord();
            drawBox(start, START_COL);

        } else if (holdingEnd) {
            //Draw previous position of dragged box as a normal box
            drawBox(end, grid.getBoxes()[end[0]][end[1]].isWalkable() ? WALKABLE_COL : WALL_COL);

            //Update end position and draw
            end = selectedBox.getCord();
            drawBox(end, END_COL);
        } else {
            //"Paint" mode, set walkable status to erase mode and draw
            selectedBox.setWalkable(eraseMode);
            drawBox(selectedBox.getCord(), selectedBox.isWalkable() ? PEN_WALKABLE_COL : PEN_WALL_COL);
        }
    }

    //Called once when the mouse is released
    public void mouseReleased() {
        //Reset boolean variables
        holdingStart = holdingEnd = false;

        //Set start and end boxes to walkable (if they were dropped on a wall etc)
        grid.getBoxes()[start[0]][start[1]].setWalkable(true);
        grid.getBoxes()[end[0]][end[1]].setWalkable(true);

        //Calculate the new path
        refreshSearch();

        //Enable constant rendering
        renderSearch = true;

        //Draw the new grid over the screen
        drawGrid();
    }

    //Called once per key pressed
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

            case 'c': //Loop through boxes and set all to walkable
                for (int x = 0; x < div[0]; x++) {
                    for (int y = 0; y < div[1]; y++) {
                        grid.getBoxes()[x][y].setWalkable(true);
                    }
                }
                break;

            case 'r': //Call a function to randomise start and end boxes
                refreshStartAndEnd();
                break;

            case 'm': // Toggle a menu and redraw the screen
                showMenu = !showMenu;
                drawGrid();
                drawClosedList();
                drawOpenList();

                //Needed to redraw a path after screenflush, if the visual search is completed
                if(!renderSearch) drawPath();
                return;

            case 'p':
                paused = !paused;
                return;

            case 'd': //Toggle diagonal mode and create a new options object
                diagonal = !diagonal;
                options = new Options('m', diagonal, true);
                break;
            default:
                return;
        }
        //Flush the screen and calculate a new search
        drawGrid();
        refreshSearch();
        renderSearch = true;
    }



    @Override
    public void settings() {

        //Get system screen size, initialises this in dim array then sets processing to use this size
        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        dim = new int[]{size.width, size.height};
        size(dim[0], dim[1]);

        fullScreen();

        //No anti aliasing
        noSmooth();
    }

    //Draw a box to the screen at grid co-ordinate
    void drawBox(int[] cord) {
        rect(cord[0] * boxWidth[0], cord[1] * boxWidth[1], boxWidth[0], boxWidth[1]);
    }

    //Draw a box to the screen at grid co-ordinate with specified colour
    void drawBox(int[] cord, int[] color) {
        fill(color[0], color[1], color[2]);
        drawBox(cord);

    }

    //Draw a grid to the screen
    void drawGrid() {
        for (int x = 0; x < div[0]; x++) {
            for (int y = 0; y < div[1]; y++) {
                Box box = grid.getBoxes()[x][y];
                drawBox(box.getCord(), box.isWalkable() ? WALKABLE_COL : WALL_COL);
            }
        }
    }

    //Draw a menu to the screen
    void drawMenu() {

        //Draw a background rectangle
        if(showMenu) {
            fill(255, 255, 255, 255);
            rect(0, dim[1] - textBoxHeight, TEXT_BOX_WIDTH, textBoxHeight);
        }
        fill(TEXT_COL[0],TEXT_COL[1],TEXT_COL[2]);

        //Draws "Press m" text
        text(textData[0], TEXT_PADDING, dim[1] - TEXT_SPACING);

        //If menu is toggled on
        if (showMenu) {

            //Update current search type
            textData[1] = "Currently Using " + searchType;

            //Draw text items at a fixed vertical spacing from each other
            for (int i = 1, max = textData.length; i < max; i++) {
                text(textData[i], TEXT_PADDING, dim[1] - TEXT_SPACING * (i+1));
            }
        }
    }

    void drawPath() {
        fill(PATH_COL[0],PATH_COL[1],PATH_COL[2]);
        for (int[] cords : path) {
            drawBox(cords);
        }
    }

    void drawOpenList() {
        fill(OPEN_LIST_COL[0],OPEN_LIST_COL[1],OPEN_LIST_COL[2]);
        for (Box b : openList) {
            drawBox(b.getCord());
        }
    }

    void drawClosedList() {
        fill(CLOSED_LIST_COL[0],CLOSED_LIST_COL[1],CLOSED_LIST_COL[2]);
        for (Box b : closedList) {
            drawBox(b.getCord());
        }
    }

    //Calculate another search from the path-finding library
    void refreshSearch() {
        //Update the path list with the new calculated path
        path = (ArrayList<int[]>) finder.findPath(start, end, grid, options);

        //Update the logger with the new states
        logger = finder.getStateLogger();
    }

    //Refresh start and end squares
    void refreshStartAndEnd() {
        start = new int[]{random.nextInt(div[0]), random.nextInt(div[1])};
        end = new int[]{random.nextInt(div[0]), random.nextInt(div[1])};

        //Set start and end squares to walkable
        grid.getBoxes()[start[0]][start[1]].setWalkable(true);
        grid.getBoxes()[end[0]][end[1]].setWalkable(true);
    }
    /*Calculates the number of horizontal and vertical divisions to make. This uses DIV_COUNT as the shortest screen dimension, scaling
    the other dimension up accordingly*/
    void setDivFactor() {
        div = new int[2];

        //Landscape Monitor
        if (dim[0] >= dim[1]) {
            div[0] = (int) ((float) DIV_COUNT * ((float) dim[0] / (float) dim[1]));
            div[1] = DIV_COUNT;
        }

        //Portrait Monitor?
        else {
            div[0] = DIV_COUNT;
            div[1] = (int) ((float) DIV_COUNT * ((float) dim[0] / (float) dim[1]));
        }
    }
}
