import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/***
 * This class represents an IO device that handles any IO requests<br>
 * We chose to set this as a Thread class because in reality, the<br>
 * CPU does not wait for IO bursts to finish before going to the <br>
 * next process.
 */
public class IODevice extends Thread {
	private static Process currentProcess;

//  	list for processes waiting for an IO
	private static Queue<Process> IOWaitingList; // process in waiting state

	public IODevice() {
		currentProcess = null;
		IOWaitingList = new ConcurrentLinkedQueue<>();
	}

	@Override
	// soqih, start the thread
	public void run() {
		// While OS is running, keep handling IO requests if available
		while (true) {

			currentProcess = IOWaitingList.poll();

			if (currentProcess != null) {
				handleIORequest();
			} else {
				try {
					sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

	}

	private void handleIORequest() {

		// Increment the process IO counter
		currentProcess.incrementIOUses();

		while (currentProcess.getCurrentBurst().getRemainingTime() > 0) {
			// Increment total IO time of process
			currentProcess.incrementIOtime();

			// Decrement the process IO burst
			currentProcess.getCurrentBurst().decrementRemainingTime();
		}

		currentProcess.nextBurst();

		if (RAM.enoughRAM(currentProcess)) {
			RAM.addToReadyQueue(currentProcess);
		} else {
			RAM.addToWaiting(currentProcess);
		}
	}

	// simple method it adds the process to the waiting list in the IODevice
	public static void addProcessToDevice(Process p) {
		p.setState(STATE.waiting);
		IOWaitingList.add(p);
	}

	boolean isEmpty() {
		return IOWaitingList.isEmpty();
	}

	Process getCurrentProcess() {
		return currentProcess;
	}

	Queue<Process> getWaitingList() {
		return IOWaitingList;
	}
}