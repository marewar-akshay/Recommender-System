package rmse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class MAEValidation {

	public static void main(String[] args) throws IOException {
		File file = new File(args[0]);
		BufferedReader br = new BufferedReader(new FileReader(file));

		int users = Integer.parseInt(args[1]) + 1;
		int items = Integer.parseInt(args[2]) + 1;
		String line = "";
		int[][] ratingMatrix = new int[users][items];
		while ((line = br.readLine()) != null) {
			String[] values = line.split(" ");
			ratingMatrix[Integer.parseInt(values[0])][Integer.parseInt(values[1])] = Integer.parseInt(values[2]);
		}
		br.close();

		file = new File(args[3]);
		br = new BufferedReader(new FileReader(file));
		int sum = 0, count = 0, missing = 0, exact = 0;
		while ((line = br.readLine()) != null) {
			String[] values = line.split("\\W+");
			int a = ratingMatrix[Integer.parseInt(values[0])][Integer.parseInt(values[1])];
			if (a == 0) {
				missing++;
			}
			int b = Integer.parseInt(values[2]);
			if (a == b) {
				exact++;
			}
			sum = sum + (Math.abs(a - b));
			count++;
		}
		System.out.println("sum of diff: "+sum);
		System.out.println("MAE = " + ((double) sum / count));
		System.out.println("Missing = " + missing);
		System.out.println("Exact = "+exact);
	}

}
