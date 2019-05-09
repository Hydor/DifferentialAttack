
public final class CommonTool {
	  private CommonTool() {
	  }
	  
	  /**
	   *  Xor two Binary String  
	   * 
	   * @param str1
	   *          Binary String without space
	   * @param str2
	   *          Binary String without space
	   */
	  public static String xorString(String str1, String str2){  	
		  if (str1.length()!=str2.length())  new IllegalArgumentException("There should be same length to xor");
		   	String str= Integer.toBinaryString(Integer.valueOf(str1,2)^Integer.valueOf(str2,2));	   	
		   	while(str.length()!=str1.length())	{	   		
		   			str="0"+str;	   		
		    }
	   		return str;
	   }
	  
	  
	  /**
	   *  Xor two int 
	   * 
	   * @param i1
	   *          number 1
	   * @param i2
	   *          number 2
	   * @param l
	   *          return String length
	   */
	  public static String xorString(int i1, int i2, int l){  	
		   	String str= Integer.toBinaryString(i1^i2);	   	
		   	while(str.length()!=l)	{	   		
		   			str="0"+str;	   		
		    }
	   		return str;
	   }
	  
	  
	  
	  public static String intToBinaryString(int n)
	  {
		return Integer.toBinaryString(Integer.valueOf(n));
	  }
	  
	}