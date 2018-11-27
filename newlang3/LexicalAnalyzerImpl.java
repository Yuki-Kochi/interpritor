package newlang3;

import java.util.Map;
import java.util.HashMap;
import java.io.*;

public class LexicalAnalyzerImpl implements LexicalAnalyzer {

	//�N���X�ϐ�
	PushbackReader r;
	private static final Map<String,LexicalType> RESERVED_WORD=new HashMap<>();
	private static final Map<String,LexicalType> SYMBOLS=new HashMap<>();
	private static final Map<String,Character> ESCAPESEQUENCE=new HashMap<>();


	static {	//�X�^�e�B�b�N�C�j�V�����C�U�@�ŏ��ɂP�x�������s
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

		SYMBOLS.put("\n",LexicalType.NL);
		SYMBOLS.put("\r",LexicalType.NL);
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
		ESCAPESEQUENCE.put("\\\r",'\r');
		ESCAPESEQUENCE.put("\\\n",'\n');
		ESCAPESEQUENCE.put("\\\"",'\"');
		ESCAPESEQUENCE.put("\\\'",'\'');
		ESCAPESEQUENCE.put("\\\t",'\t');
	}

	public LexicalAnalyzerImpl(InputStream in){
		r=new PushbackReader(new InputStreamReader(in));
	}

	public LexicalUnit get() throws Exception {
		CharType type;
		while((type=getNextCharType())==CharType.SKIP){
			r.read();						//����Ȃ��̂œǂݐi��
		}
		if (type==CharType.LETTER){
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
			throw new Exception("���ߕs�\�ȕ����������܂����B");
		}
	}

	public boolean expect(LexicalType type) throws Exception{
		return false;
	}

	public void unget(LexicalUnit token) throws Exception{
	}

	private LexicalUnit getLiteral() throws Exception {	//�擪��"�܂���'�̏ꍇ
		String s="";					//���ʕ�����i�[�p�B�O���\"��\'�͓���Ȃ��悤�ɂ���B
		char c;							//�ꎞ�I�ȕۑ��̈�
		try {
			char openChar=(char)r.read();	//�J�����L���B\"��\'������͂�
			while(true){
				if (getNextCharType()!=CharType.EOF){
					if (getNextCharType()==CharType.ESCAPE){
						s+=escapeProcess();
						continue;
					}
					c=(char)r.read();
					if ((char)c==openChar){			//���e�����I�[�ɓ��B
						break;
					} 
					if ((char)c=='\r' || (char)c=='\n'){
						throw new Exception("\r or \n in Literal.");
					}
					s+=(char)c;
				} else {
					throw new Exception("Literal not closed.");
				}
			}
		} catch (Exception e){
			System.out.println("Literal�擾���̗�O:"+e);
		}
		return new LexicalUnit(LexicalType.LITERAL,new ValueImpl(s));
	}

	private LexicalUnit getString(){	//�擪���A���t�@�x�b�g
		String target="";
		while(true){
			try {
				if (getNextCharType()==CharType.LETTER || getNextCharType()==CharType.DIGIT){
					target+=(char)r.read();
					continue;
				} else if (getNextCharType()==CharType.ESCAPE){
					target+=escapeProcess();
					continue;
				} else {
					break;
				}
			} catch (Exception e){
				System.out.println("getString�ł̗�O:"+e);
			}
		}
		if (RESERVED_WORD.containsKey(target.toLowerCase())==true){		//�\���
			return new LexicalUnit((LexicalType)RESERVED_WORD.get(target.toLowerCase()));
		} else {											//�\���ȊO���ϐ����Ȃ�
			return new LexicalUnit(LexicalType.NAME,new ValueImpl(target));
		}
	}

	private LexicalUnit getNumber(){	//�擪�������̏ꍇ
		String s="";					//���ʕ�����i�[�p�B�O���\"��\'�͓���Ȃ�
		char c;							//�ꎞ�I�ȕۑ��̈�
		boolean dp=false;				//�����_����������true�ɂ���
		try {
			while(true){
				if (getNextCharType()==CharType.DIGIT){		//����
					s+=(char)r.read();
				} else if (getNextCharType()==CharType.SYMBOL){
					c=(char)r.read();
					if (c=='.' && dp==false){			//�����_
						dp=true;
						s+=c;
					} else {
						r.unread(c);
						break;
					}
				} else {									//���̑�
					break;
				}
			}
		} catch (Exception e){
				System.out.println("getNumber�ł̗�O:"+e);
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

	private LexicalUnit getSymbol(){	//�擪���L��
		char c,c2=0;
		LexicalUnit lu=null;
		try {
			c=(char)r.read();
			if (getNextCharType()==CharType.SYMBOL){		//�����L��
				c2=(char)r.read();
			}
			if (SYMBOLS.get(""+c+c2)!=null){
				lu=new LexicalUnit((LexicalType)SYMBOLS.get(""+c+c2));
			} else {
				if (c2!=0){
					r.unread(c2);									//�Q�����ڂ͂���Ȃ�
				}
				lu=new LexicalUnit((LexicalType)SYMBOLS.get(""+c));
			}
		} catch (Exception e){
			System.out.println("getSimbol�ł̗�O:"+e);
			System.exit(-1);
		}
		throw new InternalError();
	}

	private CharType getNextCharType(){
		int ci=0;
		try {
			ci=r.read();
			if (ci<0){
				return CharType.EOF;
			}
			r.unread(ci);
		} catch (Exception e){
			System.out.println("getNextCharType���ł̗�O:"+e);
			System.exit(-1);
		}
		char c=(char)ci;
		if (c==' ' || c=='\t'){
			return CharType.SKIP;
		} else if (c=='\\'){
			return CharType.ESCAPE;
		} else if (Character.isLetter(c)){
			return CharType.LETTER;
		} else if (Character.isDigit(c)){
			return CharType.DIGIT;
		} else if (c=='\"' || c=='\''){
			return CharType.LITERAL;
		} else if (SYMBOLS.containsKey(""+c)==true){
			return CharType.SYMBOL;
		}
		System.out.println(c);
		return CharType.OTHER;
	}

	private char escapeProcess() throws Exception{
		String s="";
		char c;
		try {
			s+=(char)r.read();		//�擪��\��ǂ�
			if (getNextCharType()!=CharType.EOF){
				c=(char)r.read();	//�Q�����ڂ�ǂ�
				s+=c;
			} else {
				throw new Exception("EOF found in the \"escapeProcess()\"");
			}
			if (ESCAPESEQUENCE.containsKey(s)==true){		//�\���
				return (char)ESCAPESEQUENCE.get(s);
			}else {
				return c;
			}
		} catch (Exception e){
			throw new Exception("escapeProcess�ł̗�O:"+e);
		}
	}
}
