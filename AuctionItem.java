/*
 * EE422C Final Project submission by
 * Alexander Liu
 * al47563
 * 16320
 * Spring 2020
 */

package assignment7;

public class AuctionItem {
	private String name;
	private double currentBid;
	private double startingBid;
	private double buyNow;
	private String highestBidder;
	private boolean onSale;
	
	public AuctionItem() {
		name = "item";
		currentBid = 5;
		startingBid = 5;
		highestBidder = null;
		onSale = true;
	}
	
	public AuctionItem(String name, double startingBid, double buyNow) {
		this.name = name;
		this.currentBid = startingBid;
		this.startingBid = startingBid;
		this.buyNow = buyNow;
	}
	
	public synchronized void placeBid(double newBid) {
		if(currentBid > newBid) return;
		currentBid = newBid;
	}
	
	public double getCurrentBid() {
		return currentBid;
	}
	
	public String toString() {
		return name + ": now at " + Double.toString(currentBid) + " from " + Double.toString(startingBid);
	}
}
