import java.util.Random;

public class SelectorImpl implements Selector {
	
	Random r = new Random();
	@Override
	public int[] selectInt(int n) {
		 
		 return new int[]{StdRandom.uniform(0,n),StdRandom.uniform(0,n)};
	}

}
