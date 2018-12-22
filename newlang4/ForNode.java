package newlang4;

import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;
import newlang3.*;

public class ForNode extends Node {

	Node init=null;			//������
	Node max=null;			//�p�������̏���l
	Node operation=null;	//�������e
	String step=null;		//�X�V�Ώەϐ���

	//������first���Z�b�g�ł����Ă���
	private final static Set<LexicalType> FIRST=new HashSet<LexicalType>(Arrays.asList(
		LexicalType.FOR
	));

	public static boolean isMatch(LexicalType type){
		return FIRST.contains(type);
	}

	private ForNode(Environment in){
		env=in;
		type=NodeType.FOR_STMT;
	}

	public static Node getHandler(Environment envIn){
		return new ForNode(envIn);
	}

	public void parse() throws Exception {
		//FOR�̊m�F
		if (env.getInput().expect(LexicalType.FOR)){
			env.getInput().get();
		} else {
			throw new InternalError("for���ȊO�̉ӏ���ForNode���R�[������܂����B");
		}

		//subst
		if (SubstNode.isMatch(env.getInput().peek(1).getType())){
			init=SubstNode.getHandler(env);
			init.parse();
		} else {
			throw new SyntaxException("FOR���̍\�����s���ł��B�����������̎w�肪����Ă��܂��B"+env.getInput().getLine()+"�s��");
		}

		//TO�̊m�F
		if (env.getInput().expect(LexicalType.TO)){
			env.getInput().get();
		} else {
			throw new SyntaxException("for���̍\�����s���ł��BTO������܂���B"+env.getInput().getLine()+"�s��");
		}

		//�p������ƂȂ�INTVAL
		if (env.getInput().peek(1).getType()==LexicalType.INTVAL){
			max=ConstNode.getHandler(env,env.getInput().get().getValue());
		} else {
			throw new SyntaxException("for���̍\�����s���ł��B�p�������̏���l������܂���B"+env.getInput().getLine()+"�s��");
		}

		//NL�̊m�F
		if (env.getInput().expect(LexicalType.NL)){
			env.getInput().get();
		} else {
			throw new SyntaxException("for���̍\�����s���ł��B�p�������̌��NL������܂���B"+env.getInput().getLine()+"�s��");
		}

		//�������e
		if (StmtListNode.isMatch(env.getInput().peek(1).getType())){
			operation=StmtListNode.getHandler(env);
			operation.parse();
		} else {
			throw new SyntaxException("for���̍\�����s���ł��B�������e�̋L�q�����o�ł��܂���B"+env.getInput().getLine()+"�s��");
		}

		//NL�m�F
		if (env.getInput().expect(LexicalType.NL)){
			env.getInput().get();
		} else {
			throw new SyntaxException("for���̍\�����s���ł��B�������e�̌��NL������܂���B"+env.getInput().getLine()+"�s��");
		}

		//NEXT�̊m�F
		if (env.getInput().expect(LexicalType.NEXT)){
			env.getInput().get();
		} else {
			throw new SyntaxException("for���̍\�����s���ł��BNEXT������܂���B"+env.getInput().getLine()+"�s��");
		}

		//�X�V�Ώەϐ���
		if (env.getInput().peek(1).getType()==LexicalType.NAME){
			step=env.getInput().get().getValue().getSValue();
		} else {
			throw new SyntaxException("for���̍\�����s���ł��B�X�V�Ώۂ�����܂���B"+env.getInput().getLine()+"�s��");
		}
	}

	public Value getValue() throws Exception{
		init.getValue();
		while(true){
			if (env.getVariable(step).getValue().getIValue()>max.getValue().getIValue()){
				return null;
			}
			operation.getValue();
			env.getVariable(step).setValue
			(new ExprNode(env.getVariable(step),ConstNode.getHandler(env,new ValueImpl(1)),(LexicalType.ADD)).getValue());
		}
	}

	public String toString(int indent) {
		String ret="FOR�F��������"+init+" �p�������"+max+"�@�������e�F[\n";
		ret+=operation.toString(indent+1)+"\n";
		for(int i=0;i<indent;i++){
			ret+="\t";
		}
		ret+="]�@�X�V�ΏہF"+step;
		return ret;
	}
}
