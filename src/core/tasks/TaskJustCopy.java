package core.tasks;

import core.ImagePreprocesor;

public class TaskJustCopy extends BaseTask {

	private static final long serialVersionUID = 1985432958470781686L;

	public TaskJustCopy(ImagePreprocesor ip, Integer xFrom, Integer xTo, Integer yFrom, Integer yTo) {
		super(ip, xFrom, xTo, yFrom, yTo);
	}

	@Override
	protected void compute() {
        //System.out.println("COMPUTE JUSTCOPY ON THREAD " + Thread.currentThread() + this.toString());
		ip.procesJustCopy(xFromIncl, xToExcl, yFromIncl, yToExcl, false);
	}
	
	@Override
    public String toString(){
    	return super.toString() + this.hashCode() + this.getClass().getName();
    }
}
