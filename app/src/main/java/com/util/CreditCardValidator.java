package com.util;

public class CreditCardValidator {

	public static final byte VISA = 0;
	public static final byte MASTERCARD = 1;
	public static final byte AMEX = 2;
	public static final byte DINERS_CLUB = 3;
	public static final byte CARTE_BLANCHE = 4;
	public static final byte DISCOVER = 5;
	public static final byte ENROUTE = 6;
	public static final byte JCB = 7;
	
	public static boolean validate(final String credCardNumber, final byte type) {
		String creditCard = credCardNumber.trim();
		boolean applyAlgo = false;
		switch (type) {
		case VISA:
			// VISA credit cards has length 13 - 15
			// VISA credit cards starts with prefix 4
			if (creditCard.length() >= 13 && creditCard.length() <= 16
					&& creditCard.startsWith("4")) {
				applyAlgo = true;
			}
			break;
		case MASTERCARD:
			// MASTERCARD has length 16
			// MASTER card starts with 51, 52, 53, 54 or 55
			if (creditCard.length() == 16) {
				int prefix = Integer.parseInt(creditCard.substring(0, 2));
				if (prefix >= 51 && prefix <= 55) {
					applyAlgo = true;
				}
			}
			break;
		case AMEX:
			// AMEX has length 15
			// AMEX has prefix 34 or 37
			if (creditCard.length() == 15
					&& (creditCard.startsWith("34") || creditCard
							.startsWith("37"))) {
				applyAlgo = true;
			}
			break;
		case DINERS_CLUB:
		case CARTE_BLANCHE:
			// DINERSCLUB or CARTEBLANCHE has length 14
			// DINERSCLUB or CARTEBLANCHE has prefix 300, 301, 302, 303, 304,
			// 305 36 or 38
			if (creditCard.length() == 14) {
				int prefix = Integer.parseInt(creditCard.substring(0, 3));
				if ((prefix >= 300 && prefix <= 305)
						|| creditCard.startsWith("36")
						|| creditCard.startsWith("38")) {
					applyAlgo = true;
				}
			}
			break;
		case DISCOVER:
			// DISCOVER card has length of 16
			// DISCOVER card starts with 6011
			if (creditCard.length() == 16 && creditCard.startsWith("6011")) {
				applyAlgo = true;
			}
			break;
		case ENROUTE:
			// ENROUTE card has length of 16
			// ENROUTE card starts with 2014 or 2149
			if (creditCard.length() == 16
					&& (creditCard.startsWith("2014") || creditCard
							.startsWith("2149"))) {
				applyAlgo = true;
			}
			break;
		case JCB:
			// JCB card has length of 16 or 15
			// JCB card with length 16 starts with 3
			// JCB card with length 15 starts with 2131 or 1800
			if ((creditCard.length() == 16 && creditCard.startsWith("3"))
					|| (creditCard.length() == 15 && (creditCard
							.startsWith("2131") || creditCard
							.startsWith("1800")))) {
				applyAlgo = true;
			}
			break;
		default:
			throw new IllegalArgumentException();
		}
		if (applyAlgo) {
			return validate(creditCard);
		}
		return false;
	}

	public static boolean validate(String creditCard) {
		// 4 9 9 2 7 3 9 8 7 1 6
		// 6
		// 1 x 2 = 2 = (0 + 2) = 2
		// 7
		// 8 x 2 = 16 = (1 + 6) = 7
		// 9
		// 3 x 2 = 6 = (0 + 6) = 6
		// 7
		// 2 x 2 = 4 = (0 + 4) = 4
		// 9
		// 9 X 2 = 18 = (1 + 8) = 9
		// 4
		// 6+2+7+7+9+6+7+4+9+9+4 = 70
		// return 0 == (70 % 10)
		int sum = 0;
		int length = creditCard.length();
		for (int i = 0; i < creditCard.length(); i++) {
			if (0 == (i % 2)) {
				sum += creditCard.charAt(length - i - 1) - '0';
			} else {
				sum += sumDigits((creditCard.charAt(length - i - 1) - '0') * 2);
			}
		}
		return 0 == (sum % 10);
	}

	private static int sumDigits(int i) {
		return (i % 10) + (i / 10);
	}
}
