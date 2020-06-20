package danielbatchford.gui;

import processing.core.PApplet;

public class PathFindingVisualisation {

    public static void main(String[] args) {

        //Point Processing library to this class
        PApplet.main(new String[]{"--present", Renderer.class.getName()});
    }
}
