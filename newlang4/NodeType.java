package newlang4;

public enum NodeType {
	PROGRAM,
	STMT_LIST,
	STMT,
	FOR_STMT,
	ASSIGN_STMT,		//代入文(subst)
	BLOCK,
	IF_BLOCK,
	LOOP_BLOCK,
	COND,				//ifやwhileで使う条件判定
    EXPR_LIST,
	EXPR,				//式
	FUNCTION_CALL,
    STRING_CONSTANT,
    INT_CONSTANT,
    DOUBLE_CONSTANT,
    BOOL_CONSTANT,
	VARIABLE,
    END,
}
