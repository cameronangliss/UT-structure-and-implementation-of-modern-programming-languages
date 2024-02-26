package assignment2;

public class StrOpCoder {
	public static String strCmp() {
		return "LINK\n" +
			"PUSHIMM 0\n" +
			"STRCMPLOOP:\n" +
			"PUSHOFF -2\n" +
			"PUSHOFF 1\n" +
			"ADD\n" +
			"PUSHIND\n" +
			"PUSHOFF -1\n" +
			"PUSHOFF 1\n" +
			"ADD\n" +
			"PUSHIND\n" +
			"PUSHOFF 2\n" +
			"ISNIL\n" +
			"PUSHOFF 3\n" +
			"ISNIL\n" +
			"AND\n" +
			"JUMPC STRCMPEQ\n" +
			"PUSHOFF 2\n" +
			"PUSHOFF 3\n" +
			"GREATER\n" +
			"JUMPC STRCMPGT\n" +
			"LESS\n" +
			"JUMPC STRCMPLT\n" +
			"PUSHIMM 1\n" +
			"ADD\n" +
			"JUMP STRCMPLOOP\n" +
			"STRCMPEQ:\n" +
			"ADDSP -2\n" +
			"PUSHIMM 0\n" +
			"JUMP STRCMPDONE\n" +
			"STRCMPGT:\n" +
			"ADDSP -2\n" +
			"PUSHIMM -1\n" +
			"JUMP STRCMPDONE\n" +
			"STRCMPLT:\n" +
			"PUSHIMM 1\n" +
			"JUMP STRCMPDONE\n" +
			"STRCMPDONE:\n" +
			"STOREOFF -2\n" +
			"ADDSP -1\n" +
			"UNLINK\n" +
			"ADDSP -1\n";
	}

	public static String strConcat() {
		return "LINK\n" +
			"PUSHOFF -2\n" +
			"LINK\n" +
			"PUSHIMM 0\n" +
			"STRCONCATLOOP1:\n" +
			"DUP\n" +
			"PUSHOFF -1\n" +
			"ADD\n" +
			"PUSHIND\n" +
			"ISNIL\n" +
			"JUMPC STRCONCATDONE1\n" +
			"PUSHIMM 1\n" +
			"ADD\n" +
			"JUMP STRCONCATLOOP1\n" +
			"STRCONCATDONE1:\n" +
			"STOREOFF -1\n" +
			"UNLINK\n" +
			"PUSHOFF -1\n" +
			"LINK\n" +
			"PUSHIMM 0\n" +
			"STRCONCATLOOP2:\n" +
			"DUP\n" +
			"PUSHOFF -1\n" +
			"ADD\n" +
			"PUSHIND\n" +
			"ISNIL\n" +
			"JUMPC STRCONCATDONE2\n" +
			"PUSHIMM 1\n" +
			"ADD\n" +
			"JUMP STRCONCATLOOP2\n" +
			"STRCONCATDONE2:\n" +
			"STOREOFF -1\n" +
			"UNLINK\n" +
			"ADD\n" +
			"PUSHIMM 1\n" +
			"ADD\n" +
			"MALLOC\n" +
			"PUSHIMM 0\n" +
			"PUSHOFF -2\n" +
			"LINK\n" +
			"STRCONCATLOOP3:\n" +
			"PUSHOFF -1\n" +
			"PUSHOFF -2\n" +
			"ADD\n" +
			"PUSHIND\n" +
			"DUP\n" +
			"ISNIL\n" +
			"JUMPC STRCONCATDONE3\n" +
			"PUSHOFF -3\n" +
			"PUSHOFF -2\n" +
			"ADD\n" +
			"SWAP\n" +
			"STOREIND\n" +
			"PUSHOFF -2\n" +
			"PUSHIMM 1\n" +
			"ADD\n" +
			"STOREOFF -2\n" +
			"JUMP STRCONCATLOOP3\n" +
			"STRCONCATDONE3:\n" +
			"ADDSP -1\n" +
			"UNLINK\n" +
			"ADDSP -1\n" +
			"PUSHIMM 0\n" +
			"PUSHOFF -1\n" +
			"LINK\n" +
			"STRCONCATLOOP4:\n" +
			"PUSHOFF -1\n" +
			"PUSHOFF -2\n" +
			"ADD\n" +
			"PUSHIND\n" +
			"DUP\n" +
			"ISNIL\n" +
			"JUMPC STRCONCATDONE4\n" +
			"PUSHOFF -4\n" +
			"PUSHOFF -3\n" +
			"PUSHOFF -2\n" +
			"ADD\n" +
			"ADD\n" +
			"SWAP\n" +
			"STOREIND\n" +
			"PUSHOFF -2\n" +
			"PUSHIMM 1\n" +
			"ADD\n" +
			"STOREOFF -2\n" +
			"JUMP STRCONCATLOOP4\n" +
			"STRCONCATDONE4:\n" +
			"ADDSP -1\n" +
			"UNLINK\n" +
			"PUSHOFF 1\n" +
			"PUSHOFF 2\n" +
			"PUSHOFF 3\n" +
			"ADD\n" +
			"ADD\n" +
			"PUSHIMMCH '\0'\n" +
			"STOREIND\n" +
			"ADDSP -3\n" +
			"STOREOFF -2\n" +
			"UNLINK\n" +
			"ADDSP -1\n";
	}

