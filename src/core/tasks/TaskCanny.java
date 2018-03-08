package core.tasks;

import core.ImagePreprocesor;

public class TaskCanny extends BaseTask {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2710840494474806831L;

	public TaskCanny(ImagePreprocesor ip, Integer xFrom, Integer xTo, Integer yFrom, Integer yTo) {
		super(ip, xFrom, xTo, yFrom, yTo);
	}

	@Override
	protected void compute() {
		// System.out.println("COMPUTE CANNY ON THREAD " +
		// Thread.currentThread() + this.toString());
		ip.procesCanny(xFromIncl, xToExcl, yFromIncl, yToExcl, false);
	}

	@Override
	public String toString() {
		return super.toString() + this.hashCode() + this.getClass().getName();
	}
}
