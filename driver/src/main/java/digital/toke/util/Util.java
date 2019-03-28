package digital.toke.util;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

public class Util {

	public static String mask(String sensitiveVal) {
		
		if(sensitiveVal == null) return "<null>";
		if(sensitiveVal == "") return "<empty-string>";
		if(sensitiveVal != null && sensitiveVal.trim().length()>0) return "XXXXXXXXX";
		
		return "";
		
	}
	
	public static String base64(String in) {
		try {
			return Base64.getEncoder().encodeToString(in.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {}
		
		return null; // should not get here, UTF-8 pretty standard encoding. ;-)
	}
	
}
