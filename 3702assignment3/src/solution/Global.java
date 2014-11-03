package solution;

public class Global {
	public final static int ERROR = 0; // Nothing except critical errors
	public final static int INFO = 1; // Only important information
	public final static int DEBUG = 2; // Absolutely everything
	
	protected static void log(int mode, String str) {
		if (DEBUG >= mode) {
			System.out.println(str);
		}
	}
}