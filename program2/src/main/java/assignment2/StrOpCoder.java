package assignment2;

public class StrOpCoder {
	private int n;

	public StrOpCoder() {
		this.n = 0;
	}

	public String strCmp() {
		this.n++;
		return "// strCmp begin" +
			"    LINK\n" +
			"    PUSHIMM 0\n" +
			"    STRCMPLOOP" + this.n + ":\n" +
			"    PUSHOFF -2\n" +
			"    PUSHOFF 1\n" +
			"    ADD\n" +
			"    PUSHIND\n" +
			"    PUSHOFF -1\n" +
			"    PUSHOFF 1\n" +
			"    ADD\n" +
			"    PUSHIND\n" +
			"    PUSHOFF 2\n" +
			"    ISNIL\n" +
			"    PUSHOFF 3\n" +
			"    ISNIL\n" +
			"    AND\n" +
			"    JUMPC STRCMPEQ" + this.n + "\n" +
			"    PUSHOFF 2\n" +
			"    PUSHOFF 3\n" +
			"    GREATER\n" +
			"    JUMPC STRCMPGT" + this.n + "\n" +
			"    LESS\n" +
			"    JUMPC STRCMPLT" + this.n + "\n" +
			"    PUSHIMM 1\n" +
			"    ADD\n" +
			"    JUMP STRCMPLOOP" + this.n + "\n" +
			"    STRCMPEQ" + this.n + ":\n" +
			"    ADDSP -2\n" +
			"    PUSHIMM 0\n" +
			"    JUMP STRCMPDONE" + this.n + "\n" +
			"    STRCMPGT" + this.n + ":\n" +
			"    ADDSP -2\n" +
			"    PUSHIMM -1\n" +
			"    JUMP STRCMPDONE" + this.n + "\n" +
			"    STRCMPLT" + this.n + ":\n" +
			"    PUSHIMM 1\n" +
			"    JUMP STRCMPDONE" + this.n + "\n" +
			"    STRCMPDONE" + this.n + ":\n" +
			"    STOREOFF -2\n" +
			"    ADDSP -1\n" +
			"    UNLINK\n" +
			"    ADDSP -1\n" +
			"// strCmp end";
	}

	public String strConcat() {
		this.n++;
		return "// strConcat begin" +
			"    LINK\n" +
			"    PUSHOFF -2\n" +
			"    LINK\n" +
			"    PUSHIMM 0\n" +
			"    STRCONCATLOOP1" + this.n + ":\n" +
			"    DUP\n" +
			"    PUSHOFF -1\n" +
			"    ADD\n" +
			"    PUSHIND\n" +
			"    ISNIL\n" +
			"    JUMPC STRCONCATDONE1" + this.n + "\n" +
			"    PUSHIMM 1\n" +
			"    ADD\n" +
			"    JUMP STRCONCATLOOP1" + this.n + "\n" +
			"    STRCONCATDONE1" + this.n + ":\n" +
			"    STOREOFF -1\n" +
			"    UNLINK\n" +
			"    PUSHOFF -1\n" +
			"    LINK\n" +
			"    PUSHIMM 0\n" +
			"    STRCONCATLOOP2" + this.n + ":\n" +
			"    DUP\n" +
			"    PUSHOFF -1\n" +
			"    ADD\n" +
			"    PUSHIND\n" +
			"    ISNIL\n" +
			"    JUMPC STRCONCATDONE2" + this.n + "\n" +
			"    PUSHIMM 1\n" +
			"    ADD\n" +
			"    JUMP STRCONCATLOOP2" + this.n + "\n" +
			"    STRCONCATDONE2" + this.n + ":\n" +
			"    STOREOFF -1\n" +
			"    UNLINK\n" +
			"    ADD\n" +
			"    PUSHIMM 1\n" +
			"    ADD\n" +
			"    MALLOC\n" +
			"    PUSHIMM 0\n" +
			"    PUSHOFF -2\n" +
			"    LINK\n" +
			"    STRCONCATLOOP3" + this.n + ":\n" +
			"    PUSHOFF -1\n" +
			"    PUSHOFF -2\n" +
			"    ADD\n" +
			"    PUSHIND\n" +
			"    DUP\n" +
			"    ISNIL\n" +
			"    JUMPC STRCONCATDONE3" + this.n + "\n" +
			"    PUSHOFF -3\n" +
			"    PUSHOFF -2\n" +
			"    ADD\n" +
			"    SWAP\n" +
			"    STOREIND\n" +
			"    PUSHOFF -2\n" +
			"    PUSHIMM 1\n" +
			"    ADD\n" +
			"    STOREOFF -2\n" +
			"    JUMP STRCONCATLOOP3" + this.n + "\n" +
			"    STRCONCATDONE3" + this.n + ":\n" +
			"    ADDSP -1\n" +
			"    UNLINK\n" +
			"    ADDSP -1\n" +
			"    PUSHIMM 0\n" +
			"    PUSHOFF -1\n" +
			"    LINK\n" +
			"    STRCONCATLOOP4" + this.n + ":\n" +
			"    PUSHOFF -1\n" +
			"    PUSHOFF -2\n" +
			"    ADD\n" +
			"    PUSHIND\n" +
			"    DUP\n" +
			"    ISNIL\n" +
			"    JUMPC STRCONCATDONE4" + this.n + "\n" +
			"    PUSHOFF -4\n" +
			"    PUSHOFF -3\n" +
			"    PUSHOFF -2\n" +
			"    ADD\n" +
			"    ADD\n" +
			"    SWAP\n" +
			"    STOREIND\n" +
			"    PUSHOFF -2\n" +
			"    PUSHIMM 1\n" +
			"    ADD\n" +
			"    STOREOFF -2\n" +
			"    JUMP STRCONCATLOOP4" + this.n + "\n" +
			"    STRCONCATDONE4" + this.n + ":\n" +
			"    ADDSP -1\n" +
			"    UNLINK\n" +
			"    PUSHOFF 1\n" +
			"    PUSHOFF 2\n" +
			"    PUSHOFF 3\n" +
			"    ADD\n" +
			"    ADD\n" +
			"    PUSHIMMCH '\\0'\n" +
			"    STOREIND\n" +
			"    ADDSP -3\n" +
			"    STOREOFF -2\n" +
			"    UNLINK\n" +
			"    ADDSP -1\n" +
			"// strConcat end";
	}