	public static String strLen() {
		return "LINK\n" +
			"PUSHIMM 0\n" +
			"STRLENLOOP:\n" +
			"DUP\n" +
			"PUSHOFF -1\n" +
			"ADD\n" +
			"PUSHIND\n" +
			"ISNIL\n" +
			"JUMPC STRLENDONE\n" +
			"PUSHIMM 1\n" +
			"ADD\n" +
			"JUMP STRLENLOOP\n" +
			"STRLENDONE:\n" +
			"STOREOFF -1\n" +
			"UNLINK\n";
	}

	public static String strRepeat() {
		return "DUP\n" +
			"ISNEG\n" +
			"JUMPC STRREPEATHANDLENEGATIVE\n" +
			"STRREPEATCONTINUE:\n" +
			"SWAP\n" +
			"LINK\n" +
			"PUSHOFF -1\n" +
			"LINK\n" +
			"PUSHIMM 0\n" +
			"STRREPEATLOOP1:\n" +
			"DUP\n" +
			"PUSHOFF -1\n" +
			"ADD\n" +
			"PUSHIND\n" +
			"ISNIL\n" +
			"JUMPC STRREPEATDONE1\n" +
			"PUSHIMM 1\n" +
			"ADD\n" +
			"JUMP STRREPEATLOOP1\n" +
			"STRREPEATHANDLENEGATIVE:\n" +
			"ISPOS\n" +
			"JUMP STRREPEATCONTINUE\n" +
			"STRREPEATDONE1:\n" +
			"STOREOFF -1\n" +
			"UNLINK\n" +
			"PUSHOFF -2\n" +
			"TIMES\n" +
			"DUP\n" +
			"PUSHIMM 1\n" +
			"ADD\n" +
			"MALLOC\n" +
			"SWAP\n" +
			"PUSHOFF 1\n" +
			"ADD\n" +
			"PUSHIMMCH '\0'\n" +
			"STOREIND\n" +
			"PUSHIMM 0\n" +
			"PUSHOFF -2\n" +
			"PUSHOFF -1\n" +
			"STRREPEATBIGLOOP:\n" +
			"PUSHIMM 0\n" +
			"PUSHOFF 3\n" +
			"CMP\n" +
			"PUSHIMM 1\n" +
			"EQUAL\n" +
			"NOT\n" +
			"JUMPC STRREPEATDONE\n" +
			"LINK\n" +
			"PUSHIMM 0\n" +
			"STRREPEATLOOP3:\n" +
			"PUSHOFF -1\n" +
			"PUSHOFF 1\n" +
			"ADD\n" +
			"PUSHIND\n" +
			"DUP\n" +
			"ISNIL\n" +
			"JUMPC STRREPEATDONE3\n" +
			"PUSHOFF -4\n" +
			"PUSHOFF -3\n" +
			"ADD\n" +
			"SWAP\n" +
			"STOREIND\n" +
			"PUSHIMM 1\n" +
			"ADD\n" +
			"PUSHOFF -3\n" +
			"PUSHIMM 1\n" +
			"ADD\n" +
			"STOREOFF -3\n" +
			"JUMP STRREPEATLOOP3\n" +
			"STRREPEATDONE3:\n" +
			"ADDSP -2\n" +
			"PUSHOFF -2\n" +
			"PUSHIMM 1\n" +
			"SUB\n" +
			"STOREOFF -2\n" +
			"UNLINK\n" +
			"JUMP STRREPEATBIGLOOP\n" +
			"STRREPEATDONE:\n" +
			"PUSHOFF 1\n" +
			"STOREOFF -2\n" +
			"ADDSP -4\n" +
			"UNLINK\n" +
			"ADDSP -1\n";
	}

	public static String revStr() {
		return "LINK\n" +
			"PUSHOFF -1\n" +
			"LINK\n" +
			"PUSHIMM 0\n" +
			"STRREVLOOP1:\n" +
			"DUP\n" +
			"PUSHOFF -1\n" +
			"ADD\n" +
			"PUSHIND\n" +
			"ISNIL\n" +
			"JUMPC STRREVDONE1\n" +
			"PUSHIMM 1\n" +
			"ADD\n" +
			"JUMP STRREVLOOP1\n" +
			"STRREVDONE1:\n" +
			"STOREOFF -1\n" +
			"UNLINK\n" +
			"PUSHIMM 0\n" +
			"PUSHOFF -1\n" +
			"PUSHOFF 1\n" +
			"PUSHIMM 1\n" +
			"ADD\n" +
			"MALLOC\n" +
			"PUSHOFF 1\n" +
			"PUSHOFF 4\n" +
			"ADD\n" +
			"PUSHIMMCH '\0'" +
			"STOREIND\n" +
			"LINK\n" +
			"STRREVLOOP:\n" +
			"PUSHOFF -3\n" +
			"PUSHOFF -1\n" +
			"ADD\n" +
			"PUSHOFF -2\n" +
			"PUSHOFF -4\n" +
			"DUP\n" +
			"ISNIL\n" +
			"JUMPC STRREVDONE\n" +
			"PUSHIMM 1\n" +
			"SUB\n" +
			"ADD\n" +
			"PUSHIND\n" +
			"STOREIND\n" +
			"PUSHOFF -4\n" +
			"PUSHIMM 1\n" +
			"SUB\n" +
			"STOREOFF -4\n" +
			"PUSHOFF -3\n" +
			"PUSHIMM 1\n" +
			"ADD\n" +
			"STOREOFF -3\n" +
			"JUMP STRREVLOOP\n" +
			"STRREVDONE:\n" +
			"ADDSP -3\n" +
			"UNLINK\n" +
			"STOREOFF -1\n" +
			"ADDSP -3\n" +
			"UNLINK\n";
	}
}
