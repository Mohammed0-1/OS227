import java.util.LinkedList;
import java.io.*;

public class OperatingSystem {
    private static LinkedList<Process> finishedProcesses = new LinkedList<>();
    private static int size = 0;
    private static IODevice io;
    private static RAM ram;
    private static CPU cpu;

//    RAM ram = new RAM(new IODevice());
//    CPU cpu = new CPU(new RAM(new IODevice()));
//    Clock clock =new Clock();
    
    
    /****************** IMPORTANT*******************/ 
    // soqih,	I thought I can remove "gui" because it is just for formating the
    //			output but I think it save some info. we should consider that .
    
    
    public static void main(String[] args) {
    	io = new IODevice();
    	ram = new RAM();
    	cpu = new CPU();

        // Add processes to Waiting For Allocation Queue
       for(Process p : FileHandler.readFile()){
    	   size++;
    	   RAM.addToJobQ(p);
       }

       cpu.start();
       ram.start();
       io.start();
	}

	static void stopOS() {
        try {
            sleepAllThreads();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

       

        FileHandler.writeFile(finishedProcesses);
        System.out.println("Multiprogramming Operating System Simulation - [ Finished ]");
    }

    
	private static void sleepAllThreads() throws InterruptedException {
        io.sleep(1000);
        ram.sleep(1000);
        cpu.sleep(1000);
    }

    
    static void addFinishedProcess(Process process) {
        finishedProcesses.add(process);
    }
    
    static boolean isFullyFinished() {
    	return OperatingSystem.size == OperatingSystem.finishedProcesses.size(); 
    }
}