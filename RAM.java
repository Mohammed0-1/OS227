
public class RAM {
private final int size = 1000-320;
private int ramInUse;
private LinkedQueue<Process> jobQ;
private PQKImp<Integer,Process> readyQ;
private LinkedQueue<Process> waitingProcesses;
public RAM() {
	this.ramInUse=0;
	this.jobQ = new LinkedQueue<Process>();
	this.readyQ = new PQKImp<Integer,Process>();
	this.waitingProcesses = new LinkedQueue<Process>();
}
public void addToJobQ(Process p) {
	jobQ.enqueue(p);
}
public Process serveJobQ() {
	return jobQ.serve();
}
public void addToReadyQ(Process p, Clock c) {
	
	if (p!=null) {
		if (ramIsEnough() && p.getBrusts().peek().data instanceof CPUBrust) {
			if (ramInUse + ((CPUBrust) p.getBrusts().peek().data).getMemoryValue() < .85 * size) {
				readyQ.enqueue(((CPUBrust) p.getBrusts().peek().data).getMemoryValue(), p);
				p.setReadyQueueTime(c.currentTime);
			} else {
				addToWaiting(p);
			}
		} 
	}
}
public void addToWaiting(Process p) {
	waitingProcesses.enqueue(p);
	p.incrementMemoryWaits();
	p.setState(STATE.waiting);
}
public void freeRAM(Process p) {
	ramInUse-= p.getSize();
}
private boolean ramIsEnough() {
	return ramInUse<.85*size;
}
}
