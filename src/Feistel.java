import java.io.*;
import java.io.BufferedReader;

public class Feistel {
	
	public static int n;
	public static String inputStr;
	public static String keys;
	public static int a;
	public static String outputStr;
	public static String outputStrB;
	
	public static void main(String args[]) {
        readFile();        
        
        if(a==0) {
        	encryption();
        }
        else if (a==1){ 
        	decryption();
        }  
        
        writeFile();
        
    }
	
	public static void readFile() {
        String pathname = "input_feistel.txt"; 
        try (FileReader reader = new FileReader(pathname);
             BufferedReader br = new BufferedReader(reader)
        ) {
        	n = Integer.parseInt(br.readLine());    // Read size of half-block   
        	inputStr =   br.readLine();    			//Read message
        	keys = br.readLine();            	  // Read key
        	a = Integer.parseInt(br.readLine());   // 0 = encryption, 1=  decryption
        	
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
	public static void writeFile() {
		
		 try {
	            File writeName = new File("output_feistel.txt"); 
	            writeName.createNewFile(); 
	            try (FileWriter writer = new FileWriter(writeName);
	                 BufferedWriter out = new BufferedWriter(writer)
	            ) {

	                out.write(n+"\r\n");
	                out.write(outputStr+"\r\n");
	                out.write(keys+"\r\n");
	                out.write(a+"\r\n");
	                out.flush(); 
	                out.close();
	            }
	        } 
		 catch (IOException e) {
	            e.printStackTrace();
	        }
		 if (a==0)
		 {
		 try {
	            File writeName = new File("output2_feistel.txt"); 
	            writeName.createNewFile(); 
	            try (FileWriter writer = new FileWriter(writeName);
	                 BufferedWriter out = new BufferedWriter(writer)
	            ) {

	                out.write(n+"\r\n");
	                out.write(outputStrB+"\r\n");
	                out.write(keys+"\r\n");
	                out.write(a+"\r\n");
	                out.flush(); 
	                out.close();
	            }
	        } 
		 catch (IOException e) {
	            e.printStackTrace();
	        }
		 }

	}
	
	public static void encryption() {
		String leftPart= inputStr.substring(0,n);
		String rightPart=inputStr.substring(n,inputStr.length());		

		// First Round: 
		// f(X_r, K) = (X_r xor K mod (2^n)) xor K
		String tempRight = rightPart;	
		rightPart=getNextRight(leftPart,rightPart,keys);		
		leftPart=tempRight;		
		
		outputStr= rightPart+leftPart;
		
		

		// Second Round: 
		// K1 = key, K2 = reversed(K), which is reverses the bit order
		String k2= new StringBuilder(keys).reverse().toString();
		
		tempRight = rightPart;	
		rightPart=getNextRight(leftPart,rightPart,k2);		
		leftPart=tempRight;
		
		outputStrB= rightPart+leftPart;
		
	}
	
	public static void decryption() {
		String leftPart="";
		String rightPart="";
		
		leftPart=inputStr.substring(0,n);
		rightPart=inputStr.substring(n,inputStr.length());
		
		
		String tempRight = rightPart;	
		rightPart=getNextRight(leftPart,rightPart,keys);
		leftPart=tempRight;
		
		outputStr=rightPart+ leftPart;
		
	}
	
	


	public static String getNextRight(String oriLeft, String oriRight, String key){  	
		
		int keyInt=Integer.valueOf(key,2);
		int leftInt=Integer.valueOf(oriLeft,2);
		int rightInt=Integer.valueOf(oriRight,2);
		int funcResult = (rightInt + keyInt) % (int) Math.pow(2,n) ;	
		System.out.println(funcResult);
		System.out.println(leftInt);
		System.out.println(keyInt);
		int resultInt=funcResult^leftInt^keyInt;
		String out=Integer.toBinaryString(resultInt);
		System.out.println(out);
		while(out.length()<n)	{	   		
			out="0"+out;	   		
		}
		return out;		
   }
	
	
	
	
}