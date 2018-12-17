package newlang4;

import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;
import newlang3.*;

public class VariableNode extends Node {

	String name=null;

	//������first���Z�b�g�ł����Ă���
	private final static Set<LexicalType> FIRST=new HashSet<LexicalType>(Arrays.asList(
		LexicalType.NAME
	));

	public static boolean isMatch(LexicalType type){
		return FIRST.contains(type);
	}

	private VariableNode(Environment envIn,Value v){
		env=envIn;
		type=NodeType.VARIABLE;
		name=v.getSValue();
	}

	private VariableNode(Environment envIn){
		env=envIn;
		type=NodeType.VARIABLE;
	}

	public static Node getHandrar(Environment envin,Value v){
		return new VariableNode(envin,v);
	}

	public static Node getHandrar(Environment envin){
		return new VariableNode(envin);
	}

	public void parse() throws Exception {
		if (env.getInput().peek(1).getType()==LexicalType.NAME){
			name=env.getInput().get().getValue().getSValue();
		} else {
			throw new InternalError("�ϐ����Ƃ��ĕs�K�؂ȕ�����ł��B"+env.getInput().getLine()+"�s��");
		}
	}

	public Value getValue(){
		return env.getVariable(name).getValue();
//		return new ValueImpl(name);
	}


	public String toString() {
		return "�ϐ��F"+name;
	}
}

