package digital.toke;

/**
 * Useful for testing lifecycle
 * 
 * @author David R. Smith <davesmith.gbs@gmail.com>
 *
 */
public class LongRunningClient {

	public static void main(String [] args) {
		
		/*
		DriverConfig config = new DriverConfig()
				.proto("http")
				.host("127.0.0.1")
				.port(8200)
				.authType("TOKEN")
				//.token(token);
				.tokenFile(new File("C:\\token.txt"));
		*/
		
		DriverConfig config = new DriverConfig()
				.proto("http")
				.host("127.0.0.1")
				.port(8200)
				.kvName("toke-kv1") // driver will provide coverage for one kv1 and one kv2
				.kv2Name("toke-kv2")
				.authType("USERPASS")
				.username("bob")
				.password("password1");
		
		// driver will auto-login 
		Driver driver = new Driver(config);
		System.out.println(driver.toString());
		
		// blocks
		 try {
		        Object lock = new Object();
		        synchronized (lock) {
		            while (true) {
		                lock.wait();
		            }
		        }
		    } catch (InterruptedException ex) {
		    }
	}

}
