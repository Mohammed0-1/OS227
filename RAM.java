import java.util.*;
import java.util.concurrent.PriorityBlockingQueue;
public class RAM extends Thread{
private final int SIZE = 1000-320;
private static int usedRAM;
public static LinkedQueue<Process> jobQ;
public static PQKImp<Integer,Process>readyQ;
public static LinkedQueue<Process> waitingProcesses;
public RAM() {
	this.usedRAM=0;
	RAM.jobQ = new LinkedQueue<Process>();
	int pid = 0; int start = 0; 
	for(int i =0;i<100;i++) {
		String name = "process " + pid;
		jobQ.enqueue(new Process(pid++,name,start++));
	}
	RAM.readyQ = new PQKImp<Integer,Process>();
	RAM.waitingProcesses = new LinkedQueue<Process>();
}
public void run() {
	while(true) {
		midTermSchedular();
		longTermSchedular();	
		/*try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}
}
public void midTermSchedular(){
	if(isDeadlock()) {
		//get maxprocess p
		//p.kill
	}
	while(waitingProcesses.length()!=0 && !enoughRAM(waitingProcesses.peek().getSize())) {
		
	}
	if(waitingProcesses.length()!=0)
	System.out.println("the first process in waiting is "+waitingProcesses.peek().getName());
}
public void longTermSchedular() {
	
	while(jobQ.length()!=0 && enoughRAM(((CPUBrust)jobQ.peek().getCurrentB()).getMemoryValue())) {
		Process p = jobQ.serve();
		if(p.getCurrentB() instanceof CPUBrust) {
			addToReadyQueue(p);
			System.out.println("added " +p.getName()+" and it's size is "+ p.getSize()+" and it's first burst is "+ p.getCurrentB().getRemainingTime());
			System.out.println("the length of readyQ is "+readyQ.length());
			System.out.println("the size of the first process in ready Q is "+readyQ.peek().data.getSize()+ " and it's name is "+ readyQ.peek().data.getName());
			System.out.println("ram in use "+usedRAM);
		}else {
			// add to IOwaiting
			p.incrementIOuses();
		}
	}
	System.out.println("could not add "+jobQ.peek().getName()+"and its size is"+((CPUBrust)jobQ.peek().getCurrentB()).getMemoryValue());
	if(jobQ.length()!=0)
		addToWaiting(jobQ.serve());
	System.out.println("exited while");
}
public void addToWaiting(Process p) {
	p.incrementMemoryWaits();
	waitingProcesses.enqueue(p);
}
public static void addToReadyQueue(Process p) {
	if(p.getReadyQueueTime() == -1)
		p.setReadyQueueTime(Clock.currentTime);
readyQ.enqueue(p.getCurrentB().getRemainingTime(), p);
p.setState(STATE.ready);
allocateRAM(p);
}
public boolean isDeadlock() {
	return waitingProcesses.length()!=0 && readyQ.length() ==0 && !enoughRAM(waitingProcesses.peek().getSize());
}
public void freeRAM(Process p) {
	usedRAM -= p.getSize();
}
public static void allocateRAM(Process p) {
	usedRAM += ((CPUBrust)p.getCurrentB()).getMemoryValue();
	p.addTosize(((CPUBrust)p.getCurrentB()).getMemoryValue());
}
public boolean enoughRAM(int size) {
	return size + usedRAM <SIZE*0.85;
}
}
/*
 public void longTermSchedular() {
	Process p =
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
public static PQKImp<Integer,Process> getReadyQueue(){
	return readyQ;
}
private boolean ramIsEnough() {
	return ramInUse<.85*size;
} */
