package newlang4;

import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import newlang3.*;

public class ExprListNode extends Node {

	List<Node> list=new ArrayList<>();

	//自分のfirstをセットでもっておく
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

	public static ExprListNode getHandler(Environment envIn){
		return new ExprListNode(envIn);
	}

	public void parse() throws Exception {
		if (ExprNode.isMatch(env.getInput().peek(1).getType())){
			Node handler=ExprNode.getHandler(env);
			handler.parse();
			list.add(handler);
		} else {
			throw new InternalError("関数呼出における引数として不適切です。"+env.getInput().getLine()+"行目");
		}

		while(true){
			//コンマとexprの繰り返し
			if (env.getInput().expect(LexicalType.COMMA)){
				env.getInput().get();
			} else {
				break;
			}

			if (ExprNode.isMatch(env.getInput().peek(1).getType())){
				Node handler=ExprNode.getHandler(env);
				handler.parse();
				list.add(handler);
			} else {
				throw new SyntaxException("関数呼び出しにおける引数リストの構成が不正です。"+env.getInput().getLine()+"行目");
			}
		}
	}

	public int size(){
		return list.size();
	}

	public Value get(int i) throws Exception{
		if (list.size()>i){
			return list.get(i).getValue();
		}else {
			return null;
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
