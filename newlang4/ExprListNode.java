package newlang4;

import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import newlang3.*;

public class ExprListNode extends Node {

	List<Node> list=new ArrayList<>();

	//������first���Z�b�g�ł����Ă���
	private final static Set<LexicalType> FIRST=new HashSet<>(Arrays.asList(
		LexicalType.NAME,
		LexicalType.SUB,
		LexicalType.LP,
		LexicalType.INTVAL,
		LexicalType.DOUBLEVAL,
		LexicalType.LITERAL
	));

	public static boolean isMatch(LexicalType type){
		return FIRST.contains(type);
	}

	private ExprListNode(Environment in){
		env=in;
		type=NodeType.EXPR_LIST;
	}

	public static Node getHandrar(Environment envIn){
		return new ExprListNode(envIn);
	}

	public void parse() throws Exception {
		if (ExprNode.isMatch(env.getInput().peek(1).getType())){
			Node handrar=ExprNode.getHandrar(env);
			handrar.parse();
			list.add(handrar);
		} else {
			throw new InternalError("�֐��ďo�ɂ���������Ƃ��ĕs�K�؂ł��B"+env.getInput().getLine()+"�s��");
		}

		while(true){
			//�R���}��expr�̌J��Ԃ�

			if (env.getInput().peek(1).getType()==LexicalType.COMMA){
				env.getInput().get();
			} else {
				break;
			}

			if (ExprNode.isMatch(env.getInput().peek(1).getType())){
				Node handrar=ExprNode.getHandrar(env);
				handrar.parse();
				list.add(handrar);
			} else {
				throw new SyntaxException("�֐��Ăяo���ɂ�����������X�g�̍\�����s���ł��B"+env.getInput().getLine()+"�s��");
			}
		}
	}

	public String toString() {
		String tmp="";
		for(int i=0;i<list.size();i++){
			tmp+=list.get(i);
			if (i!=list.size()-1){
				tmp+=" , ";
			}
		}
		return tmp;
	}
}
