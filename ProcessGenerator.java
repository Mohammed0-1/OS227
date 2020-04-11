import java.io.*;
import java.util.*;

//import Bursts.Burst;
//import Bursts.CPUBurst;
//import Bursts.IOBurst;

/*
 * This class is used to generate processes to a file.
 * Each process has four bursts (as specified in the project document).
 * Then reads the processes, generating a linkedQueue of processes.
 * STILL DIDN'T FINISH
 */

public class ProcessGenerator {
	private static final String FILE_PATH = "C:\\Users\\azsal\\Desktop\\OS-227 Testfiles\\ProcessList.txt";
	// Process attributes
	private static int CPUBurstRange;
	private static int memorySizeRequired;
	private static int IOBurstRange;
	private static int ArrivalTime;
	private static int memorySizeOfTwoBursts; //This variable is to guarantee that freeing memory wont exceed used memory size

	public static void generateProcesses(int numProcesses) {
		try {
			// create ProcessList file
			File file = new File(FILE_PATH);

			// check if file exists
			if (file.createNewFile()) {
				System.out.println("File created: " + file.getName());
			} else {
				System.out.println("File already exists.");
			}

			// write processes to file
			FileWriter writer = new FileWriter(FILE_PATH);

			// each line represents a process, with 4 bursts.
			for (int i = 0; i < numProcesses; i++) {
				// generate bursts for numProcesses
				generateBurst();
				memorySizeOfTwoBursts = memorySizeRequired;
				writer.write(CPUBurstRange + "\t" + memorySizeRequired + "\t" + ArrivalTime + "\t" + IOBurstRange + "\t");

				generateBurst();
				memorySizeOfTwoBursts += memorySizeRequired;
				writer.write(CPUBurstRange + "\t" + memorySizeRequired + "\t" + ArrivalTime + "\t" + IOBurstRange + "\t");

				generateThirdBurst();
				writer.write(CPUBurstRange + "\t" + -memorySizeRequired + "\t" + ArrivalTime + "\t" + IOBurstRange + "\t");

				generateBurst();
				writer.write(CPUBurstRange + "\t" + 0 + "\t" + ArrivalTime + "\t" + IOBurstRange + "\t");

				writer.write("-1 \n");

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

	// read from ProcessList file
//	public static LinkedQueue<Process> generateJobQ() {
//		String line; // represents a process, with 4 bursts.
//		String CPUBurstRange, memorySizeRequired, IOBurstRange, ArrivalTime;
//		LinkedQueue<Process> processes = new LinkedQueue<>(); // the returned processes
//
//		try {
//			// initiate a reader
//			BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH));
//			reader.readLine();
//			// read line up to the last line (ie. last process)
//			while ((line = reader.readLine()) != null) {
//				
//				// take each process and all it's bursts
//				String[] processInfo = line.split("\t");
//				int processInfoLength = processInfo.length;
//
//				Queue<Burst> bursts = new LinkedList<>();
//
//				for (int i = 1; i < processInfoLength; i += 3) {
//					int cpu = Integer.parseInt(processInfo[i]);
//
//					if (i + 1 < processInfoLength) {
//						int memory = Integer.parseInt(processInfo[i + 1]);
//
//						if (i == 1)
//							memory = Math.abs(memory);
//
//						Burst cpuBurst = new CPUBurst(cpu, memory);
//
//						bursts.add(cpuBurst);
//
//						if (i + 2 < processInfoLength) {
//							int io = Integer.parseInt(processInfo[i + 2]);
//
//							Burst ioBurst = new IOBurst(io);
//
//							bursts.add(ioBurst);
//						}
//					} else {
//						Burst burst = new CPUBurst(cpu, 0);
//
//						bursts.add(burst);
//					}
//				}
//
//				CPUBurst currentBurst = (CPUBurst) bursts.peek();
//
//				PCB process = new PCB(pid, currentBurst.getMemoryValue(), bursts);
//
//				processes.add(process);
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return processes;
//	}

	private static void generateBurst() {
		CPUBurstRange = (int) (Math.random() * 91) + 10;
		memorySizeRequired = (int) (Math.random() * 196) + 5;
		IOBurstRange = (int) (Math.random() * 41) + 20;
		ArrivalTime = (int) (Math.random() * 80) + 1;
	}
	
	//This is the burst with negative memory allocation
	private static void generateThirdBurst() {
		CPUBurstRange = (int) (Math.random() * 91) + 10;
		memorySizeRequired = (int) (Math.random() * 196) + 5;
		//guarantee that freeing memory wont exceed used memory size
		if(memorySizeRequired > memorySizeOfTwoBursts) {
			generateThirdBurst();
		}
		IOBurstRange = (int) (Math.random() * 41) + 20;
		ArrivalTime = (int) (Math.random() * 80) + 1;
	}

//	Main test
	public static void main(String[] args) {
		ProcessGenerator.generateProcesses(5);
	}

}
