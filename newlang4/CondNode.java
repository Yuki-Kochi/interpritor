package newlang4;

import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;
import newlang3.*;

public class CondNode extends Node {

	Node left=null;				//����
	LexicalType operator=null;	//���Z�q
	Node right=null;			//�E��
	//������first���Z�b�g�ł����Ă���
	private final static Set<LexicalType> FIRST=new HashSet<LexicalType>(Arrays.asList(
		LexicalType.NAME,
		LexicalType.SUB,
		LexicalType.LP,
		LexicalType.INTVAL,
		LexicalType.DOUBLEVAL,
		LexicalType.LITERAL
	));

	private final static Set<LexicalType> OPERATOR=new HashSet<>(Arrays.asList(
		LexicalType.EQ,
		LexicalType.LT,
		LexicalType.LE,
		LexicalType.GT,
		LexicalType.GE,
		LexicalType.NE
	));

	public static boolean isMatch(LexicalType type){
		return FIRST.contains(type);
	}

	private CondNode(Environment in){
		env=in;
		type=NodeType.COND;
	}

	public static Node getHandrar(Environment envIn){
		return new CondNode(envIn);
	}

	public void parse() throws Exception {
		if (ExprNode.isMatch(env.getInput().peek(1).getType())){
			left=ExprNode.getHandrar(env);
			left.parse();
		} else {
			throw new SyntaxException("�������̊J�n���s���ł��B"+env.getInput().getLine()+"�s��");
		}

		if (OPERATOR.contains(env.getInput().peek(1).getType())){
			operator=env.getInput().get().getType();
		} else {
			throw new SyntaxException("���������ɕs���ȕ���������܂����B"+env.getInput().getLine()+"�s��");
		}

		if (ExprNode.isMatch(env.getInput().peek(1).getType())){
			right=ExprNode.getHandrar(env);
			right.parse();
		} else {
			throw new SyntaxException("���������ɕs���ȋL��������܂����B"+env.getInput().getLine()+"�s��");
		}
	}

	public String toString() {
		return "COND�F"+left+" "+operator+" "+right;
	}
}



