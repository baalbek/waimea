/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package waimea.models;

import clojure.lang.PersistentVector;
import java.util.Date;
import java.util.List;
import maunakea.financial.Derivative;
import maunakea.financial.Stock;

/**
 *
 * @author rcs
 */
public interface TaharoaModel {
    
    List<Stock> stockPrices(String ticker, Date fromDate);
    
    PersistentVector stockPrices2(String ticker, Date fromDate);
    
    List<Derivative> derivatives(String ticker);
    
    List<Derivative> calls(String ticker);
    
    List<Derivative> puts(String ticker);
    
    Stock spot(String ticker);
   
}
