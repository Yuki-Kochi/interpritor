package newlang4;

import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;
import newlang3.*;

public class LoopNode extends Node {

	Node cond=null;				//����
	Node operation=null;		//true�̎��̏���
	boolean isDo=false;		//�����Ɋւ�炸��x�͕K�����s���邩
	boolean isUntill=false;		//cond�̔���������t�ɂ��邩

	//������first���Z�b�g�ł����Ă���
	private final static Set<LexicalType> FIRST=new HashSet<>(Arrays.asList(
		LexicalType.WHILE,
		LexicalType.DO
	));

	public static boolean isMatch(LexicalType type){
		return FIRST.contains(type);
	}

	private LoopNode(Environment in){
		env=in;
		type=NodeType.LOOP_BLOCK;
	}

	public static Node getHandler(Environment envIn){
		return new LoopNode(envIn);
	}

	public void parse() throws Exception {
		if (env.getInput().expect(LexicalType.WHILE)){
			//WHILE��ǂݔ�΂�
			env.getInput().get();

			if (CondNode.isMatch(env.getInput().peek(1).getType())){
				cond=CondNode.getHandler(env);
				cond.parse();
			} else {
				throw new SyntaxException("WHILE���̍\�����s���ł��BWHILE�̒��オ�������ł͂���܂���B"+env.getInput().getLine()+"�s��");
			}

			if (env.getInput().expect(LexicalType.NL)){
				env.getInput().get();
			} else {
				throw new SyntaxException("WHILE���̍\�����s���ł��B�������̒���ɂ͉��s�������K�v�ł��B"+env.getInput().getLine()+"�s��");
			}

			if (StmtListNode.isMatch(env.getInput().peek(1).getType())){
				operation=StmtListNode.getHandler(env);
				operation.parse();
			} else {
				throw new SyntaxException("WHILE���̍\�����s���ł��B�������e�����o�ł��܂���B"+env.getInput().getLine()+"�s��");
			}

			if (env.getInput().expect(LexicalType.NL)){
				env.getInput().get();
			} else {
				throw new SyntaxException("WHILE���̍\�����s���ł��B�������e�̌�ɂ͉��s�������K�v�ł��B"+env.getInput().getLine()+"�s��");
			}

			if (env.getInput().expect(LexicalType.WEND)){
				env.getInput().get();
			} else {
				throw new SyntaxException("WHILE���̍\�����s���ł��B�I�[��WEND��������܂���B"+env.getInput().getLine()+"�s��");
			}
		} else if (env.getInput().expect(LexicalType.DO)){
			//DO��ǂݔ�΂�
			env.getInput().get();

			isDo=true;
			getDoBlockCond();

			if (env.getInput().expect(LexicalType.NL)){
				env.getInput().get();
			} else {
				throw new SyntaxException("DO�u���b�N�̍\�����s���ł��B�������e�̑O�ɂ͉��s�������K�v�ł��B"+env.getInput().getLine()+"�s��");
			}

			if (StmtListNode.isMatch(env.getInput().peek(1).getType())){
				operation=StmtListNode.getHandler(env);
				operation.parse();
			} else {
				throw new SyntaxException("DO�u���b�N�̍\�����s���ł��B�������e�����o�ł��܂���B"+env.getInput().getLine()+"�s��");
			}

			if (env.getInput().expect(LexicalType.NL)){
				env.getInput().get();
			} else {
				throw new SyntaxException("DO�u���b�N�̍\�����s���ł��B�������e�̌�ɂ͉��s�������K�v�ł��B"+env.getInput().getLine()+"�s��");
			}

			if (env.getInput().expect(LexicalType.LOOP)){
				env.getInput().get();
			} else {
				throw new SyntaxException("DO�u���b�N�̍\�����s���ł��B�u���b�N�I�[������LOOP�傪����܂���B"+env.getInput().getLine()+"�s��");
			}

			if (cond==null){
				if (!getDoBlockCond()){
					throw new SyntaxException("DO�u���b�N�ɕK�v��WHILE�܂���UNTIL�Ŏn�܂������������܂���B");
				}
			}
		} else {
			throw new InternalError("LOOPBLOCK�̊J�n����ł͂���܂���B");
		}

		if (!env.getInput().expect(LexicalType.NL)){
			throw new SyntaxException("LOOPBLOCK�̍\�����s���ł��B�I�[�̉��s���������o�ł��܂���B"+env.getInput().getLine()+"�s��");
		}
	}


	private boolean getDoBlockCond() throws Exception{
		switch (env.getInput().peek(1).getType()){
			case UNTIL:
				isUntill=true;
			case WHILE:
				env.getInput().get();
				if (CondNode.isMatch(env.getInput().peek(1).getType())){
					cond=CondNode.getHandler(env);
					cond.parse();
				} else {
					throw new SyntaxException("DO�u���b�N�̍\�����s���ł��BWHILE�܂���UNTIL�̒��オ�������ł͂���܂���B"+env.getInput().getLine()+"�s��");
				}
				break;
			default:
				return false;
		}
		return true;
	}

	public Value getValue() throws Exception{
		if (isDo){		//����̋������s
			operation.getValue();
		}

		while(true){
			if (!judge()){
				return null;
			}
			operation.getValue();
		}
	}

	private boolean judge() throws Exception {	//�������p�����邩����
		if ((cond.getValue().getBValue()==true && isUntill==false) ||
		(cond.getValue().getBValue()==false && isUntill==true)){
			return true;
		} else {
			return false;
		}
	}

	public String toString(int indent) {
		String ret="";
		ret+="LOOPBlock�F�p��������";
		if (isUntill){
			ret+="!(";
		}
		ret+=cond;
		if (isUntill){
			ret+=")";
		}
		ret+="�@";
		if (isDo){
			ret+="���񋭐����s";
		}
		ret+="[\n"+operation.toString(indent+1);
		for(int i=0;i<indent;i++){
			ret+="\t";
		}
		ret+="]";
		return ret;
	}
}
