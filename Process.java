
public class Process {
	private int PID;
	private String name;
	private int CPUuses, IOuses, memoryWaits,numberOfPreemptions;
	private STATE state;
	private int size;
	private Brust currentB;
	private int readyQueueTime,CPUtime,IOtime;
	private int startTime,terminationTime;
	private LinkedQueue<Brust> brusts;
	private int arrivalTime;
	public Process(int PID, String name,int start) {
		this.PID = PID;
		this.name = name;
		this.startTime = start;
		this.arrivalTime = (int)(1+Math.random()*81);
		state = STATE.waiting;
		this.CPUuses=this.IOuses=this.memoryWaits=this.numberOfPreemptions=0;
		readyQueueTime = -1;
		CPUtime = IOtime = 0;
		brusts = new LinkedQueue<Brust>();
		for(int i =0;i<5;i++) {
			if(i%2==0) {
				int CPUbrust = (int)(10+Math.random()*101);
				int ramUsage = (int)(5+Math.random()*201);
				brusts.enqueue(new CPUBrust(CPUbrust,ramUsage));
			}else {
				int IObrust = (int)(20+Math.random()*61);
				brusts.enqueue(new IOBrust(IObrust));
			}
		}
		brusts.enqueue(new CPUBrust(-1,0));
		currentB = brusts.serve();
	}
	public void killProcess(Clock c) {
		this.state = STATE.killed;
		this.terminationTime = c.currentTime;
		
	}
	public void terminateProcess(Clock c) {
		this.state = STATE.terminated;
		this.terminationTime = c.currentTime;
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
	public void incrementIOuses() {
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
	public void setIOtime(int iOtime) {
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
	public String getName() {
		return name;
	}
	public int getSize() {
		return size;
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
	public LinkedQueue<Brust> getBrusts() {
		return brusts;
	} 
	public Brust getCurrentB() {
		return currentB;
	}
	public double CPUUtilization() {
		return CPUtime/(terminationTime-startTime);
	}
	public void nextBrust() {
		currentB = brusts.serve();
	}
}
