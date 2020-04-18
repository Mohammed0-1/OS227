public class CPU extends Thread {
	private static Process excutingProcess;
	private static int busyTime;
	private static int idelTime;

	public CPU() {
		CPU.excutingProcess = null;
		CPU.busyTime = 0;
		CPU.idelTime = 0;
	}

	@Override
	public void run() {
		while (true) {
			excutingProcess = RAM.readyQServe();
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
		if (excutingProcess.getStartTime() == -1) {
			excutingProcess.setStartTime(Clock.getCurrentTime()); // the time it started execution
		}
		excutingProcess.incrementCPUUses();// inc the num of times entered CPU

		while (excutingProcess.getCurrentBurst() instanceof CPUBurst
				&& excutingProcess.getCurrentBurst().getRemainingTime() > 0) {
			
			excutingProcess.setState(STATE.running); // set state to running
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

			excutingProcess = checkReadyQueue(excutingProcess);
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
	public Process checkReadyQueue(Process current) {
		Process oldProcess = current;
		Process newProcess = null;
		// check if there is a shorter process
		if (RAM.readyQPeek() != null
				&& RAM.readyQPeek().data.getTotalTime() < oldProcess.getTotalTime()) { 
			oldProcess.incrementNumberOfPreemptions();
			
			if(RAM.enoughRAM(oldProcess)) {
				RAM.addToReadyQueue(oldProcess); // return process to the readyQ
			} else {
				RAM.addToWaiting(oldProcess); // goes to the midTermS
			}
			newProcess = RAM.readyQServe(); // change to the shorter process
			return newProcess;
		}
		return oldProcess;
	}

	public static int getbusyTime() {
		return busyTime;
	}

	public static int getidelTime() {
		return idelTime;
	}
}
