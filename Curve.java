package ca.etchells.curves;

import java.awt.*;
import java.util.HashSet;

//represents a curve in the game as a series of points the curve has visited.
public class Curve {
    //size of each circle centered around a coordinate
    private final int SIZE = 10;
    //speed at which curves move
    private final double SPEED = 2.2;
    //4 is the max # of players RN so we specify 4 values for each of these
    //these hardcoded values are hopefully temporary. maybe switch to starting from the corners?
    private static final Color[]    COLORS =    {
                                                    Color.MAGENTA,
                                                    Color.CYAN,
                                                    Color.YELLOW,
                                                    Color.GREEN
                                                };
    //contains the circles that make up each curve
    private HashSet<CurveSegment> path;
    //x, y are the coordinates of the 'current' curve segment, heading is the
    private double  x, y,
    //angle at which the curve is going
                    heading,
    //window dimensions
                    width, height;
    //only final variables should be in caps, but this way it's consistent with CurvesPanel
    //color of the curve
    private Color color;
    //after colliding, the curve is dead. turns red and stops responding to commands
    private boolean isAlive;
    //which curve number this is
    private int id;
    //used to give each new CurveSegment a unique ID
    private int segment_counter = 0;

    /**
     * Constructor for a curve, initializes fields and starts it from the centre of the window.
     * @param idIn is the player number so that each curve is unique
     * @param size is a bit of a strange way to do this, but it works.
     */
    public Curve(int idIn, Dimension size) {
        //get dimensions from the input
        width = size.getWidth();
        height = size.getHeight();
        //initialize fields
        id = idIn;
        path = new HashSet<>();
        color = COLORS[id];
        isAlive = true;
        //set initial positions
        //how far to offset the curves from each corner
        final int OFFSET = 5;
        //players 0 and 2 start from left
        //these are done manually because there are only 4 maximum
        if(id == 0 || id == 2) {
            x = OFFSET;
            if(id == 0) {
                y = OFFSET;
                heading = 45;
            }
            else {
                y = height-OFFSET;
                heading = 315;
            }
        }
        //1 and 3 start from right
        else {
            x = width-OFFSET;
            if(id == 1) {
                y = OFFSET;
                heading = 135;
            }
            else {
                y = height-OFFSET;
                heading = 225;
            }
        }
    }
    /**
     * Moves the curve forward in time, calculating the position of and
     * drawing the next circle that adds to the curve.
     * @param curves the set of curves for collision checking
     */
    public void advance(Curve[] curves) {
        //access this value twice so we store it, modifying heading leads to issues
        double toRadians = Math.toRadians(heading);
        //get the x and y component of speed
        x += Math.cos(toRadians) * SPEED;
        y += Math.sin(toRadians) * SPEED;
        //check for collisions
        //wall collisions
        if(x >= width || x <= SIZE/2 || y >= height || y <= SIZE/2)
            killCurve();

        //next part of the curve to be added
        CurveSegment next = new CurveSegment(x, y, SIZE, SIZE, segment_counter++);

        for(Curve c : curves) {
            checkCollision(next, c, getId() == c.getId());
        }
        //update the path if everything is OK
        path.add(next);
    }
    /**
     * Checks to see if a node collides with a specified curve
     * @param current node to check for collisions with
     * @param curve   curve to check for collisions against
     * @param isSelfCollision if we are checking to see if the curve is colliding with itself or not
     */
    public void checkCollision(CurveSegment current, Curve curve, boolean isSelfCollision) {
        for(CurveSegment c : curve.getPath()) {
            //this part is confusing - if we are checking for a self collision, we have to ensure the segment we are
            //checking against is not the successive segment.
            //without this if statement, all curves instantly die
            //the number of segments to ignore is scaled by speed.
            if(isSelfCollision && (current.getId() <= c.getId() + SPEED*2)) {
                return;
            }
            //calculate distance and see if there is a collision or not.
            double distance = cartDistance(c.getX(), c.getY(), current.getX(), current.getY());
            if (distance <= SIZE) {
                //System.out.printf("Curve %d has collided with curve %d%n", getId(), curve.getId());
                //System.out.printf("Collision points: x: %g y: %g x: %g y: %g%n", c.getX(), c.getY(), current.getX(), current.getY());
                killCurve();
            }
        }
    }
    /**
     * Returns the distance between two points on a cartesian plane using Pythagorean theorem.
     * @param x1 first x
     * @param x2 second x
     * @param y1 first y
     * @param y2 second y
     * @return the distance between the two points.
     */
    private static double cartDistance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(   (x2 - x1)*(x2 - x1) +
                            (y2 - y1)*(y2 - y1) );
    }
    /**
     * Adjusts the direction of the curve when user input is given.
     * @param b specifies whether to move in the positive or negative direction
     */
    public void adjustHeading(boolean b) {
        //adjust this value for turns to accelerate faster
        int turnSpeed = 6;
        if(!b)
            heading -= turnSpeed;
        else
            heading += turnSpeed;
    }
    /**
     * Returns a hash set of ellipses representing the curve
     * @return the path as a hashset of ellipses
     */
    public HashSet<CurveSegment> getPath() {
        return path;
    }
    /**
     * @return the color of this curve (red if dead)
     */
    public Color getColor() {
        return color;
    }
    /**
     * @return whether or not this curve is still active
     */
    public boolean isAlive() {
        return isAlive;
    }
    /**
     * @return this curve's unique id
     */
    public int getId() {
        return id;
    }
    /**
     * kills the curve, making it red and making it no longer respond to input (since all input method calls check isAlive())
     */
    private void killCurve() {
        isAlive = false;
        color = Color.RED;
    }
}
