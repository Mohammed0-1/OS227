import DataStructures.*;

public class RAM extends Thread {
	private final int SIZE = 1024 - 320;
	private static int usedRAM;
	public static LinkedQueue<Process> jobQ; // Jobs that have not entered the RAM yet.
	public static PQKImp<Integer, Process> readyQ; // Processes that are in the RAM.
	public static LinkedQueue<Process> waitingProcesses; // Processes that needed more memory than available.

	public RAM(LinkedQueue<Process> jobQ) {
		this.usedRAM = 0;
		RAM.jobQ = jobQ;
		RAM.readyQ = new PQKImp<Integer, Process>();
		RAM.waitingProcesses = new LinkedQueue<Process>();
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
			//remove max process from waitingQueue
		}
		while (waitingProcesses.length() != 0 && waitingProcesses.peek().getCurrentBurst() instanceof CPUBurst
				&& enoughRAM(((CPUBurst) waitingProcesses.peek().getCurrentBurst()).getMemoryValue())) {
			Process p = waitingProcesses.serve();
			addToReadyQueue(p);
		}
		if (waitingProcesses.length() != 0)
			System.out.println("the first process in waiting is " + waitingProcesses.peek().getName());
	}

// takes jobs out of the job queue and inserts it in ready queue until the ram is 85% full.
	public void longTermSchedular() {

		// check for total size of process <----- correct to
		while (jobQ.length() != 0 && enoughRAM(((CPUBurst) jobQ.peek().getCurrentBurst()).getMemoryValue())) {
			Process p = jobQ.serve();
			if (p.getCurrentBurst() instanceof CPUBurst) {
				addToReadyQueue(p);
				
				System.out.println("added " + p.getName() + " and it's size is " + p.getSize()
						+ " and it's first burst is " + p.getCurrentBurst().getRemainingTime());
				System.out.println("the length of readyQ is " + readyQ.length());
				System.out.println("the size of the first process in ready Q is " + readyQ.peek().data.getSize()
						+ " and it's name is " + readyQ.peek().data.getName());
				System.out.println("ram in use " + usedRAM);
				
			} else {
				/*
				 * add to IOwaiting **************** ****************
				 */
				p.incrementIOuses();
			}
		}
		System.out.println("could not add " + jobQ.peek().getName() + " and its size is"
				+ ((CPUBurst) jobQ.peek().getCurrentBurst()).getMemoryValue());
		if (jobQ.length() != 0)
			jobQ.enqueue(jobQ.serve());
		System.out.println("exited while");
	}

// Adds a process to waiting queue.
// Changes the process state to waiting.
// incrments the process memory waits.
	public void addToWaiting(Process p) {
		p.incrementMemoryWaits();
		p.setState(STATE.waiting);
		waitingProcesses.enqueue(p);
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
		return waitingProcesses.length() != 0 && readyQ.length() == 0 && !enoughRAM(waitingProcesses.peek().getSize());
	}

// Returns the process with the max size.
	private Process getMaxProcess() {
		if (waitingProcesses.length() != 0) {
			int length = waitingProcesses.length();
			Process max = waitingProcesses.peek();
			for (int i = 0; i < length; i++) {
				Process temp = waitingProcesses.serve();
				waitingProcesses.enqueue(temp);
				if (max.getSize() < temp.getSize())
					max = temp;
			}
			return max;
		}
		return null;
	}

// Frees memory if a process is killed or terminated.
	public void freeRAM(Process p) {
		// serve the max waiting process
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
