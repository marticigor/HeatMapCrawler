package core.tasks;

import core.ImagePreprocesor;

public class TaskSharpen extends BaseTask {

	// Instances of RecursiveAction represent executions that do not yield a
	// return value.

	// First and foremost, fork/join tasks should operate as “pure” in-memory
	// algorithms in which no I/O operations come into play. Also, communication
	// between tasks through shared state should be avoided as much as possible,
	// because that implies that locking might have to be performed. Ideally,
	// tasks
	// communicate only when one task forks another or when one task joins
	// another.

	// http://www.oracle.com/technetwork/articles/java/fork-join-422606.html

	/**
	 * 
	 */
	private static final long serialVersionUID = 3395050943108602251L;

	public TaskSharpen(ImagePreprocesor ip, Integer xFrom, Integer xTo, Integer yFrom, Integer yTo) {
		super(ip, xFrom, xTo, yFrom, yTo);
	}

	@Override
	protected void compute() {
		// System.out.println("COMPUTE SHARPEN ON THREAD " +
		// Thread.currentThread() + this.toString());
		ip.procesSharpen(xFromIncl, xToExcl, yFromIncl, yToExcl, false); // false
																			// for
																			// real
																			// segments
	}

	@Override
	public String toString() {
		return super.toString() + this.hashCode() + this.getClass().getName();
	}
}
