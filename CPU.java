public class CPU extends Thread {
private Process excutingProcess;
private int upTime;
private int downTime;

public CPU () {
	excutingProcess = null;
	upTime = downTime = 0;
}
public void run() {
	while(true) {
		this.excutingProcess = RAM.readyQ.serve();
		if(excutingProcess !=null) {
			excutingProcess.setState(STATE.running);
		excute();
		}else {
			try {
				sleep(5);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			downTime++;
			System.out.println("down time "+downTime);
			Clock.increment(1);
			System.out.println("clock time "+Clock.currentTime);
		}
	}
}
public void excute() {
	Brust currentBrust = excutingProcess.getCurrentB();
	while(currentBrust.getRemainingTime()>0) {
		currentBrust.decrementRemainingTime();
		System.out.println("ecxuting prcess "+excutingProcess.getName()+"current burst remaining time "+ currentBrust.getRemainingTime());
		excutingProcess.incrmentCPUtime();
		upTime++;
		System.out.println("up time "+upTime);
		Clock.increment(1);
		currentBrust = checkReadyQueue(currentBrust);
	}
	excutingProcess.nextBrust();
	if(excutingProcess.getCurrentB() instanceof IOBrust) {
		//add to IOwaiting
	}else {
		if(excutingProcess.getCurrentB().getRemainingTime()==-1) {
			excutingProcess.terminateProcess();
		}else {
			RAM.addToReadyQueue(excutingProcess);
		}
	}
}
public Brust checkReadyQueue(Brust current) {
	if(RAM.readyQ.peek() != null &&RAM.readyQ.peek().data.getCurrentB().getRemainingTime()<current.getRemainingTime()) {
		excutingProcess.incrementNumberOfPreemptions();
		excutingProcess.setState(STATE.ready);
		RAM.readyQ.enqueue(current.getRemainingTime(), excutingProcess);
		excutingProcess = RAM.readyQ.serve();
		excutingProcess.setState(STATE.running);
		return excutingProcess.getCurrentB();
	}
	return current;
}
public int getUpTime() {
	return upTime;
}
public int getDownTIme() {
	return downTime;
}
}
