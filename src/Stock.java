import java.io.Serializable;
import java.util.ArrayList;

public class Stock implements Serializable {
	private String symbol;
	private ArrayList<Record> stockRecs;
	
	public Stock(String symbol) {
		this.symbol = symbol;
		this.stockRecs = new ArrayList<Record>();
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public ArrayList<Record> getStockRecs() {
		return stockRecs;
	}

	public void addRecord(String symbol, Record record) {
		if (this.stockRecs.isEmpty()) {
			this.stockRecs.add(record);
			return;
		}
		for (int i = 0; i < stockRecs.size(); i++) {
			if (this.stockRecs.get(i).getDate().equals(record.getDate())) {
				return;
			}
			if (this.getSymbol().equals(symbol)) {
				this.stockRecs.add(record);
				return;
			}
		}
	} 
	
	public void setStockRecs(ArrayList<Record> stockRecs) {
		this.stockRecs = stockRecs;
	}
	
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!( obj instanceof Stock)) {
			return false;
		}
		Stock s = (Stock) obj;
		return this.symbol.equalsIgnoreCase(s.symbol);
	}
	

}
