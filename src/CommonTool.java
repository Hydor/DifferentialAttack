    
import java.util.BitSet;

public final class CommonTool {
	  private CommonTool() {
	  }

	  
	  
	  public static int BitSetToInteger(BitSet set, int n) {
	    int acc = 0;
	    for (int i = 0; i < n; i++) {
	      acc += ((set.get(n - i - 1)) ? 1 : 0) << i;
	    }
	    return acc;
	  }

	  //Convert integer to bit set with desired value 	   
	  public static BitSet IntToBitSet(int value, int n) {
	    int idx = n - 1;
	    BitSet result = new BitSet();
	    while (value > 0) {
	      result.set(idx--, value % 2 == 1);
	      value /= 2;
	    }
	    return result;
	  }

 
	  public static boolean IsEqualsBitSet(BitSet a, BitSet b, int n) {
	    for (int i = 0; i < n; i++) {
	      boolean ca = a.get(i);
	      boolean cb = b.get(i);
	      if (ca != cb)
	        return false;
	    }
	    return true;
	  }

	  
	  public static BitSet copyBitSet(BitSet set, int n) {
	    BitSet newset = new BitSet();
	    for (int i = 0; i < n; i++) {
	      newset.set(i, set.get(i));
	    }
	    return newset;

	  }
	  
	//Increment the numeric value represented by this bit set	   
	  public static BitSet increment(BitSet set, int n) {
	    boolean carry = true;
	    int place = n - 1;
	    while (carry && place >= 0) {
	      carry = set.get(place);
	      set.set(place, !set.get(place));
	      place--;
	    }
	    return set;
	  }

	  //Get the direct sum (XOR) of all the bits	   
	  public static boolean DirectSum(BitSet set, int n) {
	    boolean acc = false;
	    for (int i = 0; i < n; i++) {
	      acc ^= set.get(i);
	    }
	    return acc;
	  }
	  

	  //Concatenate bit sets with specified numbers of bits to bit set with number	   
	  public static BitSet concatenate(BitSet a, int totalBitsA, BitSet b,
	      int totalBitsB) {
	    for (int i = 0; i < totalBitsB; i++) {
	      a.set(totalBitsA + i, b.get(i));
	    }
	    return a;
	  }

	  //Concatenate an array of bit sets of equal length	   
	  public static BitSet concatenate(BitSet[] sets, int totalBitsEach) {
	    BitSet result = new BitSet(sets.length * totalBitsEach);
	    for (int i = 0; i < sets.length; i++) {
	      for (int j = 0; j < totalBitsEach; j++) {
	        result.set((i * totalBitsEach) + j, sets[i].get(j));
	      }
	    }
	    return result;
	  }
	  
	  
	  public static void printBitSet(BitSet set, int n) {
	    int ptr = 0;
	    while (ptr < n) {
	      int val = CommonTool.BitSetToInteger(set.get(ptr, ptr + 4), 4);
	      System.out.print(Integer.toHexString(val).toUpperCase());

	      ptr += 4;
	    }
	    System.out.println();
	  }
	}