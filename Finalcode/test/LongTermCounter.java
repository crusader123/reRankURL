package parsing;

public class LongTermCounter 
{
	int type1;
	int type2;
	int type3;
	int type4;
	int type5;
	int value;
	LongTermCounter()
	{
		type1=0;
		type2=0;
		type3=0;
		type4=0;
		type5=0;
	}

	@Override
	public String toString() {
		value = 50*type1 +30*type2 + 15*type3 + 5*type4 + type5;
		return " |l 1:"+ value;
	}
	
	
}