package newlang4;

import java.io.IOException;
import newlang3.*;

public class Node {
	NodeType type;
	Environment env;

	/** Creates a new instance of Node */
	public Node() {
	}

	public NodeType getType() {
		return type;
	}

	//���@��͂̎��s�B�߂�l�Ő���or���s��Ԃ�
	public void parse() throws Exception {
	}

	//�v���O�����̎��s
	public Value getValue() throws Exception {
		return null;
	}

	public String toString() {
		if (type == NodeType.END) return "END";
		else return "Node";
	}

	public String toString(int indent) {
		if (type == NodeType.END) return "END";
		else return "Node";
	}

}
