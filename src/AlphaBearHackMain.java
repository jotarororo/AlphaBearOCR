import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.imageio.ImageIO;

// AlphaBearHackMain takes a screenshot of AlphaBear and perfrom image processing to get produce
//     available words in the screenshot 
public class AlphaBearHackMain {

	private static int dataColor = -2295585;
	private static int whiteColor = -1;
	private static int blackColor = -16777216;

	public static void main(String[] args) throws IOException {
	    long startTime = System.currentTimeMillis();
	    
		File img = new File("img.png");
		BufferedImage in = ImageIO.read(img);
		int width = in.getWidth();
		int height = in.getHeight();
		BufferedImage croped = in.getSubimage(0, (height-width)/3, width, width);
		//System.out.println(in.getRGB(0, 0)); //-2295585
		binarize(croped, dataColor);
		//outputImage(croped, "saved");
		
		/*
		Scanner dictionary = new Scanner(new File("dict.txt"));
		AlphaBearSolver abs = new AlphaBearSolver(dictionary);
		String input = "";
		Scanner reader = new Scanner(System.in);
		while(!input.equals("QUIT")){
			System.out.println("Enter input:");
			input = reader.next();
			ArrayList<String> results = abs.getWords(input);
			displayFirstFive(results);
		}
		*/
		
	    long stopTime = System.currentTimeMillis();
	    long elapsedTime = stopTime - startTime;
	    System.out.println("Execution time: " + elapsedTime + "ms");
	}

	// Perform binarization on the input BufferedImage based on the dataColor
	public static void binarize(BufferedImage img, int dataColor){
		for(int i=0; i<img.getWidth(); i++) {
            for(int j=0; j<img.getHeight(); j++) {
            	if(img.getRGB(i, j) != dataColor){
            		img.setRGB(i, j, blackColor);
            	}else {
            		img.setRGB(i, j, whiteColor);
            	}
            }
		}
	}
	
	//Takes a BufferedImage and the title of the output file name, and write PNG image to that file
	public static void outputImage(BufferedImage croped, String title) throws IOException{
		File outputfile = new File(title + ".png");
	    ImageIO.write(croped, "png", outputfile);
	}
	
	// Takes a list of String, and print the first five elements in there
	public static void displayFirstFive(ArrayList<String> results){
		System.out.print("[");
		if(results.size() > 5){
			for(int i = 0; i < 4; i++){
				System.out.print(results.get(i) + ", ");
			}
		}
		System.out.println(results.get(4) + "]");	
	}
}
