package core.image_filters.filter_utils;

public class DirectionEnumeration {

    private final static double[] DIRDEG;
    static {
    	DIRDEG = new double[8];
        initDirection();
    }
    
    public static void testPrint() {

        int[] xA = new int[] {
            0,
            0,
            -80,
            80
        };
        int[] yA = new int[] {
            -80,
            80,
            0,
            0
        };

        for (int i = 0; i < xA.length; i++) {

            System.out.println("x " + xA[i] + " y " + yA[i] + " atan2(y,x) = " +
                Math.atan2((double) yA[i], (double) xA[i]) + " DEG = " +
                Math.toDegrees(Math.atan2((double) yA[i], (double) xA[i])));

        }

        for (int x = -5; x < 6; x++) {
            for (int y = -5; y < 6; y++) {
                double atanD = Math.atan2((double) y, (double) x);
                double dirDegD = Math.toDegrees(atanD) + 180.0;
                System.out.println("x " + x + " y " + y + " atan2(y,x) = " +
                    atanD + " DEG = " + dirDegD + " >> " + getDirection(dirDegD));
                System.out.println("___________________________________________");

            }
        }
    }

    private static void initDirection() {
        for (int i = 0; i < 8; i++) {
            DIRDEG[i] = 22.5 + ((i * 2) * 22.5);
            System.out.println(" INIT " + DIRDEG[i]);
        }
    }

    public static Direction getDirection(double dirD) {

        if ((dirD >= DIRDEG[7] || dirD < DIRDEG[0])) return Direction.N;
        if ((dirD >= DIRDEG[3] && dirD < DIRDEG[4])) return Direction.S;

        if ((dirD >= DIRDEG[0] && dirD < DIRDEG[1])) return Direction.NE;
        if ((dirD >= DIRDEG[4] && dirD < DIRDEG[5])) return Direction.SW;

        if ((dirD >= DIRDEG[1] && dirD < DIRDEG[2])) return Direction.E;
        if ((dirD >= DIRDEG[5] && dirD < DIRDEG[6])) return Direction.W;

        if ((dirD >= DIRDEG[2] && dirD < DIRDEG[3])) return Direction.SE;
        if ((dirD >= DIRDEG[6] && dirD < DIRDEG[7])) return Direction.NW;

        return Direction.ERROR;
    }

    public enum Direction {
        N,
        NE,
        E,
        SE,
        S,
        SW,
        W,
        NW,
        ZERO,
        ERROR
    }
}