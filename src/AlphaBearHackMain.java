import java.awt.Color;
import java.awt.Component;
import java.awt.Frame;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.management.InvalidApplicationException;

import net.sourceforge.javaocr.ocrPlugins.mseOCR.CharacterRange;
import net.sourceforge.javaocr.ocrPlugins.mseOCR.OCRScanner;
import net.sourceforge.javaocr.ocrPlugins.mseOCR.TrainingImage;
import net.sourceforge.javaocr.ocrPlugins.mseOCR.TrainingImageLoader;

// AlphaBearHackMain takes a screenshot of AlphaBear and perfrom image processing to get produce
//     available words in the screenshot 
public class AlphaBearHackMain {

	private static int dataColor = -2295585;
	private static int whiteColor = -1;
	private static int blackColor = -16777216;
	private static int tileSize = 0;
	
	public static void main(String[] args) throws IOException {
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
	    System.out.println("Done loading OCR");
		File img = new File("test/9-ELPDHTHEELRHINRURETSUEE.png");
		BufferedImage in = ImageIO.read(img);
		int width = in.getWidth();
		int height = in.getHeight();
		BufferedImage croped = in.getSubimage(0, (height-width)/2, width, width);
		//System.out.println(in.getRGB(0, 0)); //-2295585
		tileSize = getTileSize(croped, width);
		System.out.println(tileSize);
		binarize(croped, dataColor);
	    System.out.println("Done binarization");
		//outputImage(croped, "saved");
		ImagePiece[][] grid = divideImage(croped, width, scanner);
		//printGrid(grid);
		//solveWord();
		Scanner dictionary = new Scanner(new File("words.txt"));
		AlphaBearSolver abs = new AlphaBearSolver(dictionary);
		ArrayList<String> results = abs.getWords(printLetters(grid));
		displayFirstFive(results);
		long stopTime = System.currentTimeMillis();
	    long elapsedTime = stopTime - startTime;
	    System.out.println("Execution time: " + elapsedTime + "ms");
	}

	public static void solveWord() throws FileNotFoundException{
		Scanner dictionary = new Scanner(new File("words.txt"));
		AlphaBearSolver abs = new AlphaBearSolver(dictionary);
		String input = "";
		Scanner reader = new Scanner(System.in);
		while(!input.equals("QUIT")){
			System.out.println("Enter input:");
			input = reader.next();
			ArrayList<String> results = abs.getWords(input);
			displayFirstFive(results);
		}
	}
	
	public static void printGrid(ImagePiece[][] grid) throws IOException{
		for(int y=0; y<tileSize; y++) {
            for(int x=0; x<tileSize; x++) {
            	if(grid[x][y].isHasInfo()){
            		outputImage(grid[x][y].getImage(), "smallPiece/"+y+x+grid[x][y].getLetter());
            	}
            	if(grid[x][y].isHasInfo()){
            		System.out.println("printGrid: " + grid[x][y].isHasInfo() + grid[x][y].getLetter());
            	}
            }
       	}
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
		System.out.println(result);
		return result;
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
	
	//Takes a BufferedImage and the title of the output file name, and write PNG image to that file
	public static void outputImage(BufferedImage croped, String title) throws IOException{
		File outputfile = new File(title + ".png");
	    ImageIO.write(croped, "png", outputfile);
	    System.out.println("Image saved");
	}
	
	// Takes a list of String, and print the first five elements in there
	public static void displayFirstFive(ArrayList<String> results){
		System.out.print("[");
		int size = 0;
		if(results.size() > 5){
			size = 5;
		} else {
			size = results.size();
		}
		for(int i = 0; i < size; i++){
			System.out.print(results.get(i) + ", ");
		}
		System.out.println(results.get(size-1) + "]");	
	}
}
