package core.tasks;

import java.util.concurrent.RecursiveAction;

import core.ImagePreprocesor;

public class TaskGaussian extends RecursiveAction{

	private static final long serialVersionUID = 2573784757014343735L;
	
	private final int xFromIncl;
    private final int xToExcl;
    private final int yFromIncl;
    private final int yToExcl;

    private final ImagePreprocesor ip;
    
    /**
     * 
     */
    public TaskGaussian(ImagePreprocesor ip, Integer xFrom, Integer xTo, Integer yFrom, Integer yTo) {
        this.ip = ip;
        //note (int) only, of course
        this.xFromIncl = (int)xFrom;
        this.xToExcl = (int)xTo;
        this.yFromIncl = (int)yFrom;
        this.yToExcl = (int)yTo;
    }

	@Override
	protected void compute() {
        //System.out.println("COMPUTE GAUSSIAN ON THREAD " + Thread.currentThread());
        ip.procesGaussian(xFromIncl, xToExcl, yFromIncl, yToExcl, false); //false for real segments
		
	}
	
    /**
     * 
     */
    public String toString(){
    	return this.hashCode() + this.getClass().getName() + " -----| " + this.xFromIncl + " | " +
        this.xToExcl + " | " + this.yFromIncl + " | " + this.yToExcl + " -----| " + ip;
    }
	
}
