package digital.toke;

import java.io.File;

public class LongRunningClient {

	public static void main(String [] args) {
		
		DriverConfig config = new DriverConfig()
				.proto("http")
				.host("127.0.0.1")
				.port(8200)
				.authType("TOKEN")
				//.token(token);
				.tokenFile(new File("C:\\token.txt"));
		
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
