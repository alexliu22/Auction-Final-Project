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
		this.highestBidder = "";
		this.onSale = true;
	}
	
	public void placeBid(double newBid) {
		if(currentBid > newBid || !onSale) return;
		currentBid = newBid;
	}
	
	public void bought() {
		onSale = false;
	}
	
	public double getCurrentBid() {
		return currentBid;
	}
	
	public String getName() {
		return name;
	}
	
	public double getStartingBid() {
		return startingBid;
	}
	
	public boolean getOnSale() {
		return onSale;
	}
	
	public double getBuyNow() {
		return buyNow;
	}
	
	public String toString() {
		return name + ": now at " + Double.toString(currentBid) + " from " + Double.toString(startingBid);
	}
}
