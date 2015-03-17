package ca.etchells.curves;

public class Coordinate {
    private double x, y;

    public Coordinate (double xin, double yin) {
        x = xin;
        y = yin;
    }

    public void move(double xStep, double yStep) {
        x+=xStep;
        y+=yStep;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
