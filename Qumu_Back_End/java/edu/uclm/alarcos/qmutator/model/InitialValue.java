package edu.uclm.alarcos.qmutator.model;

public enum InitialValue {
	ZERO, ONE, POS_I, NEG_I, PLUS, MINUS;

	static InitialValue parse(Object value) {
		if (value.toString().equals("0"))
			return ZERO;
		if (value.toString().equals("1"))
			return ONE;
		if (value.equals("i"))
			return POS_I;
		if (value.equals("-i"))
			return NEG_I;
		if (value.equals("+"))
			return PLUS;
		return MINUS;
	}

	String getValue() {
		if (this==ZERO)
			return "0";
		if (this==ONE)
			return "1";
		if (this==POS_I)
			return "i";
		if (this==NEG_I)
			return "-i";
		if (this==PLUS)
			return "+";
		return "-";
	}
}
