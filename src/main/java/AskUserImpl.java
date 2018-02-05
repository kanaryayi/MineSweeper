
public class AskUserImpl implements AskUser {

	@Override
	public int ask() {
		
		return StdIn.readInt();

	}

}
