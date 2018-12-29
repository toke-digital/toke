package digital.toke;

import java.util.Set;

public class Housekeeping implements Runnable {

	private Set<Token> tokens;
	
	private Auth auth;
	
	public Housekeeping(Auth auth) {
		this.auth = auth;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

	public void setTokens(Set<Token> tokens) {
		this.tokens = tokens;
	}


}
