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
	
	private int devToMakeItValidRoutable,
	    lookVicinity,
	    surfaceOfSalientArea1,
	    surfaceOfSalientArea2,
	    surfaceOfSalientArea3,
	    surfaceOfSalientArea4;
	//reasonable defaults to start with: - aplicationContext.xml
	private boolean visual, debug;
	
	@Override
	public void kickStart() {
        Runner runner = new Runner(devToMakeItValidRoutable, lookVicinity,
        		surfaceOfSalientArea1, surfaceOfSalientArea2, surfaceOfSalientArea3, surfaceOfSalientArea4,
        		visual, debug);
        Thread forked = new Thread(runner);
        forked.start();
        //try {
			//Thread.currentThread().join();
		//} catch (InterruptedException e) {
			//e.printStackTrace();
			//throw new RuntimeException("interupted main thread");
		//}
	}

	public int getSurfaceOfSalientAreaConstant1() {
		return surfaceOfSalientArea1;
	}

	public void setSurfaceOfSalientAreaConstant1(int surface) {
		this.surfaceOfSalientArea1 = surface;
	}
	
	public int getSurfaceOfSalientAreaConstant2() {
		return surfaceOfSalientArea2;
	}

	public void setSurfaceOfSalientAreaConstant2(int surface) {
		this.surfaceOfSalientArea2 = surface;
	}
	
	public int getSurfaceOfSalientAreaConstant3() {
		return surfaceOfSalientArea3;
	}

	public void setSurfaceOfSalientAreaConstant3(int surface) {
		this.surfaceOfSalientArea3 = surface;
	}
	public int getSurfaceOfSalientAreaConstant4() {
		return surfaceOfSalientArea4;
	}

	public void setSurfaceOfSalientAreaConstant4(int surface) {
		this.surfaceOfSalientArea4 = surface;
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
