public class CPUBrust extends Brust {
	private int memoryValue;

	public CPUBrust(int time, int memoryValue) {
		super(time);
		this.memoryValue = memoryValue;
		this.setType("CPU");
	}

	public int getMemoryValue() { return memoryValue; }
}


