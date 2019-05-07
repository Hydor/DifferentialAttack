
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Random;


public class DifferentialAttack {

  /**
   * Generate random Plain/Cipher pairs via the SPN cipher with the given
   * round keys
   * 
   * @param pairs
   *          Number of pairs to generate
   * @param roundKeys
   *          Round keys to use for the SPN cipher
   * @return List of plain/cipher sets (each set is 16 bits long)
   */
  public List<BitSet[]> generateChosenPairs(int pairs, BitSet[] roundKeys) {
    Random r = new Random();
    SPN_Enc spn = new SPN_Enc();
    List<BitSet[]> sets = new ArrayList<BitSet[]>();
    for (int i = 0; i < pairs; i++) {
      // choose a random plaintext-ciphertext pair
      BitSet p1 = CommonTool.IntToBitSet(r.nextInt(65536), 16);
      BitSet c1 = spn.encryption(p1);

      // choose another pair such that dP = [0000 1011 0000 0000] is satisfied
      BitSet p2 = CommonTool.copyBitSet(p1, 16);
      p2.xor(CommonTool.IntToBitSet(2816, 16));
      BitSet c2 = spn.encryption(p2);

      // discard the 'wrong pairs'
      // 'right pairs' should have dC = [0000 **** 0000 ****]
      BitSet dC = CommonTool.copyBitSet(c1, 16);
      dC.xor(c2);

      if ((CommonTool.BitSetToInteger(dC.get(0, 4), 4) == 0)
          && (CommonTool.BitSetToInteger(dC.get(8, 12), 4) == 0)) {
        sets.add(new BitSet[] { c1, c2 });
      }
    }
    return sets;
  }

  /**
   * Backtrack across one s-box with a partial ciphertext and partial subkey
   * guess
   * 
   * @param partialc
   *          4 bit partial of the ciphertext
   * @param partialk
   *          4 bit partial of the subkey
   * @return
   */
  public BitSet backtrackPartialSubkeyGuess(BitSet partialc, BitSet partialk) {
    partialc = CommonTool.copyBitSet(partialc, 4);
    partialc.xor(partialk);
    
    BitSet U = sBox(partialc);
    return U;
  }

