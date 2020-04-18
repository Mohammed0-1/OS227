import DataStructures.*;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class RAM extends Thread {
	private static final int SIZE = 704; // 1024 - 320 MB
	private static int usedRAM;
	private static PQKImp<Integer, Process> jobQ; // Jobs that have not entered the RAM yet.
	private static Queue<Process> waitingProcesses; // Processes that needed more memory than available.
	private static PQKImp<Integer, Process> readyQ; // Processes that are in the RAM.

	public RAM(PQKImp<Integer, Process> jobQ) {
		RAM.usedRAM = 0;
		RAM.jobQ = jobQ;
		RAM.readyQ = new PQKImp<Integer, Process>();
		RAM.waitingProcesses = new ConcurrentLinkedQueue<Process>();
	}

	public void run() {
		while (true) {
			try {
				midTermSchedular();
				longTermSchedular();
				Thread.sleep(200); // every 200ms longTermS will wake-up (point 11,4 in project description)
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
		while (waitingProcesses.size() != 0 && enoughRAM(waitingProcesses.peek())) {
			Process p = waitingProcesses.poll();
			addToReadyQueue(p);
		}

		// test case ---------------------------------------
		if (Test.TEST_MODE)
			if (waitingProcesses.size() != 0)
				System.out.println("the first process in waiting is " + waitingProcesses.peek().getPID());
		// -------------------------------------------------
	}

// takes jobs out of the job queue and inserts it in ready queue until the ram is 85% full.
	public void longTermSchedular() {

		// check for total size of process and inserts to readyQ
		while (jobQ.length() != 0 && enoughRAM(jobQ.peek().data)) {
			Process p = jobQ.serve();
			addToReadyQueue(p);

			// test case ---------------------------------------
			if (Test.TEST_MODE) {
				System.out.println("added " + p.getPID() + " and it's size is " + p.getSize()
						+ " and it's first burst is " + p.getCurrentBurst().getRemainingTime());
				System.out.println("the length of readyQ is " + readyQ.length());
				System.out.println("the size of the first process in ready Q is " + readyQ.peek().data.getSize()
						+ " and it's name is " + readyQ.peek().data.getPID());
				System.out.println("ram in use " + usedRAM);
			}
			// -------------------------------------------------
		}

		// test case ---------------------------------------
		if (Test.TEST_MODE)
			System.out.println("could not add " + jobQ.peek().data.getPID() + " and its size is"
					+ ((CPUBurst) jobQ.peek().data.getCurrentBurst()).getMemoryValue());
		// -------------------------------------------------

		// just waist time :)
		if (jobQ.length() != 0) {
			Process p = jobQ.serve();
			jobQ.enqueue(p.getArrivalTime(), p); // we might do a sleep???
		}

		// test case ---------------------------------------
		if (Test.TEST_MODE)
			System.out.println("exited while loop");
		// -------------------------------------------------

	}

// Adds a process to waiting queue.
// Changes the process state to waiting.
// increments the process memory waits.
	public static void addToWaiting(Process p) { // used in CPU
		p.incrementMemoryWaits();
		p.setState(STATE.waiting);
		waitingProcesses.add(p);
	}

// Adds a process to ready queue.
// Changes the process state to ready.
// Allocates the ram as needed.
	public static void addToReadyQueue(Process p) {
		// this is for new processes (not coming from midTermS)
		if (p.getReadyQueueTime() == -1) {
			p.setReadyQueueTime(Clock.currentTime);
		}
		readyQ.enqueue(p.getCurrentBurst().getRemainingTime(), p);
		p.setState(STATE.ready);
		allocateRAM(p);
	}

// Checks if the system is in a deadlock.
	private boolean isDeadlock() {
		return waitingProcesses.size() != 0 && readyQ.length() == 0 && !enoughRAM(waitingProcesses.peek());
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
	public static void freeRAM(Process p) {
		if(p.getState() == STATE.waiting) {
			waitingProcesses.remove(p);
		}
		usedRAM -= ((CPUBurst)p.getCurrentBurst()).getMemoryValue();
	}

// Allocates memory to a process as needed.
	private static void allocateRAM(Process p) {
		usedRAM += ((CPUBurst)p.getCurrentBurst()).getMemoryValue();
	}

// Checks if adding a process will make the RAM 85% full or not.
	public static boolean enoughRAM(Process p) {
		int size = ((CPUBurst)p.getCurrentBurst()).getMemoryValue();
		return size + usedRAM <= SIZE * 0.85;
	}
	
	public static Process readyQServe() {
		return readyQ.serve();
	}
	
	public static PQNode<Integer, Process> readyQPeek() {
		return readyQ.peek();
	}
	
	public static void readyQEnqueue(Process p) {
		readyQ.enqueue(p.getCurrentBurst().getRemainingTime(), p);
	}
	
}
