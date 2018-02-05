
public class FakeUser implements AskUser {
	private int[] is ;
	int c;
	public FakeUser(int... is) {
		this.is = is;
	}

	@Override
	public int ask() {
		
		// TODO Auto-generated method stub
		return is[c++];

	}

}
