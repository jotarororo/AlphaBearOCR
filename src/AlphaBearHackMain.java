import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;


public class AlphaBearHackMain {

	public static void main(String[] args) throws FileNotFoundException {
		Scanner dictionary = new Scanner(new File("dict.txt"));
		AlphaBearSolver abs = new AlphaBearSolver(dictionary);
		String input = "";
		while(!input.equals("QUIT")){
			Scanner reader = new Scanner(System.in);
			System.out.println("Enter input:");
			input = reader.next();
			ArrayList<String> results = abs.getWords(input);
			displayFirstFive(results);
		}
	}

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
