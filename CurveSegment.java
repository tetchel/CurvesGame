package ca.etchells.curves;

import java.awt.geom.Ellipse2D;

public class CurveSegment extends Ellipse2D.Double {
    private int id;
    public CurveSegment(double x, double y, double w, double h, int idIn) {
        super(x, y, w, h);
        id = idIn;
    }

    public int getId() {
        return id;
    }
}
