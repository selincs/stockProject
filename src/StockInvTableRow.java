import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class StockInvTableRow {

	private SimpleStringProperty symbol;
	private SimpleDoubleProperty adjClose;
	private SimpleIntegerProperty volume;
	
	public StockInvTableRow(Stock s) {
			this.symbol = new SimpleStringProperty(s.getSymbol());
			this.adjClose = new SimpleDoubleProperty(s.getStockRecs().get(0).getAdjClose());
			this.volume = new SimpleIntegerProperty(s.getStockRecs().get(0).getVolume());
	}
	
}
