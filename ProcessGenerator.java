import java.io.*;
import java.util.*;
import DataStructures.LinkedQueue;

//import processBursts.Burst;
//import processBursts.CPUBurst;
//import processBursts.IOBurst;
/*
 * This class is used to generate processes to a file.
 * Each process has four processBursts (as specified in the project document).
 * Then reads the processes, generating a linkedQueue of processes.
 */

public class ProcessGenerator {
	private static final String FILE_PATH = "C:\\Users\\azsal\\Desktop\\OS-227 Testfiles\\ProcessList.txt";
	// Process attributes
	private static int CPUBurstRange;
	private static int memorySizeRequired;
	private static int IOBurstRange;
	private static int ArrivalTime;

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

			// each line represents a process, with 4 processBursts.
			for (int i = 0; i < numProcesses; i++) {
				// generate processBursts for numProcesses
				// in the format of CPUBurstRange, memorySizeRequired, ArrivalTime, IOBurstRange
				// |
				generateBurst();
				writer.write(CPUBurstRange + "\t" + memorySizeRequired + "\t" + ArrivalTime + "\t" + IOBurstRange
						+ "\t | \t");

				generateBurst();
				writer.write(CPUBurstRange + "\t" + memorySizeRequired + "\t" + ArrivalTime + "\t" + IOBurstRange
						+ "\t | \t");

				generateBurst();
				writer.write(CPUBurstRange + "\t" + -memorySizeRequired + "\t" + ArrivalTime + "\t" + IOBurstRange
						+ "\t | \t");

				generateBurst();
				writer.write(CPUBurstRange + "\t" + 0 + "\t" + ArrivalTime + "\t" + IOBurstRange + "\t | \t");

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
	public static LinkedQueue<Process> generateJobQ() {
		String line; // represents a process, with 4 processBursts.
		int processID = 1; // used when reading a new process (TEST)
		int CPUBurstRange, memorySizeRequired, ArrivalTime, IOBurstRange; // created local variables so it doesn't
																			// conflict.
		LinkedQueue<Process> JobQ = new LinkedQueue<>(); // the returned processes

		try {
			// initiate a reader
			BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH));
			reader.readLine();

			while ((line = reader.readLine()) != null) { // read line until the last line (ie. last process)

				String[] processArray = line.split("\t"); // take one process and all it's processBursts
				int ProcessArrayLength = processArray.length;
				LinkedQueue<Burst> processBurstsQ = new LinkedQueue<>(); // holds the bursts of each process

				// read and create the processBursts queue
				for (int i = 0; i < 20; i += 5) { // i+=5 so we can read the next burst of the same process
																	
					// check if process ended (ie.no more bursts)
					if (processArray[i].equals("-1")) {
						break;
					}
					// allocate each burst to processBursts queue
					CPUBurstRange = Integer.parseInt(processArray[i]);
					memorySizeRequired = Integer.parseInt(processArray[i + 1]);
					ArrivalTime = Integer.parseInt(processArray[i + 2]);
					IOBurstRange = Integer.parseInt(processArray[i + 3]);
					
					Burst cpuBurst = new CPUBurst(CPUBurstRange, processID, memorySizeRequired, ArrivalTime);
					Burst ioBurst = new IOBurst(IOBurstRange, processID);
					processBurstsQ.enqueue(cpuBurst);
					processBurstsQ.enqueue(ioBurst);
				}

//				PCB pcb = new PCB(processID, FullSize, processBurstsQ);   <--- this is what it should be
//				JobQ.enqueue(pcb);										  <--- this is what it should be
//				processID++;											  <--- this is what it should be
				Process p = new Process(processID, "", 0);
				JobQ.enqueue(p);
				processID++;	
			}
			
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return JobQ;
	}

	private static void generateBurst() {
		CPUBurstRange = (int) (Math.random() * 91) + 10;
		memorySizeRequired = (int) (Math.random() * 196) + 5;
		IOBurstRange = (int) (Math.random() * 41) + 20;
		ArrivalTime = (int) (Math.random() * 80) + 1;
	}

//	Main test
	public static void main(String[] args) {
//		ProcessGenerator.generateProcesses(5);
		LinkedQueue<Process> JobQ = ProcessGenerator.generateJobQ();
		System.out.println(JobQ.serve().toString());
	}

}
