package ca.etchells.curves;

import java.awt.geom.Ellipse2D;

/**
 * This class is just an Ellipse2D.Double with one extra int field "id".
 * This allows me to check curve collisions with itself easily, since each curve segment now has its own ID representing
 * the order in which the segments were created.
 * You can see this at work in the if(isSelfCollision) block of Curve.checkCollision.
 */
public class CurveSegment extends Ellipse2D.Double {
    private int id;
    public CurveSegment(double x, double y, double w, double h, int idIn) {
        //construct the ellipse
        super(x, y, w, h);
        //assign the extra field.
        id = idIn;
    }

    /**
     *
     * @return the unique id for this segment.
     */
    public int getId() {
        return id;
    }
}
