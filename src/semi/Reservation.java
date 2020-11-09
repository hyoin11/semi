package semi;

public class Reservation {
	private String date;
	private String seat;
	private String price;
	
	public Reservation() {
		
	}
	
	public Reservation(String date, String seat, String price) {
		this.date = date;
		this.seat = seat;
		this.price = price;
	}
	
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getSeat() {
		return seat;
	}
	public void setSeat(String seat) {
		this.seat = seat;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}

	@Override
	public String toString() {
		return date + "/" + seat + "/" + price;
	}

}
