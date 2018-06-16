package core.tasks;

import java.util.concurrent.RecursiveAction;
import core.ImagePreprocesor;

abstract class BaseTask extends RecursiveAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2114509344399124353L;
	protected final int xFromIncl;
	protected final int xToExcl;
	protected final int yFromIncl;
	protected final int yToExcl;

	protected final ImagePreprocesor ip;

	/**
	 * 
	 */
	protected BaseTask(ImagePreprocesor ip, Integer xFrom, Integer xTo, Integer yFrom, Integer yTo) {
		this.ip = ip;
		// note (int) only, of course
		this.xFromIncl = xFrom;
		this.xToExcl = xTo;
		this.yFromIncl = yFrom;
		this.yToExcl = yTo;
	}

	/**
	 * 
	 */
	@Override
	public String toString() {
		return " -----| " + this.xFromIncl + " | " + this.xToExcl + " | " + this.yFromIncl + " | " + this.yToExcl
				+ " -----| " + ip;
	}

}
