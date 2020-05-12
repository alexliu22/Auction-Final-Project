/*
 * EE422C Final Project submission by
 * Alexander Liu
 * al47563
 * 16320
 * Spring 2020
 */

package final_exam;

import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class Server extends Observable{
	
	private Gson g = new Gson();
	public Map<String, AuctionItem> auctionItems = new HashMap<>();
	private String log = "";
	private Object lock = new Object();

	public static void main(String[] args) {
		new Server().runServer();
	}

	private void runServer() {
		Type aitype = new TypeToken<HashMap<String, AuctionItem>>(){}.getType();
		synchronized(lock) {
			try {
				InputStream in = getClass().getResourceAsStream("/final_exam/test1.json");
				auctionItems = g.fromJson(new InputStreamReader(in), aitype);
			} catch (Exception e1) {
				System.out.println("File not found");
			}
		}
	  
		synchronized(lock) {
			try {
				setUpNetworking();
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
		}
	}

	private void setUpNetworking() throws Exception {
		@SuppressWarnings("resource")
		ServerSocket serverSock = new ServerSocket(4242);
		
		Thread timeHandler = new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					for(Map.Entry<String, AuctionItem> item: auctionItems.entrySet()) {
						int time = item.getValue().getTimeLeft();
						if(time == 0)
							continue;
						
						item.getValue().decTime();
						if(item.getValue().getTimeLeft() == 0) {
							item.getValue().bought();
							String sec;
							
							if(item.getValue().getHighestBidder() == null || item.getValue().getHighestBidder().equals("")) 
								sec = "Time's up! Nobody bid on " + item.getValue().getName();
							else
								sec = item.getValue().getName() + " has been sold to " + 
								      item.getValue().getHighestBidder() + " for " +
									  "$" + String.format("%.2f", item.getValue().getCurrentBid()) + "!!!";
							
							log += sec + "&";	            			
	            			setChangedR();
	            			notifyObserversR("1" + log);
						}
							
					}
					String ai = g.toJson(auctionItems);
        			ai = "0" + ai;
        			setChangedR();
            		notifyObserversR(ai);
				}
			}
		});
		timeHandler.start();
		
		while (true) {
			Socket clientSocket = serverSock.accept();
			System.out.println("Connecting to... " + clientSocket);
			
			ClientHandler handler = new ClientHandler(this, clientSocket);
		    String json = g.toJson(auctionItems);
			handler.sendToClient("0" + json);
			handler.sendToClient("1" + log);
			this.addObserver(handler);

			Thread t = new Thread(handler);
			t.start();
		}
	}

	protected void notifyObserversR(String sec) {
		this.notifyObservers(sec);
	}
	
	protected void setChangedR() {
		this.setChanged();
	}

	protected synchronized void processRequest(String input) {
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