package core;

public class ImageChunks {

    private final int x;
    private final int y;
    private final int size;
    public final int[] fromX;//INCLUSIVE
    public final int[] toX;//EXCLUSIVE
    public final int[] fromY;//INCLUSIVE
    public final int[] toY;//EXCLUSIVE
    private final int chunkX;
    private final int chunkY;
    
    /**
     *
     */
    public ImageChunks(int x, int y, int size){
        this.x = x;
        this.y = y;
        assert x > 100 && y > 100;
        assert size >= 2 && size <= 64;
        this.size = size;
        this.fromX = new int [size];
        this.toX = new int [size];
        this.fromY = new int [size];
        this.toY = new int [size];
        this.chunkX = x / size;
        this.chunkY = y / size;
        compute();
    }
    
    /**
     *
     */    
    private void compute(){
        for(int i = 1; i < size; i++){
            
            toX[i - 1] = (i * chunkX);
            fromX[i] = toX[i - 1];
            toY[i - 1] = (i * chunkY);
            fromY[i] = toY[i - 1];
            
        }
        toX[size - 1] = this.x;
        toY[size - 1] = this.y;
    }
    
    /**
     *
     */    
    public void testPrint(){
        System.out.println("TEST PRINT CHUNK ----------------------------\n");
        System.out.println("chunk x "+chunkX);
        System.out.println("chunk y "+chunkY);
        for(int x: fromX) System.out.print(" fromX "+x+"\n");
        for(int x: toX) System.out.print(" toX "+x+"\n");
        for(int y: fromY) System.out.print(" fromY "+y+"\n");
        for(int y: toY) System.out.print(" toY "+y+"\n");
        System.out.println("TEST PRINT CHUNK ----------------------------");
    }
}

