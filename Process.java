
public class Process {
	private int PID;
	private String name;
	private int CPUuses, IOuses, memoryWaits,numberOfPreemptions;
	private State state;
	private int size;
	private int readyQueueTime,CPUtime,IOtime;
	private int startTime,terminationTime;
	private LinkedQueue<Brust> brusts; 
}
