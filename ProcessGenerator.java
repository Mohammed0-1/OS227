import java.io.*;
import java.util.*;
import DataStructures.LinkedQueue;

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
	private static int arrivalTime;

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
			Random rand = new Random();
			int totalMemorySize = 0;

			for (int i = 0; i < numProcesses; i++) { // each line represents a process

				int numBursts = rand.nextInt(6) + 2;

				for (int j = 0; j < numBursts; j++) {
					generateBurst();

					totalMemorySize += memorySizeRequired; // this block is to make the required memory size (-ve) when
															// appropriate
					if ((j == rand.nextInt(numBursts + 1) + 2) && totalMemorySize > memorySizeRequired) {
						writer.write(CPUBurstRange + "\t" + -memorySizeRequired + "\t" + IOBurstRange + "\t");
						totalMemorySize -= memorySizeRequired;
						continue;
					}

					writer.write(CPUBurstRange + "\t" + memorySizeRequired + "\t" + IOBurstRange + "\t");

				}
				writer.write("-1\n");
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
		String line; // represents a process
		int processID = 1;
		int CPUBurstRange, memorySizeRequired, arrivalTime, IOBurstRange; // created local variables so it doesn't
																			// conflict.
		LinkedQueue<Process> jobQ = new LinkedQueue<>(); // the returned processes

		try {
			// initiate a reader
			BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH));

			while ((line = reader.readLine()) != null) { // read line until the last line (ie. last process)

				String[] processArray = line.split("\t"); // take one process and all it's processBursts
				int ProcessArrayLength = processArray.length;
				LinkedQueue<Burst> processBurstsQ = new LinkedQueue<>(); // holds the bursts of each process

				// read and create the processBursts queue
				for (int i = 0; i < ProcessArrayLength; i += 3) { // i+=3 so we can read the next burst of the same
																	// process

					if (Integer.parseInt(processArray[i]) == -1) {
						// test case ---------------------------------------
						if (Test.TEST_MODE) {
							System.out.println(i);
							System.out.println("Process" + processID + " has finished");
						}
						// -------------------------------------------------
						break;
					}

					CPUBurstRange = Integer.parseInt(processArray[i]);
					memorySizeRequired = Integer.parseInt(processArray[i + 1]);
					arrivalTime = Integer.parseInt(processArray[i + 2]);
					IOBurstRange = Integer.parseInt(processArray[i + 3]);

					Burst cpuBurst = new CPUBurst(CPUBurstRange, memorySizeRequired/* , arrivalTime */);
					Burst ioBurst = new IOBurst(IOBurstRange);
					processBurstsQ.enqueue(cpuBurst);
					processBurstsQ.enqueue(ioBurst);

				}

//				PCB pcb = new PCB(processID, FullSize, processBurstsQ);   <--- this is what it should be
				arrivalTime = generateArrivalTime();
				Process p = new Process(processID, "Process number" + processID, 0,/*arrivalTime,*/ processBurstsQ);
				jobQ.enqueue(p);
				processID++;
			}

			reader.close();
		} catch (IOException e) {
			System.out.println("An error occurred in the generateJobQ method");
			e.printStackTrace();
		}
		return jobQ;
	}

	private static void generateBurst() {
		Random r = new Random();

		CPUBurstRange = r.nextInt(91) + 10;
		memorySizeRequired = r.nextInt(196) + 5;
		IOBurstRange = r.nextInt(41) + 20;
	}
	
	private static int generateArrivalTime() {
		Random r = new Random();
		arrivalTime = r.nextInt(80) + 1;
		return arrivalTime;
	}

	// Main test case ---------------------------------------
	public static void main(String[] args) {
		if (Test.TEST_MODE) {
			ProcessGenerator.generateProcesses(5);
			LinkedQueue<Process> JobQ = ProcessGenerator.generateJobQ();
			for (int i = 0; i < JobQ.length(); i++) {
				System.out.println(JobQ.serve().toString());
			}
		}
	}
	// -------------------------------------------------

}
