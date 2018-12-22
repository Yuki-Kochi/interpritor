package newlang4;

import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;
import newlang3.*;

public class CallNode extends Node {

	String funcName=null;		//�֐���
	ExprListNode arguments=null;		//����

	//������first���Z�b�g�ł����Ă���
	private final static Set<LexicalType> FIRST=new HashSet<LexicalType>(Arrays.asList(
		LexicalType.NAME
	));

	public static boolean isMatch(LexicalType type){
		return FIRST.contains(type);
	}

	private CallNode(Environment envIn){
		env=envIn;
		type=NodeType.FUNCTION_CALL;
	}

	public static Node getHandler(Environment in){
		return new CallNode(in);
	}

	public Value getValue() throws Exception{
		return env.getFunction(funcName).invoke(arguments);
	}

	public void parse() throws Exception {
		boolean isBracket=false;		//���ʂ����������ۂ�

		//�ďo�֐���
		if (env.getInput().peek(1).getType()==LexicalType.NAME){
			funcName=env.getInput().get().getValue().getSValue();
		} else {
			throw new SyntaxException("�L���Ȋ֐����ł͂���܂���B("+env.getInput().getLine()+"�s��");
		}

		//LP�̊m�F
		if (env.getInput().peek(1).getType()==LexicalType.LP){
			env.getInput().get();
			isBracket=true;
		}

		//�������X�g
		if (ExprListNode.isMatch(env.getInput().peek(1).getType())){
			arguments=ExprListNode.getHandler(env);
			arguments.parse();
		}

		if (isBracket){
			//������
			if (env.getInput().get().getType()!=LexicalType.RP){
				throw new SyntaxException("�֐��ďo�̊��ʂ������Ă��܂���B("+env.getInput().getLine()+"�s��");
			}
		}
	}


	public String toString(int indent) {
		return "�֐��F�֐�����"+funcName+" �������X�g��["+arguments+"]";
	}
}
