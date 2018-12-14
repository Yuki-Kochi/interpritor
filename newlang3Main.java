import newlang3.*;
import java.io.*;

class newlang3Main {

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
				System.out.print(la.getLine()+": ");
				lu=la.get();
				System.out.println(lu);
				if (lu.getType()==LexicalType.EOF){
					break;
				}
			}
		} catch (IOException e){
			System.out.println("I/O�G���[���������܂����B");
		} catch (SyntaxException e){
			System.out.println(e.getMessage());
		} catch (Exception e){
			System.out.println("�s���ȗ�O�F"+e);
		} finally {
			try {
				in.close();
			} catch(IOException e){
				System.out.println("�t�@�C���̃N���[�Y�Ɏ��s���܂����B");
			}
		}
	}
}
