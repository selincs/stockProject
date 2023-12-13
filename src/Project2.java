import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import javafx.application.Application;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Project2 extends Application implements Serializable {
	
	static Stage staticStage;

	@Override
	public void start(Stage stage) throws Exception {
		staticStage = stage;
		staticStage.setMaximized(true);
		
		staticStage.setScene(new Scene(loginGUI()));
		
		staticStage.setTitle("Stock Project");
		staticStage.show();
	}
	 
	public void getStockInfo(String urlStr, String symbol) {
		String fileName = "data.dat";

		try {
			URL stockURL = new URL(urlStr);
			BufferedReader reader = new BufferedReader(new InputStreamReader(stockURL.openStream()));
			String line;
			reader.readLine();
			while ((line = reader.readLine()) != null) {
				String[] info = line.split(",");
 				Record rcrd = new Record(info[0], Double.parseDouble(info[1]), Double.parseDouble(info[2]),
						Double.parseDouble(info[3]), Double.parseDouble(info[4]), Double.parseDouble(info[5]),
						Integer.parseInt(info[6]));
 				
				if (DataCenter.getInstance().stockList.isEmpty()) {
					DataCenter.getInstance().stockList.add(new Stock(symbol));
				}
				
				for (int i = 0; i < DataCenter.getInstance().stockList.size(); i++) {
					if (DataCenter.getInstance().stockList.get(i).getSymbol().equalsIgnoreCase(symbol)) {
						if (DataCenter.getInstance().stockList.get(i).getStockRecs().contains(rcrd)) {
							break;
						}
						else {
							DataCenter.getInstance().stockList.get(i).addRecord(symbol, rcrd);
							break;
						}
			
					}
					if (i == DataCenter.getInstance().stockList.size()-1) { //(and stock !exists) {
						DataCenter.getInstance().stockList.add(new Stock(symbol));
						break;
					}
				}
			}
			reader.close();
		} catch (IOException ioe) {

		}
	}
	
	public class MyDatePicker extends BorderPane {

		private TextField tfStock;
		private DatePicker start;
		private DatePicker end;
		private Button btnChart;
		private Button btnTable;

		public MyDatePicker() {
			initialGUI();
		}

		public void initialGUI() {
			HBox hbox = new HBox();
			tfStock = new TextField();
			start = new DatePicker();
			end = new DatePicker();
			btnChart = new Button("Chart");
			btnChart.setPrefWidth(80);
			btnTable = new Button("Table");
			btnTable.setPrefWidth(80);
			
			Label lbEnter = new Label("Enter Stock Symbol:");
			lbEnter.setPadding(new Insets(10));
			lbEnter.setTextFill(Color.FLORALWHITE);
			
			Label lbDates = new Label("View Stock Data Between :");
			lbDates.setTextFill(Color.FLORALWHITE);
			lbDates.setPadding(new Insets(10));
			hbox.getChildren().addAll(lbEnter, tfStock, lbDates, start, end, btnChart, btnTable);
			hbox.setStyle("-fx-background-color:rgba(12, 48, 32, .7)");
			hbox.setAlignment(Pos.CENTER);
			hbox.setSpacing(3);

			this.setTop(hbox);
			
			btnChart.setDisable(true);
			end.setDisable(true);
			
			start.setOnAction(e-> {
				end.setDisable(false);
			});

			start.setDayCellFactory(param -> new DateCell() {
			    private LocalDate now = LocalDate.now();

			    @Override
			    public void updateItem(LocalDate date, boolean empty) {
			        super.updateItem(date, empty);
			        if (date != null && !empty) {
			            setDisable(date.compareTo(now) > 0);
			        }
			    }
			});
			
			end.setDayCellFactory(param -> new DateCell() {
			    private LocalDate minDate = start.getValue();
			    private LocalDate now = LocalDate.now();

			    @Override
			    public void updateItem(LocalDate date, boolean empty) {
			        super.updateItem(date, empty);
			        if (date != null && !empty) {
			            setDisable(date.compareTo(minDate) < 0 || date.compareTo(now) > 0);
			        }
			    }
			});
			
			BooleanBinding booleanBind = tfStock.textProperty().isEmpty()
					.or(start.valueProperty().isNull())
					.or(end.valueProperty().isNull());
			btnChart.disableProperty().bind(booleanBind);
			btnTable.disableProperty().bind(booleanBind);
			
			btnChart.setOnAction(e -> {
				LocalDate period1 = start.getValue();
				LocalDate period2 = end.getValue();

				String stockSymbol = tfStock.getText(); 
				
				stockHistory(stockSymbol, period1, period2);
			});
		}
	}
	
	public Pane tableView(String stockSymbol, LocalDate period1, LocalDate period2) {  //look at lineChartStockHistory 
		BorderPane bpane = new BorderPane();
		bpane.setStyle("-fx-background-color:rgba(2, 48, 32, .7); ");
		bpane.setPadding(new Insets(20));
		Label lblTitle = new Label("Stock Records");
		
		int year1 = period1.getYear();
		int month1 = period1.getMonthValue();
		int day1 = period1.getDayOfMonth();

		int year2 = period2.getYear();
		int month2 = period2.getMonthValue();
		int day2 = period2.getDayOfMonth();

		long startPeriod = getPeriod(year1, month1, day1);
		long endPeriod = getPeriod(year2, month2, day2);
		
		String strUrl = getURL(stockSymbol, startPeriod, endPeriod);
		getStockInfo(strUrl, stockSymbol);
		
		TableView<Record> tvRecords = new TableView<>();
		
		TableColumn<Record, String> dateCol = new TableColumn<>("Date");
		dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));

		TableColumn<Record, Double> openCol = new TableColumn<>("Open");
		openCol.setCellValueFactory(new PropertyValueFactory<>("open"));

		TableColumn<Record, Double> highCol = new TableColumn<>("High");
		highCol.setCellValueFactory(new PropertyValueFactory<>("high"));
		
		TableColumn<Record, Double> lowCol = new TableColumn<>("Low");
		lowCol.setCellValueFactory(new PropertyValueFactory<>("low"));
		
		TableColumn<Record, Double> closeCol = new TableColumn<>("Close");
		closeCol.setCellValueFactory(new PropertyValueFactory<>("close"));
		
		TableColumn<Record, Double> adjCloseCol = new TableColumn<>("Adj-Close");
		adjCloseCol.setCellValueFactory(new PropertyValueFactory<>("adjClose"));
		
		TableColumn<Record, Integer> volumeCol = new TableColumn<>("Volume");
		volumeCol.setCellValueFactory(new PropertyValueFactory<>("volume"));
		
		tvRecords.getColumns().addAll(dateCol, openCol, highCol, lowCol, closeCol, adjCloseCol, volumeCol);
		tvRecords.getItems().addAll(DataCenter.getInstance().getStock(stockSymbol).getStockRecs());
		
		bpane.setCenter(tvRecords);
		
		return bpane;
	}
	
	
	public Pane tableViewHistory(String stockSymbol, LocalDate period1, LocalDate period2) {  //look at lineChartStockHistory 
		BorderPane bpane = new BorderPane();
		bpane.setStyle("-fx-background-color:rgba(2, 48, 32, .7); ");
		bpane.setPadding(new Insets(20));
		Label lblTitle = new Label("Stock Records");
		
		int year1 = period1.getYear();
		int month1 = period1.getMonthValue();
		int day1 = period1.getDayOfMonth();

		int year2 = period2.getYear();
		int month2 = period2.getMonthValue();
		int day2 = period2.getDayOfMonth();

		long startPeriod = getPeriod(year1, month1, day1);
		long endPeriod = getPeriod(year2, month2, day2);
		
		String strUrl = getURL(stockSymbol, startPeriod, endPeriod);
		getStockInfo(strUrl, stockSymbol);
		
		TableView<Record> tvRecords = new TableView<>();
		
		TableColumn<Record, String> dateCol = new TableColumn<>("Date");
		dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));

		TableColumn<Record, Double> openCol = new TableColumn<>("Open");
		openCol.setCellValueFactory(new PropertyValueFactory<>("open"));

		TableColumn<Record, Double> highCol = new TableColumn<>("High");
		highCol.setCellValueFactory(new PropertyValueFactory<>("high"));
		
		TableColumn<Record, Double> lowCol = new TableColumn<>("Low");
		lowCol.setCellValueFactory(new PropertyValueFactory<>("low"));
		
		TableColumn<Record, Double> closeCol = new TableColumn<>("Close");
		closeCol.setCellValueFactory(new PropertyValueFactory<>("close"));
		
		TableColumn<Record, Double> adjCloseCol = new TableColumn<>("Adj-Close");
		adjCloseCol.setCellValueFactory(new PropertyValueFactory<>("adjClose"));
		
		TableColumn<Record, Integer> volumeCol = new TableColumn<>("Volume");
		volumeCol.setCellValueFactory(new PropertyValueFactory<>("volume"));
		
		tvRecords.getColumns().addAll(dateCol, openCol, highCol, lowCol, closeCol, adjCloseCol, volumeCol);
		tvRecords.getItems().add(DataCenter.getInstance().getStock(stockSymbol).getStockRecs().get(0));
		
		bpane.setCenter(tvRecords);
		
		return bpane;
	}
	

	public Pane stockListView(User user) {
		BorderPane pane = new BorderPane();
		pane.setStyle("-fx-background-color:rgba(2, 48, 32, .7); ");
		
		LocalDate today = LocalDate.now();
		if (LocalDate.now().getDayOfWeek().getValue() == 1 || LocalDate.now().getDayOfWeek().getValue() == 6 || LocalDate.now().getDayOfWeek().getValue() == 7) {
			for (int i = 0; i < 4; i++) {
			today = LocalDate.now().minusDays(i);
				if (!(LocalDate.now().getDayOfWeek().getValue() == 1 || LocalDate.now().getDayOfWeek().getValue() == 6 || LocalDate.now().getDayOfWeek().getValue() == 7)) {
				break;
				}
			}
		}

		int weekendYear = today.minusDays(1).getYear();
		int weekendMonth = today.minusDays(1).getMonthValue();
		int weekendDay = today.minusDays(1).getDayOfMonth();
		
		int todayYear = today.getYear();
		int todayMonth = today.getMonthValue();
		int todayDay = today.getDayOfMonth();
		
		
		long startPeriod = getPeriod(weekendYear, weekendMonth, weekendDay);
		long endPeriod = getPeriod(todayYear, todayMonth, todayDay);
				
		TextField tfStockSymbol = new TextField();
		TextField tfQuantity = new TextField();
		Label lbSymbol = new Label("Enter Stock Symbol");
		Label lbQuantity = new Label("Quantity : ");
		Label lbPrice = new Label("Price");
		
		String strUrl = getURL(tfStockSymbol.getText(), startPeriod, endPeriod);
		getStockInfo(strUrl, tfStockSymbol.getText());

		pane.setPadding(new Insets(20)); // This is listview, you can use this to set each record in GUI
		Label lbStockInv = new Label("Your Stock Inventory");

		ListView<Stock> listStockInv = new ListView();
		listStockInv.getItems().addAll(user.getStockInv()); // setItems allows me to set the list contents with an external source
		
		ObservableList<Stock> stockObsList = FXCollections.observableArrayList();
		listStockInv.setItems(stockObsList);
//		int stockIdx = DataCenter.getInstance().stockIdx(DataCenter.getInstance().getStock(tfStockSymbol.getText()));
//		double currentPrice = DataCenter.getInstance().stockList.get(stockIdx).getStockRecs().get(DataCenter.getInstance().stockList.get(stockIdx).getStockRecs().size()).getAdjClose();
		
		Button btnPrice = new Button("Get Price");
		Button btnBuy = new Button("Buy");
		Button btnSell = new Button("Sell");
		
		btnPrice.setOnAction(e->{
//			lbPrice.setText(String.valueOf(currentPrice));
		});
		btnBuy.setOnAction(e-> {
	//		user.getStockInv().get(0)
	//		stockObsList.add(user.getStockInv().add(null))
		});

		btnSell.setOnAction(e->{
			
		});
		
		HBox hbox = new HBox();
		hbox.setAlignment(Pos.CENTER);
		hbox.setSpacing(10);
		hbox.setPadding(new Insets(8));
		hbox.getChildren().addAll(btnBuy, btnSell, lbPrice);

		pane.setTop(lbStockInv);
		pane.setCenter(listStockInv);
		pane.setBottom(hbox);	
		
		return pane;
	}
	
	public Pane stockPurchaseHistory(ArrayList<Stock> stockInv) {
		BorderPane pane = new BorderPane();
		pane.setStyle("-fx-background-color:rgba(2, 48, 32, .7); ");
		pane.setPadding(new Insets(20)); 
		Label lbStockInv = new Label("Stock Inventory");

		
		
		ListView<Stock> lvStockInv = new ListView();
	//	lvStockInv.getItems().addAll(DataCenter.getInstance().userList.get(DataCenter.getInstance().getUser( PUT USER HERE )).getStockInv()); // setItems allows me to set the list contents with an external source
		
		LocalDate today = LocalDate.now();
		if (LocalDate.now().getDayOfWeek().getValue() == 1 || LocalDate.now().getDayOfWeek().getValue() == 6 || LocalDate.now().getDayOfWeek().getValue() == 7) {
			for (int i = 0; i < 4; i++) {
			today = LocalDate.now().minusDays(i);
				if (!(LocalDate.now().getDayOfWeek().getValue() == 1 || LocalDate.now().getDayOfWeek().getValue() == 6 || LocalDate.now().getDayOfWeek().getValue() == 7)) {
				break;
				}
			}
		}
		
		HBox hbox = new HBox();
		hbox.setAlignment(Pos.CENTER);
		hbox.setSpacing(10);
		hbox.setPadding(new Insets(8));
		

		pane.setTop(lbStockInv);
		pane.setCenter(lvStockInv);
		pane.setBottom(hbox);	
		
		return pane;
	}
	
	public LineChart nDayMovingAvgChart(String stockSymbol, int n) {
		LocalDate today = LocalDate.now();

		int noWeekends = ((n/7)*4);   

		int nYear = today.minusDays(n+noWeekends).getYear();
		int nMonth = today.minusDays(n+noWeekends).getMonthValue();
		int nDay = today.minusDays(n+noWeekends).getDayOfMonth();
		
		int todayYear = today.getYear();
		int todayMonth = today.getMonthValue();
		int todayDay = today.getDayOfMonth();
		
		long startPeriod = getPeriod(nYear, nMonth, nDay);
		long endPeriod = getPeriod(todayYear, todayMonth, todayDay);
		
		String strUrl = getURL(stockSymbol, startPeriod, endPeriod);
		getStockInfo(strUrl, stockSymbol);
		
		NumberAxis xAxis = new NumberAxis();
		xAxis.setLabel("Days");
		NumberAxis yAxis = new NumberAxis();
		yAxis.setLabel("Stock Price");
		
		LineChart lineChart = new LineChart(xAxis, yAxis);
		XYChart.Series<Number, Number> series1 = new XYChart.Series<>();
		series1.setName(stockSymbol.toUpperCase() + " over the last " + n + " days");
		// i == start period, n == end period
		for (int i = 0; i < n; i++) {
			if (i == DataCenter.getInstance().getStock(stockSymbol).getStockRecs().size()) {
			    break;
			}
			series1.getData().add(new XYChart.Data<>(i+1, DataCenter.getInstance().getStock(stockSymbol).getStockRecs().get(i).getAdjClose()));
		}
		lineChart.getData().add(series1);
		return lineChart;
	}
	
	public LineChart stockHistory(String stockSymbol, LocalDate period1, LocalDate period2) {
		
		int year1 = period1.getYear();
		int month1 = period1.getMonthValue();
		int day1 = period1.getDayOfMonth();

		int year2 = period2.getYear();
		int month2 = period2.getMonthValue();
		int day2 = period2.getDayOfMonth();

		long startPeriod = getPeriod(year1, month1, day1);
		long endPeriod = getPeriod(year2, month2, day2);

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
		String[] dates = {period1.format(formatter), period2.format(formatter)};
		
		
		NumberAxis xAxis = new NumberAxis();
		xAxis.setLabel("Days");
		NumberAxis yAxis = new NumberAxis();
		yAxis.setLabel("Stock Price"); 
		
		String strUrl = getURL(stockSymbol, startPeriod, endPeriod);
		getStockInfo(strUrl, stockSymbol);
		
		int recordCt = DataCenter.getInstance().getStock(stockSymbol).getStockRecs().size();
		
		LineChart lineChart = new LineChart(xAxis, yAxis);
		XYChart.Series<Number, Number> series1 = new XYChart.Series<>();
		series1.setName(stockSymbol.toUpperCase() + " between " + dates[0] + " to " + dates[1]);
		
		for (int i = 0; i < recordCt; i++) { 
			if (i == recordCt) {
			    break;
			}
			series1.getData().add(new XYChart.Data<>(i+1, DataCenter.getInstance().getStock(stockSymbol).getStockRecs().get(i).getAdjClose()));
		}
		lineChart.getData().add(series1);
		return lineChart;
	}
	
	private Pane accountHome(User user) {
		GridPane pane = new GridPane();
		BorderPane borderPane = new BorderPane();
		StackPane stockPane = new StackPane();
		
		MyDatePicker datePicker = new MyDatePicker();
		stockPane.getChildren().add(datePicker);
		stockPane.setStyle("-fx-background-color:rgba(2, 48, 32, .7)");
	
		pane.setAlignment(Pos.TOP_LEFT);
		pane.setPadding(new Insets(20));
		pane.setHgap(10);
		pane.setVgap(10);
		pane.setStyle("-fx-background-color:rgba(2, 48, 32, .7); ");
		stockPane.setStyle("-fx-background-color: rgba(2, 48, 32, .6); -fx-padding: 20px;");
		
		Label lbUsername = new Label("Username : " + user.getUsername());
		lbUsername.setTextFill(Color.FLORALWHITE);
		Label lbPassword = new Label("Password : " + encrypt(user.getPassword()));
		lbPassword.setTextFill(Color.FLORALWHITE);
		Label lbCash = new Label("Cash : $" + String.format("%.2f", user.getCash()));
		lbCash.setTextFill(Color.FLORALWHITE);
		Label lbStocks = new Label("Stocks Owned : ");
		lbStocks.setTextFill(Color.FLORALWHITE);
		Button btnPortfolio = new Button("View my Stock Portfolio");
		
		VBox vboxUser = new VBox();
		vboxUser.setAlignment(Pos.CENTER);
		vboxUser.setSpacing(10);
		vboxUser.getChildren().addAll(lbUsername, lbPassword, lbCash, btnPortfolio);
		pane.add(vboxUser, 1, 1, 1, 1);
		
		Label lbAnalyze = new Label("Enter Stock Symbol and number of days to create n-Day Moving Average Chart : ");
		lbAnalyze.setTextFill(Color.FLORALWHITE);
		Button btnAnalyze = new Button("Analyze Price Trends");
		TextField tfSymbol = new TextField();
		TextField tfNDays = new TextField();

		Button btnHistory = new Button("Purchase History");
		Button btnClear = new Button("Reset Data");
		Button btnBuy = new Button("Buy Stock");
		
		btnAnalyze.setPrefWidth(220);
		btnHistory.setPrefWidth(220);
		btnClear.setPrefWidth(220);
		btnBuy.setPrefWidth(220);
		
		VBox vboxAnalyze = new VBox();
		vboxAnalyze.setAlignment(Pos.CENTER);
		vboxAnalyze.setSpacing(10);
		vboxAnalyze.getChildren().addAll(lbAnalyze, tfSymbol, tfNDays, btnAnalyze, btnBuy, btnHistory, btnClear);
		pane.add(vboxAnalyze, 1, 5, 1, 1);

		borderPane.setLeft(pane);
		borderPane.setCenter(stockPane);

		datePicker.btnChart.setOnAction(e->{
			borderPane.setCenter(stockHistory(datePicker.tfStock.getText(), datePicker.start.getValue(), datePicker.end.getValue()));
		});
		
		datePicker.btnTable.setOnAction(e->{
			borderPane.setCenter(tableView(datePicker.tfStock.getText(), datePicker.start.getValue(), datePicker.end.getValue()));
		});
		
		btnAnalyze.setOnAction(e->{
			borderPane.setCenter(nDayMovingAvgChart(tfSymbol.getText(), Integer.parseInt(tfNDays.getText())));
		});
		
		btnHistory.setOnAction(e->{
			// USER STOCKS PURCHASE HISTORY LIST VIEW 
			borderPane.setCenter(tableViewHistory(tfSymbol.getText(), LocalDate.now().minusDays(4), LocalDate.now()));
			//borderPane.setCenter(tableViewHistory(tfSymbol.getText(), LocalDate.now().minusDays(4), LocalDate.now()));
		});
		
		btnBuy.setOnAction(e->{
			borderPane.setCenter(tableViewHistory(tfSymbol.getText(), LocalDate.now().minusDays(1), LocalDate.now()));
		});
		
		btnPortfolio.setOnAction(e->{
			//borderPane.setCenter( LIST VIEW HERE )
			borderPane.setCenter(stockListView(user));
		});
		
		btnClear.setOnAction(e->{
			borderPane.setCenter(stockPane);
			DataCenter.getInstance().stockList.clear();
		});
		// ListViewGUI to display stocks + include volume of stocks (Or TableView?)
		
		return borderPane;
	}
	

	private Pane signupGUI() {
		GridPane pane = new GridPane();
		
		pane.setAlignment(Pos.CENTER);
		pane.setPadding(new Insets(20));
		pane.setHgap(10);
		pane.setVgap(10);
		pane.setStyle("-fx-background-color:rgba(2, 48, 32, .7)");

		Label lbUsername = new Label("Username");
		Label lbPassword = new Label("Password");
		Label lbPassword2 = new Label("Confirm Password");
		GridPane.setHalignment(lbUsername, HPos.RIGHT);
		GridPane.setHalignment(lbPassword, HPos.RIGHT);
		GridPane.setHalignment(lbPassword2, HPos.RIGHT);
		
		TextField tfUsername = new TextField();
		PasswordField tfPassword = new PasswordField();
		PasswordField tfPassword2 = new PasswordField();
		tfUsername.setMaxWidth(170);
		tfPassword.setMaxWidth(170);
		tfPassword2.setMaxWidth(170);
		
		pane.add(lbUsername, 0, 0);
		pane.add(tfUsername, 1, 0, 3, 1);
		pane.add(lbPassword, 0, 1, 1, 1);
		pane.add(tfPassword, 1, 1, 3, 1);
		pane.add(lbPassword2, 0, 2);
		pane.add(tfPassword2, 1, 2, 3, 1);

		Button btnCreate = new Button("Create account");
		btnCreate.setAlignment(Pos.CENTER);
		btnCreate.setPadding(new Insets(10));
		pane.add(btnCreate, 2, 3, 1, 3);
		btnCreate.setPrefSize(150, 30);
		btnCreate.setDisable(true);
		
		Button btnReturn = new Button("Return to Log In");
		btnReturn.setAlignment(Pos.CENTER);
		btnReturn.setPadding(new Insets(10));
		btnReturn.setTextFill(Color.DARKGREEN);
		btnReturn.setStyle("-fx-background-color:rgba(100, 255, 100, 0.8);");
		btnReturn.setPrefSize(150, 30);
		pane.add(btnReturn, 2, 3, 1, 3);
		btnReturn.setVisible(false);

		Label lbInvalidUsername = new Label("That username already exists. Please try again.");
		lbInvalidUsername.setPadding(new Insets(5));
		lbInvalidUsername.setTextFill(Color.RED);
		pane.add(lbInvalidUsername, 4, 0, 3, 1);
		lbInvalidUsername.setVisible(false);

		Label lbAccountCreated = new Label("Account successfully created!");
		lbAccountCreated.setPadding(new Insets(-2));
		GridPane.setHalignment(lbAccountCreated, HPos.LEFT);
		lbAccountCreated.setTextFill(Color.FLORALWHITE);
		lbAccountCreated.setVisible(false);
		pane.add(lbAccountCreated, 2, 6);

		class MyKeyEventHandler implements EventHandler<KeyEvent> {

			public void handle(KeyEvent e) {
				String username = tfUsername.getText();
				String password = tfPassword.getText();
				String password2 = tfPassword2.getText();
				btnCreate.setDisable(!validateUserSignup(username, password, password2));
			}
		}
		MyKeyEventHandler keyHandler = new MyKeyEventHandler();
		tfUsername.setOnKeyTyped(keyHandler);
		tfPassword.setOnKeyTyped(keyHandler);
		tfPassword2.setOnKeyTyped(keyHandler);

		btnCreate.setOnAction(e -> {
			for (int i = 0; i < DataCenter.getInstance().userList.size(); i++) {
				if (DataCenter.getInstance().userList.get(i).getUsername().equalsIgnoreCase(tfUsername.getText())) {
					if (lbAccountCreated.isVisible()) {
						lbAccountCreated.setVisible(false);
					}
					lbInvalidUsername.setVisible(true);
					break;
				}
				if (i == DataCenter.getInstance().userList.size() - 1) {
					DataCenter.getInstance().userList.add(new User(tfUsername.getText(), tfPassword.getText()));
					if (lbInvalidUsername.isVisible()) {
						lbInvalidUsername.setVisible(false);
					}
					lbAccountCreated.setVisible(true);
					btnCreate.setVisible(false);
					btnReturn.setVisible(true);
					DataCenter.getInstance().saveUserList();
					break;
				}
			}
		});
		
		btnReturn.setOnAction(e->{ 
			staticStage.getScene().setRoot(loginGUI());
		});

		return pane;
	}

	private Pane loginGUI() {
		GridPane pane = new GridPane();
		pane.setStyle("-fx-background-color:rgba(2, 48, 32, .7)");
		pane.setAlignment(Pos.CENTER);
		pane.setPadding(new Insets(20));
		pane.setHgap(10);
		pane.setVgap(10);

		Label lbError = new Label("The login credentials do not match any saved accounts. Please try again.");
		lbError.setPadding(new Insets(-20));
		lbError.setTextFill(Color.DARKRED);
		lbError.setVisible(false);

		Label lbUsername = new Label("Username");
		Label lbPassword = new Label("Password");
		GridPane.setHalignment(lbError, HPos.LEFT);
		GridPane.setHalignment(lbUsername, HPos.CENTER);
		GridPane.setHalignment(lbPassword, HPos.CENTER);
		lbUsername.setTextFill(Color.FLORALWHITE);
		lbPassword.setTextFill(Color.FLORALWHITE);

		TextField tfUsername = new TextField();
		PasswordField tfPassword = new PasswordField();

		pane.add(lbError, 0, 0, 10, 1);
		pane.add(lbUsername, 2, 1, 1, 1);
		pane.add(lbPassword, 2, 2, 1, 1);
		pane.add(tfUsername, 3, 1, 5, 1);
		pane.add(tfPassword, 3, 2, 5, 1);

		HBox hboxButtons = new HBox();
		hboxButtons.setAlignment(Pos.CENTER);
		hboxButtons.setSpacing(10);
		Button btnLogin = new Button("Login");
		btnLogin.setTextFill(Color.DARKGREEN);
		btnLogin.setStyle("-fx-background-color:rgba(100, 255, 100, 0.8);");
		btnLogin.setPrefWidth(100);
		btnLogin.setPadding(new Insets(5));
		btnLogin.setAlignment(Pos.CENTER);


		hboxButtons.getChildren().addAll(btnLogin);
		pane.add(hboxButtons, 3, 3, 5, 1);

		HBox hboxSignup = new HBox();
		hboxSignup.setAlignment(Pos.CENTER);
		hboxSignup.setSpacing(10);

		Label lbSignUp = new Label("New here?");
		lbSignUp.setTextFill(Color.FLORALWHITE);
		GridPane.setHalignment(lbSignUp, HPos.CENTER);
		Button btnSignUp = new Button("Sign Up");
		btnSignUp.setAlignment(Pos.CENTER_LEFT);
		btnSignUp.setStyle("-fx-background-color: transparent");
		btnSignUp.setTextFill(Color.LIGHTSKYBLUE);
		btnSignUp.setUnderline(true);
		btnSignUp.setPadding(new Insets(-5));

		hboxSignup.getChildren().addAll(lbSignUp, btnSignUp);
		pane.add(hboxSignup, 3, 4, 5, 1);

		btnLogin.setDisable(true);

		class MyKeyEventHandler implements EventHandler<KeyEvent> {

			public void handle(KeyEvent e) {
				String username = tfUsername.getText();
				String password = tfPassword.getText();
				btnLogin.setDisable(!validateUserLogin(username, password));
			}
		}
		MyKeyEventHandler keyHandler = new MyKeyEventHandler();
		tfUsername.setOnKeyTyped(keyHandler);
		tfPassword.setOnKeyTyped(keyHandler);

		btnLogin.setOnAction(e -> {
			String username = tfUsername.getText();
			String password = tfPassword.getText();
			User user = new User(username, password);
			if (DataCenter.getInstance().findUser(user)) {
				staticStage.getScene().setRoot(accountHome(DataCenter.getInstance().userList.get(DataCenter.getInstance().userIdx(user))));
			} else {
				lbError.setVisible(true);
			}

		});

		btnSignUp.setOnAction(e -> {
			staticStage.getScene().setRoot(signupGUI());
		});

		return pane;
	}

	private boolean validateUserLogin(String username, String password) {
		return username.length() >= 4 && password.length() >= 6;
	}

	private boolean validateUserSignup(String username, String password1, String password2) {
		return username.length() >= 4 && password1.length() >= 6 && password1.equals(password2);
	}

	


	public long getPeriod(int y, int m, int d) {
		Calendar cal = Calendar.getInstance(); // Singleton class -> Dont need more than one calendar
		cal.set(y, m - 1, d);
		Date date = cal.getTime();
		return date.getTime() / 1000; // Nanoseconds / 1000 = milliseconds

	}

	public String getURL(String stockSymbol, long period1, long period2) {
		// %s, %d, %d - > Like printf, replace the stock symbol and periods with the %s
		String urlTemplate = "https://query1.finance.yahoo.com/v7/finance/download/%s?period1=%d&period2=%d&interval=1d&events=history&includeAdjustedClose=true";
		String urlStr = String.format(urlTemplate, stockSymbol, period1, period2);

		return urlStr;
	}
	
	public String encrypt(String pw) {
		char[] pwEncrypt = pw.toCharArray();
		for (int i = 1; i < pwEncrypt.length; i++) {
			pwEncrypt[i] = '*';
		}
		return new String(pwEncrypt);
	}

	public static void main(String[] args) {
		launch(args);
	}

}
