package core.tasks;

import core.ImagePreprocesor;

public class TaskGaussian extends BaseTask {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4178515888257000505L;

	public TaskGaussian(ImagePreprocesor ip, Integer xFrom, Integer xTo, Integer yFrom, Integer yTo) {
		super(ip, xFrom, xTo, yFrom, yTo);
	}

	@Override
	protected void compute() {
		// System.out.println("COMPUTE GAUSSIAN ON THREAD " +
		// Thread.currentThread() + this.toString());
		ip.procesGaussian(xFromIncl, xToExcl, yFromIncl, yToExcl, false); // false
																			// for
																			// real
																			// segments
	}

	@Override
	public String toString() {
		return super.toString() + this.hashCode() + this.getClass().getName();
	}
}
