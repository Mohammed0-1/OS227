public abstract class Brust {
	private int remainingTime;
	private String type;

	public Brust(int time) {
		this.remainingTime = time;
	}
	public void setType(String type) {
		this.type=type;
	}

	public String getType() {
		return type;
	}
	public int getRemainingTime() {
		return this.remainingTime;
	}
	public int decrementRemainingTime() {
		return --this.remainingTime;
	}


}
