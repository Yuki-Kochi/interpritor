package newlang3;

import java.util.Map;
import java.util.HashMap;
import java.io.*;
import java.util.List;
import java.util.ArrayList;

public class LexicalAnalyzerImpl implements LexicalAnalyzer {

	//�N���X�ϐ�
	PushbackReader r;
	int line=1;
	private static final Map<String,LexicalType> RESERVED_WORD=new HashMap<>();
	private static final Map<String,LexicalType> SYMBOLS=new HashMap<>();
	private static final Map<String,Character> ESCAPESEQUENCE=new HashMap<>();
	private List<LexicalUnit> unitList=new ArrayList<>();


	static {
		RESERVED_WORD.put("if",LexicalType.IF);
		RESERVED_WORD.put("then",LexicalType.THEN);
		RESERVED_WORD.put("else",LexicalType.ELSE);
		RESERVED_WORD.put("elseif",LexicalType.ELSEIF);
		RESERVED_WORD.put("endif",LexicalType.ENDIF);
		RESERVED_WORD.put("for",LexicalType.FOR);
		RESERVED_WORD.put("forall",LexicalType.FORALL);
		RESERVED_WORD.put("next",LexicalType.NEXT);
		RESERVED_WORD.put("func",LexicalType.FUNC);
		RESERVED_WORD.put("dim",LexicalType.DIM);
		RESERVED_WORD.put("as",LexicalType.AS);
		RESERVED_WORD.put("end",LexicalType.END);
		RESERVED_WORD.put("while",LexicalType.WHILE);
		RESERVED_WORD.put("do",LexicalType.DO);
		RESERVED_WORD.put("until",LexicalType.UNTIL);
		RESERVED_WORD.put("loop",LexicalType.LOOP);
		RESERVED_WORD.put("to",LexicalType.TO);
		RESERVED_WORD.put("wend",LexicalType.WEND);

		SYMBOLS.put(".",LexicalType.DOT);
		SYMBOLS.put("+",LexicalType.ADD);
		SYMBOLS.put("-",LexicalType.SUB);
		SYMBOLS.put("*",LexicalType.MUL);
		SYMBOLS.put("/",LexicalType.DIV);
		SYMBOLS.put("(",LexicalType.LP);
		SYMBOLS.put(")",LexicalType.RP);
		SYMBOLS.put(",",LexicalType.COMMA);
		SYMBOLS.put("=",LexicalType.EQ);
		SYMBOLS.put("<",LexicalType.LT);
		SYMBOLS.put("<=",LexicalType.LE);
		SYMBOLS.put("=<",LexicalType.LE);
		SYMBOLS.put("<>",LexicalType.NE);
		SYMBOLS.put(">",LexicalType.GT);
		SYMBOLS.put(">=",LexicalType.GE);
		SYMBOLS.put("=>",LexicalType.GE);

		ESCAPESEQUENCE.put("\\\\",'\\');
		ESCAPESEQUENCE.put("\\r",'\r');
		ESCAPESEQUENCE.put("\\n",'\n');
		ESCAPESEQUENCE.put("\\\"",'\"');
		ESCAPESEQUENCE.put("\\\'",'\'');
		ESCAPESEQUENCE.put("\\t",'\t');
	}

	public LexicalAnalyzerImpl(InputStream in){
		r=new PushbackReader(new InputStreamReader(in));
	}

	public LexicalUnit get() throws IOException,SyntaxException {

		//unget���ꂽ���̂��Ȃ����m�F����
		if (!unitList.isEmpty()){
			LexicalUnit tmp=unitList.get(unitList.size()-1);
			unitList.remove(unitList.size()-1);
			if (tmp.getType()==LexicalType.NL){
				line++;
			}
			return tmp;
		}

		CharType type;
		while((type=getNextCharType())==CharType.SKIP){
			r.read();
		}
		if (type==CharType.NEWLINE){
			parseNL();
			return new LexicalUnit(LexicalType.NL);
		} else if (type==CharType.LETTER){
			return getString();
		} else if (type==CharType.DIGIT){
			return getNumber();
		} else if (type==CharType.LITERAL){
			return getLiteral();
		} else if (type==CharType.SYMBOL){
			return getSymbol();
		} else if (type==CharType.EOF){
			return new LexicalUnit(LexicalType.EOF);
		} else {
			throw new SyntaxException("�s���ȕ���"+(char)r.read()+"�������܂����B("+line+"�s��)");
		}
	}

	public void unget(LexicalUnit token) throws IOException{
		if (token.getType()==LexicalType.NL){
			line--;
		}
		unitList.add(token);
	}

	public boolean expect(LexicalType type) throws IOException,SyntaxException{
		return type==peek(1).getType();
	}

	public LexicalUnit peek(int c) throws IOException,SyntaxException{
		List<LexicalUnit> tmpList=new ArrayList<>();
		for(int i=0;i<c-1;i++){
			tmpList.add(get());
		}

		LexicalUnit ret=get();
		tmpList.add(ret);

		while(!tmpList.isEmpty()){
			unget(tmpList.get(tmpList.size()-1));
			tmpList.remove((tmpList.size()-1));
		}
		return ret;
	}

