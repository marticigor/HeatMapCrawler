package core;

import ifaces.IColorScheme;
import lib_duke.ImageResource;
import lib_duke.Pixel;

public class ImageSharpener implements IColorScheme {

 private final ImageResource inMap;
 private final ImageResource sharpenedMap;

 private int avg = 0;
 private int dev = 0;

 private final int devToMakeItValidRoutable; //80;
 private final int offsetForSharpener;

 public ImageSharpener(int valueOfDev, int offsetForSharpener) {
  this.devToMakeItValidRoutable = valueOfDev;
  this.offsetForSharpener = offsetForSharpener;
  
  //initMap
  inMap = new ImageResource(); //opens dialog
  //drawMap
  inMap.draw();
  //initSharpenedMap
  sharpenedMap = new ImageResource(inMap.getWidth(), inMap.getHeight());
 }
 
 public int getX(){ return inMap.getWidth();}
 public int getY(){ return inMap.getHeight();}
 
 /**
  *
  */
 public void sharpen(int widthFrom, int widthTo, int heightFrom, int heightTo, boolean wholePicture) {

  widthFrom = (wholePicture) ? 0 : widthFrom;
  widthTo = (wholePicture) ? inMap.getWidth() : widthTo;

  heightFrom = (wholePicture) ? 0 : heightFrom;
  heightTo = (wholePicture) ? inMap.getHeight() : heightTo;

  Pixel outP;
  Pixel inP;
  int valueOfGray;

  for ( int y = heightFrom; y < heightTo; y++ ) {
   for ( int x = widthFrom; x < widthTo; x++ ) {
       
    inP = inMap.getPixel(x, y);
    outP = sharpenedMap.getPixel(inP.getX(), inP.getY());

    if (inP.getRed() == inP.getGreen() && inP.getGreen() == inP.getBlue()) {

     valueOfGray = inP.getRed();
     outP.setRed(valueOfGray);
     outP.setGreen(valueOfGray);
     outP.setBlue(valueOfGray);


    } else if (isRoutable(inP) && (inP.getX() > offsetForSharpener &&
               inP.getX() < inMap.getWidth() - offsetForSharpener) &&
              (inP.getY() > offsetForSharpener && inP.getY() < inMap.getHeight() - offsetForSharpener)) {
     outP.setRed(redScheme[0]);
     outP.setGreen(redScheme[1]);
     outP.setBlue(redScheme[2]);
    } else {
     outP.setRed(lightGreenScheme[0]);
     outP.setGreen(lightGreenScheme[1]);
     outP.setBlue(lightGreenScheme[2]);
    }
   }
  }
  // Canny edge detection stage here begin
  // sharpenedMap is the resource
  
  // Canny edge detection stage here end
 }

 /**
  *
  */
 private boolean isRoutable(Pixel p) {

  avg = (p.getRed() + p.getGreen() + p.getBlue()) / 3;
  dev = 0;
  dev += Math.abs(p.getRed() - avg);
  dev += Math.abs(p.getGreen() - avg);
  dev += Math.abs(p.getBlue() - avg);

  if (dev > devToMakeItValidRoutable) return true;
  return false;
 }
 
 /**
  *
  */
 public ImageResource getSharpened() {
  return sharpenedMap;
 }
}
