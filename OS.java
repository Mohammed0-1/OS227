
public class OS {
public static void main(String[] args) {
	Thread ram = new Thread (new RAM());
	CPU CPU = new CPU();
	System.out.println(RAM.jobQ.peek().getPID()+" "+ RAM.jobQ.peek().getName());
	System.out.println(RAM.jobQ.peek().getCurrentB().getRemainingTime());
	System.out.println(((CPUBrust)RAM.jobQ.peek().getCurrentB()).getMemoryValue());
	System.out.println(RAM.jobQ.peek().getSize());
	System.out.println("this is in main "+ RAM.readyQ.length());
	ram.run();
	
	/*System.out.println(RAM.readyQ.length());
	System.out.println(RAM.waitingProcesses.length());
	CPU.start();*/
}
}
