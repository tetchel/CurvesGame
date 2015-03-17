package ca.etchells.curves;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.HashSet;

//represents a curve in the game as a series of points the curve has visited.
public class Curve {

    //size of each circle centered around a coordinate
    private static final int SIZE = 10;
    private static final double SPEED = 1.8;
    //4 is the max # of players RN so we specify 4 values for each of these
    private static final double[]   START_HEADINGS =   {45, 135, 225, 315};
    private static final Color[]    COLORS =   {
                                                Color.MAGENTA,
                                                Color.WHITE,
                                                Color.CYAN,
                                                Color.GREEN
                                            };

    private HashSet<Ellipse2D.Double> path;
    private Coordinate current;
    private double heading;
    private Color color;
    private boolean isAlive;

    //should take a color or ID as well;
    public Curve(int id) {
        path = new HashSet<>();
        heading = START_HEADINGS[id];
        current = new Coordinate(800,450);
        color = COLORS[id];
        isAlive = true;
    }

    public void advance() {
        //advance current based on heading first
        //access this value twice so we store it, can't modify heading
        if(!this.isAlive())
            return;

        double toRadians = Math.toRadians(heading);
        current.move(Math.cos(toRadians) * SPEED, Math.sin(toRadians)*SPEED);
        //update the path
        path.add(new Ellipse2D.Double(current.getX(), current.getY(), SIZE, SIZE));
    }

    /**
     *
     * @param b specifies whether to move in the positive or negative direction (it doesn't matter which is which)
     */
    public void adjustHeading(boolean b) {
        if(!this.isAlive())
            return;

        int speed = 5;
        if(!b)
            speed = -speed;

        heading += speed;
    }

    public HashSet<Ellipse2D.Double> getPath() {
        return path;
    }

    public Color getColor() {
        return color;
    }

    public boolean isAlive() {
        return isAlive;
    }
}
