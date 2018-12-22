package newlang4;

import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;
import newlang3.*;

public class EndNode extends Node {

	//������first���Z�b�g�ł����Ă���
	private final static Set<LexicalType> FIRST=new HashSet<LexicalType>(Arrays.asList(
		LexicalType.END
	));

	public static boolean isMatch(LexicalType type){
		return FIRST.contains(type);
	}

	private EndNode(Environment envIn){
		env=envIn;
		type=NodeType.END;
	}

	public static Node getHandler(Environment in){
		return new EndNode(in);
	}

	public void parse() throws Exception {
		if (env.getInput().expect(LexicalType.END)){
			env.getInput().get();
		} else {
			throw new InternalError("END�ł͂���܂���B("+env.getInput().getLine()+"�s��");
		}
	}

	public Value getValue(){
		System.exit(0);
		return null;
	}

	public String toString(int indent) {
		return "END";
	}
}

