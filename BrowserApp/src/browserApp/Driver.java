package browserApp;

public class Driver {

	public static void main(String[] args) {
		int i = 0;
		while (true) {
			new WebClient(i).start();
			i++;
		}
	}

}
