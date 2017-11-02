package core.salient_areas_detectors;

import lib_duke.ImageResource;
import ifaces.I_ColorScheme;
import ifaces.I_SalientDetector;

public class SimilaritySalientDetector implements I_SalientDetector, I_ColorScheme {

	public SimilaritySalientDetector(
			ImageResource workBench,
			ImageResource noded,
			ImageResource testAgainst,
			int borderInSharpenStage, 
			int lookAheadAndBack,
			int surfaceConstant1_1, int surfaceConstant1_2,
			int surfaceConstant2_1, int surfaceConstant2_2,
			int neighbourghsConstant,
			boolean visual, boolean debug
			) {

		this.workBench = workBench;
		this.noded = noded;
		this.testAgainst = testAgainst;
		this.borderInSharpenStage = borderInSharpenStage;
		this.lookAheadAndBack = lookAheadAndBack;
		this.width = workBench.getWidth();
		this.height = workBench.getHeight();
		this.surfaceConstant1_1 = surfaceConstant1_1;
		this.surfaceConstant1_2 = surfaceConstant1_2;
		this.surfaceConstant2_1 = surfaceConstant2_1;
		this.surfaceConstant2_2 = surfaceConstant2_2;
		this.neighbourghsConstant = neighbourghsConstant;
		this.visual = visual;
		this.debug = debug;
	}

	private ImageResource workBench, noded, testAgainst;
	private int borderInSharpenStage, lookAheadAndBack;
	private int width, height;
	private int surfaceConstant1_1, surfaceConstant1_2, surfaceConstant2_1,
			surfaceConstant2_2, neighbourghsConstant;
	private boolean visual, debug;
	
	
	@Override
	public void detectSalientAreas(boolean testAgainstAnotherImageResource) {
		

	}
	
	private static final int KERNEL_BORDERS = 2;
    private static final int[][] KERNEL_VER = new int[][] {
        {
            0,0,1,0,0
        }, {
        	0,0,1,0,0
        }, {
        	0,0,1,0,0
        }, {
        	0,0,1,0,0
        }, {
        	0,0,1,0,0
        }
    };
    private static final int[][] KERNEL_HOR = new int[][] {
        {
            0,0,0,0,0
        }, {
        	0,0,0,0,0
        }, {
        	1,1,1,1,1
        }, {
        	0,0,0,0,0
        }, {
        	0,0,0,0,0
        }
    };
    private static final int[][] KERNEL_DIAG_FORWARD = new int[][] {
        {
            0,0,0,0,1
        }, {
        	0,0,0,1,0
        }, {
        	0,0,1,0,0
        }, {
        	0,1,0,0,0
        }, {
        	1,0,0,0,0
        }
    };
    private static final int[][] KERNEL_DIAG_BACKWARD = new int[][] {
        {
            1,0,0,0,0
        }, {
        	0,1,0,0,0
        }, {
        	0,0,1,0,0
        }, {
        	0,0,0,1,0
        }, {
        	0,0,0,0,1
        }
    };
}

