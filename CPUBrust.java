public class CPUBrust extends Brust {
	private int memoryValue;

	public CPUBrust(int time, int memoryValue) {
		super(time);
		this.memoryValue = memoryValue;
	}

	public int getMemoryValue() { return memoryValue; }
}


