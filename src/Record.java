import java.io.Serializable;
import java.util.Date;

public class Record implements Serializable {
	//Date,	Open,	High,	Low,	Close,	Adj Close,	Volume
	//This is basically a stamp of the user's interaction-- save on any call of info
	
	private String date;
	private double open;
	private double high;
	private double low;
	private double close;
	private double adjClose;
	private int volume;
	
	public Record(String date, double open, double high, double low, double close, double adjClose, int volume) {
		this.date = date;
		this.open = open;
		this.high = high;
		this.low = low;
		this.close = close;
		this.adjClose = adjClose;
		this.volume = volume;
	}
	
	public double getMovingAvg(Stock stock, int n) {
		double sum = 0.0;
		for (int i = 0; i < n; i++) {
			sum += stock.getStockRecs().get(i).getAdjClose();
		}
		return sum / n;
	}
	
	public double getOpen() {
		return open;
	}

	public void setOpen(double open) {
		this.open = open;
	}

	public double getHigh() {
		return high;
	}

	public void setHigh(double high) {
		this.high = high;
	}

	public double getLow() {
		return low;
	}

	public void setLow(double low) {
		this.low = low;
	}

	public double getClose() {
		return close;
	}

	public void setClose(double close) {
		this.close = close;
	}

	public double getAdjClose() {
		return adjClose;
	}

	public void setAdjClose(double adjClose) {
		this.adjClose = adjClose;
	}

	public int getVolume() {
		return volume;
	}

	public void setVolume(int volume) {
		this.volume = volume;
	}
	
	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
	
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!( obj instanceof Record)) {
			return false;
		}
		Record r = (Record) obj;
		return this.date.equals(r.date);
	}
	
}