	public String strLen() {
		this.n++;
		return "// strLen begin" +
			"    LINK\n" +
			"    PUSHIMM 0\n" +
			"    STRLENLOOP" + this.n + ":\n" +
			"    DUP\n" +
			"    PUSHOFF -1\n" +
			"    ADD\n" +
			"    PUSHIND\n" +
			"    ISNIL\n" +
			"    JUMPC STRLENDONE" + this.n + "\n" +
			"    PUSHIMM 1\n" +
			"    ADD\n" +
			"    JUMP STRLENLOOP" + this.n + "\n" +
			"    STRLENDONE" + this.n + ":\n" +
			"    STOREOFF -1\n" +
			"    UNLINK\n" +
			"// strLen end";
	}

	public String strRepeat() {
		this.n++;
		return "// strRepeat begin" +
			"    DUP\n" +
			"    ISNEG\n" +
			"    JUMPC STRREPEATHANDLENEGATIVE" + this.n + "\n" +
			"    STRREPEATCONTINUE" + this.n + ":\n" +
			"    SWAP\n" +
			"    LINK\n" +
			"    PUSHOFF -1\n" +
			"    LINK\n" +
			"    PUSHIMM 0\n" +
			"    STRREPEATLOOP1" + this.n + ":\n" +
			"    DUP\n" +
			"    PUSHOFF -1\n" +
			"    ADD\n" +
			"    PUSHIND\n" +
			"    ISNIL\n" +
			"    JUMPC STRREPEATDONE1" + this.n + "\n" +
			"    PUSHIMM 1\n" +
			"    ADD\n" +
			"    JUMP STRREPEATLOOP1" + this.n + "\n" +
			"    STRREPEATHANDLENEGATIVE" + this.n + ":\n" +
			"    ISPOS\n" +
			"    JUMP STRREPEATCONTINUE" + this.n + "\n" +
			"    STRREPEATDONE1" + this.n + ":\n" +
			"    STOREOFF -1\n" +
			"    UNLINK\n" +
			"    PUSHOFF -2\n" +
			"    TIMES\n" +
			"    DUP\n" +
			"    PUSHIMM 1\n" +
			"    ADD\n" +
			"    MALLOC\n" +
			"    SWAP\n" +
			"    PUSHOFF 1\n" +
			"    ADD\n" +
			"    PUSHIMMCH '\\0'\n" +
			"    STOREIND\n" +
			"    PUSHIMM 0\n" +
			"    PUSHOFF -2\n" +
			"    PUSHOFF -1\n" +
			"    STRREPEATBIGLOOP" + this.n + ":\n" +
			"    PUSHIMM 0\n" +
			"    PUSHOFF 3\n" +
			"    CMP\n" +
			"    PUSHIMM 1\n" +
			"    EQUAL\n" +
			"    NOT\n" +
			"    JUMPC STRREPEATDONE" + this.n + "\n" +
			"    LINK\n" +
			"    PUSHIMM 0\n" +
			"    STRREPEATLOOP3" + this.n + ":\n" +
			"    PUSHOFF -1\n" +
			"    PUSHOFF 1\n" +
			"    ADD\n" +
			"    PUSHIND\n" +
			"    DUP\n" +
			"    ISNIL\n" +
			"    JUMPC STRREPEATDONE3" + this.n + "\n" +
			"    PUSHOFF -4\n" +
			"    PUSHOFF -3\n" +
			"    ADD\n" +
			"    SWAP\n" +
			"    STOREIND\n" +
			"    PUSHIMM 1\n" +
			"    ADD\n" +
			"    PUSHOFF -3\n" +
			"    PUSHIMM 1\n" +
			"    ADD\n" +
			"    STOREOFF -3\n" +
			"    JUMP STRREPEATLOOP3" + this.n + "\n" +
			"    STRREPEATDONE3" + this.n + ":\n" +
			"    ADDSP -2\n" +
			"    PUSHOFF -2\n" +
			"    PUSHIMM 1\n" +
			"    SUB\n" +
			"    STOREOFF -2\n" +
			"    UNLINK\n" +
			"    JUMP STRREPEATBIGLOOP" + this.n + "\n" +
			"    STRREPEATDONE" + this.n + ":\n" +
			"    PUSHOFF 1\n" +
			"    STOREOFF -2\n" +
			"    ADDSP -4\n" +
			"    UNLINK\n" +
			"    ADDSP -1\n" +
			"// strRepeat end";
	}

