import DataStructures.LinkedQueue;

public class Process {
	private int PID;
	private int size;
	private int arrivalTime, startTime, terminationTime, totalTime;
	private int readyQueueTime, CPUtime, IOtime;
	private int CPUuses, IOuses, memoryWaits, numberOfPreemptions;
	private Burst currentBurst;
	private STATE state;
	private LinkedQueue<Burst> bursts;

	public Process(int PID, int arrivalTime, int processSize, int totalTime, LinkedQueue<Burst> bursts) {
		this.PID = PID;
		this.size = processSize;
		this.arrivalTime = arrivalTime;
		this.startTime = -1;
		this.totalTime = totalTime;
		this.readyQueueTime = -1;
		this.CPUtime = 0;
		this.IOtime = 0;
		this.CPUuses = 0;
		this.IOuses = 0;
		this.memoryWaits = 0;
		this.numberOfPreemptions = 0;
		this.currentBurst = this.bursts.serve(); // serve or peak??
		this.state = STATE.waiting;
		this.bursts = bursts;
	}

	// Kills a process when the system is in deadlock.
	// sets it termination time.
	// sets the process state to KILLED.
	public void killProcess() {
		this.state = STATE.killed;
		setTerminationTime(Clock.currentTime);

	}

	// terminates a process when it finishes execution.
	// sets it termination time.
	// sets the process state to TERMINATED.
	public void terminateProcess() {
		this.state = STATE.terminated;
		setTerminationTime(Clock.currentTime);
	}

	public int getCPUuses() {
		return CPUuses;
	}

	public void incrementCPUuses() {
		CPUuses++;
	}

	public int getIOuses() {
		return IOuses;
	}

	public void incrementIOuses() { // number of times it performing IO
		IOuses++;
	}

	public int getMemoryWaits() {
		return memoryWaits;
	}

	public void incrementMemoryWaits() {
		this.memoryWaits++;
	}

	public int getNumberOfPreemptions() {
		return numberOfPreemptions;
	}

	public void incrementNumberOfPreemptions() {
		this.numberOfPreemptions++;
	}

	public STATE getState() {
		return state;
	}

	public void setState(STATE state) {
		this.state = state;
	}

	public int getCPUtime() {
		return CPUtime;
	}

	public void incrmentCPUtime() {
		CPUtime++;
	}

	public int getIOtime() {
		return IOtime;
	}

	public void incrementIOtime() {
		IOtime++;
	}

	public int getTerminationTime() {
		return terminationTime;
	}

	public void setTerminationTime(int terminationTime) {
		this.terminationTime = terminationTime;
	}

	public int getPID() {
		return PID;
	}

	public int getSize() {
		return size;
	}

	public void addTosize(int s) {
		size += s;
	}

	public int getReadyQueueTime() {
		return readyQueueTime;
	}

	public void setReadyQueueTime(int t) {
		this.readyQueueTime = t;
	}

	public int getStartTime() {
		return startTime;
	}

	public LinkedQueue<Burst> getBursts() {
		return bursts;
	}

	public Burst getCurrentBurst() {
		return currentBurst;
	}
	
	public int getArrivalTime() {
		return arrivalTime;
	}
	
	public int getTotalTime() {
		return totalTime;
	}

	public double CPUUtilization() {
		return CPUtime / (terminationTime - startTime);
	}

	public void nextBurst() {
		currentBurst = bursts.serve();
	}
}
