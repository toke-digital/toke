package digital.toke.accessor;

public class SealStatus extends Accessor {

	public SealStatus(Toke resp) {
		super(resp);
	}
	
	public boolean isSealed() {
		return json().optBoolean("sealed", true);
	}
	
	/**
	 * @return the threshhold or -1 if not present in response
	 */
	public int threshhold() {
		return json().optInt("t", -1);
	}
	
	/**
	 * @return the number of shares or -1 if not present in response
	 */
	public int numberOfShares() {
		return json().optInt("n", -1);
	}
	
	@Override
	public String toString() {
		return toke.toString();
	}

}
