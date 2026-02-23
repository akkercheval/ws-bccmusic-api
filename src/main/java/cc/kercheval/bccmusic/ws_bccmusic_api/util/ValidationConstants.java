package cc.kercheval.bccmusic.ws_bccmusic_api.util;

import java.util.regex.Pattern;

public class ValidationConstants {
	public static final Pattern zipCodePattern = Pattern.compile("^\\d{5}(-\\d{4})?$");
	public static final Pattern usStatePattern = Pattern.compile("^(AA|AE|AP|AL|AK|AS|AZ|AR|CA|CO|CT|DE|DC|FM|FL|GA|GU|\r\n"
			+ "HI|ID|IL|IN|IA|KS|KY|LA|ME|MH|MD|MA|MI|MN|MS|MO|MT|NE|\r\n"
			+ "NV|NH|NJ|NM|NY|NC|ND|MP|OH|OK|OR|PW|PA|PR|RI|SC|SD|TN|\r\n"
			+ "TX|UT|VT|VI|VA|WA|WV|WI|WY)$");
}
