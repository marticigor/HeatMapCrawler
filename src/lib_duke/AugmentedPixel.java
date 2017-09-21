package lib_duke;

public class AugmentedPixel{

	private int verGradient;
	private int horGradient;
	private double gradientComputed = Double.MIN_VALUE;
	private double direction;
	private static final double PI = Math.PI;
	private static final double [] VERT_MORE_INCL = new double[]{PI/6,(7*PI)/6};
	private static final double [] VERT_LESS_EXCL = new double[]{(11*PI)/6,(5*PI)/6};
	private static final double [] DIAG_E_MORE_INCL = new double[]{(11*PI)/6,(5*PI)/6};
	private static final double [] DIAG_E_LESS_EXCL = new double[]{(5*PI)/3,(2*PI)/3};
	private static final double [] HOR_MORE_INCL = new double[]{(5*PI)/3,(2*PI)/3};
	private static final double [] HOR_LESS_EXCL = new double[]{(4*PI)/3,PI/3};
	private static final double [] DIAG_W_MORE_INCL = new double[]{(4*PI/3),PI/3};
	private static final double [] DIAG_W_LESS_EXCL = new double[]{(7*PI)/6,PI/6};
	
	public AugmentedPixel(int v, int h) {
		this.verGradient = v;
		this.horGradient = h;
	}
	
	public int getVerGr(){return verGradient;}
	public int getHorGr(){return horGradient;}
	public double getGradientComputed(){return gradientComputed;}
	public double getDirection(){
		
		if( direction <= VERT_MORE_INCL[0] || direction > VERT_LESS_EXCL[0]) {
			//System.out.println("VERT");
		    return 2*PI;
		}
		if( direction <= VERT_MORE_INCL[1] && direction > VERT_LESS_EXCL[1]) {
			//System.out.println("VERT");
		    return 2*PI;
		}
		
		for(int i = 0; i < 2; i++){

			if( direction <= DIAG_E_MORE_INCL[i] && direction > DIAG_E_LESS_EXCL[i] ){
				//System.out.println("DIAG_E");
			    return (7*PI)/4;
			}
			if( direction <= HOR_MORE_INCL[i] && direction > HOR_LESS_EXCL[i] ){
				//System.out.println("HOR");
			    return (3*PI)/2;
			}
			if( direction <= DIAG_W_MORE_INCL[i] && direction > DIAG_W_LESS_EXCL[i] ){
			    //System.out.println("DIAG_W");	
			    return (5*PI)/4;
            }
		}
		throw new RuntimeException("ERROR APPROXIMATING DIRECTION");
	}

	public void compute(){
		//hypot function
		gradientComputed = Math.sqrt((verGradient * verGradient) + (horGradient * horGradient));
	    //atan2 function
		direction = Math.atan2(verGradient, horGradient);
	}
	
	public void test(){
	    for (double d = 2*PI; d > 0; d = d - 0.02){
	        direction = d;
	        System.out.println(d + "------------");
	        getDirection();
	    }
	    double [] edgeCases = new double []{2*PI,11*PI/6,7*PI/4,5*PI/3,3*PI/2,
	       4*PI/3,5*PI/4,7*PI/6,PI,5*PI/6,3*PI/4,2*PI/3,PI/2,PI/3,PI/4,PI/6};
	    System.out.println("--------------------------------------------------");
	    for(double d : edgeCases) {
	        direction = d;
	        System.out.println(d + "------------");
	        getDirection();
	    }    
	}
}
