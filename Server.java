/*
 * EE422C Final Project submission by
 * Alexander Liu
 * al47563
 * 16320
 * Spring 2020
 */

package assignment7;

import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;

import com.google.gson.Gson;

class Server extends Observable {
	
	public Map<String, AuctionItem> auctionItems;

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
		      
			System.out.println("\nInventory: ");
			for(Map.Entry<String, AuctionItem> item: auctionItems.entrySet()) {
				System.out.println(item.getValue());
			}
			System.out.println();

			ClientHandler handler = new ClientHandler(this, clientSocket);
			this.addObserver(handler);

			Thread t = new Thread(handler);
			t.start();
		}
	}

	protected void processRequest(String input) {
		String output = "Error";
		Gson gson = new Gson();
		Message message = gson.fromJson(input, Message.class);
		try {
			String temp = "";
			switch (message.type) {
			case "upper":
				temp = message.input.toUpperCase();
				break;
			case "lower":
				temp = message.input.toLowerCase();
				break;
			case "strip":
				temp = message.input.replace(" ", "");
				break;
			}
			output = "";
			for (int i = 0; i < message.number; i++) {
				output += temp;
				output += " ";
			}
			this.setChanged();
			this.notifyObservers(output);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}