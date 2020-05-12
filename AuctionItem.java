/*
 * EE422C Final Project submission by
 * Alexander Liu
 * al47563
 * 16320
 * Spring 2020
 */

package final_exam;

public class AuctionItem {
	private String name;
	private String desc;
	private double currentBid;
	private double startingBid;
	private double buyNow;
	private String highestBidder;
	private boolean onSale;
	private int timeLeft;
	
	public AuctionItem() {
		name = "item";
		desc = "";
		currentBid = 5;
		startingBid = 5;
		highestBidder = null;
		onSale = true;
		timeLeft = 60;
	}
	
	public AuctionItem(String name, String desc, double startingBid, double buyNow, int timeLeft) {
		this.name = name;
		this.desc = desc;
		this.currentBid = startingBid;
		this.startingBid = startingBid;
		this.buyNow = buyNow;
		this.highestBidder = "";
		this.onSale = true;
		this.timeLeft = timeLeft;
	}
	
	public void placeBid(double newBid, String bidder) {
		if(currentBid > newBid || !onSale) return;
		currentBid = newBid;
		highestBidder = bidder;
	}
	
	public void bought() {
		onSale = false;
	}
	
	public void decTime() {
		timeLeft--;
	}
	
	public String getDesc() {
		return desc;
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
	
	public int getTimeLeft() {
		return timeLeft;
	}
	
	public String getHighestBidder() {
		return highestBidder;
	}
	
	public String toString() {
		return name + ": now at " + Double.toString(currentBid) + " from " + Double.toString(startingBid);
	}
}
