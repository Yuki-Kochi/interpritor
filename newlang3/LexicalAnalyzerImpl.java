package newlang3;

import java.util.Map;
import java.util.HashMap;
import java.io.*;

public class LexicalAnalyzerImpl implements LexicalAnalyzer {

	//�N���X�ϐ�
	private static final Map RESERVED_WORD=new HashMap();
	static {	//�X�^�e�B�b�N�C�j�V�����C�U�B�ŏ��̂P�x�������s
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
	}
	private static final Map SIMBOL_FIRSTWORD=new HashMap();
	static {	//�X�^�e�B�b�N�C�j�V�����C�U�B�ŏ��̂P�x�������s
		SIMBOL_FIRSTWORD.put('\n',LexicalType.NL);
		SIMBOL_FIRSTWORD.put('\r',LexicalType.NL);
//		SIMBOL_FIRSTWORD.put(13,LexicalType.NL);
		SIMBOL_FIRSTWORD.put(".",LexicalType.DOT);
		SIMBOL_FIRSTWORD.put("+",LexicalType.ADD);
		SIMBOL_FIRSTWORD.put("-",LexicalType.SUB);
		SIMBOL_FIRSTWORD.put("*",LexicalType.MUL);
		SIMBOL_FIRSTWORD.put("/",LexicalType.DIV);
		SIMBOL_FIRSTWORD.put("(",LexicalType.LP);
		SIMBOL_FIRSTWORD.put(")",LexicalType.RP);
		SIMBOL_FIRSTWORD.put(",",LexicalType.COMMA);
		SIMBOL_FIRSTWORD.put('=',null);
		SIMBOL_FIRSTWORD.put("<",null);
		SIMBOL_FIRSTWORD.put(">",null);
	}

	PushbackReader r;

	//�R���X�g���N�^
	public LexicalAnalyzerImpl(PushbackReader in){
		r=in;
	}

	//�P�P���Ԃ�
	public LexicalUnit get() throws Exception {
		int c;							//�ŏ��̂P�������i�[
		while((c=r.read())>=0){
			if (c!=' ' && c!='\t'){		//�X�y�[�X�ƃ^�u�͓ǂݔ�΂��ׂ�
				break;					//����ȊO��������break���Ă��ւ�����
			}
		}
System.out.println(c);
		if(c>=0){						//EOF�ȊO
			//c=�A���t�@�x�b�g
			if ((c>='A' && c<='Z') || (c>='a' && c<='z')){
				r.unread(c);			//�擪��������ŕK�v�Ȃ̂Ŗ߂��Ă���
				return getString();
			}
			//c=����
			if (c>='0' && c<='9'){
				r.unread(c);			//�擪��������ŕK�v�Ȃ̂Ŗ߂��Ă���
				return getNumber();
			}
			//c=�N�I�[�e�[�V����
			if (c=='\"' || c=='\''){
				r.unread(c);			//�擪��������ŕK�v�Ȃ̂Ŗ߂��Ă���
				return getLiteral();
			}
			//c=�L��
			if (SIMBOL_FIRSTWORD.containsKey((char)c)==true){
				r.unread(c);
				return getSimbol();
			}
			//���ߕs�\�͗�O������
			System.out.println(c+": ���ߕs�\�ȕ����������܂����B");
			throw new Exception();
		} else {						//EOF
//System.out.println("EOF����");
			LexicalUnit lu=new LexicalUnit(LexicalType.EOF);
			return lu;
		}
	}

	public boolean expect(LexicalType type) throws Exception{
		return false;
	}
	public void unget(LexicalUnit token) throws Exception{
		return;
	}

	//�擪���N�I�[�e�[�V�����̏ꍇ
	private LexicalUnit getLiteral(){
		String s=null;					//���ʕ�����i�[�p�B�O���\"��\'�͓���Ȃ��悤�ɂ���B
		char c;							//�ꎞ�I�ȕۑ��̈�
		try {
			char openChar=(char)r.read();	//�J�����L���B\"��\'�̂����ꂩ������͂�
			while(true){
				c=(char)r.read();
				if (c==openChar){			//�t�@�C���I�[�ɓ��B
					break;
				} else if (c==0){			//�t�@�C�������ɓ��B
					break;
				} else {					//���e�������̕��ʂ̕���
					s+=c;
				}
			}		
		} catch (Exception e){
				System.out.println(e);
		}
		ValueImpl v=new ValueImpl(s);
		LexicalUnit lu=new LexicalUnit(LexicalType.LITERAL,v);
		return lu;
	}

