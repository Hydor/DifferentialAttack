import java.io.*;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
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
	
	
	public static void main(String args[]) {
		readFile();
		PrintDifferenceDistributionTable();
		
		
		
		generatePairs(5);
		
		//Integer.toBinaryString(i1);
		
		
		//SPN_Enc.encryption();
	}
	
	
	// GET Differential Distribution Table
	public static void PrintDifferenceDistributionTable()
	{
		// dX = {0000, ..., 1111}
        //  X = {0000, ..., 1111}
		// X' = X xor dX
		// Y' = s-box(X')
		// dY = Y xor Y' 
		// Traversing the value of the remaining dX = {0000, ..., 1111}
		
		int[][] differenceDistributionTable = new int[16][16];     //Store as [dx][dY count]
		
		for (int dX=0;dX<16 ; dX++)
		{
			for (int X=0;X<16 ; X++)
			{
				int dY = Integer.valueOf(sboxOutput4(dX ^ X ),2)^Integer.valueOf(sboxOutput4(X),2);
				differenceDistributionTable[dX][dY]++;		
			}
		}
		
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
	
	
	 public static void generatePairs(int pairs) {
		    Random r = new Random();
		    SPN_Enc spn = new SPN_Enc();

		    for (int i = 0; i < pairs; i++) {
		      // generate a random plaintext-ciphertext pair 
		      int intp1 = r.nextInt(16777215);
		      String p1= Integer.toBinaryString(intp1);		      
		      String c1 = spn.encryption(addSpaceEvery8(p1)); 
		      System.out.println(p1);
		      System.out.println(c1);
				
		    }
		      
//		      // choose another pair such that dP = [0000 1011 0000 0000] is satisfied
//		      BitSet p2 = Util.copyBitSet(p1, 24);
//		      p2.xor(Util.toBitSet(393216, 24)); /////??????????????????????????????????????????????????????
//		      BitSet c2 = spn.SpnEncBlock(p2, roundKeys);
//
//		      // discard the 'wrong pairs'
//		      // 'right pairs' should have dC = [0000 **** 0000 ****]
//		      BitSet dC = Util.copyBitSet(c1, 24);
//		      dC.xor(c2);
//
//		      if ((Util.toInteger(dC.get(0, 4), 4) == 0)
//		          && (Util.toInteger(dC.get(8, 12), 4) == 0)) {
//		        sets.add(new BitSet[] { c1, c2 });
//		      }
//		    }
//		    return sets;
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
	
	public static String addSpaceEvery8(String s)
	{
		    String regex = "(.{8})";
		    s = s.replaceAll(regex, "$1 ");
		    return s;		
	}
	
}
