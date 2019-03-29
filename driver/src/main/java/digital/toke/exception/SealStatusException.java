package digital.toke.exception;

import digital.toke.accessor.SealStatusResponseDecorator;

/**
 * Thrown when the vault is sealed when we are trying to start up
 * 
 * @author David R. Smith &lt;davesmith.gbs@gmail.com&gt;
 *
 */
public class SealStatusException extends RuntimeException {

	SealStatusResponseDecorator status;
	
	private static final long serialVersionUID = 1L;

	public SealStatusException() {}
	
	public SealStatusException(SealStatusResponseDecorator status) {
		super();
		this.status = status;
	}

	public SealStatusException(String arg0) {
		super(arg0);
	}

	public SealStatusException(Throwable arg0) {
		super(arg0);
	}

}
