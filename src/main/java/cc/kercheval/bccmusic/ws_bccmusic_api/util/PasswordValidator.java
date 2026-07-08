package cc.kercheval.bccmusic.ws_bccmusic_api.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Validates password strength with simple, practical rules:
 * - Minimum length of 8 characters
 * - Must not be in the common passwords list
 */
public final class PasswordValidator {

	private static final int MIN_LENGTH = 8;

	/**
	 * Top 100 most commonly used passwords (sourced from breach data compilations).
	 * Checked case-insensitively.
	 */
	private static final Set<String> COMMON_PASSWORDS = Set.of(
			"password", "12345678", "123456789", "1234567890", "12345678910",
			"qwerty123", "qwertyuiop", "1q2w3e4r", "aa12345678", "abc12345",
			"password1", "password123", "iloveyou", "sunshine1", "princess1",
			"football1", "charlie1", "shadow12", "master12", "dragon12",
			"trustno1", "letmein1", "baseball1", "michael1", "jordan23",
			"superman1", "harley12", "whatever1", "jennifer1", "hunter12",
			"abcdefgh", "abcd1234", "asdfghjk", "zxcvbnm1", "qwerty12",
			"welcome1", "monkey12", "passw0rd", "p@ssword", "p@ssw0rd",
			"changeme", "admin123", "login123", "hello123", "access14",
			"master123", "mustang1", "shadow123", "ashley12", "jessica1",
			"starwars1", "batman12", "dragon123", "thomas12", "robert12",
			"summer12", "winter12", "george12", "daniel12", "hannah12",
			"william1", "richard1", "charles1", "joseph12", "matthew1",
			"andrew12", "joshua12", "amanda12", "nicole12", "samantha",
			"computer", "internet", "password12", "12341234", "11111111",
			"00000000", "88888888", "99999999", "22222222", "87654321",
			"iloveu12", "lovely12", "sunshine", "princess", "football",
			"baseball", "1234abcd", "test1234", "pass1234", "user1234",
			"temp1234", "secret12", "qwer1234", "asdf1234", "zxcv1234",
			"welcome123", "letmein123", "monkey123", "dragon1234", "superman",
			"trustno12", "whatever", "jennifer", "blahblah", "fuckyou1"
	);

	private PasswordValidator() {}

	/**
	 * Validates a password and returns a list of failure reasons.
	 * Returns an empty list if the password passes all checks.
	 */
	public static List<String> validate(String password) {
		List<String> errors = new ArrayList<>();

		if (password == null || password.length() < MIN_LENGTH) {
			errors.add("Password must be at least " + MIN_LENGTH + " characters long.");
		}

		if (password != null && COMMON_PASSWORDS.contains(password.toLowerCase())) {
			errors.add("Password is too common. Please choose a more unique password.");
		}

		return errors;
	}
}
