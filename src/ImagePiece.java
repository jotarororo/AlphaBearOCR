import java.awt.image.BufferedImage;

import javax.management.InvalidApplicationException;

import net.sourceforge.javaocr.ocrPlugins.mseOCR.CharacterRange;
import net.sourceforge.javaocr.ocrPlugins.mseOCR.OCRScanner;
import net.sourceforge.javaocr.scanner.PixelImage;


public class ImagePiece {
	private BufferedImage img;
	private boolean hasInfo = false;
	private String letter;
	private static int blackColor = -16777216;
	private static int whiteColor = -1;
	private static int dataColor = -2295585;
	private int letterPixelCount;

	public ImagePiece(BufferedImage img, int x, int y, double pieceSize, OCRScanner scanner) throws InvalidApplicationException {
		this.img = img.getSubimage((int)(x*pieceSize), (int)(y*pieceSize), (int)(pieceSize), (int)(pieceSize));
		letterPixelCount = 0;
		CharacterRange[] cr = {new CharacterRange('A', 'Y')};
        for(int iy=0; iy<this.img.getHeight() && !hasInfo; iy++) {
    		for(int ix=0; ix<this.img.getWidth() && !hasInfo; ix++) {
            	if(this.img.getRGB(ix, iy) != whiteColor){
            		hasInfo = true;
            		double cy = 25.0;
            		double cx = getLetterWidth(iy, ix, this.img);
    	        	//System.out.println("getLetterWidth is " + cx);
            		cutCorner(cy/154, cx/this.img.getWidth(), ix, iy);
        	        String text = scanner.scan(this.img, 0, 0, 0, 0, cr);
        	        letter = text.trim();
        	        
        	        while((letter.contains("\n") || (!letter.startsWith(" ") && letter.contains(" "))) && cx > 0 && cy > 0 ){
        	        	//System.out.println("cutting");
        	        	cutCorner(cy--/154, cx--/this.img.getWidth(), ix, iy);
            	        text = scanner.scan(this.img, 0, 0, 0, 0, null);
            	        letter = text.trim();
        	        	//System.out.println(letter);
        	        }
        	        if(letter.trim().length() != 1) {
        	        	throw new InvalidApplicationException("Invalid Size");
        	        }
    	        	//System.out.println(letter);

            	}
            }
		}
			
	}
	
	public double getLetterWidth(int iy, int ix, BufferedImage img){
		double result = 0.0;
		int initial = ix;
		for(int x = ix; x<img.getHeight(); x++) {
			if(img.getRGB(x, iy) != whiteColor){
				result = x - ix;
			}
		}
		if(result <= 29){
			return 29;
		}else {
			return result;
		}
	}
	
	public void cutCorner(double heightRatio, double widthRatio, int ix, int iy){
		int w = (int) (img.getWidth()*widthRatio);
		int h = (int) (img.getHeight()*heightRatio);

		for(int y = h+iy; y<img.getHeight(); y++) {
            for(int x = w+ix; x<img.getWidth(); x++) {
            	if( y < 0){
            		return;
            	}
            	img.setRGB(x, y, whiteColor);
            }
		}
	}
	
	public boolean isHasInfo() {
		return hasInfo;
	}

	public String getLetter() {
		return letter;
	}

	public void setLetter(String letter) {
		this.letter = letter;
	}
	
	public BufferedImage getImage(){
		return this.img;
	}

	public int getLetterPixelCount() {
		return letterPixelCount;
	}
}
