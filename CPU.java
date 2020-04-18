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
			excutingProcess = RAM.readyQ.serve();
			if (excutingProcess != null) {
				excuteProcess();
			} else {
				try {
					sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				idelTime++;
				Clock.incrementClock();

				// test case ---------------------------------------
				if (Test.TEST_MODE) {
					System.out.println("down time " + idelTime);
					System.out.println("clock time " + Clock.currentTime);
				}
				// -------------------------------------------------

			}
		}
	}

	// handle the process in the CPU
	public void excuteProcess() {
		excutingProcess.setState(STATE.running); // set state to running
		if (excutingProcess.getStartTime() == -1) {
			excutingProcess.setStartTime(Clock.getCurrentTime()); // the time it started execution
		}
		excutingProcess.incrementCPUUses();// inc the num of times entered CPU

//		while (excutingProcess.getTotalTime() > 0) {
//			Burst currentBurst = excutingProcess.getCurrentBurst(); // get the first burst of the process

			while (excutingProcess.getCurrentBurst() instanceof CPUBurst && excutingProcess.getTotalTime() > 0) {
				excutingProcess.incrmentCPUtime(); // inc the time spent in CPU
				excutingProcess.decrementTotalTime(); // dec process remaining time including burst time
				busyTime++;
				Clock.incrementClock();

				// test case ---------------------------------------
				if (Test.TEST_MODE) {
					System.out.println("ecxuting prcess " + excutingProcess.getPID() + "current burst remaining time "
							+ excutingProcess.getCurrentBurst().getRemainingTime());
					System.out.println("up time " + busyTime);
				}
				// -------------------------------------------------

				currentBurst = checkReadyQueue(currentBurst); // change to process instead of burst
				// sleep??
			}

			excutingProcess.nextBurst();
			// if IO-burst
			if (excutingProcess.getCurrentBurst() instanceof IOBurst) {
				// add to IOwaiting
			} else {
				if (excutingProcess.getCurrentBurst().getRemainingTime() == -1) {
					excutingProcess.terminateProcess();
				} else {
					RAM.addToReadyQueue(excutingProcess);
				}
			}

//		}
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
