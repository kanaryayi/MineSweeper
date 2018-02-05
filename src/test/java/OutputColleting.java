import java.util.ArrayList;

public class OutputColleting implements Output {

	ArrayList<String> outputs = new ArrayList<>();

	@Override
	public void println(String s) {
		// TODO Auto-generated method stub
		outputs.add(s);
	}

}
