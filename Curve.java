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
    //these hardcoded values are hopefully temporary. maybe switch to starting from the corners?
    private static final double[]   START_SPOTS     =   {45, 855, 45, 855};
    private static final Color[]    COLORS =    {
                                                    Color.MAGENTA,
                                                    Color.WHITE,
                                                    Color.CYAN,
                                                    Color.GREEN
                                                };

    private HashSet<Ellipse2D.Double> path;
    private double x, y;
    private double heading;
    private Color color;
    private boolean isAlive;

    /**
     * Constructor for a curve, initializes fields and starts it from the centre of the window.
     * @param id is the player number so that each curve is unique
     */
    public Curve(int id) {
        path = new HashSet<>();
        //players 0 and 1 start from left
        if(id < 2) {
            heading = 0;
            x = 50;
        }
        //2 and 3 start from right
        else {
            heading = 180;
            x = 1550;
        }
        y = START_SPOTS[id];
        color = COLORS[id];
        isAlive = true;
    }

    /**
     * Moves the curve forward in time, calculating the position of and
     * drawing the next circle that adds to the curve.
     */
    public void advance() {
        //advance current based on heading first
        //access this value twice so we store it, can't modify heading
        if(!this.isAlive())
            return;

        //calculate next part of the curve
        double toRadians = Math.toRadians(heading);
        x += Math.cos(toRadians) * SPEED;
        y += Math.sin(toRadians)*SPEED;
        //check for collisions
        //wall collisions
        double  centrex = this.x + (Math.sqrt(5) / 2) * SIZE,
                centrey = this.y + (Math.sqrt(5) / 2) * SIZE;

        //curve collisions

        //update the path if everything is OK
        path.add(new Ellipse2D.Double(x, y, SIZE, SIZE));
    }

    /**
     * Returns the distance between two points on a cartesian plane using Pythagorean theorem.
     * @param x1 i think they are all self explanatory
     * @return the distance between the two points.
     */
    private double cartDistance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(   (x2 - x1)*(x2 - x1) +
                            (y2 - y1)*(y2 - y1) );
    }

    /**
     * Adjusts the direction of the curve when user input is given.
     * @param b specifies whether to move in the positive or negative direction (it doesn't matter which is which)
     */
    public void adjustHeading(boolean b) {
        if(!this.isAlive())
            return;

        int speed = 5;
        if(!b)
            heading -= speed;
        else
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
