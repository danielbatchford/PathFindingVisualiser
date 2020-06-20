package danielbatchford.pathfinding.util;

public class Options {

    public char distanceMetric = 'm';
    public boolean allowDiagonal = false;
    public boolean attachStateLogger = false;

    public Options(char distanceMetric, boolean allowDiagonal, boolean attachStateLogger) {
        this.distanceMetric = distanceMetric;
        this.allowDiagonal = allowDiagonal;
        this.attachStateLogger = attachStateLogger;
    }

    public Options(char distanceMetric, boolean allowDiagonal) {
        this.distanceMetric = distanceMetric;
        this.allowDiagonal = allowDiagonal;
    }

    public Options(char distanceMetric) {
        this.distanceMetric = distanceMetric;
    }

}
