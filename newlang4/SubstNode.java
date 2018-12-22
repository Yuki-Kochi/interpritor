package newlang4;

import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;
import newlang3.*;

public class SubstNode extends Node {

	String leftVar=null;
	Node expr=null;

	//������first���Z�b�g�ł����Ă���
	private final static Set<LexicalType> FIRST=new HashSet<LexicalType>(Arrays.asList(
		LexicalType.NAME
	));

	public static boolean isMatch(LexicalType type){
		return FIRST.contains(type);
	}

	private SubstNode(Environment in){
		env=in;
		type=NodeType.ASSIGN_STMT;
	}

	public static Node getHandler(Environment envIn){
		return new SubstNode(envIn);
	}

	public void parse() throws Exception {
		//Name�����邱�Ƃ����O�m�F�ς�
		leftVar=env.getInput().get().getValue().getSValue();

		//�u���v���擾���邪�A���������ł��鎖��Stmt��gethandrer()�Ŋm�F�ς�
		LexicalUnit lu=env.getInput().get();

		if (ExprNode.isMatch(env.getInput().peek(1).getType())){
			expr=ExprNode.getHandler(env);
			expr.parse();
		} else {
			throw new SyntaxException("������̌㔼�����Ƃ��ĕ]���ł��܂���B("+env.getInput().getLine()+"�s��)");
		}
	}

	public Value getValue() throws Exception{
		env.getVariable(leftVar).setValue(expr.getValue());
		return null;
	}

	public String toString(int indent) {
		return "SUBST:"+expr.toString()+"��"+leftVar.toString()+"";
	}
}
