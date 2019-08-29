import java.awt.Frame;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.management.InvalidApplicationException;

import net.sourceforge.javaocr.ocrPlugins.mseOCR.CharacterRange;
import net.sourceforge.javaocr.ocrPlugins.mseOCR.OCRScanner;
import net.sourceforge.javaocr.ocrPlugins.mseOCR.TrainingImage;
import net.sourceforge.javaocr.ocrPlugins.mseOCR.TrainingImageLoader;


public class OCRTest {
	private static int dataColor = -2295585;
	private static int whiteColor = -1;
	private static int blackColor = -16777216;
	private static int tileSize = 0;
	
	public static void main(String[] args) throws IOException{
        OCRScanner scanner = new OCRScanner();
        TrainingImageLoader loader = new TrainingImageLoader();
        HashMap<Character, ArrayList<TrainingImage>> trainingImageMap = new HashMap<Character, ArrayList<TrainingImage>>();
		Frame frame = new Frame();
        loader.load(frame, "letter2.png", new CharacterRange('A', 'Y'), trainingImageMap);
        loader.load(frame, "number.png", new CharacterRange('0', '9'), trainingImageMap);
        loader.load(frame, "outlier/B.png", new CharacterRange('B', 'B'), trainingImageMap);
        loader.load(frame, "outlier/C.png", new CharacterRange('C', 'C'), trainingImageMap);
        loader.load(frame, "outlier/C2.png", new CharacterRange('C', 'C'), trainingImageMap);
        loader.load(frame, "outlier/D.png", new CharacterRange('D', 'D'), trainingImageMap);
        loader.load(frame, "outlier/D2.png", new CharacterRange('D', 'D'), trainingImageMap);
        loader.load(frame, "outlier/G.png", new CharacterRange('G', 'G'), trainingImageMap);        
        loader.load(frame, "outlier/J.png", new CharacterRange('J', 'J'), trainingImageMap);
        loader.load(frame, "outlier/N.png", new CharacterRange('N', 'N'), trainingImageMap);
        loader.load(frame, "outlier/M.png", new CharacterRange('M', 'M'), trainingImageMap);
        loader.load(frame, "outlier/O.png", new CharacterRange('O', 'O'), trainingImageMap);
        loader.load(frame, "outlier/O2.png", new CharacterRange('O', 'O'), trainingImageMap);
        loader.load(frame, "outlier/R.png", new CharacterRange('R', 'R'), trainingImageMap);
        loader.load(frame, "outlier/T.png", new CharacterRange('T', 'T'), trainingImageMap);
        loader.load(frame, "outlier/U.png", new CharacterRange('U', 'U'), trainingImageMap);
        scanner.addTrainingImages(trainingImageMap);
        
		long startTime = System.currentTimeMillis();
		File folder = new File("test");
		int totalTest = 0;
		int passTest = 0;
		for(File f : folder.listFiles()){
			if(!f.getName().startsWith(".")){
				BufferedImage in = ImageIO.read(f);
				int width = in.getWidth();
				int height = in.getHeight();
				
				BufferedImage croped = in.getSubimage(0, (height-width)/2, width, width);
				//System.out.println(in.getRGB(0, 0)); //-2295585
				tileSize = getTileSize(croped, width);
				binarize(croped, dataColor);
				ImagePiece[][] grid = divideImage(croped, width, scanner);
				
				String letter = f.getName().split("\\.")[0].split("-")[1];
				if(printLetters(grid).equals("" + letter)){
					System.out.println("PASS " + f.getName());
					passTest++;
				} else {
					System.out.println("-FAIL " + f.getName());
				}
				totalTest++;
			}
		}
		long stopTime = System.currentTimeMillis();
	    long elapsedTime = stopTime - startTime;
	    System.out.println("Execution time: " + elapsedTime + "ms");
	    System.out.println("Success Rate " + passTest +"/"+totalTest + "->" + 1.0*passTest/totalTest*100 + "%");
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
	
	// Perform binarization on the input BufferedImage based on the dataColor
	public static void binarize(BufferedImage img, int dataColor){
		for(int i=0; i<img.getWidth(); i++) {
            for(int j=0; j<img.getHeight(); j++) {
            	if(img.getRGB(i, j) != dataColor){
            		img.setRGB(i, j, whiteColor);
            	}else {
            		img.setRGB(i, j, blackColor);
            	}
            }
		}
	}
	
	public static ImagePiece[][] divideImage(BufferedImage img, int imageSize, OCRScanner scanner){
		ImagePiece[][] grid = new ImagePiece[tileSize][tileSize];
		for(int y=0; y<tileSize; y++) {
            for(int x=0; x<tileSize; x++) {
				try {
					grid[x][y] = new ImagePiece(img, x, y, (double)imageSize/tileSize, scanner);
				} catch (InvalidApplicationException e) {
					System.out.println("Alternating tile size");
					tileSize = tileSize == 7 ? 9:7;
					return divideImage(img, imageSize, scanner);
				}
			}
		}
		return grid;
	}
	
	public static String printLetters(ImagePiece[][] grid) {
		String result = "";
		for(int y=0; y<tileSize; y++) {
            for(int x=0; x<tileSize; x++) {
            	if(grid[x][y].isHasInfo()){
            		result += grid[x][y].getLetter();
            	}
            }
       	}
		//System.out.println(result);
		return result;
	}
}
