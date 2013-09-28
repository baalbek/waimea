package waimea.views.chart;

import oahu.exceptions.NotImplementedException;
import oahux.chart.IRuler;
import org.apache.log4j.Logger;
import org.joda.time.DateMidnight;
import org.joda.time.DateTimeConstants;
import org.joda.time.Days;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: rcs
 * Date: 3/29/13
 * Time: 6:31 PM
 */
public class DefaultDateRuler implements IRuler {
    private Logger log = Logger.getLogger(getClass().getPackage().getName());

    private final double x0;
    private final double ppx;
    private final DateMidnight start;
    private final int snapUnit;

    public DefaultDateRuler(double x0, Date start, double ppx, int snapUnit) {
        this.x0 = x0;
        this.ppx = ppx;
        this.start = new DateMidnight(start);
        this.snapUnit = snapUnit;
    }

    //region Interface Methods
    @Override
    public double calcPix(Object value) {
        int daysElapsed =  Days.daysBetween(start,(DateMidnight)value).getDays();

        double result = x0 + (ppx * daysElapsed);

        if (log.isDebugEnabled()) {
            log.debug(String.format("(calcPix) ppx: %.4f, daysElapsed: %d, result: %.4f", ppx, daysElapsed, result));

        }

        return result;
    }


    @Override
    public Object calcValue(double pix) {

        int adjSec = calcAdjustedSections(pix);

        DateMidnight dm = start.plusDays(adjSec);

        //return dm.toDate();
        return dm;
    }


    @Override
    public double snapTo(double pix) {
        switch (snapUnit) {
            case IRuler.SNAP_UNIT_DAY:
                return calculateSnapToDay(pix);
            case IRuler.SNAP_UNIT_WEEK:
                return calculateSnapToWeek(pix);
            default:
               return pix;
        }
    }
    //endregion Interface Methods

    //region Private methods

    private double calculateSnapToWeek(double pix) {
        DateMidnight curDay = (DateMidnight)calcValue(pix);
        DateMidnight nearestFriday = calcNearestFriday(curDay);
        if (log.isDebugEnabled()) {
            log.debug(String.format("(calculateSnapToWeek) pix: %.4f, curDay: %s, nearest friday: %s", pix, curDay, nearestFriday));
        }
        return calcPix(nearestFriday);
    }

    private DateMidnight calcNearestFriday(DateMidnight curDay) {
        return curDay.withDayOfWeek(DateTimeConstants.FRIDAY);
    }

    private double calculateSnapToDay(double pix) {
        int adjustedSections = calcAdjustedSections(pix);

        double result = (adjustedSections  * ppx) + x0;

        if (log.isDebugEnabled()) {
            log.debug(String.format("x0: %.4f, ppx: %.4f, pix: %.2f, result: %.4f", x0, ppx, pix, result));
        }
        return result;
    }

    private int calcAdjustedSections(double pix) {
        double actualPix = pix - x0;

        double dblSections = actualPix / ppx;

        int intSections = (int)dblSections;

        double remainder = dblSections - intSections;

        int result =  remainder >= 0.5 ? intSections + 1 : intSections;

        if (log.isDebugEnabled()) {
            log.debug(String.format("(calcAdjustedSections) pix: %.4f, act. pix: %.4f, sections: %d, remainder: %.4f, result: %d",
                    pix,
                    actualPix,
                    intSections,
                    remainder,
                    result));
        }
        return result;
    }
    //endregion Private methods

}
