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
    private static PCB cuProcess;
    
    // soqih,	list for processes waiting for an IO
    private static Queue<PCB> IOWaitingList; //PCB in waiting state

    public IODevice() {
        cuProcess = null;
        IOWaitingList = new ConcurrentLinkedQueue<>();
    }

    @Override
    // soqih,	start the thread
    public void run() {
        //While OS is running, keep handling IO requests if available
        while(true) {
        	// soqih,	I don't know what poll() does
        	cuProcess = IOWaitingList.poll();

            if(cuProcess != null) {
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
        cuProcess.incrementIoCounter();

//        System.out.print("["+currentProcess.getPid()+"] (" + currentProcess.getCurrentBurst().getRemainingTime()+ ") -->\t");
        
        while(cuProcess.getCurrentBurst().getRemainingTime() > 0) {
            // Increment total IO time of process
            cuProcess.incrementIoTotalTime();

            // Decrement the process IO burst
            cuProcess.getCurrentBurst().decrementRemainingTime();

//            System.out.print(currentProcess.getCurrentBurst().getRemainingTime()+"\t");
            try {
                //Wait for x millisecond
                sleep(1);
            } catch(InterruptedException e) {
                e.printStackTrace();
            }
        }
//        System.out.println();

        Burst nxtProc = cuProcess.nextBurst();

        if(nxtProc == null) {
            cuProcess.terminateProcess();
            return;
        }

        cuProcess.letProcessReady();
    }
    
    // soqih, simple method it adds the process to the waiting list in the IODevice
    public void addProcessToDevice(PCB proc) {
    	IOWaitingList.add(proc);
    }
    
    // soqih,	remove the process from the waiting IODevice list and kill it
    void killProcessFromIOQueue(PCB proc) {
        if(IOWaitingList.remove(proc)) {
            proc.killProcess();
        }
    }
    
    // soqih,	get the max size process from the waiting list
    PCB getMaxProcess() {
        Object[] wList = IOWaitingList.toArray();

        // Let the first process be the max size process
        PCB maxSizePCB = (PCB) wList[0];

        for(int i = 1; i < wList.length; i++) {
            PCB currentPCB = (PCB) wList[i];

            // If current process size is greater than the max process
            if(currentPCB.getSize() > maxSizePCB.getSize())
                // Then, it is the new max process
                maxSizePCB = currentPCB;
        }

        return maxSizePCB;
    }

    boolean isEmpty() { 
    	return IOWaitingList.isEmpty(); 
    }
    	
    PCB getCurrentProcess() { 
    	return cuProcess; 
    }
    	
    Queue<PCB> getWaitingList() {
    	return IOWaitingList; 
    }
}