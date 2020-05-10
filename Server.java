/*
 * EE422C Final Project submission by
 * Alexander Liu
 * al47563
 * 16320
 * Spring 2020
 */

package assignment7;

import java.lang.reflect.Type;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

class Server extends Observable {
	
	private Gson g = new Gson();
	public Map<String, AuctionItem> auctionItems;
	private String log = "";

	public static void main(String[] args) {
		new Server().runServer();
	}

	private void runServer() {
		auctionItems = new HashMap<>();
		auctionItems.put("Mona Lizard", new AuctionItem("Mona Lizard", 99.99, 1000));
		auctionItems.put("First Supper", new AuctionItem("First Supper", 200, 550));
		auctionItems.put("Spaghetti", new AuctionItem("Spaghetti", 1000, 1000));
		auctionItems.put("Starry nope", new AuctionItem("Starry nope", 50000, 500000));
		auctionItems.put("lolwhat", new AuctionItem("lolwhat", 1234.56, 2345.67));
	  
		try {
			setUpNetworking();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

	private void setUpNetworking() throws Exception {
		@SuppressWarnings("resource")
		ServerSocket serverSock = new ServerSocket(4242);
		while (true) {
			Socket clientSocket = serverSock.accept();
			System.out.println("Connecting to... " + clientSocket);
			
			Date dNow = new Date( );
		    SimpleDateFormat ft = new SimpleDateFormat ("E yyyy.MM.dd 'at' hh:mm:ss a zzz");
		    System.out.println("Current Date: " + ft.format(dNow));	
			System.out.println();

			ClientHandler handler = new ClientHandler(this, clientSocket);
		    String json = g.toJson(auctionItems);
			handler.sendToClient("0" + json);
			this.addObserver(handler);

			Thread t = new Thread(handler);
			t.start();
		}
	}

	protected void processRequest(String input) {
		if(input.charAt(0) == '0') {
			String sec = input.substring(1);
			Type aitype = new TypeToken<HashMap<String, AuctionItem>>(){}.getType();
			auctionItems = g.fromJson(sec, aitype);
			this.setChanged();
			this.notifyObservers(input);
		}
		else if(input.charAt(0) == '1') {
			String sec = input.substring(1);
			log += sec + "&";
			this.setChanged();
			this.notifyObservers("1" + log);
		}
	}
}