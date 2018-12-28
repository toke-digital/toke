package digital.toke.exception;

public class OutOfTokensException extends TokeException {

	private static final long serialVersionUID = 1L;

	public OutOfTokensException() {}

	public OutOfTokensException(String arg0) {
		super(arg0);
	}

	public OutOfTokensException(Throwable arg0) {
		super(arg0);
	}


}
