package newlang4;

import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;
import newlang3.*;

public class ConstNode extends Node {

	Value value=null;

	//������first���Z�b�g�ł����Ă���
	private final static Set<LexicalType> FIRST=new HashSet<LexicalType>(Arrays.asList(
		LexicalType.SUB,
		LexicalType.INTVAL,
		LexicalType.DOUBLEVAL,
		LexicalType.LITERAL
	));

	public static boolean isMatch(LexicalType type){
		return FIRST.contains(type);
	}

	private ConstNode(Environment envIn,Value v){
		env=envIn;
		switch (v.getType()){
			case INTEGER:
				type=NodeType.INT_CONSTANT;
				break;
			case DOUBLE:
				type=NodeType.DOUBLE_CONSTANT;
				break;
			case STRING:
				type=NodeType.STRING_CONSTANT;
				break;
			default:
				throw new InternalError("���Ή��̌^��Value����ConstNode�̐��������N�G�X�g����܂����B");
		}
		value=v;
	}

	public static Node getHandler(Environment envin,Value v){
		return new ConstNode(envin,v);
	}

	public void parse() throws Exception {
		throw new InternalError("Const�ɑ΂���Parse�͎��s�ł��܂���B");
	}

	public Value getValue(){
		return value;
	}

	public String toString() {
		return "�萔�F"+value;
	}
}
