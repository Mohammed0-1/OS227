import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Random;

import DataStructures.LinkedQueue;

public class OperatingSystem {
	private static final String FILE_PATH = "C:\\Users\\azsal\\Desktop\\OS-227 Testfiles\\";
    private static LinkedList<Process> finishedProcesses = new LinkedList<>();
    private static int size = 0;
    private static IODevice io;
    private static RAM ram;
    private static CPU cpu;

//    RAM ram = new RAM(new IODevice());
//    CPU cpu = new CPU(new RAM(new IODevice()));
//    Clock clock =new Clock();
       
    public static void main(String[] args) {
    	ProcessGenerator.generateProcesses(5);
    	LinkedQueue<Process> jobQ = ProcessGenerator.generateJobQ();
    	int jobQSize = jobQ.length();
    	size = jobQSize;
    	
    	io = new IODevice();
    	ram = new RAM(jobQ);
    	cpu = new CPU();

       cpu.start();
       ram.start();
       io.start();
       
       if(isFullyFinished()) {
    	   stopOS();
    	   
    	   for (int i=0; i< finishedProcesses.size(); i++) {
    		   System.out.println(finishedProcesses.poll().getPID());
    		   System.out.println(finishedProcesses.poll().getReadyQueueTime());
    		   System.out.println(finishedProcesses.poll().getCPUUses());
    		   System.out.println(finishedProcesses.poll().getCPUtime());
//    		   System.out.println(finishedProcesses.poll().getPID());
//    		   System.out.println(finishedProcesses.poll().getPID());
//    		   System.out.println(finishedProcesses.poll().getPID());
//    		   System.out.println(finishedProcesses.poll().getPID());
//    		   System.out.println(finishedProcesses.poll().getPID());
    	   }
    	   
    	   
//    	   try {
//   			// create ProcessList file
//   			File file = new File(FILE_PATH);
//
//   			// check if file exists
//   			if (file.createNewFile()) {
//   				System.out.println("File created: " + file.getName());
//   			} else { 
//   				System.out.println("File already exists.");
//   			}
//
//   			// write processes to file
//   			FileWriter writer = new FileWriter(FILE_PATH);
//   			Random rand = new Random();
//   			int totalMemorySize = 0;
//
//   			for (int i = 0; i < numProcesses; i++) { // each line represents a process
//
//   				int numBursts = rand.nextInt(6) + 2;
//
//   				for (int j = 0; j < numBursts; j++) {
//   					generateBurst();
//
//   					totalMemorySize += burstMemorySize; // this block is to make the required memory size (-ve) when
//   															// appropriate
//   					if ((j == rand.nextInt(numBursts + 1) + 2) && totalMemorySize > burstMemorySize) {
//   						writer.write(CPUBurstRange + "\t" + -burstMemorySize + "\t" + IOBurstRange + "\t");
//   						totalMemorySize -= burstMemorySize;
//   						continue;
//   					}
//
//   					writer.write(CPUBurstRange + "\t" + burstMemorySize + "\t" + IOBurstRange + "\t");
//
//   				}
//   				writer.write("-1\n");
//   			}
//   			writer.close();
//
//   			// test case ---------------------------------------
//   			if (Test.TEST_MODE)
//   				System.out.println("File is written");
//   			// -------------------------------------------------
//
//   		} catch (IOException e) {
//   			System.out.println("An error occurred in the generateProcesses method");
//   			e.printStackTrace();
//   		}
    	   
    	   
       }
       
       
       
       
	}

	static void stopOS() {
        try {
            sleepAllThreads();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

       

       /* FileHandler.writeFile(finishedProcesses);
        System.out.println("Multiprogramming Operating System Simulation - [ Finished ]");
       */
    }

    
	private static void sleepAllThreads() throws InterruptedException {
        io.sleep(1000);
        ram.sleep(1000);
        cpu.sleep(1000);
    }

    
    public static void addFinishedProcess(Process process) {
        finishedProcesses.add(process);
    }
    
    public static boolean isFullyFinished() {
    	return OperatingSystem.size == OperatingSystem.finishedProcesses.size(); 
    }
}