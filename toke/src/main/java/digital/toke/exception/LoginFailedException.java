package digital.toke.exception;

/**
 * Thrown when login fails for a network-oriented reason
 * 
 * @author David R. Smith <davesmith.gbs@gmail.com>
 *
 */
public class LoginFailedException extends TokeException {

	private static final long serialVersionUID = 1L;

	public LoginFailedException() {
	}

	public LoginFailedException(String arg0) {
		super(arg0);
		
	}

	public LoginFailedException(Throwable arg0) {
		super(arg0);
	}


}
