import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class DataCenter implements Serializable {

	private static DataCenter instance = null;
	ArrayList<User> userList = null;
	ArrayList<Stock> stockList = null;

	private DataCenter() {
		userList = new ArrayList<>();
		stockList = new ArrayList<>();
		readUserList();
	}

	public static DataCenter getInstance() {
		if (instance == null) {
			instance = new DataCenter();
		}
		return instance;
	}

	public void saveUserList() {
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("userList.dat"));) {
			oos.writeObject(instance.userList);
		} catch (IOException ioe) {

		} catch (Exception e) {

		}
	}

	public void readUserList() {
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("userList.dat"));) {
			userList = (ArrayList<User>) ois.readObject();
		} catch (IOException ioe) {

		} catch (Exception e) {

		}
	}

	public boolean findUser(User user) {
		return userList.contains(user);
	}

	public int userIdx(User user) {
		if (userList.contains(user)) {
			return userList.indexOf(user);
		} else {
			return -1;
		}
	}

	public User getUser(String username) {
		for (int i = 0; i < stockList.size(); i++) {
			if (userList.get(i).getUsername().equals(username)) {
				return userList.get(i);
			}
		}
		return null;
	}

	public boolean findStock(Stock stock) {
		return stockList.contains(stock);
	}

	public Stock getStock(String stockSymbol) {
		for (int i = 0; i < stockList.size(); i++) {
			if (stockList.get(i).getSymbol().equals(stockSymbol)) {
				return stockList.get(i);
			}
		}
		return null;
	}

	public int stockIdx(Stock stock) {
		if (stockList.contains(stock)) {
			return stockList.indexOf(stock);
		} else {
			return -1;
		}
	}

}