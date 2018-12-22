package newlang4;

import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;
import newlang3.*;


public class BlockNode extends Node {

	//������first���Z�b�g�ł����Ă���
	private final static Set<LexicalType> FIRST=new HashSet<LexicalType>(Arrays.asList(
		LexicalType.DO,
		LexicalType.WHILE,
		LexicalType.IF
	));

	public static boolean isMatch(LexicalType type){
		return FIRST.contains(type);
	}

	private BlockNode(Environment in){
		env=in;
		type=NodeType.BLOCK;
	}

	public static Node getHandler(Environment envIn) throws Exception{
		if (IfNode.isMatch(envIn.getInput().peek(1).getType())){
			return IfNode.getHandler(envIn);
		}else if (LoopNode.isMatch(envIn.getInput().peek(1).getType())){
			return LoopNode.getHandler(envIn);
		} else {
			throw new InternalError("BlockNode�𐶐��ł��Ȃ�����ł��B"+envIn.getInput().getLine()+"�s��");
		}
	}

	public void parse() throws Exception {
		throw new InternalError("BlockNode�N���X��parse�͎��s�ł��܂���B");
	}

	public String toString() {
		return "Block:";
	}
}

