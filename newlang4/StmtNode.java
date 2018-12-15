package newlang4;

import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;
import newlang3.*;

public class StmtNode extends Node {

	//������first���Z�b�g�ł����Ă���
	private final static Set<LexicalType> FIRST=new HashSet<LexicalType>(Arrays.asList(
		LexicalType.NAME,
		LexicalType.FOR,
		LexicalType.END
	));

	public static boolean isMatch(LexicalType type){
		return FIRST.contains(type);
	}

	private StmtNode(Environment in){
		throw new InternalError("StmtNode�N���X�̃C���X�^���X�͐����ł��܂���B("+in.getInput().getLine()+"�s��)");
	}

	public static Node getHandrar(Environment envIn) throws Exception{
		switch (envIn.getInput().peek(1).getType()){
			case NAME:
				if (envIn.getInput().peek(2).getType()==LexicalType.EQ){
					return SubstNode.getHandrar(envIn);
				} else if (ExprListNode.isMatch(envIn.getInput().peek(2).getType())){
					return CallNode.getHandrar(envIn);
				} else {
					throw new SyntaxException("���������ł͂���܂���B("+envIn.getInput().getLine()+"�s��)");
				}
			case FOR:
				return ForNode.getHandrar(envIn);
			case END:
				return EndNode.getHandrar(envIn);
			default:
				throw new InternalError("StmtNode�ɓK�����Ȃ��^��getHandrar���R�[������܂����B"+envIn.getInput().peek(1).getType());
		}
	}

	public void parse() throws Exception {
		throw new InternalError("StmtNode�N���X��parse�͎��s�ł��܂���B");
	}

	public String toString() {
		return "Stmt";
	}
}

