import newlang3.*;

import java.io.*;

class Main {

	public static void main(String[] args) {
		InputStream in;
		String fileName="test1.bas";
		LexicalAnalyzer la;
		LexicalUnit lu;

		if (args.length>0){
			fileName=args[0];
		}

		try{
			in=new FileInputStream(fileName);
		} catch(IOException e) {
			System.out.println(fileName+"��ǂݍ��߂܂���ł����B");
			return;
		}
		la=new LexicalAnalyzerImpl(in);

		try {
			while(true){
				lu=la.get();
				String tmp=lu.toString();
				System.out.println(tmp);
				if (tmp=="EOF"){
					break;
				}
			}
		} catch (Exception e){
			System.out.println(e);
		} finally {
			try {
				in.close();
			} catch(IOException e){
				System.out.println("�t�@�C���̃N���[�Y�Ɏ��s���܂����B");
			}
		}
	}
}
