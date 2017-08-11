import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
import java.util.Scanner;

public class RecommenderSystem {
	private Scanner filein;
	private final int noOfUser = 943;
	private final int noOfItems = 1682;
	private int[][] mMatrix = new int[noOfUser+1][noOfItems+1];
	private double[][] similarMatrix = new double[noOfUser+1][noOfItems+1];
	private int[] rowSum = new int[noOfUser+1];
	private int[] ratingCount = new int[noOfUser+1];
	private int[][] arrhm1 = new int[noOfUser+1][noOfUser+1];
	private double[][] arrhm = new double[noOfUser+1][noOfUser+1];
	private int[][] pred_array = new int[noOfUser+1][noOfItems+1]; 
	private static FileWriter filewriter;
	private static File file;
	
	
	/**
	 * This methods writes the output data to file.
	 */
	private void writeOutputToFile(){
		try {
			file = new File("Output.txt");
			filewriter = new FileWriter(file);
			for(int i=1;i<=noOfUser;i++){
				for(int j=1;j<=noOfItems;j++){
					filewriter.write(i + " " + j + " " + pred_array[i][j] + "\n");
				}
			}
			filewriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		} 		
	}
	
	/**
	 * this method recommends the user to give rating to the missing items.
	 */
	private void predictMissingValues(){
		for(int i=1;i<=noOfUser;i++){
			for(int j=1;j<=noOfItems;j++){
				if(mMatrix[i][j] == 0){
					double pred_numerator = 0.0;
					double pred_denominator = 0.0;
					for(int k=1;k<=arrhm.length-1;k++){
						if(mMatrix[k][j] != 0){
							pred_numerator += mMatrix[k][j] * arrhm[i][k];
							pred_denominator += Math.abs(arrhm[i][k]);
						
						}
					}
					
					double pred_missing_value = 0.0;
					pred_missing_value = Math.round(pred_numerator/pred_denominator);
				/*	if(pred_numerator == 0.0 && pred_denominator==0.0){
						pred_missing_value = 1;
					}
				*/	
					if(pred_missing_value < 1)
						pred_missing_value = 1;
					else if(pred_missing_value > 5)
						pred_missing_value = 5;
				/*	else if(pred_missing_value <= 1.7d)
						pred_missing_value = 1;
					else if(pred_missing_value > 1.7d && pred_missing_value <= 2.2d)
						pred_missing_value = 2;
					else if(pred_missing_value > 2.2d && pred_missing_value <= 3.25d)
						pred_missing_value = 3;
					else if(pred_missing_value > 3.25d && pred_missing_value <=4.06d)
						pred_missing_value = 4;
					else if(pred_missing_value > 4.06d && pred_missing_value <=5.0d)
						pred_missing_value = 5;
				*/	
					pred_array[i][j] = (int)pred_missing_value;
					
				}
				else
					pred_array[i][j] = mMatrix[i][j];
			}
		}
	}
	
	
	/**
	 * This method return the pearson coefficient calculated by the formula based on pearson 
	 * correlation.
	 * @return pearson coefficient.
	 */
	private double pearsonCorrelation(double[] X, double[] Y, int countX, int countY) {
/*		double sumXY = 0.0;
		double squareX = 0.0;
		double squareY = 0.0;
		double pCoefficient = 0.0;
		
		for(int i=1;i<X.length;i++){
			sumXY += X[i] * Y[i];
			squareX += Math.pow(X[i], 2);
			squareY += Math.pow(Y[i], 2);
		}

		pCoefficient = (sumXY/(Math.sqrt(squareX * squareY)));
		
*/		double sumXY1 = 0.0;
		double sumX1 = 0.0;
		double sumY1 = 0.0;
		double squareX1 = 0.0;
		double squareY1 = 0.0;
		double squareSumX1 = 0.0;
		double squareSumY1 = 0.0;
		
		double pCoefficient = 0.0;
		
		for(int i=1;i<X.length;i++){
			sumXY1 += X[i] * Y[i];
			sumX1 += X[i];
			sumY1 += Y[i];
			squareX1 += Math.pow(X[i], 2);
			squareY1 += Math.pow(Y[i], 2);
		}
		
		squareSumX1 = Math.pow(sumX1, 2);
		squareSumY1 = Math.pow(sumY1, 2);
		
		pCoefficient = ((noOfItems * sumXY1) - (sumX1 * sumY1))/(Math.sqrt(((noOfItems * squareX1) - squareSumX1)*((noOfItems * squareY1) - squareSumY1)));

		return pCoefficient;
	}
	
	/**
	 * This method calculates the Pearson coefficient based on the normalized matrix.
	 */
	private void calculatePearsonCoefficient(){
		for(int i=1;i<=noOfUser;i++){
			for(int j=1;j<=noOfUser;j++){
				if(i != j)
				{	
					double pearsonCoefficient = 0.0;
					pearsonCoefficient = pearsonCorrelation(similarMatrix[i],similarMatrix[j], ratingCount[i], ratingCount[j]);
					arrhm[i][j] = pearsonCoefficient;
					arrhm1[i][j] = j;
				}
			}
		}
	}
	
	/**
	 * This method normalizes the given matrix by calculating the mean and 
	 * subtracting mean from each rating.
	 */
	private void normalizeMatrix(){

	for(int i=1;i<=noOfUser;i++){
			for(int j=1;j<=noOfItems;j++){
				if(mMatrix[i][j] != 0){
					similarMatrix[i][j] = (double) (mMatrix[i][j]) - ((double)rowSum[i]/noOfItems);
				}
				else
					similarMatrix[i][j] = (double) (mMatrix[i][j]) - ((double)rowSum[i]/noOfItems);
			}
		}
	}
	
	/**
	 * This method reads input data from file and puts into the 2D matrix.
	 */
	private void setMatrix() {
		try {
			filein = new Scanner(new FileInputStream("train_all_txt.txt"));
			while(filein.hasNext()){
				int user = filein.nextInt();
				int item = filein.nextInt();
				int rating = filein.nextInt();
				mMatrix[user][item]=rating;
				ratingCount[user]++;
				rowSum[user] += rating;
			}
		} catch (FileNotFoundException e) {
			System.err.println("Error: File Not found.");
			System.exit(1);
		}
	}

	public static void main(String[] args) {
		RecommenderSystem RS = new RecommenderSystem();
		RS.setMatrix();
		RS.normalizeMatrix();
		RS.calculatePearsonCoefficient();
		RS.predictMissingValues();
		RS.writeOutputToFile();
	}
}