  /**
   * Mount the differential cryptanalysis attack on the SPN cipher to recover 8
   * bits of the final round key
   */
  public static void main(String[] args) {
    System.out.println("Running differential cryptanalysis on SPN....");
    // the expression to compute:
    // dU_4 = [0000 0110 0000 0110]

    // 0 - set the keys to be recovered - the partial
    // subkey bits that will be found are *2*4 in the last
    // round.
    BitSet[] roundKeys = new BitSet[] { CommonTool.IntToBitSet(0x1234, 16),
        CommonTool.IntToBitSet(0xDEAD, 16), CommonTool.IntToBitSet(0x5678, 16),
        CommonTool.IntToBitSet(0xACE1, 16), CommonTool.IntToBitSet(0xF2E4, 16), };

    // 1 - generate 10000 pairs
    int pairct = 10000;
    
    // print out the real key bits and some info
    System.out.println("4-Round SPN Differential Cryptanalysis.");
    System.out.println("Using " + pairct + " pairs.");
    BitSet realPskA = roundKeys[4].get(4, 8);
    BitSet realPskB = roundKeys[4].get(12, 16);
    System.out.println("Real partial subkey values:");
    System.out.println("\tK4[4-8]:\t" + 
        Integer.toHexString(CommonTool.BitSetToInteger(realPskA, 4)));
    System.out.println("\tK4[12-16]:\t" + 
        Integer.toHexString(CommonTool.BitSetToInteger(realPskB, 4)));
    System.out.println("**********************");
    System.out.println("Press Enter to run differential attack...");
    try {
      System.in.read();
    } catch (IOException exn) {
    
    }
      

    DifferentialAttack datk = new DifferentialAttack();
    List<BitSet[]> pairs = datk.generateChosenPairs(pairct, roundKeys);

    // 2 - for each pair, we perform the attack
    int[] counts = new int[256];
    for (int i = 0; i < counts.length; i++) {
      counts[i] = 0;
    }

    int rightpairct = pairs.size();

    int count = 0;
    for (BitSet[] pair : pairs) {
      if (count++ % (rightpairct / 100) == 0) {
        System.out
            .print(((int) (count / ((double) rightpairct) * 100)) + "%\n");
      }
      BitSet c1 = pair[0];
      BitSet c2 = pair[1];

      for (int i = 0; i < 16; i++) {
        for (int j = 0; j < 16; j++) {
          // This is the partial subkey guess for key bits 4 to 8
          BitSet partialSubkeyA = CommonTool.IntToBitSet(i, 4);

          // This is the partial subkey guess for key bits 12 to 16
          BitSet partialSubkeyB = CommonTool.IntToBitSet(j, 4);

          BitSet guessKeySet = CommonTool.concatenate(partialSubkeyA, 4,
              partialSubkeyB, 4);
          int guessKey = CommonTool.BitSetToInteger(guessKeySet, 8);

          // Backtrack to find dU-bits 4 to 8
          BitSet partialcipherA1 = c1.get(4, 8);
          BitSet UA1 = datk.backtrackPartialSubkeyGuess(partialcipherA1,
              partialSubkeyA);
          BitSet partialcipherA2 = c2.get(4, 8);
          BitSet UA2 = datk.backtrackPartialSubkeyGuess(partialcipherA2,
              partialSubkeyA);
          BitSet dUA = CommonTool.copyBitSet(UA1, 4);
          dUA.xor(UA2);

          // Backtrack to find U-bits 12-16
          BitSet partialcipherB1 = c1.get(12, 16);
          BitSet UB1 = datk.backtrackPartialSubkeyGuess(partialcipherB1,
              partialSubkeyB);
          BitSet partialcipherB2 = c2.get(12, 16);
          BitSet UB2 = datk.backtrackPartialSubkeyGuess(partialcipherB2,
              partialSubkeyB);
          BitSet dUB = CommonTool.copyBitSet(UB1, 4);
          dUB.xor(UB2);

          // 4 - compute the differential expression
          // dU_4 = [0000 0110 0000 0110]
          boolean lhs = (!dUA.get(0)) & dUA.get(1) & dUA.get(2) & (!dUA.get(3))
              & (!dUB.get(0)) & dUB.get(1) & dUB.get(2) & (!dUB.get(3));
          // increment if the above expression holds
          if (lhs == true)
            counts[guessKey]++;
        }
      }
    }

    // 5 - determine the probabilities
    double[] prob = new double[counts.length];
    for (int i = 0; i < counts.length; i++) {
      prob[i] = counts[i] / ((double) pairct);
    }

    // 6 - print the results
    int maxi = 0;
    int maxj = 0;
    double maxprob = 0;

    System.out.println();
    System.out.println("Partial subkey bits : prob");
    for (int i = 0; i < 16; i++) {
      for (int j = 0; j < 16; j++) {
        int idx = i * 16 + j;
        System.out.print(Integer.toHexString(i));
        System.out.print(" " + Integer.toHexString(j));
        System.out.println(":\t" + prob[idx]);

        if (prob[idx] > maxprob) {
          maxprob = prob[idx];
          maxi = i;
          maxj = j;
        }
      }
    }

    System.out.println();
    System.out.println();
    System.out.println("Number of 'right pairs' was " + rightpairct
        + " out of " + pairct);

    System.out.println();
    System.out.println("Best candidate partial subkey is "
        + Integer.toHexString(maxi) + " " + Integer.toHexString(maxj)
        + " with probability " + maxprob);
  }
  
  
  public  BitSet sBox (BitSet index){
		 
	   String sBoxStr = "00001110 00001111 00000000 00000001 00000010 00000011 00000100 00000101 00000110 00000111 00001000 00001001 00001010 00001011 00001100 00001101";
	   
		String[] sboxSub= sBoxStr.split(" ");
		for(int i=0;i<sboxSub.length;i++) {sboxSub[i]=sboxSub[i].substring(4, 8);}
		BitSet outbs= new BitSet();
		outbs.set(Integer.valueOf(sboxSub[CommonTool.BitSetToInteger(index,4)],2));
	        return outbs; 
	        
	  }
	

  
}