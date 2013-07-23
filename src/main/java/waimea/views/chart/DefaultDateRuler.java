package waimea.views.chart;

import oahu.exceptions.NotImplementedException;
import oahux.chart.IRuler;

/**
 * Created with IntelliJ IDEA.
 * User: rcs
 * Date: 3/29/13
 * Time: 6:31 PM
 */
public class DefaultDateRuler implements IRuler {

    @Override
    public double calcPix(Object value) {
        throw new NotImplementedException();
    }

    @Override
    public Object calcValue(double pix) {
        throw new NotImplementedException();
    }

}
