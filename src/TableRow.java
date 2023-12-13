import java.io.Serializable;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class TableRow implements Serializable {

	private SimpleStringProperty date;
	private SimpleDoubleProperty open;
	private SimpleDoubleProperty high;
	private SimpleDoubleProperty low;
	private SimpleDoubleProperty close;
	private SimpleDoubleProperty adjClose;
	private SimpleIntegerProperty volume;

	public TableRow(String date, double open, double high, double low, double close, double adjClose, int volume) {
		this.date = new SimpleStringProperty(date);
		this.open = new SimpleDoubleProperty(open);
		this.high = new SimpleDoubleProperty(high);
		this.low = new SimpleDoubleProperty(low);
		this.close = new SimpleDoubleProperty(close);
		this.adjClose = new SimpleDoubleProperty(adjClose);
		this.volume = new SimpleIntegerProperty(volume);
	}
	
	public TableRow(Record r) {
		this.date = new SimpleStringProperty(r.getDate());
		this.open = new SimpleDoubleProperty(r.getOpen());
		this.high = new SimpleDoubleProperty(r.getHigh());
		this.low = new SimpleDoubleProperty(r.getLow());
		this.close = new SimpleDoubleProperty(r.getClose());
		this.adjClose = new SimpleDoubleProperty(r.getAdjClose());
		this.volume = new SimpleIntegerProperty(r.getVolume());
	}

	public String getDate() {
		return this.date.get();
	}

	public void setDate(String date) {
		this.date.set(date);
	}

	public double getOpen() {
		return this.open.get();
	}

	public void setOpen(double open) {
		this.open.set(open);
	}

	public double getHigh() {
		return this.high.get();
	}

	public void setHigh(double high) {
		this.high.set(high);
	}

	public double getLow() {
		return this.low.get();
	}

	public void setLow(double low) {
		this.low.set(low);
	}

	public double getClose() {
		return this.close.get();
	}

	public void setClose(double close) {
		this.close.set(close);
	}

	public double getAdjClose() {
		return this.adjClose.get();
	}

	public void setAdjClose(double adjClose) {
		this.adjClose.set(adjClose);
	}

	public int getVolume() {
		return this.volume.get();
	}

	public void setVolume(int volume) {
		this.volume.set(volume);
	}

}
