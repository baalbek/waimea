package waimea.views.chart;

import javafx.geometry.Point2D;
import oahu.exceptions.NotImplementedException;
import oahux.chart.IBoundaryRuler;


/**
 * Created with IntelliJ IDEA.
 * User: rcs
 * Date: 3/29/13
 * Time: 6:30 PM
 */
public class DefaultVRuler implements IBoundaryRuler {
    private final Point2D ul;
    private final Point2D lr;
    private final double ppx;

    public DefaultVRuler(Point2D ul, Point2D lr, double ppx) {
        this.ppx = ppx;
        this.ul = ul;
        this.lr = lr;
    }

    @Override
    public double calcPix(Object value) {
        throw new NotImplementedException();
    }

    @Override
    public Object calcValue(double pix) {
        throw new NotImplementedException();
    }

    @Override
    public Point2D getLowerRight() {
        return lr;
    }

    @Override
    public Point2D getUpperLeft() {
        return ul;
    }

    @Override
    public String toString() {
        return "Upper left: " + ul.toString() + ", lower right: " + lr.toString();
    }

}
