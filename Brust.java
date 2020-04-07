public abstract class Brust {
	private int remainingTime;

	public Brust(int time) {
		this.remainingTime = time;
	}
	public int getRemainingTime() {
		return this.remainingTime;
	}
	public int decrementRemainingTime() {
		return --this.remainingTime;
	}


}
