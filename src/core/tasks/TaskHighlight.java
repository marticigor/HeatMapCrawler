package core.tasks;

import core.ImagePreprocesor;

public class TaskHighlight extends BaseTask {

	/**
	 * 
	 */
	private static final long serialVersionUID = -630567374823198437L;

	public TaskHighlight(ImagePreprocesor ip, Integer xFrom, Integer xTo, Integer yFrom, Integer yTo) {
		super(ip, xFrom, xTo, yFrom, yTo);
	}

	@Override
	protected void compute() {
        //System.out.println("COMPUTE HIGHLIGHT ON THREAD " + Thread.currentThread() + this.toString());
		ip.procesHighlight(xFromIncl, xToExcl, yFromIncl, yToExcl, false);
	}

    public String toString(){
    	return super.toString() + this.hashCode() + this.getClass().getName();
    }
	
	
}
