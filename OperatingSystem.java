import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.Thread.State;
import java.util.LinkedList;
import java.util.Scanner;

import DataStructures.LinkedQueue;
import jdk.management.resource.NotifyingMeter;

public class OperatingSystem {
	private static final String FILE_PATH = "C:\\Users\\azsal\\Desktop\\OS-227 Testfiles\\result.txt";
	private static LinkedList<Process> finishedProcesses = new LinkedList<>();
	private static int size = 0;
	private static IODevice io;
	private static RAM ram;
	private static CPU cpu;
	private static PrintResult print;
	private static Scanner s;
	private static double CPUUtilization = 0;
	public static Object syncObj = new Object();

	public static void main(String[] args) {
		s = new Scanner(System.in);
		System.out.println("Enter number of processes");
		int processNum = s.nextInt();

		ProcessGenerator.generateProcesses(processNum);
		LinkedQueue<Process> jobQ = ProcessGenerator.generateJobQ();
		int jobQSize = jobQ.length();
		size = jobQSize;

		ram = new RAM(jobQ);
		io = new IODevice();
		cpu = new CPU();
		print = new PrintResult();

		ram.start();
		cpu.start();
		io.start();
		print.start();


	}
	

	static void stopOS() {
		try {
			sleepAllThreads();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	@SuppressWarnings("deprecation")
	private static void sleepAllThreads() throws InterruptedException {
		io.sleep(2000);
		ram.sleep(2000);
		cpu.sleep(2000);
		io.destroy();
		ram.destroy();
		cpu.destroy();
	}

	public static void addFinishedProcess(Process process) {
		finishedProcesses.add(process);
	}

	public static boolean isFullyFinished() {
		return OperatingSystem.size == OperatingSystem.finishedProcesses.size();
	}
	
	public static void writeFile() {
//			stopOS();
			
			CPUUtilization = (CPU.getbusyTime()/Clock.currentTime)*100;
			
			try {
				// create ProcessList file
				File file = new File(FILE_PATH);

				// check if file exists
				if (file.createNewFile()) {
					System.out.println("Result File created: " + file.getName());
				} else {
					System.out.println("Result File already exists.");
				}

				// write processes to file
				FileWriter writer = new FileWriter(FILE_PATH);
				writer.write("CPU Utilization= " + CPUUtilization + "% \n");

				for (int i = 0; i < finishedProcesses.size(); i++) { 
					System.out.println("FP: length: "+finishedProcesses.size());

					Process p = finishedProcesses.poll();
					writer.write("Process ID: " + p.getPID());
					writer.write("\n");
					writer.write("Time loaded into Ready Queue: " + p.getReadyQueueTime());
					writer.write("\n");
					writer.write("Number of times it was in the CPU: " + p.getCPUUses());
					writer.write("\n");
					writer.write("Total time spent in the CPU: " + p.getCPUtime());
					writer.write("\n");
					writer.write("Number of times it performed an IO: " + p.getIOUses());
					writer.write("\n");
					writer.write("Total time spent in performing IO: " + p.getIOtime());
					writer.write("\n");
					writer.write("Number of times it was waiting for memory: " + p.getMemoryWaits());
					writer.write("\n");
					writer.write("Number of times its preempted: " + p.getNumberOfPreemptions());
					writer.write("\n");
					writer.write("Time it terminated or was killed: " + p.getTerminationTime());
					writer.write("\n");
					writer.write("final state: " + p.getState());
					writer.write("\n");
					writer.write("----------------------------------- End Of Process "+ p.getPID() +"-----------------------------------------\n");

				}

				writer.close();

				// test case ---------------------------------------
				if (Test.TEST_MODE)
					System.out.println("File is written");
				// -------------------------------------------------

			} catch (IOException e) {
				System.out.println("An error occurred in the generateProcesses method");
				e.printStackTrace();
			}

		}
		
	}
	
