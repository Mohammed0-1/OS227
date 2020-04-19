import DataStructures.*;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class RAM extends Thread {
	private static final int SIZE = 704; // 1024 - 320 MB
	private static int usedRAM;
	private static LinkedQueue<Process> jobQ; // Jobs that have not entered the RAM yet.
	private static Queue<Process> waitingProcesses; // Processes that needed more memory than available.
	private static PQKImp<Integer, Process> readyQ; // Processes that are in the RAM.

	public RAM(LinkedQueue<Process> jobQ) {
		RAM.usedRAM = 0;
		RAM.jobQ = jobQ;
		RAM.readyQ = new PQKImp<Integer, Process>();
		RAM.waitingProcesses = new ConcurrentLinkedQueue<Process>();
	}

	public void run() {

		while (!OperatingSystem.isFullyFinished()) {
			midTermSchedular();
			longTermSchedular();
			try {
				Thread.sleep(1); // every 200ms longTermS will wake-up (point 11,4 in project description)
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
			waitingProcesses.remove(max);
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
		int oldTime = Clock.getCurrentTime();
		int notifyTime = oldTime + 200;
		while (notifyTime > Clock.getCurrentTime()) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		while (jobQ.length() != 0 && enoughRAM(jobQ.peek())) {
			Process p = jobQ.serve();
				addToReadyQueue(p);

		}

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
		//
		if (Test.TEST_MODE)
			System.out.println("readyQ lenght: " + readyQ.length() + " PID: " + p.getPID());
		//
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
		usedRAM -= p.getSize();
		OperatingSystem.addFinishedProcess(p);
	}

// Allocates memory to a process as needed.
	private static void allocateRAM(Process p) {
		if (p.getCurrentBurst() instanceof IOBurst) { 
			return;
		}
		usedRAM += ((CPUBurst) p.getCurrentBurst()).getMemoryValue();
		p.addTosize(((CPUBurst) p.getCurrentBurst()).getMemoryValue());
	}

// Checks if adding a process will make the RAM 85% full or not.
	public static boolean enoughRAM(Process p) {
		if (p.getCurrentBurst() instanceof IOBurst) {
			return true;
		}
		int size = ((CPUBurst) p.getCurrentBurst()).getMemoryValue();
		return size + usedRAM <= SIZE * 0.85;
	}

	public static Process readyQServe() {
		return readyQ.serve();
	}

	public static PQNode<Integer, Process> readyQPeek() {
		return readyQ.peek();
	}

	public static void readyQEnqueue(Process p) {
		p.setState(STATE.ready);
		readyQ.enqueue(p.getCurrentBurst().getRemainingTime(), p);
	}

}
