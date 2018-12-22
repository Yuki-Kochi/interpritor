package newlang4;

import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;
import newlang3.*;

public class IfNode extends Node {

	Node cond=null;				//����
	Node operation=null;		//true�̎��̏���
	Node elseOperation=null;	//else�̎��̏���

	//������first���Z�b�g�ł����Ă���
	private final static Set<LexicalType> FIRST=new HashSet<LexicalType>(Arrays.asList(
		LexicalType.IF
	));

	public static boolean isMatch(LexicalType type){
		return FIRST.contains(type);
	}

	private IfNode(Environment in){
		env=in;
		type=NodeType.IF_BLOCK;
	}

	public static Node getHandler(Environment in){
		return new IfNode(in);
	}

	public void parse() throws Exception {
		boolean isELSEIF=false;			//ELSEIF�̎���ENDIF������Ȃ�

		//IF�܂���ELSEIF
		if (env.getInput().expect(LexicalType.ELSEIF)){
			isELSEIF=true;
			env.getInput().get();
		} else if (env.getInput().expect(LexicalType.IF)){
			env.getInput().get();
		} else {
			throw new InternalError("IF�܂���IFELSE�ȊO�̉ӏ���IFNode��getHandler���R�[������܂����B");
		}

		//Cond
		if (CondNode.isMatch(env.getInput().peek(1).getType())){
			cond=CondNode.getHandler(env);
			cond.parse();
		} else {
			throw new SyntaxException("IF���̍\�����s���ł��B�����������o�ł��܂���B"+env.getInput().getLine()+"�s��");
		}

		//THEN�̊m�F
		if (env.getInput().expect(LexicalType.THEN)){
				env.getInput().get();
		} else {
			throw new SyntaxException("IF���̍\�����s���ł��BTHEN������܂���B"+env.getInput().getLine()+"�s��");
		}

		//�p�^�[���P�@NL�̏ꍇ���u���b�N
		//�p�^�[���Q�@stmt+NL
		//�p�^�[���R�@stmt+ELSE+stmt+NL
		if (StmtNode.isMatch(env.getInput().peek(1).getType())){
			operation=StmtNode.getHandler(env);
			operation.parse();

			if (env.getInput().expect(LexicalType.ELSE)){
				env.getInput().get();

				if (StmtNode.isMatch(env.getInput().peek(1).getType())){
					elseOperation=StmtNode.getHandler(env);
					elseOperation.parse();
				} else {
					throw new SyntaxException("ELSE���̍\�����s���ł��B"+env.getInput().getLine()+"�s��");
				}
			}
		} else if (env.getInput().expect(LexicalType.NL)){
			//NL
			env.getInput().get();

			//StmtList
			if (StmtListNode.isMatch(env.getInput().peek(1).getType())){
				operation=StmtListNode.getHandler(env);
				operation.parse();
			} else {
				throw new SyntaxException("IF���̍\�����s���ł��B�������e�����o�ł��܂���B"+env.getInput().getLine()+"�s��");
			}

			if (env.getInput().expect(LexicalType.NL)){
				env.getInput().get();
			} else {
				throw new SyntaxException("IF���̍\�����s���ł��Btrue�̏ꍇ�̏����̏I�[NL�����o�ł��܂���B"+env.getInput().getLine()+"�s��");
			}

			if (env.getInput().expect(LexicalType.ELSEIF)){
				elseOperation=IfNode.getHandler(env);
				elseOperation.parse();
			} else if (env.getInput().expect(LexicalType.ELSE)){
				//ELSE
				env.getInput().get();

				if (env.getInput().expect(LexicalType.NL)){
					env.getInput().get();

					if (StmtListNode.isMatch(env.getInput().peek(1).getType())){
						elseOperation=StmtListNode.getHandler(env);
						elseOperation.parse();
					} else {
						throw new SyntaxException("ELSE���̍\�����s���ł��B"+env.getInput().getLine()+"�s��");
					}

					if (env.getInput().expect(LexicalType.NL)){
						env.getInput().get();
					} else {
						throw new SyntaxException("IF���̍\�����s���ł��Belse�̏ꍇ�̏����̏I�[NL�����o�ł��܂���B"+env.getInput().getLine()+"�s��");
					}
				} else {
					throw new SyntaxException("ELSE���̍\�����s���ł��B�L�[���[�hELSE�̒���ɂ͉��s�������K�v�ł��B"+env.getInput().getLine()+"�s��");
				}
			}

			if (!isELSEIF){
				if (env.getInput().expect(LexicalType.ENDIF)){
					env.getInput().get();
				} else {
					throw new SyntaxException("IF���̍\�����s���ł��BENDIF������܂���B"+env.getInput().getLine()+"�s��");
				}
			}
		} else {
			throw new SyntaxException("IF���̍\�����s���ł��B"+env.getInput().getLine()+"�s��");
		}

		if (!isELSEIF){
			if (env.getInput().expect(LexicalType.NL)){
				env.getInput().get();
			} else {
				throw new SyntaxException("IF���̍\�����s���ł��B�I�[��NL�����o�ł��܂���B"+env.getInput().getLine()+"�s��");
			}
		}
	}

	public Value getValue() throws Exception{
		if (cond.getValue().getBValue()==true){
			operation.getValue();
		} else if (elseOperation!=null){
			elseOperation.getValue();
		}
		return null;
	}

	public String toString(int indent) {
		String ret="";
		ret+="IF�F���������"+cond+"�@true�̏ꍇ�F[\n";
		if (operation.getType()!=NodeType.STMT_LIST){
			for(int i=0;i<indent+1;i++){
				ret+="\t";
			}
		}
		ret+=operation.toString(indent+1);
		if (operation.getType()!=NodeType.STMT_LIST){
			ret+="\n";
		}
		for(int i=0;i<indent;i++){
			ret+="\t";
		}
		ret+="] else�F[\n";
		if (elseOperation!=null){
			if (elseOperation.getType()!=NodeType.STMT_LIST){
				for(int i=0;i<indent+1;i++){
					ret+="\t";
				}
			}
			ret+=elseOperation.toString(indent+1);
			if (elseOperation.getType()!=NodeType.STMT_LIST){
				ret+="\n";
			}
		} else {
			for(int i=0;i<indent+1;i++){
				ret+="\t";
			}
			ret+="�����Ȃ�\n";
		}
		for(int i=0;i<indent;i++){
			ret+="\t";
		}
		ret+="]";
		return ret;
	}
}
