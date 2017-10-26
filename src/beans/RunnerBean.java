package beans;

import core.Runner;
import ifaces.for_string_beans.I_KickStart;

public class RunnerBean implements I_KickStart{

	
	// TODO but how...
	//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	public static final String NAME = "test_7";
	public static final String TABLE_SHOTS = NAME + "_shots";
	public static final String TABLE_NODES = NAME + "_nodes";
	public static final String TABLE_ADJACENTS = NAME + "_adjacents";
	//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	
	private int devToMakeItValidRoutable, lookVicinity, surfaceOfSalientArea;
	//reasonable defaults to start with: 30,2,32
	private boolean visual, debug;
	
	@Override
	public void kickStart() {
        Runner runner = new Runner(devToMakeItValidRoutable, lookVicinity, surfaceOfSalientArea,
        		visual, debug);
        Thread forked = new Thread(runner);
        forked.start();
        try {
			Thread.currentThread().join();
		} catch (InterruptedException e) {
			e.printStackTrace();
			throw new RuntimeException("interupted main thread");
		}
	}

	public int getSurfaceOfSalientArea() {
		return surfaceOfSalientArea;
	}

	public void setSurfaceOfSalientArea(int surfaceOfSalientArea) {
		this.surfaceOfSalientArea = surfaceOfSalientArea;
	}

	public int getLookVicinity() {
		return lookVicinity;
	}

	public void setLookVicinity(int lookVicinity) {
		this.lookVicinity = lookVicinity;
	}

	public int getDevToMakeItValidRoutable() {
		return devToMakeItValidRoutable;
	}

	public void setDevToMakeItValidRoutable(int devToMakeItValidRoutable) {
		this.devToMakeItValidRoutable = devToMakeItValidRoutable;
	}

	public boolean isVisual() {
		return visual;
	}

	public void setVisual(boolean visual) {
		this.visual = visual;
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}
}
