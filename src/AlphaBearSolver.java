import java.util.ArrayList;
import java.util.Scanner;


public class AlphaBearSolver {
	private ArrayList<LetterInventory> dict;
	
	public AlphaBearSolver(Scanner dictionary) {
		dict = new ArrayList<LetterInventory>();
		while(dictionary.hasNextLine()){
			String current = dictionary.nextLine();
			dict.add(new LetterInventory(current));
		}
	}

	public ArrayList<String> getWords(String input) {
		ArrayList<String> results = new ArrayList<String>();
		LetterInventory in = new LetterInventory(input);
		for(LetterInventory li:dict){			
			if(in.subtract(li) != null){
				results.add(li.getOriginal());
			}
		}
		java.util.Collections.sort(results, new MyComparator(""));
		return results;
	}
}
