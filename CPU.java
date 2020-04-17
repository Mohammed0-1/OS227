public class CPU extends Thread {
	private Process excutingProcess;
	private int busyTime;
	private int idelTime;

	public CPU() {
		this.excutingProcess = null;
		this.busyTime = 0;
		this.idelTime = 0;
	}

	@Override
	public void run() {
		while (true) {
			this.excutingProcess = RAM.readyQ.serve();
			if (excutingProcess != null) {
				excuteProcess();
			} else {
				try {
					sleep(5);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				idelTime++;
				System.out.println("down time " + idelTime);
				Clock.incrementClock();
				System.out.println("clock time " + Clock.currentTime);
			}
		}
	}

	// handle the process in the CPU
	public void excuteProcess() {
		excutingProcess.setState(STATE.running); // set state to running

		Burst currentBurst = excutingProcess.getCurrentBurst(); // get the first burst of the process
		// increment cpu counter/uses??

		while (currentBurst.getRemainingTime() > 0) {
			Clock.incrementClock();
			excutingProcess.incrmentCPUtime();
			busyTime++;
			currentBurst.decrementRemainingTime();

			// test case ---------------------------------------
			if (Test.TEST_MODE)
				System.out.println("ecxuting prcess " + excutingProcess.getPID() + "current burst remaining time "
						+ currentBurst.getRemainingTime());
			System.out.println("up time " + busyTime);
			// -------------------------------------------------

			currentBurst = checkReadyQueue(currentBurst);
			// sleep??
		}

		excutingProcess.nextBurst();
		if (excutingProcess.getCurrentBurst() instanceof IOBurst) {
			// add to IOwaiting
		} else {
			if (excutingProcess.getCurrentBurst().getRemainingTime() == -1) {
				excutingProcess.terminateProcess();
			} else {
				RAM.addToReadyQueue(excutingProcess);
			}
		}
	}

	// close from the shortTimeScheduler idea
	public Burst checkReadyQueue(Burst current) {
		// check if there is a shorter burst
		if (RAM.readyQ.peek() != null
				&& RAM.readyQ.peek().data.getCurrentBurst().getRemainingTime() < current.getRemainingTime()) {
			excutingProcess.incrementNumberOfPreemptions();
			excutingProcess.setState(STATE.ready);
			RAM.readyQ.enqueue(current.getRemainingTime(), excutingProcess); // return the currentBurst to the readyQ
			excutingProcess = RAM.readyQ.serve(); // change to the shorter burst
			excutingProcess.setState(STATE.running);
			return excutingProcess.getCurrentBurst();
		}
		return current;
	}

	public int getbusyTime() {
		return busyTime;
	}

	public int getidelTime() {
		return idelTime;
	}
}
