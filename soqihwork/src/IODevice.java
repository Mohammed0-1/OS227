import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;


/***
 * This class represents an IO device that handles any IO requests
 * We chose to set this as a Thread class because in reality, the
 * CPU does not wait for IO bursts to finish before going to the 
 * next process.
 */
public class IODevice extends Thread {
    private static Process curruntProcess;
    
    // soqih,	list for processes waiting for an IO
    private static Queue<Process> IOWaitingList; //Process in waiting state

    public IODevice() {
        curruntProcess = null;
        IOWaitingList = new ConcurrentLinkedQueue<>();
    }

    @Override
    // soqih,	start the thread
    public void run() {
        //While OS is running, keep handling IO requests if available
        while(true) {
        	
        	curruntProcess = IOWaitingList.poll();

            if(curruntProcess != null) {
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
        curruntProcess.incrementIoCounter();

//        System.out.print("["+currentProcess.getPid()+"] (" + currentProcess.getCurrentBurst().getRemainingTime()+ ") -->\t");
        
        while(curruntProcess.getCurrentBurst().getRemainingTime() > 0) {
            // Increment total IO time of process
            curruntProcess.incrementIoTotalTime();

            // Decrement the process IO burst
            curruntProcess.getCurrentBurst().decrementRemainingTime();

//            System.out.print(currentProcess.getCurrentBurst().getRemainingTime()+"\t");
            try {
                //Wait for x millisecond
                sleep(1);
            } catch(InterruptedException e) {
                e.printStackTrace();
            }
        }
//        System.out.println();

        Burst nxtProc = curruntProcess.nextBurst();

        if(nxtProc == null) {
            curruntProcess.terminateProcess();
            return;
        }

        curruntProcess.letProcessReady();
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
    Process getMaxProcess() {
        Object[] wList = IOWaitingList.toArray();

        // Let the first process be the max size process
        Process maxSizeProcess = (Process) wList[0];

        for(int i = 1; i < wList.length; i++) {
            Process currentProcess = (Process) wList[i];

            // If current process size is greater than the max process
            if(currentProcess.getSize() > maxSizeProcess.getSize())
                // Then, it is the new max process
                maxSizeProcess = currentProcess;
        }

        return maxSizeProcess;
    }

    boolean isEmpty() { 
    	return IOWaitingList.isEmpty(); 
    }
    	
    Process getCurrentProcess() { 
    	return curruntProcess; 
    }
    	
    Queue<Process> getWaitingList() {
    	return IOWaitingList; 
    }
}