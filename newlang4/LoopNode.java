package newlang4;

import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;
import newlang3.*;

public class LoopNode extends Node {

	Node cond=null;				//����
	Node operation=null;		//true�̎��̏���
	boolean isCondFirst=true;	//������O���肷�邩
	boolean isDoMust=false;		//�����Ɋւ�炸��x�͕K�����s���邩
	boolean isUntill=false;		//cond�̔���������t�ɂ��邩

	//������first���Z�b�g�ł����Ă���
	private final static Set<LexicalType> FIRST=new HashSet<LexicalType>(Arrays.asList(
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

	public static Node getHandrar(Environment envIn){
		return new LoopNode(envIn);
	}

	public void parse() throws Exception {
		if (env.getInput().peek(1).getType()==LexicalType.WHILE){
			//WHILE��ǂݔ�΂�
			env.getInput().get();

			if (CondNode.isMatch(env.getInput().peek(1).getType())){
				cond=CondNode.getHandrar(env);
				cond.parse();
			} else {
				throw new SyntaxException("WHILE���̍\�����s���ł��BWHILE�̒��オ�������ł͂���܂���B"+env.getInput().getLine()+"�s��");
			}

			if (env.getInput().get().getType()!=LexicalType.NL){
				throw new SyntaxException("WHILE���̍\�����s���ł��B�������̒���ɂ͉��s�������K�v�ł��B"+env.getInput().getLine()+"�s��");
			}

			if (StmtListNode.isMatch(env.getInput().peek(1).getType())){
				operation=StmtListNode.getHandrar(env);
				operation.parse();
			} else {
				throw new SyntaxException("WHILE���̍\�����s���ł��B�������e�����o�ł��܂���B"+env.getInput().getLine()+"�s��");
			}

			if (env.getInput().get().getType()!=LexicalType.NL){
				throw new SyntaxException("WHILE���̍\�����s���ł��B�������e�̌�ɂ͉��s�������K�v�ł��B"+env.getInput().getLine()+"�s��");
			}

			if (env.getInput().get().getType()!=LexicalType.WEND){
				throw new SyntaxException("WHILE���̍\�����s���ł��B�I�[��WEND��������܂���B"+env.getInput().getLine()+"�s��");
			}
		} else if (env.getInput().peek(1).getType()==LexicalType.DO){
			//DO��ǂݔ�΂�
			env.getInput().get();

			isDoMust=true;
			getDoBlockCond();

			if (env.getInput().get().getType()!=LexicalType.NL){
				throw new SyntaxException("DO�u���b�N�̍\�����s���ł��B�������e�̑O�ɂ͉��s�������K�v�ł��B"+env.getInput().getLine()+"�s��");
			}

			if (StmtListNode.isMatch(env.getInput().peek(1).getType())){
				operation=StmtListNode.getHandrar(env);
				operation.parse();
			} else {
				throw new SyntaxException("DO�u���b�N�̍\�����s���ł��B�������e�����o�ł��܂���B"+env.getInput().getLine()+"�s��");
			}

			if (env.getInput().get().getType()!=LexicalType.NL){
				throw new SyntaxException("DO�u���b�N�̍\�����s���ł��B�������e�̌�ɂ͉��s�������K�v�ł��B"+env.getInput().getLine()+"�s��");
			}

			if (env.getInput().get().getType()!=LexicalType.LOOP){
				throw new SyntaxException("DO�u���b�N�̍\�����s���ł��B�������e�̌�ɂ͉��s�������K�v�ł��B"+env.getInput().getLine()+"�s��");
			}

			if (cond==null){
				isCondFirst=false;
				if (!getDoBlockCond()){
					throw new SyntaxException("DO�u���b�N�ɕK�v��WHILE�܂���UNTIL�Ŏn�܂������������܂���B");
				}
			}
		} else {
			throw new InternalError("LOOPBLOCK�̊J�n����ł͂���܂���B");
		}

		if (env.getInput().get().getType()!=LexicalType.NL){
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
					cond=CondNode.getHandrar(env);
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
		if (isCondFirst){
			ret+="�O����@";
		} else {
			ret+="�㔻��@";
		}
		if (isDoMust){
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
