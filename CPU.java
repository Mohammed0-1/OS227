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
		while (!OperatingSystem.isFullyFinished()) {
				excutingProcess = RAM.readyQServe();
				if (excutingProcess != null) {
					excuteProcess();
				} else {
					idelTime++;
					Clock.incrementClock();
					try {
						sleep(1);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					// test case ---------------------------------------
					if (Test.TEST_MODE) {
						System.out.println("In CPU class");
						System.out.println("down time " + idelTime);
						System.out.println("clock time " + Clock.currentTime);
					}
					// -------------------------------------------------

				}
		}
		if(OperatingSystem.isFullyFinished()) {
			OperatingSystem.writeFile();
		}
	}

	// handle the process in the CPU
	public void excuteProcess() {
		if (excutingProcess.getStartTime() == -1) {
			excutingProcess.setStartTime(Clock.getCurrentTime()); // the time it started execution
		}
		Burst currentBurst = excutingProcess.getCurrentBurst();
		excutingProcess.incrementCPUUses();// inc the num of times entered CPU

		while (currentBurst instanceof CPUBurst && currentBurst.getRemainingTime() > 0) {

			excutingProcess.incrmentCPUtime(); // inc the time spent in CPU
			currentBurst.decrementRemainingTime(); // dec process burst remaining time
			busyTime++;
			Clock.incrementClock();

			// test case ---------------------------------------
			if (Test.TEST_MODE) {
				System.out.println("ecxuting prcess " + excutingProcess.getPID() + "current burst remaining time "
						+ excutingProcess.getCurrentBurst().getRemainingTime());
				System.out.println("up time " + busyTime);
			}
			// -------------------------------------------------
			try {
				// Wait for x millisecond before proceeding
				sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			currentBurst = checkReadyQueue(currentBurst);
		}

		// go to IOBurst
		if (excutingProcess.nextBurst() != null) {
			// if IO-burst
			if (excutingProcess.getCurrentBurst() instanceof IOBurst) {
				// add to IOwaiting
				IODevice.addProcessToDevice(excutingProcess);
			}

		} else {
			excutingProcess.terminateProcess();
			RAM.freeRAM(excutingProcess);
		}
	}

	// close from the shortTimeScheduler idea
	public Burst checkReadyQueue(Burst b) {
		// check if there is a shorter process
		if (RAM.readyQPeek() != null
				&& RAM.readyQPeek().data.getCurrentBurst().getRemainingTime() < b.getRemainingTime()) {
			excutingProcess.incrementNumberOfPreemptions();
			RAM.readyQEnqueue(excutingProcess); // return process to the readyQ
			excutingProcess = RAM.readyQServe(); // change to the shorter process
			excutingProcess.setState(STATE.running);
			excutingProcess.incrementCPUUses();// inc the num of times entered CPU
			return excutingProcess.getCurrentBurst();
		}

		return excutingProcess.getCurrentBurst();
	}

	public static int getbusyTime() {
		return busyTime;
	}

	public static int getidelTime() {
		return idelTime;
	}
}