	public String strRev() {
		this.n++;
		return "// strRev begin" +
			"    LINK\n" +
			"    PUSHOFF -1\n" +
			"    LINK\n" +
			"    PUSHIMM 0\n" +
			"    STRREVLOOP1" + this.n + ":\n" +
			"    DUP\n" +
			"    PUSHOFF -1\n" +
			"    ADD\n" +
			"    PUSHIND\n" +
			"    ISNIL\n" +
			"    JUMPC STRREVDONE1" + this.n + "\n" +
			"    PUSHIMM 1\n" +
			"    ADD\n" +
			"    JUMP STRREVLOOP1" + this.n + "\n" +
			"    STRREVDONE1" + this.n + ":\n" +
			"    STOREOFF -1\n" +
			"    UNLINK\n" +
			"    PUSHIMM 0\n" +
			"    PUSHOFF -1\n" +
			"    PUSHOFF 1\n" +
			"    PUSHIMM 1\n" +
			"    ADD\n" +
			"    MALLOC\n" +
			"    PUSHOFF 1\n" +
			"    PUSHOFF 4\n" +
			"    ADD\n" +
			"    PUSHIMMCH '\\0'\n" +
			"    STOREIND\n" +
			"    LINK\n" +
			"    STRREVLOOP" + this.n + ":\n" +
			"    PUSHOFF -3\n" +
			"    PUSHOFF -1\n" +
			"    ADD\n" +
			"    PUSHOFF -2\n" +
			"    PUSHOFF -4\n" +
			"    DUP\n" +
			"    ISNIL\n" +
			"    JUMPC STRREVDONE" + this.n + "\n" +
			"    PUSHIMM 1\n" +
			"    SUB\n" +
			"    ADD\n" +
			"    PUSHIND\n" +
			"    STOREIND\n" +
			"    PUSHOFF -4\n" +
			"    PUSHIMM 1\n" +
			"    SUB\n" +
			"    STOREOFF -4\n" +
			"    PUSHOFF -3\n" +
			"    PUSHIMM 1\n" +
			"    ADD\n" +
			"    STOREOFF -3\n" +
			"    JUMP STRREVLOOP" + this.n + "\n" +
			"    STRREVDONE" + this.n + ":\n" +
			"    ADDSP -3\n" +
			"    UNLINK\n" +
			"    STOREOFF -1\n" +
			"    ADDSP -3\n" +
			"    UNLINK\n" +
			"// strRev end";
	}
}
