package newlang3;

public class ValueImpl {

	int iValue=0;
	double dValue=0.0;
	String SValue="";
	boolean bValue=false;
	ValueType vType;


//  �������ׂ��R���X�g���N�^
	public ValueImpl(String s){
		vType=ValueType.STRING;
		SValue=s;
		try {
			iValue=Integer.parseInt(s);
		} catch (Exception e){
			iValue=0;
		}
	}
//	public Value(int i);
//	public Value(double d);
//	public Value(boolean b);
//	public String get_sValue();

	public String getSValue(){
		return SValue;
	}
	// �X�g�����O�^�Œl�����o���B�K�v������΁A�^�ϊ����s���B
    public int getIValue(){
		return 0;
	}
   	// �����^�Œl�����o���B�K�v������΁A�^�ϊ����s���B
    public double getDValue(){
		return 0.00;
	}
    // �����_�^�Œl�����o���B�K�v������΁A�^�ϊ����s���B
    public boolean getBValue(){
		return false;
	}
    // �_���^�Œl�����o���B�K�v������΁A�^�ϊ����s���B
    public ValueType getType(){
		return vType;
	}
}



//valueOf���g��
