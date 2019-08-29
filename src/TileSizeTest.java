// 20/23 success rate

import java.awt.Frame;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;

import net.sourceforge.javaocr.ocrPlugins.mseOCR.CharacterRange;
import net.sourceforge.javaocr.ocrPlugins.mseOCR.OCRScanner;
import net.sourceforge.javaocr.ocrPlugins.mseOCR.TrainingImage;
import net.sourceforge.javaocr.ocrPlugins.mseOCR.TrainingImageLoader;


public class TileSizeTest {
	private static int dataColor = -2295585;
	private static int whiteColor = -1;
	private static int blackColor = -16777216;
	private static int borderColor = -5736387;
	
	public static void main(String[] args) throws IOException {
		long startTime = System.currentTimeMillis();
		File folder = new File("test");
		for(File f : folder.listFiles()){
			if(!f.getName().startsWith(".")){
				BufferedImage in = ImageIO.read(f);
				int width = in.getWidth();
				int height = in.getHeight();
				BufferedImage croped = in.getSubimage(0, (height-width)/2, width, width);
				int tileSize = getTileSize(croped, width);
				//System.out.println(f.getName());
				String actSize = f.getName().split("\\.")[0].split("-")[0];
				if(actSize.equals("" + tileSize)){
					System.out.println("PASS " + f.getName());
				} else {
					System.out.println("-FAIL " + f.getName());
				}
			}
		}
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
            	if(img.getRGB(x, y) == currentColor){
            		counter++;
            	} else {
            		if(counter > 80 && counter < 130){
            			double ratio = counter/(width*1.0);
            			//System.out.println("ratio is :" + ratio + " counter is:" + counter +  " " + x + " " + y);
            			if (ratio < 0.089814815) {
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
}
