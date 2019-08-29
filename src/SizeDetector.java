import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;



public class SizeDetector {
	private static int dataColor = -2295585;
	private static int whiteColor = -1;
	private static int blackColor = -16777216;
	private static int borderColor = -5736387;
	
	public static void main(String[] args) throws IOException {
		long startTime = System.currentTimeMillis();
	    System.out.println("Done loading OCR");
		File img = new File("img22.png");
		BufferedImage in = ImageIO.read(img);
		int width = in.getWidth();
		int height = in.getHeight();
		BufferedImage croped = in.getSubimage(0, (height-width)/2, width, width);
		//System.out.println(in.getRGB(0, 0)); //-2295585
		/*
		//create the detector
		CannyEdgeDetector detector = new CannyEdgeDetector();

		//adjust its parameters as desired
		detector.setLowThreshold(0.1f);
		detector.setHighThreshold(0.1f);

		//apply it to an image
		detector.setSourceImage(croped);
		detector.process();
		BufferedImage edges = detector.getEdgesImage();
		*/
		int tileSize = getTileSize2(croped, width);
		System.out.println(tileSize);
		
		outputImage(croped, "size" + tileSize);
		long stopTime = System.currentTimeMillis();
	    long elapsedTime = stopTime - startTime;
	    System.out.println("Execution time: " + elapsedTime + "ms");
	}
	
	public static int getTileSize(BufferedImage img, int width){
		int size = 0;
		int currentColor = img.getRGB(0, 0);
		int counter = 0;
		int sumTime = 0;
		int sum = 0;
		for(int y=0; y<img.getWidth(); y++) {
            for(int x=0; x<img.getHeight(); x++) {
            	if(img.getRGB(x, y) == whiteColor){
            		counter++;
            	} else {
            		if(counter > 65 && counter < 99){
            			double ratio = counter/(width*1.0);
            			System.out.println("ratio is :" + ratio + " counter is:" + counter +  " " + x + " " + y);
            			if (ratio < 0.07) {
            				sum += 9;
            			} else {
            				sum += 7;
            			}
            			sumTime++;
            		}
            		counter=0;
            		currentColor = img.getRGB(x, y);
            	}
            }
		}
		sum/=sumTime;
		if(sum >= 8){
			return 9;
		}
		return 7;
	}
	
	public static int getTileSize2(BufferedImage img, int width){
		int size = 0;
		int currentColor = img.getRGB(0, 0);
		int counter = 0;
		int sumTime = 0;
		int sum = 0;
		for(int y=0; y<img.getWidth(); y++) {
            for(int x=0; x<img.getHeight(); x++) {
            	if(img.getRGB(x, y) == currentColor){
            		counter++;
            	} else {
            		if(counter > 80 && counter < 130){
            			double ratio = counter/(width*1.0);
            			System.out.println("ratio is :" + ratio + " counter is:" + counter +  " " + x + " " + y);
            			if (ratio < 0.09) {
            				sum += 9;
            			} else {
            				sum += 7;
            			}
            			sumTime++;
            		}
            		counter=0;
            		currentColor = img.getRGB(x, y);
            	}
            }
		}
		sum/=sumTime;
		if(sum >= 8){
			return 9;
		}
		return 7;
	}
	
	//Takes a BufferedImage and the title of the output file name, and write PNG image to that file
	public static void outputImage(BufferedImage croped, String title) throws IOException{
		File outputfile = new File(title + ".png");
	    ImageIO.write(croped, "png", outputfile);
	}
}
