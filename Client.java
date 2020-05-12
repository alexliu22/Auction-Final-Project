/*
 * EE422C Final Project submission by
 * Alexander Liu
 * al47563
 * 16320
 * Spring 2020
 */

package final_exam;

import java.util.HashMap;
import java.util.Map;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.UnsupportedAudioFileException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.net.Socket;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Client extends Application{

	private static String host = "127.0.0.1";
	private BufferedReader fromServer;
	private PrintWriter toServer;

	private Gson g = new Gson();
	private Map<String, AuctionItem> auctionItems;
	private String clientName;
	private Double money;
	private Map<String, Double> amtBid = new HashMap<>();
	private Double newScroll = 0.0;
	private Double logScroll = 0.0;
	private String userLog = "";
	private boolean allLogs = true;
	
	private Text bottomText = new Text();
	private String selectedItem;
    private String auctionLog = "";
    private TextArea ta = new TextArea();
    private String taString;
    private TextArea log = new TextArea();
    private String logString = "";
    private Text moneyLeft = new Text();
    private Object lock = new Object();

	public static void main(String[] args) {
		launch(args);
	}
	
	private void setUpNetworking() throws Exception {
		@SuppressWarnings("resource")
		Socket socket = new Socket(host, 4242);
		System.out.println("Connecting to... " + socket);
		fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		toServer = new PrintWriter(socket.getOutputStream());

		Thread readerThread = new Thread(new Runnable() {
			@Override
			public void run() {
				String input;
				try {
					// This while loop reads what the server sends
					while ((input = fromServer.readLine()) != null) {
						if(input.charAt(0) == '0') {
							newScroll = ta.getScrollTop();
							Type aitype = new TypeToken<HashMap<String, AuctionItem>>(){}.getType();
							String sec = input.substring(1);
							auctionItems = g.fromJson(sec, aitype);
							updateItems();
						}
						else if(input.charAt(0) == '1') {
							logScroll = log.getScrollTop();
							String sec = input.substring(1);
							auctionLog = sec;
							if(allLogs)
								updateLog(sec);
							else
								personalLog();
						}
						
						System.out.println("From server: " + input);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		readerThread.start();
	}

	protected void sendToServer(String string) {
		System.out.println("Sending to server: " + string);
		toServer.println(string);
		toServer.flush();
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// Set up networking first
		try {
			this.setUpNetworking();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// Login landing page
	    Stage secondStage = new Stage();
	    BorderPane bp = new BorderPane();
	    
	    // top
	    HBox t = new HBox();
	    Text greeting = new Text("welcome to the auction house!");
	    greeting.setFont(new Font(50));
	    t.setPadding(new Insets(30, 30, 10, 60));
	    t.setAlignment(Pos.CENTER);
	    t.getChildren().add(greeting);
	    bp.setTop(t);
	    
	    // middle
	    VBox mid = new VBox();
	    Text intro = new Text("we are an auction house with stolen paintings from across the world!*");
	    intro.setFont(new Font(30));
	    mid.getChildren().add(intro);
	    
	    Text u = new Text("enter username here: ");
	    u.setFont(new Font(20));
	    mid.getChildren().add(u);
	    
	    TextField user = new TextField();
	    user.setPrefWidth(400);
	    user.setMaxWidth(400);
	    mid.getChildren().add(user);
	    
	    Text m = new Text("enter how much money you are bringing (in $): ");
	    m.setFont(new Font(20));
	    mid.getChildren().add(m);
	    
	    TextField dolla = new TextField();
	    dolla.setMaxWidth(400);
	    dolla.setPrefHeight(400);
	    mid.getChildren().add(dolla);
	    
	    Button bt = new Button("let's start bidding!");
	    mid.getChildren().add(bt);
	    bt.setOnAction(new EventHandler<ActionEvent>() {   	 
            @Override
            public void handle(ActionEvent event) {
            	try {
            		Double moneyyy = Double.parseDouble(dolla.getText());
            		money = moneyyy;
            	}
            	catch(Exception e) {
            		errorInvMoney();
            		return;
            	}
            	
            	clientName = user.getText();
            	
            	secondStage.close();
            	showPrimary(primaryStage);
            }
        });
	    
	    mid.setPadding(new Insets(50, 30, 30, 50));
	    mid.setSpacing(30);
	    bp.setCenter(mid);
	    
	    // left and right images lol
	    Image image = new Image("https://s3.amazonaws.com/pix.iemoji.com/images/emoji/apple/ios-12/256/money-with-wings.png");
		ImageView img = new ImageView(image);
		bp.setLeft(img);
		ImageView img2 = new ImageView(image);
		bp.setRight(img2);
	    
	    // footer
	    HBox foot = new HBox();
	    foot.setPadding(new Insets(0, 30, 10, 30));
	    Text footer = new Text("*all items on here are illegal lmao");
	    foot.getChildren().add(footer);
	    bp.setBottom(foot);
	    
	    // set scene and show stage
	    bp.setPrefSize(700, 500);
	    bp.setBackground(new Background(new BackgroundFill(Color.DARKSEAGREEN, CornerRadii.EMPTY, Insets.EMPTY)));
	    secondStage.setScene(new Scene(bp));
	    secondStage.show(); 
	}
	
	public void showPrimary(Stage primaryStage) {
		try {
			//InputStream in = getClass().getResourceAsStream("/assignment7/Demicheli.wav"); 
			AudioInputStream ais = AudioSystem.getAudioInputStream(new File("Demicheli.wav"));
			Clip clip = AudioSystem.getClip();
			clip.open(ais);
			clip.start();
		} catch (Exception e) {
			System.out.println("File not found");
		}
		
		for(Map.Entry<String, AuctionItem> item: auctionItems.entrySet()) {
			amtBid.put(item.getKey(), 0.0);
		}
					
		BorderPane bPane = new BorderPane();    	    
	    
	    // Set Top node		
	    HBox h = new HBox();
	    Text topText = new Text("Welcome to the auction, " + clientName + "!");
	    topText.setFont(new Font(50));
	    h.setPadding(new Insets(30, 10, 10, 30));
	    h.setAlignment(Pos.CENTER);
	    h.getChildren().add(topText);
	    
	    bPane.setTop(h); 
	    
	    // Set Left node
	    VBox left = new VBox();
	    
	    // Make dropdown menu
	    moneyLeft.setText("You have $" + String.format("%.2f", money) + " left.");
	    left.getChildren().add(moneyLeft);
	    
	    ComboBox cb = new ComboBox();
	    for(Map.Entry<String, AuctionItem> item: auctionItems.entrySet()) {
			cb.getItems().add(item.getKey());
		}
	    cb.setPromptText("Select an item to bid on");
	    left.getChildren().add(cb);
	    
	    Text desc = new Text("Enter bid amount (in $): ");
	    left.getChildren().add(desc);
	    
	    TextField tf = new TextField();
	    left.getChildren().add(tf);
	    
	    Button buy = new Button("Place bid");
	    buy.setOnAction(new EventHandler<ActionEvent>() {   	 
            @Override
            public void handle(ActionEvent event) {      	
            	
            	double amt = 0;
            	try {
            		amt = Double.parseDouble(tf.getText());
            	}
            	catch(Exception e) {
            		errorInvAmount();
            		return;
            	}
            	
            	String it = (String) cb.getValue();
            	if(!auctionItems.get(it).getOnSale()) {
            		errorSoldOut(); 
            		return;
            	}
            	
            	if(!auctionItems.containsKey(it)) {
            		errorSoldOut();
            		return;
            	}
            	else {
            		if(amt > auctionItems.get(it).getBuyNow())
            			errorTooMuch();
            		else if(amt <= auctionItems.get(it).getCurrentBid()) {
            			errorLessThanCurrent();
            		}
            		else if(money - amt < 0.009) {
            			errorNotEnough();
            		}
            		else {
        				newScroll = ta.getScrollTop();
            			
            			auctionItems.get(it).placeBid(amt, clientName);
            			double displaced = amt - amtBid.get(it);
            			
            			money -= displaced;
            			amtBid.replace(it, amt);
            			updateMoney();
            			
            			String ai = g.toJson(auctionItems);
            			ai = "0" + ai;
            			sendToServer(ai);
            			
            			userLog += "You successfully bid $" + String.format("%.2f", amt) + " on " + it + "\n";
            			String sec = clientName + " successfully bid $" + String.format("%.2f", amt) + " on " + it;
            			sec = "1" + sec;
            			
            			sendToServer(sec);
            		}
            	}
            }
        });
	    left.getChildren().add(buy);
	    
	    Button buyNow = new Button("Buy now!!!");
	    buyNow.setOnAction(new EventHandler<ActionEvent>() {   	 
            @Override
            public void handle(ActionEvent event) {
            	String it = (String) cb.getValue();
            	
            	if(!auctionItems.get(it).getOnSale()) {
            		errorSoldOut(); 
            		return;
            	}
            	
            	if(auctionItems.get(it).getBuyNow() > money) {
            		errorCantBuy();
            		return;
            	}
            	money -= auctionItems.get(it).getBuyNow();
            	updateMoney();
            	auctionItems.get(it).bought();
            	
            	String ai = g.toJson(auctionItems);
            	ai = "0" + ai;
            	sendToServer(ai);
            	
            	userLog += "You bought " + it + "!!!\n";
            	String sec = clientName + " bought " + it + "!!!";
            	sec = "1" + sec;
            	sendToServer(sec);
            }
        });
	    left.getChildren().add(buyNow);
	    
	    
	    left.setPadding(new Insets(55, 30, 40, 30));
	    left.setSpacing(30);

	    bPane.setLeft(left);
	    
	    // Set Center node
	    VBox cent = new VBox();
	    Text inv = new Text("Available items: ");
	    cent.getChildren().add(inv);
	    
	    ta.setPrefHeight(500);
	    ta.setPrefWidth(250);
	    ta.setFont(new Font(20));
	    cent.getChildren().add(ta);
	    
	    cent.setPadding(new Insets(55, 130, 30, 90));
	    cent.setSpacing(30);
	    
	    bPane.setCenter(cent);
	    
	    // Set Right node
	    VBox right = new VBox();

	    HBox choices = new HBox();
	    choices.setSpacing(20);
	    ComboBox logChoice = new ComboBox();
	    logChoice.getItems().add("All Transactions");
	    logChoice.getItems().add("Your Transactions");
	    logChoice.setPromptText("All Transactions");
	    logChoice.setValue("All Transactions");
	    
	    choices.getChildren().add(logChoice);
	    
	    Button show = new Button("Show");
	    show.setOnAction(new EventHandler<ActionEvent>() {   	 
            @Override
            public void handle(ActionEvent event) {
            	String transaction = (String) logChoice.getValue();
            	if(transaction.equals("Your Transactions")) {
            		allLogs = false;
            		personalLog();
            	}
            	else if(transaction.equals("All Transactions")) {
            		allLogs = true;
            		updateLog(auctionLog);         
            	}
            }
        });
	    choices.getChildren().add(show);
	    
	    log = new TextArea();
	    log.setPrefHeight(500);
	    log.setPrefWidth(500);
	    updateLog(auctionLog);
	    log.setFont(new Font(20));
	    
	    //right.getChildren().add(logChoice);
	    right.getChildren().add(choices);
	    right.getChildren().add(log);
	    right.setSpacing(20);
	    right.setPadding(new Insets(55, 50, 30, 10));
	    bPane.setRight(right);
	    
	    // Set Bottom node
	    HBox bot = new HBox();	
	    Text funfact = new Text("Fun fact: The Mona Lisa is essentially priceless. But you're still bidding money on it lmao.");
	    funfact.setFont(new Font(20));
	    bot.getChildren().add(funfact);
	    
	    Button exit = new Button("Exit");
	    exit.setOnAction(new EventHandler<ActionEvent>() {   	 
            @Override
            public void handle(ActionEvent event) {
            	System.exit(1);
            }
        });
	    bot.getChildren().add(exit);
	    	    
	    bot.setSpacing(600);
	    bot.setPadding(new Insets(30, 30, 30, 30));
	    bPane.setBottom(bot);
	    
	    // Scene stuff
	    bPane.setPrefSize(1500, 900); 
	    bPane.setBackground(new Background(new BackgroundFill(Color.ANTIQUEWHITE, CornerRadii.EMPTY, Insets.EMPTY)));
	    Scene scene = new Scene(bPane);  
	    primaryStage.setTitle("yo i can't even begin to describe how lit this auction house is"); 
	    primaryStage.setScene(scene);  
	    primaryStage.show();
	}
	
	private void updateItems() {
		taString = "";
		for(Map.Entry<String, AuctionItem> item: auctionItems.entrySet()) {
			String time = "";
			if(item.getValue().getTimeLeft() / 60 < 1) 
				time = item.getValue().getTimeLeft() + "s";
			else {
				int m = item.getValue().getTimeLeft() / 60;
				int s = item.getValue().getTimeLeft() % 60;
				time = m + "m " + s + "s";
			}
				
			taString += "Item: " + item.getValue().getName() + "\n" + 
						"Desc: " + item.getValue().getDesc() + "\n" + 
						"Current Bid: $" + String.format("%.2f", item.getValue().getCurrentBid()) + "\n" +
						"Buy Now Price: $" + String.format("%.2f", item.getValue().getBuyNow()) + "\n" + 
						"Highest Bidder: " + item.getValue().getHighestBidder() + "\n" +
						"Time Left: " + time + "\n" +
						"On sale: " + item.getValue().getOnSale() + "\n\n";
		}
	    ta.setText(taString);
		ta.setScrollTop(newScroll);
	}
	
	private void updateLog(String newLog) {
		String formattedLog = "";
		for(int i=0; i<newLog.length(); i++) {
			if(newLog.charAt(i) == '&')
				formattedLog += "\n";
			else
				formattedLog += newLog.charAt(i);
		}
		log.setText(formattedLog);
	}
	
	private void personalLog() {
		log.setText(userLog);
	}
	
	private void updateMoney() {
		moneyLeft.setText("You have $" + String.format("%.2f", money) + " left.");
	}
	
	public void errorSoldOut() {
		error("Sorry, this item was sold!");
	}
	
	public void errorInvAmount() {
		error("You entered an invalid bid amount");
	}
	
	public void errorTooMuch() {
		error("You bid over the buy now");
	}
	
	public void errorLessThanCurrent() {
		error("You bid less than or equal to the current bid");
	}
	
	public void errorInvMoney() {
		error("You entered an invalid money amount");
	}
	
	public void errorNotEnough() {
		error("You don't have enough money to bid");
	}
	
	public void errorCantBuy() {
		error("You don't have enough money to buy now");
	}
	
	private void error(String msg) {
		Stage s = new Stage();
		BorderPane bp = new BorderPane();
		Text t = new Text(msg);
		bp.setCenter(t);
		
		Image image = new Image("https://clip.cookdiary.net/sites/default/files/wallpaper/red-cross-mark-clipart/172360/red-cross-mark-clipart-blue-172360-8908624.jpg");
		ImageView img = new ImageView(image);
		bp.setLeft(img);
		
		bp.setPrefSize(600, 300);
		s.setTitle("Error message");
		s.setScene(new Scene(bp));
		s.show();
	}

}