/*
 * EE422C Final Project submission by
 * Alexander Liu
 * al47563
 * 16320
 * Spring 2020
 */

package assignment7;

import java.util.Scanner;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Client extends Application{

	private static String host = "127.0.0.1";
	private BufferedReader fromServer;
	private PrintWriter toServer;
	private Scanner consoleInput = new Scanner(System.in);
	private String clientName;

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
						System.out.println("From server: " + input);
						processRequest(input);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		Thread writerThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					// This part takes input from console and sends message to the server
          			String input = consoleInput.nextLine();
          			String[] variables = input.split(",");
          			Message request = new Message(variables[0], variables[1], Integer.valueOf(variables[2]));
          			GsonBuilder builder = new GsonBuilder();
          			Gson gson = builder.create();
          			sendToServer(gson.toJson(request));
          			
				}
			}
		});

		readerThread.start();
		writerThread.start();
	}

	protected void processRequest(String input) {
		return;
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
	    mid.getChildren().add(u);
	    
	    TextField user = new TextField();
	    user.setPrefWidth(400);
	    user.setMaxWidth(400);
	    mid.getChildren().add(user);
	    
	    Button bt = new Button("let's start bidding!");
	    mid.getChildren().add(bt);
	    bt.setOnAction(new EventHandler<ActionEvent>() {   	 
            @Override
            public void handle(ActionEvent event) {
            	clientName = user.getText();
            	secondStage.close();
            	showPrimary(primaryStage);
            }
        });
	    
	    mid.setPadding(new Insets(50, 30, 30, 50));
	    mid.setSpacing(30);
	    bp.setCenter(mid);
	    
	    // left and right testing
	    FileInputStream inputstream;
	    Image image = null;
		try {
			inputstream = new FileInputStream("C:\\Users\\theal\\Documents\\EE422C\\money-with-wings.png");
			image = new Image(inputstream); 
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
		ImageView img = new ImageView(image);
		bp.setLeft(img);
		ImageView img2 = new ImageView(image);
		bp.setRight(img2);
	    
	    // footer
	    HBox foot = new HBox();
	    foot.setPadding(new Insets(0, 30, 10, 30));
	    Text footer = new Text("*all items on here are illegal lmfao");
	    foot.getChildren().add(footer);
	    bp.setBottom(foot);
	    
	    // set scene and show stage
	    bp.setPrefSize(700, 500);
	    bp.setBackground(new Background(new BackgroundFill(Color.DARKSEAGREEN, CornerRadii.EMPTY, Insets.EMPTY)));
	    secondStage.setScene(new Scene(bp));
	    secondStage.show(); 
	}
	
	public void showPrimary(Stage primaryStage) {
		BorderPane bPane = new BorderPane();   
	       
	    //Setting the top, bottom, center, right and left nodes to the pane 

	    bPane.setBottom(new TextField("Bottom")); 
	    bPane.setLeft(new TextField("Left")); 
	    bPane.setRight(new TextField("Right")); 
	    bPane.setCenter(new TextField("Center")); 	    
	    bPane.setPrefSize(1500, 900); 
	    
	    // Set Top node
	    HBox h = new HBox();
	    Text topText = new Text("welcome to the auction, " + clientName + "!");
	    topText.setFont(new Font(50));
	    h.setPadding(new Insets(30, 10, 10, 30));
	    h.setAlignment(Pos.CENTER);
	    h.getChildren().add(topText);
	    bPane.setTop(h); 
	    
	    // Set Left node
	    VBox left = new VBox();
	    MenuBar itemSelect = new MenuBar();
	    Menu items = new Menu("Select an item to bid on");
	    MenuItem test = new MenuItem("lol");
	    MenuItem test2 = new MenuItem("wut");
	    items.getItems().addAll(test, test2);
	    itemSelect.getMenus().add(items);
	    left.getChildren().add(itemSelect);
	    
	    Text desc = new Text("Enter bid amount (in USD): ");
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
            		tf.setText(Double.toString(amt + 10.2));
            	}
            	catch(Exception e) {
            		System.out.println("Invalid amount");
            	}
            }
        });
	    left.getChildren().add(buy);
	    
	    left.setPadding(new Insets(55, 30, 40, 30));
	    left.setSpacing(30);

	    bPane.setLeft(left);
	    
	    // Set Center node
	    VBox cent = new VBox();
	    Text inv = new Text("Available items: ");
	    cent.getChildren().add(inv);
	    
	    cent.setPadding(new Insets(55, 30, 30, 90));
	    cent.setSpacing(30);
	    
	    bPane.setCenter(cent);
	    
	    // Set Right node
	    // Set Bottom node
	    
	    // Scene stuff
	    bPane.setBackground(new Background(new BackgroundFill(Color.ANTIQUEWHITE, CornerRadii.EMPTY, Insets.EMPTY)));
	    Scene scene = new Scene(bPane);  
	    primaryStage.setTitle("yo i can't even begin to describe how lit this auction house is"); 
	    primaryStage.setScene(scene);  
	    primaryStage.show();
	}
	
	public void updateItems() {
		
	}

}