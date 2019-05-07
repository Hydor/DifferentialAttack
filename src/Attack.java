import java.io.*;

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
		 
		
		// GET Differential Distribution Table
		//
		// dX = {0000, ..., 1111}
        //  X = {0000, ..., 1111}
		// X' = X xor dX
		// Y' = s-box(X')
		// dY = Y xor Y' 
		// Traversing the value of the remaining dX = {0000, ..., 1111}
		
		int[][] differenceDistributionTable = new int[16][16];     //Store [dx][dY count]
		
		for (int dX=0;dX<16 ; dX++)
		{
			System.out.println(dX);
			System.out.println("dx ------------------------------ ");
			for (int X=0;X<16 ; X++)
			{
				int dY = Integer.valueOf(sboxOutput4(dX ^ X ),2)^Integer.valueOf(sboxOutput4(X),2);
				differenceDistributionTable[dX][dY]++;
				System.out.println(dY);
				 
			}
		}
		
		for (int dX=0;dX<16 ; dX++)
		{
			for (int Yc=0;Yc<16 ; Yc++)		
			{
				System.out.println(differenceDistributionTable[dX][Yc]);
			}
			}
		
		//Integer.toBinaryString(i1);
		
		
		//SPN_Enc.encryption();
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
