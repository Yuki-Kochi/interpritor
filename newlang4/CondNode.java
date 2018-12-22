package newlang4;

import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;
import newlang3.*;
import newlang5.*;

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

	public static Node getHandler(Environment envIn){
		return new CondNode(envIn);
	}

	public void parse() throws Exception {
		if (ExprNode.isMatch(env.getInput().peek(1).getType())){
			left=ExprNode.getHandler(env);
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
			right=ExprNode.getHandler(env);
			right.parse();
		} else {
			throw new SyntaxException("���������ɕs���ȋL��������܂����B"+env.getInput().getLine()+"�s��");
		}
	}

	public Value getValue() throws Exception{
		Value val1=left.getValue();
		Value val2=right.getValue();
		if (val1==null || val2==null){
			throw new CalcurateException("null�ɑ΂��ĉ��Z�����݂܂����B");
		}
		if (val1.getType()==ValueType.STRING || val2.getType()==ValueType.STRING){
			if (operator==LexicalType.EQ){
				return new ValueImpl(val1.getSValue().equals(val2.getSValue()));
			} else if(operator==LexicalType.NE){
				return new ValueImpl(val1.getSValue()!=val2.getSValue());
			} else {
				throw new CalcurateException("������ɑ΂��Ė����ȉ��Z�q���w�肳��Ă��܂��B");
			}
		}

		if (operator==LexicalType.LT){
			return new ValueImpl(val1.getDValue()<val2.getDValue());
		} else if (operator==LexicalType.LE){
			return new ValueImpl(val1.getDValue()<=val2.getDValue());
		} else if (operator==LexicalType.GT){
			return new ValueImpl(val1.getDValue()>val2.getDValue());
		} else if (operator==LexicalType.GE){
			return new ValueImpl(val1.getDValue()>=val2.getDValue());
		} else if (operator==LexicalType.EQ){
			return new ValueImpl(val1.getDValue()==val2.getDValue());
		} else if (operator==LexicalType.NE){
			return new ValueImpl(val1.getDValue()!=val2.getDValue());
		} else {
			throw new InternalError("�s���ȉ��Z�q�ŏ������f�����݂܂����B");
		}
	}

	public String toString() {
		return "COND�F"+left+" "+operator+" "+right;
	}
}



