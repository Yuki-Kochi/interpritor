import newlang3.*;
import newlang4.*;
import newlang5.*;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.IOException;

public class newlang5Main {

	public static void main(String[] args) throws Exception {
		InputStream in;
		String fileName="test.bas";
	  
		System.out.println("basic parser");

		if (args.length>0){
			fileName=args[0];
		}

		try{
			in=new FileInputStream(fileName);
		} catch(IOException e) {
			System.out.println(fileName+"��ǂݍ��߂܂���ł����B");
			return;
		}

		LexicalAnalyzer lex = new LexicalAnalyzerImpl(in);
		Environment env = new Environment(lex);

		try {
			Node program = Program.getHandler(env);
			program.parse();
			System.out.println(program.getValue());
		} catch (IOException e){
			System.out.println("I/O�G���[���������܂����B");
		} catch (SyntaxException e){
			System.out.println(e.getMessage());
		} catch (CalcurateException e){
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
