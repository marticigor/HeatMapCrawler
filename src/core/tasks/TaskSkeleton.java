package core.tasks;

import core.ImagePreprocesor;

public class TaskSkeleton extends BaseTask {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6392626622144260023L;

	public TaskSkeleton(ImagePreprocesor ip, Integer xFrom, Integer xTo, Integer yFrom, Integer yTo) {
		super(ip, xFrom, xTo, yFrom, yTo);
	}

	@Override
	protected void compute() {
		// System.out.println("COMPUTE SKELETON ON THREAD " +
		// Thread.currentThread() + this.toString());
		ip.procesSkeleton(xFromIncl, xToExcl, yFromIncl, yToExcl, false); // false
																			// for
																			// real
																			// segments
	}

	@Override
	public String toString() {
		return super.toString() + this.hashCode() + this.getClass().getName();
	}

}
