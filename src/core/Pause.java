package core;

public class Pause {

	/**
	 * 
	 */
	public static void pause(int pause) {
		// if(true)return;
		System.err.println("WARNING - PAUSE " + pause / 1000 + " s.");
		try {
			Thread.sleep(pause);
		} catch (Exception e) {
		}
	}

}