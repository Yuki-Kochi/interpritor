package newlang5;

import newlang3.*;
import newlang4.*;

public class PrintFunction extends Function{

    public PrintFunction() {
    }

    public Value invoke(ExprListNode arg) throws Exception {
		if (arg==null || arg.size()==0){
			throw new CalcurateException("PRINT�֐��ւ̈������s���ł��B");
		}
		System.out.print(arg.get(0).getSValue());
		return null;        
    }
}
