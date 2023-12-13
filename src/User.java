import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {
	private String username;
	private String password;
	private double cash;
	private ArrayList<Stock> stockInv;
	
	public User(String username, String password) {
		this.username = username;
		this.password = password;
		this.cash = 10000.00;
		this.stockInv = new ArrayList<>();
	}

	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	public double getCash() {
		return cash;
	}
	
	public void setCash(double cash) {
		this.cash=cash;
	}
	
	public ArrayList<Stock> getStockInv() {
		return stockInv;
	}

	public void setStockInv(ArrayList<Stock> stockInv) {
		this.stockInv = stockInv;
	}
	
	public String toString() {
		return "Username: " + username + " Password: " + password;
	}
	
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof User)) {
			return false;
		}
		User u = (User) obj;
		return u.getUsername().equalsIgnoreCase(this.username) && u.getPassword().equals(this.password);
	}
	/*
	public void buyStocks(ArrayList<Stock> stockInv, Stock stock) {
		if (this.stockInv.isEmpty()) {
			this.stockInv.add(stock);
			return;
		}
		if (this.stockInv.contains(stock)) { 
			int idx = DataCenter.getInstance().stockIdx(stock);
			this.stockInv = DataCenter.getInstance().stockList.add;
			
			for (int i = 0; i < stockInv.size(); i++) {
				//buy from current date
			}
		}
	*/
	
}
