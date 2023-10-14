/*
Author: Brenden Johnson
Course: 3010.03
Assignment: Project 1
Description: Performs gaussian elimination using the scaled partial pivoting method.
*/
package com.mycompany.gaussianelimination;
import java.util.*;
import java.io.*;

public class GaussianElimination {
    private static List<double[]> matrix = new ArrayList<>();
    private static List<Integer> pivotOrder = new ArrayList<>();
    private static int colcnt;
    private static int rowcnt;
    private static int iteration = 0;
    private static double[] xBar ;

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        int n = -1;
        
        //User Prompts
        while (n > 10 || n < 0) {
            System.out.print("How many linear equations would you like to solve? (0 < n <= 10): ");
            n = input.nextInt();
            if (n > 10 || n < 0) {
                System.out.println("n must abide by (0 < n <= 10)");
            }
            else{
                rowcnt = n;
                colcnt = rowcnt + 1;
            }
        }
        System.out.print("1: Read in a File\n" + "2: Manually enter coefficients\n" + "Enter 1 or 2: ");
        int choice = input.nextInt();

        //Read File if choice is 1
        if (choice == 1){
            System.out.print("Enter file name: ");
            String filename = input.nextLine();
            try{
                BufferedReader reader = new BufferedReader(new FileReader(input.nextLine()));
                String line;
                while((line = reader.readLine())!=null){
                    String[] splitline = line.split(" ", 11);
                    double[] array = new double[colcnt];
                    for(int i = 0; i < splitline.length; i++) {
                        array[i] = Double.parseDouble(splitline[i]);
                    }
                    matrix.add(array);
                }
            }catch(Exception e){
                System.out.println(e);
            }
        }
        //Manually enter coefficients otherwise
        else {
            for(int i = 0; i < rowcnt; i++){
                System.out.printf("\nEquation %d\n", i+1);
                double[] array = new double[colcnt];
                for(int j = 0; j < colcnt; j++){
                    if(j == colcnt-1)
                        System.out.print("Please enter equation solution: ");
                    else
                        System.out.printf("Please enter coefficient for X%d: ", j+1);
                    array[j] = input.nextDouble();
                }    
                matrix.add(array);
            }
        }
        
        //Output our input matrix
        System.out.println("=========================================================");
        System.out.println("Input Matrix");
        System.out.println("=========================================================");
        printMatrix();
        
        //Perform Gaussian Elimination
        elimination();
        backSubstitution();
        
        //Output Solution
        printSolution();
    }
    
    public static void elimination(){
        double[] gc = new double[rowcnt-iteration];
        double[] scaleRatios = new double[rowcnt-iteration];
        double[] multipliers =  new double[rowcnt-1-iteration];
        int pivotIndex = iteration;
        double max;
        int coefIndex = pivotOrder.size();
        
        
        System.out.println("\n=========================================================");
        System.out.printf("Elimination %d\n", iteration + 1 );
        System.out.println("=========================================================");
        
        //Find maximum coefficient for each equation
        for(int i = iteration; i < rowcnt; i++){
            max = -2000000000;
            for(int j = 0; j < colcnt; j++){
                if(matrix.get(i)[j] > max){
                   max = matrix.get(i)[j];
                }
            }
            gc[i-iteration] = max;
        }
        
        //Find scale ratios for each equations
        for(int i = iteration; i < rowcnt; i++){
            scaleRatios[i-iteration] = matrix.get(i)[iteration] /gc[i-iteration];
            if(scaleRatios[i-iteration] > scaleRatios[pivotIndex-iteration]){
                pivotIndex = i;
            }
        }
        
        //Output Scale Ratio and Pivot Row
        System.out.println("\nScale Ratios");
        System.out.println(Arrays.toString(scaleRatios));
        System.out.printf("\nPivot row at index %d\n", pivotIndex);
        System.out.println(Arrays.toString(matrix.get(pivotIndex)));
        
        //Swap pivot row to the top
        pivotOrder.add(pivotIndex);
        swapPivot(pivotIndex);
        
        //Calculate Multipliers
        double divisor = matrix.get(iteration)[coefIndex];
        for (int i = pivotOrder.size(); i < rowcnt; i++ ){
                multipliers[i-pivotOrder.size()] = matrix.get(i)[coefIndex] /divisor; 
        }

        //Subtract multiplied pivot row from remaining rows
        for(int i = pivotOrder.size(); i < rowcnt; i++){
            for(int j = coefIndex; j < colcnt; j++){
                matrix.get(i)[j]-= multipliers[i-pivotOrder.size()]*matrix.get(iteration)[j];
            }
        }
        
        System.out.println("\nMatrix after pivot swap and elimination");
        printMatrix();

        //Increment iteration and recursive call
        iteration += 1;
        if(iteration < rowcnt-1)
            elimination();
    }
    
    public static void backSubstitution(){
        xBar = new double[colcnt-1];
        double product, calcedTerms;
        for(int i = rowcnt-1 ; i > -1; i--){
            product = 0;
            calcedTerms = 0;
            for(int j = colcnt-1; j > -1; j--){
                if(j == colcnt-1)
                    product = matrix.get(i)[j];
                else{
                    calcedTerms += matrix.get(i)[j]*xBar[j];
                }
            }
        xBar[i] = (product - calcedTerms) / matrix.get(i)[i];
        }
    }
    
    public static void swapPivot(int pivotIndex){
        double[] temp = matrix.get(pivotIndex);
        matrix.set(pivotIndex, matrix.get(iteration));
        matrix.set(iteration, temp);            
    }
    
    public static void printMatrix(){
            for(int i = 0; i < matrix.size(); i++){
                System.out.print("{");
                for(int j = 0; j < colcnt; j++){
                    System.out.print(matrix.get(i)[j]);
                    if(j != rowcnt)
                        System.out.print(", ");
                }
                System.out.print("}\n");
        }
    }
    
    public static void printSolution(){
        System.out.println("\n=========================================================");
        System.out.println("Contents of solutions matrix after back substitution");
        System.out.println("=========================================================");
        for(int i = 0; i < xBar.length; i++){
            System.out.printf("X%d: %f\n", i+1, xBar[i]);
        }
    }
}