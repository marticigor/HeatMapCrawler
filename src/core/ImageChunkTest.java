package core;

public class ImageChunkTest {

    public void test(){
        ImageChunks ich = new ImageChunks(111,131,2);
    ich.testPrint();
        ImageChunks ich1 = new ImageChunks(120,140,4);
    ich1.testPrint();
        ImageChunks ich2 = new ImageChunks(120,200,2);
    ich2.testPrint();
        ImageChunks ich3 = new ImageChunks(513,675,12);
    ich3.testPrint();
    
    }
}
