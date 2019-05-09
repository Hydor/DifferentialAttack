import java.io.*;
import java.util.Random;

public class Attack {

	public static String[] keyStr;
	public static String[] sBoxStr;
	public static String[] permStr;
	public static int m;
	public static String[] inputStr;
	public static int a;
	public static int Round=6;
	public static String[] outputStr;
	public static int rightPairsCount;
	public static int pairsCount=10000;
	public static String dP="000000000000000000000110";
	
	public static void main(String args[]) {
		readFile();
				
		System.out.println("The Differential Cryptanalysis Attack on 6 round SPN is Running...\n\n");
		
		// Step 1 : Print Difference Distribution Table
		PrintDifferenceDistributionTable();
		
		// Step 2 : Generate Pairs
		String[][] rightPairs = generatePairs(pairsCount);
		
		// Step 3 : Count Guess Sub Key Hit Time
		int[][] hitCounts=  countGuessKeyHitTime(rightPairs);
		    
		// Step 4 : Determine the Probabilities
		calculateProbability(hitCounts);

		 
	}
	
	
	/*
	 * Print Difference Distribution Table
	 * GET Differential Distribution Table  
	 * dX = {0000, ..., 1111} X = {0000, ...,1111} 
	 * X' = X xor dX Y' = s-box(X') 
	 * dY = Y xor Y' 
	 * Traversing the value of the remaining dX = {0000, ..., 1111}
	 */
	public static void PrintDifferenceDistributionTable()
	{
		int[][] differenceDistributionTable = new int[16][16];     //Store as [dx][dY count]
		
		for (int dX=0;dX<16 ; dX++)
		{
			for (int X=0;X<16 ; X++)
			{
				int dY = Integer.valueOf(sboxOutput4(dX ^ X ),2)^Integer.valueOf(sboxOutput4(X),2);
				differenceDistributionTable[dX][dY]++;		
			}
		}
		System.out.println("  Step 1:");
		System.out.println("  Difference Distribution Table: \n");
		System.out.println("\t   0  1  2  3  4  5  6  7  8  9 10 11 12 13 14 15");
		System.out.println("\t   -----------------------------------------------");
		for (int dX=0;dX<16 ; dX++)
		{
			System.out.print("dX="+dX + "\t");
			for (int Yc=0;Yc<16 ; Yc++)		
			{
				System.out.print( "  " + differenceDistributionTable[dX][Yc]);
			}
			System.out.println();
		}
	}
	
	
	// Generate p number of pairs, choose right pair from these generated pairs
	// Right pairs should satisfy [0000 0000 **** **** 0000 0000] format
	 public static String[][] generatePairs(int pairs) {
		 	String[][] rightPairs = new String[pairs][2];		   
		    SPN_Enc spn = new SPN_Enc();
		    int k=0;
		    
		    for (int i = 0; i < pairs; i++) {
		      
		      
		      String p1=generatePlaintext();
		      String c1 = spn.encryption(CommonTool.addSpaceEvery8(p1)); 		    
 
		      
		      String p2 = CommonTool.xorString(p1, dP);	
		      String c2 = spn.encryption(CommonTool.addSpaceEvery8(p2)); 
		          
		      // discard the wrong pairs
		      String dC = CommonTool.xorString(c1.replace(" ", ""), c2.replace(" ", ""));
		     
		      // Right pairs should satisfy [0000 0000 **** **** 0000 0000] format
		      if( (Integer.valueOf(dC.substring(0,8),2) ==0 )
		    		 
		    		  && (Integer.valueOf(dC.substring(16,24),2) ==0 )){
		    	  	rightPairs[k][0] = c1;
		    	  	rightPairs[k][1] = c2; 
		    	  	k++;
		    	  }		     
		    }
		    rightPairsCount=k;


			System.out.println("--------------------------------------------------------- \n\n");
			System.out.println(" Step 2:");	
			System.out.println(" Generate Pairs Finished... \n");	
			System.out.println(" Total Pairs Count:" + pairsCount);		
			System.out.println(" Right Pairs Count:" + rightPairsCount);
			
		    return rightPairs;
		  }

	 
	 // generate a random plain text  
	 public static String generatePlaintext()
	 {
		 Random r = new Random();		
	      int n = r.nextInt(16777215);
	      String p= Integer.toBinaryString(n);		  
	      while (p.length()!=24)
	      {
	    	  p="0"+p;
	      }
	      return p;
	 }
	 
	 
	 // Count Guess SubKey Hit Time
	 public static int[][] countGuessKeyHitTime(String[][] rightPairs)
	 {
		
		 int[][] hitCounts = new int[16][16];
		  
		
		    for (int i=0; i < rightPairsCount ; i++) {
		       
		      String c1 = rightPairs[i][0].replace(" ", "");
		      String c2 = rightPairs[i][1].replace(" ", "");
 
		      
		      for (int k1 = 0; k1 < 16; k1++) {
		        for (int k2 = 0; k2 < 16; k2++) {
		         
		        	// K1 is the partial sub-key guess for key bits 0 to 4 
		        	// K2 is the partial sub-key guess for key bits 8 to 12
		          String subkey1 = CommonTool.intToBinaryString(k1, 4);
		          String subkey2 = CommonTool.intToBinaryString(k2, 4);
		          
		          // Backtrack to find dUA by xor UA1 and UA2 
		          String UA1 =decPartialGuess(c1.substring(8,12),  subkey1);		        
		          String UA2 = decPartialGuess(c2.substring(8,12),  subkey1);		          
		          String dUA =  CommonTool.xorString(UA1, UA2);
  
		          String UB1 =decPartialGuess(c1.substring(12,16),  subkey2);		        
		          String UB2 = decPartialGuess(c2.substring(12,16),  subkey2);		          
		          String dUB =  CommonTool.xorString(UB1, UB2);

		     	
		         
		          //System.out.println  (Integer.valueOf(dUB,2));
		          // 4 - compute the differential expression
		          // right dU6 = 0000 0000 1100 0001 0000 0000
		      	  if ((Integer.valueOf(dUA,2) ==12)  && (Integer.valueOf(dUB,2) ==1) )  
		      		   hitCounts[k1][k2]  ++;
		        }
		      }
		      
		    }
		    System.out.println("--------------------------------------------------------- \n\n");
			System.out.println(" Step 3:");	
			System.out.println(" Count Guess SubKey Hit Time Finished... \n");	
		    return hitCounts;
		    }
	 
	 
	 // Calculate Probabilities by sub-key hit time
	 // Find the max value
	 // Print the results
	 public static void calculateProbability(int[][] hitCounts)
	 {
		 System.out.println("--------------------------------------------------------- \n\n");
			System.out.println(" Step 4:");	
			System.out.println(" Calculate Probabilities Finished... \n");	
		 double max =0;
		    double[][] prob = new double[16][16];

		    System.out.println( );
		    for (int k1 = 0; k1 < 16; k1++) {
		        for (int k2 = 0; k2 < 16; k2++) {
		        	prob[k1][k2]=  hitCounts[k1][k2] / ((double) rightPairsCount);
		        	System.out.print("subkey1=" +k1 + ", subkey2=" + k2 + ": " +  prob[k1][k2] + "\t\t" );
		        	 if (max <  prob[k1][k2]) {
		                    max = prob[k1][k2]; 
		                }	 
		        }
		     System.out.println();
		     }
		    System.out.println("--------------------------------------------------------- \n\n");
			System.out.println(" Step 5:");	 
		    System.out.println(" Max Probabilities: " + max + "\n" );

		    System.out.println(" All subkeys that meet the max probability is: "  );
		    for (int k1 = 0; k1 < 16; k1++) {
		        for (int k2 = 0; k2 < 16; k2++) {
		        	 if (max ==  prob[k1][k2]) { 		        		 
		        		 System.out.print("subkey1= " );
		        		 System.out.printf("%x",  k1 );
		        		 System.out.print(", subkey2= ") ;
		        		 System.out.printf("%x",  k2 );
		        		 System.out.println();
		        	 }
		        }
		    }
		    System.out.println("\n" + " The right K7[9-12] and K7[13-16] is [1100 0001] that is [C 1] "   + "\n" );

		    System.out.println(" The guessed subkeys contains the correct key. However, the exact key cannot be found because there are too many results of the same probability. "   + "\n" );
		        
	 }
	 
	 
	 //Decryption Partial Guess in Last Round
	 public static String decPartialGuess(String  partialc, String partialk) {
		 	String xorResult = CommonTool.xorString(partialc, partialk);		   
		 	return sboxOutput4(xorResult);
		     
		  }
	  
	  
	public static String sboxOutput4 (String inputStr){
			 
		String[] sboxSub=new String[16];
		for(int i=0;i<sBoxStr.length;i++) {sboxSub[i]=sBoxStr[i].substring(4, 8);}
		
	   		char[] buf=inputStr.toCharArray();
			int n=0;
			if(buf[0]=='1') {n += 8;}
			if(buf[1]=='1') {n += 4;}
			if(buf[2]=='1') {n += 2;}
			if(buf[3]=='1') {n += 1;}
	        return sboxSub[n]; 
	        
	        }
	public static String sboxOutput4 (int index){
		 
		String[] sboxSub=new String[16];
		for(int i=0;i<sBoxStr.length;i++) {sboxSub[i]=sBoxStr[i].substring(4, 8);}
		
	        return sboxSub[index]; 
	        
	        }
	
	public static void readFile() {
        String pathname = "input_spn.txt"; 
        try (FileReader reader = new FileReader(pathname);
             BufferedReader br = new BufferedReader(reader)
        ) {
        	keyStr = br.readLine().split(" ");    
        	sBoxStr =   br.readLine().split(" ");    
        	permStr = br.readLine().split(" ");            	
        	m = Integer.parseInt(br.readLine());
        	inputStr = br.readLine().split(" ");    
        	a = Integer.parseInt(br.readLine());          	
        	       
        	
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
