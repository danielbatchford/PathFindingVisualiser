package danielbatchford.gui;

import java.util.Random;

public interface Constants {

    //Colours, stored as [Red,Green,Blue]
    int[] WALKABLE_COL = new int[]{246, 246, 246};
    int[] WALL_COL = new int[]{39, 48, 67};
    int[] START_COL = new int[]{219, 84, 97};
    int[] END_COL = new int[]{107,72,173};
    int[] CLOSED_LIST_COL = new int[]{193, 219, 227};
    int[] OPEN_LIST_COL = new int[]{128, 182, 198};
    int[] PEN_WALKABLE_COL = new int[]{208, 208, 208};
    int[] PEN_WALL_COL = new int[]{83, 94, 121};
    int[] PATH_COL = new int[]{168, 36, 49};
    int[] TEXT_COL = new int[]{0,0,0};

    //Number of subdivisions across shortest dimension of screen
    int DIV_COUNT = 20;

    //Text pt size
    int TEXT_SIZE = 30;

    //Spacing in pixels between list items
    int TEXT_SPACING = 40;

    //Horizontal and vertical padding from corner of screen
    int TEXT_PADDING = 15;

    //Width of text box in pixels
    int TEXT_BOX_WIDTH = 1000;

    String TEXT_FONT = "Arial";

    int FRAME_RATE = 60;

    Random random = new Random();
}
