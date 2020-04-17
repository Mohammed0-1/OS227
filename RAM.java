import DataStructures.*;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.PriorityBlockingQueue;


public class RAM extends Thread {
	private final int SIZE = 1024 - 320;
	private static int usedRAM;
	private static LinkedQueue<Process> jobQ; // Jobs that have not entered the RAM yet.
	private static PQKImp<Integer, Process> readyQ; // Processes that are in the RAM.
	private static Queue<Process> waitingProcesses; // Processes that needed more memory than available.

	public RAM(LinkedQueue<Process> jobQ) {
		this.usedRAM = 0;
		RAM.jobQ = jobQ;
		RAM.readyQ = new PQKImp<Integer, Process>();
		RAM.waitingProcesses = new ConcurrentLinkedQueue<Process>();
	}

	public void run() {
		while (true) {
			midTermSchedular();
			longTermSchedular();
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

// Checks if the system is in a deadlock and handles the deadlock if there is one.
// checks if there is enough space to satisfy memory needs for a process and inserts the process in the ready queue. 
	public void midTermSchedular() {
		if (isDeadlock()) {
			Process max = getMaxProcess();
			max.killProcess();
			freeRAM(max);
		}
		while (waitingProcesses.size() != 0 && waitingProcesses.peek().getCurrentBurst() instanceof CPUBurst
				&& enoughRAM(waitingProcesses.peek().getSize())) {
			Process p = waitingProcesses.poll();
			addToReadyQueue(p);
		}
		if (waitingProcesses.size() != 0)
			System.out.println("the first process in waiting is " + waitingProcesses.peek().getPID());
	}

// takes jobs out of the job queue and inserts it in ready queue until the ram is 85% full.
	public void longTermSchedular() {

		// check for total size of process and inserts to readyQ
		while (jobQ.length() != 0 && enoughRAM(jobQ.peek().getSize())) {
			Process p = jobQ.serve();
			addToReadyQueue(p);

			// test case ---------------------------------------
			if (Test.TEST_MODE)
				System.out.println("added " + p.getPID() + " and it's size is " + p.getSize()
						+ " and it's first burst is " + p.getCurrentBurst().getRemainingTime());
				System.out.println("the length of readyQ is " + readyQ.length());
				System.out.println("the size of the first process in ready Q is " + readyQ.peek().data.getSize()
						+ " and it's name is " + readyQ.peek().data.getPID());
				System.out.println("ram in use " + usedRAM);
			// -------------------------------------------------
		}

		// test case ---------------------------------------
		if (Test.TEST_MODE)
			System.out.println("could not add " + jobQ.peek().getPID() + " and its size is"
					+ ((CPUBurst) jobQ.peek().getCurrentBurst()).getMemoryValue());
		// -------------------------------------------------

		// just waist time :)
		if (jobQ.length() != 0)
			jobQ.enqueue(jobQ.serve()); // we might do a sleep???

		// test case ---------------------------------------
		if (Test.TEST_MODE)
			System.out.println("exited while");
		// -------------------------------------------------

	}

// Adds a process to waiting queue.
// Changes the process state to waiting.
// incrments the process memory waits.
	public void addToWaiting(Process p) {
		p.incrementMemoryWaits();
		p.setState(STATE.waiting);
		waitingProcesses.add(p);
	}

// Adds a process to ready queue.
// Changes the process state to ready.
// Allocates the ram as needed.
	public static void addToReadyQueue(Process p) {
		if (p.getReadyQueueTime() == -1) {
			p.setReadyQueueTime(Clock.currentTime);
		}
		readyQ.enqueue(p.getCurrentBurst().getRemainingTime(), p);
		p.setState(STATE.ready);
		allocateRAM(p);
	}

// Checks if the system is in a deadlock.
	public boolean isDeadlock() {
		return waitingProcesses.size() != 0 && readyQ.length() == 0 && !enoughRAM(waitingProcesses.peek().getSize());
	}

// Returns the process with the max size.
	private Process getMaxProcess() {
		if (waitingProcesses.size() != 0) {
			int length = waitingProcesses.size();
			Process max = waitingProcesses.peek();
			for (int i = 0; i < length; i++) {
				Process temp = waitingProcesses.poll();
				waitingProcesses.add(temp);
				if (max.getSize() < temp.getSize())
					max = temp;
			}
			return max;
		}
		return null;
	}

// Frees memory if a process is killed or terminated.
	public void freeRAM(Process p) {
		waitingProcesses.remove(p);
		usedRAM -= p.getSize();
	}

// Allocates memory to a process as needed.
	public static void allocateRAM(Process p) {
		usedRAM += ((CPUBurst) p.getCurrentBurst()).getMemoryValue();
		p.addTosize(((CPUBurst) p.getCurrentBurst()).getMemoryValue());
	}

// Checks if the adding  a process will make the RAM 85% full or not.
	public boolean enoughRAM(int size) {
		return size + usedRAM < this.SIZE * 0.85;
	}
}
