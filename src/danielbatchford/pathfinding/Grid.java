package danielbatchford.pathfinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Grid {

    private final int[] dim;
    private final Box[][] boxes;


    public Grid(int[] dimensions) {
        boxes = new Box[dimensions[0]][dimensions[1]];

        for (int x = 0; x < dimensions[0]; x++) {
            for (int y = 0; y < dimensions[1]; y++) {
                boxes[x][y] = new Box(new int[]{x, y});
            }
        }

        this.dim = getDimensions();

    }

    public void setWalkable(int[] cord, boolean walkable) {
        boxes[cord[0]][cord[1]].setWalkable(walkable);

    }

    public int[] getDimensions() {
        return new int[]{boxes.length, boxes[0].length};

    }

    public List<Box> getNeighbors(Box box, boolean allowDiagonal) {

        List<Box> neighbors = new ArrayList<>();

        int[] boxCord = box.getCord();

        if (boxCord[0] > 0) {
            neighbors.add(boxes[boxCord[0] - 1][boxCord[1]]);
        }
        if (boxCord[0] < dim[0] - 1) {
            neighbors.add(boxes[boxCord[0] + 1][boxCord[1]]);
        }
        if (boxCord[1] > 0) {
            neighbors.add(boxes[boxCord[0]][boxCord[1] - 1]);
        }
        if (boxCord[1] < dim[1] - 1) {
            neighbors.add(boxes[boxCord[0]][boxCord[1] + 1]);
        }

        neighbors.remove(box.getParent()); //may not be needed

        if (allowDiagonal) {


            if (boxCord[0] > 0 && boxCord[1] > 0) {
                neighbors.add(boxes[boxCord[0] - 1][boxCord[1] - 1]);
            }
            if (boxCord[0] < dim[0] - 1 && boxCord[1] < dim[1] - 1) {
                neighbors.add(boxes[boxCord[0] + 1][boxCord[1] + 1]);
            }
            if (boxCord[0] > 0 && boxCord[1] < dim[1] - 1) {
                neighbors.add(boxes[boxCord[0] - 1][boxCord[1] + 1]);
            }
            if (boxCord[0] < dim[0] - 1 && boxCord[1] > 0) {
                neighbors.add(boxes[boxCord[0] + 1][boxCord[1] - 1]);
            }
        }
        Collections.shuffle(neighbors);
        return neighbors;
    }

    @Override
    public String toString() {
        return this.toString(new ArrayList<>());
    }

    public String toString(List<int[]> path) {

        String[][] stringArr = new String[dim[0]][dim[1]];
        for (int x = 0; x < dim[0]; x++) {
            for (int y = 0; y < dim[1]; y++) {
                Box b = boxes[x][y];

                stringArr[x][y] = (b.isWalkable()) ? "·" : "■";
            }
        }

        for (int[] p : path) {
            stringArr[p[0]][p[1]] = "O";
        }

        StringBuilder sb = new StringBuilder();

        //Note transpose here
        for (int y = 0; y < dim[1]; y++) {
            for (int x = 0; x < dim[0]; x++) {
                sb.append(stringArr[x][y]);
                sb.append(" ");
            }
            sb.append("\n");
        }
        return sb.toString();

    }

    public Box[][] getBoxes() {
        return boxes;
    }

}
