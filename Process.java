import DataStructures.LinkedQueue;

public class Process {

	private int PID;
	private int size;
	private int arrivalTime;
//	private int totalTime;
	private int startTime, terminationTime;
	private int readyQueueTime, CPUtime, IOtime; // the time spent there
	private int CPUUses, IOUses, memoryWaits, numberOfPreemptions;
	private Burst currentBurst;
	private STATE state;
	private LinkedQueue<Burst> bursts;

	public Process(int PID, int arrivalTime, int processSize,/* int totalTime,*/ LinkedQueue<Burst> bursts) {
		this.PID = PID;
		this.size = processSize;
		this.arrivalTime = arrivalTime;
		this.startTime = -1;
//		this.totalTime = totalTime;
		this.readyQueueTime = -1;
		this.CPUtime = 0;
		this.IOtime = 0;
		this.CPUUses = 0;
		this.IOUses = 0;
		this.memoryWaits = 0;
		this.numberOfPreemptions = 0;
		this.bursts = bursts;
		this.state = STATE.waiting;
		this.currentBurst = this.bursts.serve(); // serve or peak??
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

	public void incrementCPUUses() {
		CPUUses++;
	}

	public void incrementIOUses() { // number of times it performing IO
		IOUses++;
	}

	public void incrementMemoryWaits() {
		this.memoryWaits++;
	}

	public void incrementNumberOfPreemptions() {
		this.numberOfPreemptions++;
	}

	public void incrmentCPUtime() {
		CPUtime++;
	}

	public void incrementIOtime() {
		IOtime++;
	}
	
//	public void decrementTotalTime() {  // dec process remaining time including burst time
//		this.totalTime--;
//		this.bursts.peek().decrementRemainingTime();
//	}

	public void addTosize(int s) {
		size += s;
	}

	public double CPUUtilization() {
		return CPUtime / (terminationTime - startTime);
	}

	public Burst nextBurst() {
		return currentBurst = bursts.serve();
	}

//	Getters and Setters -----------------------------------

	// Getters
	public int getCPUUses() {
		return CPUUses;
	}

	public int getIOUses() {
		return IOUses;
	}

	public int getMemoryWaits() {
		return memoryWaits;
	}

	public int getNumberOfPreemptions() {
		return numberOfPreemptions;
	}

	public STATE getState() {
		return state;
	}

	public int getCPUtime() {
		return CPUtime;
	}

	public int getIOtime() {
		return IOtime;
	}

	public int getTerminationTime() {
		return terminationTime;
	}

	public int getPID() {
		return PID;
	}

	public int getSize() {
		return size;
	}

	public int getReadyQueueTime() {
		return readyQueueTime;
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

//	public int getTotalTime() {
//		return totalTime;
//	}

	// Setters
	public void setTerminationTime(int terminationTime) {
		this.terminationTime = terminationTime;
	}

	public void setState(STATE state) {
		this.state = state;
	}

	public void setReadyQueueTime(int t) {
		this.readyQueueTime = t;
	}

	public void setStartTime(int startTime) {
		this.startTime = startTime;
	}

	public void setCPUtime(int cPUtime) {
		CPUtime = cPUtime;
	}

	public void setIOtime(int iOtime) {
		IOtime = iOtime;
	}

	public void setCPUUses(int CPUUses) {
		CPUUses = CPUUses;
	}

	public void setIOUses(int IOUses) {
		IOUses = IOUses;
	}

	public void setMemoryWaits(int memoryWaits) {
		this.memoryWaits = memoryWaits;
	}

	public void setNumberOfPreemptions(int numberOfPreemptions) {
		this.numberOfPreemptions = numberOfPreemptions;
	}
}
