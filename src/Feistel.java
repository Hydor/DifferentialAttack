import java.io.*;
import java.io.BufferedReader;

public class Feistel {
	
	public static int n;
	public static String inputStr;
	public static String keys;
	public static int a;
	public static String outputStr;
	
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
        	n = Integer.parseInt(br.readLine());      
        	inputStr =   br.readLine();    
        	keys = br.readLine();            	
        	a = Integer.parseInt(br.readLine());  
        	
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
	

		
	

		//System.out.println(a);

	}
	
	public static void encryption() {
		String leftPart="";
		String rightPart="";
		
		leftPart=inputStr.substring(0,n);
		rightPart=inputStr.substring(n,inputStr.length());
		char[] keyChar=keys.toCharArray();
		for (int i=0;i< keys.length();i++)
		{
			System.out.println("i:"+i);
			System.out.println("leftPart:"+ leftPart); 
			System.out.println("rightPart"+rightPart);
			String tempRight = rightPart;	
			rightPart=getNextRight(leftPart,rightPart,keyChar[i]);
			leftPart=tempRight;
			

		}
		
		outputStr= rightPart+leftPart;
		
	}
	
	public static void decryption() {
		String leftPart="";
		String rightPart="";
		
		leftPart=inputStr.substring(0,n);
		rightPart=inputStr.substring(n,inputStr.length());
		
		
		char[] keyChar=keys.toCharArray();
		for (int i=keys.length()-1;i>=0;i--)
		{
			String tempRight = rightPart;	
		
			rightPart=getNextRight(leftPart,rightPart,keyChar[i]);
			leftPart=tempRight;
			
			

		}
		outputStr=rightPart+ leftPart;
		
	}
	
	


	public static String getNextRight(String oriLeft, String oriRight, char key){  	
		
		int keyInt=Integer.valueOf(Character.toString(key),2);
		int leftInt=Integer.valueOf(oriLeft,2);
		int rightInt=Integer.valueOf(oriRight,2);
		int funcResult = (rightInt + keyInt) % (int) Math.pow(2,n) ;		
		int resultInt=funcResult^leftInt^keyInt;
		String out=Integer.toBinaryString(resultInt);
		
		while(out.length()<n)	{	   		
			out="0"+out;	   		
		}
		return out;		
   }
	
	
	
	
}