	//�擪���A���t�@�x�b�g�̏ꍇ
	private LexicalUnit getString(){
		String target="";
		char c;
		LexicalUnit lu;

		while(true){
			try {
				c=(char)r.read();
				//����Ȃ����̂�ǂ�ł��܂����ꍇ
				if (!((c>='A' && c<='Z') || (c>='a' && c<='z') || (c>='0' && c<='9'))){
					r.unread((int)c);
					break;
				}
				target+=c;
			} catch (Exception e){
				System.out.println(e);
			}
		}
		if (RESERVED_WORD.containsKey(target.toLowerCase())==true){		//�\���
			lu=new LexicalUnit((LexicalType)RESERVED_WORD.get(target.toLowerCase()));
		} else {											//�\���ȊO���ϐ����Ȃ�
			ValueImpl v=new ValueImpl(target);
			lu=new LexicalUnit(LexicalType.NAME,v);
		}
		return lu;
	}

	//�擪�������̏ꍇ
	private LexicalUnit getNumber(){
		String s="";					//���ʕ�����i�[�p�B�O���\"��\'�͓���Ȃ��悤�ɂ���B
		char c;							//�ꎞ�I�ȕۑ��̈�
		boolean dp=false;				//�����_����������true�ɂ���
		try {
			while(true){
				c=(char)r.read();
				if (c>='0' && c<='9'){					//����
					s+=c;
				} else if (c=='.' && dp==false){			//�����_
					s+=c;
					dp=true;
				} else {								//���̑�
					break;
				}
			}		
		} catch (Exception e){
				System.out.println(e);
		}
		ValueImpl v=new ValueImpl(s);
		LexicalUnit lu;
		if(dp){
			lu=new LexicalUnit(LexicalType.DOUBLEVAL,v);
		} else {
			lu=new LexicalUnit(LexicalType.INTVAL,v);
		}
		return lu;
	}

	private LexicalUnit getSimbol(){
		char c;							//�ꎞ�I�ȂP�����ڂ̕ۑ��̈�
		char c2;						//�ꎞ�I�ȂQ�����ڂ̕ۑ��̈�
		LexicalUnit lu=null;
		try {
			c=(char)r.read();
			if (c=='='){
				c2=(char)r.read();
				if (c2=='<') {
					lu=new LexicalUnit(LexicalType.LE);
				} else if (c2=='>') {
					lu=new LexicalUnit(LexicalType.GE);
				} else {
					r.unread((int)c2);		//2�����ڂ͊֌W�Ȃ����̂������̂Ŗ߂�
					lu=new LexicalUnit(LexicalType.EQ);
				}
			} else if (c=='<'){
				c2=(char)r.read();
				if (c2=='=') {
					lu=new LexicalUnit(LexicalType.LE);
				} else if (c2=='>') {
					lu=new LexicalUnit(LexicalType.NE);
				} else {
					r.unread((int)c2);		//2�����ڂ͊֌W�Ȃ����̂������̂Ŗ߂�
					lu=new LexicalUnit(LexicalType.LT);
				}
			} else if (c=='>'){
				c2=(char)r.read();
				if (c2=='=') {
					lu=new LexicalUnit(LexicalType.GE);
				} else {
					r.unread((int)c2);		//2�����ڂ͊֌W�Ȃ����̂������̂Ŗ߂�
					lu=new LexicalUnit(LexicalType.GT);
				}
			} else {						//�P�����ňӖ��𐬂��L��
				lu=new LexicalUnit((LexicalType)SIMBOL_FIRSTWORD.get(c));
			}
		} catch (Exception e){
				System.out.println(e);
		}
		return lu;
	}
}


