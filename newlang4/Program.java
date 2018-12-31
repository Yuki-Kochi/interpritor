package newlang4;

import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;
import newlang3.*;

public class Program extends Node {

	Node list=null;

	//������first���Z�b�g�ł����Ă���
	private final static Set<LexicalType> FIRST=new HashSet<LexicalType>(Arrays.asList(
		LexicalType.IF,
		LexicalType.WHILE,
		LexicalType.DO,
		LexicalType.NAME,
		LexicalType.FOR,
		LexicalType.END,
		LexicalType.NL
	));

	public static boolean isMatch(LexicalType type){
		return FIRST.contains(type);
	}

	private Program(Environment in){
		env=in;
		type=NodeType.PROGRAM;
	}

	public static Node getHandler(Environment envIn){
		return new Program(envIn);
	}

	public void parse() throws Exception {
		if (StmtListNode.isMatch(env.getInput().peek(1).getType())){
			list=StmtListNode.getHandler(env);
			list.parse();
		}

		//�t�@�C���I�[(EOF���O)��NL������Γǂݔ�΂�
		while (env.getInput().expect(LexicalType.NL)){
			env.getInput().get();
		}

		//���̎��傪EOF�łȂ���΂�������
		if (!env.getInput().expect(LexicalType.EOF)){
			throw new SyntaxException("�s���Ȏ���"+env.getInput().peek(1)+"�����������߁A�\����͂𑱍s�ł��܂���ł����B("+env.getInput().getLine()+"�s��)");
		}
	}

	public Value getValue() throws Exception{
		if (list!=null){
			return list.getValue();
		}
		return null;
	}

	public String toString(int indent) {
		if (list!=null){
			return list.toString(indent);
		}
		return "";
	}
}
