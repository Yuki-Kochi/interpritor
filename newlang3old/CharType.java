package newlang3;

public enum CharType {
	SKIP,			//���p�X�y�[�X�Ɛ����^�u
	NEWLINE,		//���s����
	ESCAPE,			//���p��\
	LETTER,			//���p�A���t�@�x�b�g
	DIGIT,			//���p����
	LITERAL,		//"��'
	SYMBOL,			//LexicalType�Œ�`���ꂽ�L���ɗp�����镶��
	OTHER,			//���̑�
	EOF				//�t�@�C���I�[
}
