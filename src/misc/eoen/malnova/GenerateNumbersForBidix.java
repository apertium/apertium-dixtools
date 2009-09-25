package misc.eoen.malnova;

public class GenerateNumbersForBidix {
    public static void mainx(String[] args) {

	String[][] unuoj = { {"x","x"},
	{"unu", "one"},
	{"du", "two"},
	{"tri", "three"},
	{"kvar", "four"},
	{"kvin", "five"},
	{"ses", "six"},
	{"sep", "seven"},
	{"ok", "eight"},
	{"naŭ", "nine"},
	};

	String[] dekoj = { "x", "x",
			 "twenty",
			 "thirty",
			 "forty",
			 "fifty",
			 "sixty",
			 "seventy",
			 "eighty",
			 "ninety",
	};

	for (int deko=2; deko<=9; deko++) {
	    for (int unuo = 1; unuo <=9; unuo++) {
		System.out.println("    <e><p><l>"+unuoj[deko][0]+"dek<b/>"+unuoj[unuo][0]+"<s n=\"num\"/></l><r>"+dekoj[deko]+"-"+unuoj[unuo][1]+"<s n=\"num\"/></r></p></e>");
	    }
	}


    }



    public static void main(String[] args) {

	String[][] unuoj = { {"x","x"},
	{"unua", "first"},
	{"dua", "secont"},
	{"tria", "third"},
	{"kvara", "fourth"},
	{"kvina", "fifth"},
	{"sesa", "sixth"},
	{"sepa", "seventh"},
	{"oka", "eight"},
	{"naŭa", "nineth"},
	};

	String[] dekoj = { "x", "x",
			 "twenty",
			 "thirty",
			 "forty",
			 "fifty",
			 "sixty",
			 "seventy",
			 "eighty",
			 "ninety",
	};

	for (int deko=2; deko<=9; deko++) {
	    for (int unuo = 1; unuo <=9; unuo++) {
		System.out.println("    <e><p><l>"+unuoj[deko][0]+"dek<b/>"+unuoj[unuo][0]+"<s n=\"num\"/></l><r>"+dekoj[deko]+"-"+unuoj[unuo][1]+"<s n=\"num\"/></r></p></e>");
	    }
	}


    }
}
