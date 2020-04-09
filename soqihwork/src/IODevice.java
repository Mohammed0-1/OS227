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
    private static PCB currentProcess;
    
    // soqih,	list for processes waiting for an IO
    private static Queue<PCB> IOWaitingList; //PCB in waiting state

    public IODevice() {
        currentProcess = null;
        IOWaitingList = new ConcurrentLinkedQueue<>();
    }

    @Override
    // soqih,	start the thread
    public void run() {
        //While OS is running, keep handling IO requests if available
        while(true) {
        	// soqih,	I don't know what poll() does
        	currentProcess = IOWaitingList.poll();

            if(currentProcess != null) {
                handleIORequest();
            } else {
            	// soqih,	I think we can replace "Utility.TIME" by 1
                //sleep for x millisecond
                try {
                    sleep(Utility.TIME);
                } catch(InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void handleIORequest() { // soqih,	i didn't understand this method well 
        
    	// Increment the process IO counter
        currentProcess.incrementIoCounter();

//        System.out.print("["+currentProcess.getPid()+"] (" + currentProcess.getCurrentBurst().getRemainingTime()+ ") -->\t");
        
        while(currentProcess.getCurrentBurst().getRemainingTime() > 0) {
            // Increment total IO time of process
            currentProcess.incrementIoTotalTime();

            // Decrement the process IO burst
            currentProcess.getCurrentBurst().decrementRemainingTime();

//            System.out.print(currentProcess.getCurrentBurst().getRemainingTime()+"\t");
            try {
                //Wait for x millisecond
                sleep(Utility.TIME);
            } catch(InterruptedException e) {
                e.printStackTrace();
            }
        }
//        System.out.println();

        Burst nextBurst = currentProcess.nextBurst();

        if(nextBurst == null) {
            currentProcess.terminateProcess();
            return;
        }

        currentProcess.letProcessReady();
    }
    
    // soqih, simple method it adds the process to the waiting list in the IODevice
    public void addProcessToDevice(PCB process) {
    	IOWaitingList.add(process);
    }
    
    // soqih,	remove the process from the waiting IODevice list and kill it
    void killProcessFromIOQueue(PCB process) {
        if(IOWaitingList.remove(process)) {
            process.killProcess();
        }
    }
    
    // soqih,	get the max size process from the waiting list
    PCB getMaxProcess() {
        Object[] list = IOWaitingList.toArray();

        // Let the first process be the max size process
        PCB maxPCB = (PCB) list[0];

        for(int i = 1; i < list.length; i++) {
            PCB currentPCB = (PCB) list[i];

            // If current process size is greater than the max process
            if(currentPCB.getSize() > maxPCB.getSize())
                // Then, it is the new max process
                maxPCB = currentPCB;
        }

        return maxPCB;
    }

    boolean isEmpty() { 
    	return IOWaitingList.isEmpty(); 
    }
    	
    PCB getCurrentProcess() { 
    	return currentProcess; 
    }
    	
    Queue<PCB> getWaitingList() {
    	return IOWaitingList; 
    }
}