import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import Bursts.Burst;

/***
 * This class represents an IO device that handles any IO requests<br>
 * We chose to set this as a Thread class because in reality, the<br>
 * CPU does not wait for IO bursts to finish before going to the <br>
 * next process.
 */
public class IODevice extends Thread {
    private static Process currentProcess;
    
    // soqih,	list for processes waiting for an IO
    private static Queue<Process> IOWaitingList; //process in waiting state

    public IODevice() {
        currentProcess = null;
        IOWaitingList = new ConcurrentLinkedQueue<>();
    }

    @Override
    // soqih,	start the thread
    public void run() {
        //While OS is running, keep handling IO requests if available
        while(true) {
        	
        	currentProcess = IOWaitingList.poll();

            if(currentProcess != null) {
                handleIORequest();
            } else {
            	// soqih,	I think we can replace "Utility.TIME" by 1
                //sleep for 1 millisecond
                try {
                    sleep(1);
                } catch(InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void handleIORequest() { // soqih,	i didn't understand this method well 
        
    	// Increment the process IO counter
        currentProcess.incrementIOuses();

//        System.out.print("["+currentProcess.getPid()+"] (" + currentProcess.getCurrentBurst().getRemainingTime()+ ") -->\t");
        
        while(currentProcess.getCurrentBurst().getRemainingTime() > 0) {
            // Increment total IO time of process
            currentProcess.incrementIOtime();

            // Decrement the process IO burst
            currentProcess.getCurrentBurst().decrementRemainingTime();

//            System.out.print(currentProcess.getCurrentBurst().getRemainingTime()+"\t");
            try {
                //Wait for x millisecond
                sleep(1);
            } catch(InterruptedException e) {
                e.printStackTrace();
            }
        }
//        System.out.println();
        
        currentProcess.nextBurst();
        Burst nxtBurst= currentProcess.getCurrentBurst(); 
        //Burst nxtProc = currentProcess.nextBurst();

        if(nxtBurst == null) {
            currentProcess.terminateProcess();
            return;
        }
        RAM.addToReadyQueue(currentProcess);
        //currentProcess.letProcessReady();
    }
    
    // soqih, simple method it adds the process to the waiting list in the IODevice
    public void addProcessToDevice(Process proc) {
    	IOWaitingList.add(proc);
    }
    
    // soqih,	remove the process from the waiting IODevice list and kill it
    void killProcessFromIOQueue(Process proc) {
        if(IOWaitingList.remove(proc)) {
            proc.killProcess();
        }
    }
    
    // soqih,	get the max size process from the waiting list
   /* Process getMaxProcess() {
        Object[] wList = IOWaitingList.toArray();

        // Let the first process be the max size process
        Process maxSizeprocess = (Process) wList[0];

        for(int i = 1; i < wList.length; i++) {
        	Process currentprocess = (Process) wList[i];

            // If current process size is greater than the max process
            if(currentprocess.getSize() > maxSizeprocess.getSize())
                // Then, it is the new max process
                maxSizeprocess = currentprocess;
        }

        return maxSizeprocess;
    }*/

    boolean isEmpty() { 
    	return IOWaitingList.isEmpty(); 
    }
    	
    Process getCurrentProcess() { 
    	return currentProcess; 
    }
    	
    Queue<Process> getWaitingList() {
    	return IOWaitingList; 
    }
}