	private void parseNL() throws IOException,SyntaxException{
		while(true){
			if(getNextCharType()==CharType.NEWLINE){
				r.read();
				line++;
			} else if (getNextCharType()==CharType.SKIP){
				r.read();
			} else {
				break;
			}
		}
	}

	private LexicalUnit getLiteral() throws SyntaxException,IOException {
		String s="";					//���ʕ�����B�O���"��'�͊܂܂Ȃ��B
		char c;							//�ꎞ�I�ȕۑ��̈�
		char openChar=(char)r.read();	//�J�����L���B"��'������͂�
		while(true){
			if (getNextCharType()!=CharType.EOF){
				if (getNextCharType()==CharType.ESCAPE){
					s+=escapeProcess();
					continue;
				}
				if (getNextCharType()==CharType.NEWLINE){
					throw new SyntaxException("���e����������ꂸ�ɉ��s����܂����B("+line+"�s��)");
				}
				c=(char)r.read();
				if ((char)c==openChar){			//���e�����I�[�ɓ��B
					break;
				} 
				s+=(char)c;
			} else {
				throw new SyntaxException("���e����������ꂸ�Ƀt�@�C���I�[�ɓ��B���܂����B("+line+"�s��)");
			}
		}
		return new LexicalUnit(LexicalType.LITERAL,new ValueImpl(s));
	}

	private LexicalUnit getString() throws IOException{
		String target="";
		while(true){
			if (getNextCharType()==CharType.LETTER || getNextCharType()==CharType.DIGIT){
				target+=(char)r.read();
				continue;
			} else if (getNextCharType()==CharType.ESCAPE){
				target+=escapeProcess();
				continue;
			} else {
				break;
			}
		}
		if (RESERVED_WORD.containsKey(target.toLowerCase())==true){		//�\���
			return new LexicalUnit((LexicalType)RESERVED_WORD.get(target.toLowerCase()));
		} else {											//�\���ȊO���ϐ����Ȃ�
			return new LexicalUnit(LexicalType.NAME,new ValueImpl(target));
		}
	}

	private LexicalUnit getNumber() throws IOException {
		String s="";				//���ʕ�����
		char c;						//�ꎞ�I�ȕۑ��̈�
		boolean dp=false;			//�r���ŏ����_��ǂ񂾂�true
		while(true){
			if (getNextCharType()==CharType.DIGIT){
				s+=(char)r.read();
			} else if (getNextCharType()==CharType.SYMBOL){
				c=(char)r.read();
				if (c=='.' && dp==false && getNextCharType()==CharType.DIGIT){
					dp=true;
					s+=c;
				} else {
					r.unread(c);
					break;
				}
			} else {
				break;
			}
		}
		LexicalUnit lu;
		if(dp){
			ValueImpl v=new ValueImpl(Double.parseDouble(s));
			lu=new LexicalUnit(LexicalType.DOUBLEVAL,v);
		} else {
			ValueImpl v=new ValueImpl(Integer.parseInt(s));
			lu=new LexicalUnit(LexicalType.INTVAL,v);
		}
		return lu;
	}

	private LexicalUnit getSymbol() throws IOException{
		char c,c2=0;
		LexicalUnit lu=null;
		c=(char)r.read();
		if (getNextCharType()==CharType.SYMBOL){
			c2=(char)r.read();
		}
		if (SYMBOLS.get(""+c+c2)!=null){
			lu=new LexicalUnit((LexicalType)SYMBOLS.get(""+c+c2));
		} else {
			if (c2!=0){
				r.unread(c2);
			}
			lu=new LexicalUnit((LexicalType)SYMBOLS.get(""+c));
		}
		return lu;
	}

	private CharType getNextCharType() throws IOException{
		int ci=0;
		ci=r.read();
		if (ci<0){
			return CharType.EOF;
		}
		r.unread(ci);
		char c=(char)ci;
		if (c==' ' || c=='\t' || c=='\r'){
			return CharType.SKIP;
		} else if (c=='\n'){
			return CharType.NEWLINE;
		} else if (c=='\\'){
			return CharType.ESCAPE;
		} else if (c>='A' && c<='Z' || c>='a' && c<='z'){
			return CharType.LETTER;
		} else if (c>='0' && c<='9'){
			return CharType.DIGIT;
		} else if (c=='\"' || c=='\''){
			return CharType.LITERAL;
		} else if (SYMBOLS.containsKey(""+c)==true){
			return CharType.SYMBOL;
		}
		return CharType.OTHER;
	}

	public int getLine(){
		return line;
	}

	private char escapeProcess() throws IOException{
		String s="";
		char c;
		s+=(char)r.read();		//�擪��\��ǂ�
		if (getNextCharType()!=CharType.EOF){
			c=(char)r.read();	//�Q�����ڂ�ǂ�
			s+=c;
		} else {
			return ' ';
		}
		if (ESCAPESEQUENCE.containsKey(s)==true){
			return (char)ESCAPESEQUENCE.get(s);
		}else {
			return c;
		}
	}
}
