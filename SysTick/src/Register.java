public class Register {
	
	/*======================= Private field =======================*/
	
	private int value = 0; 
	
	/*======================= Public methods =======================*/
	
	public void setValue(int num) {
		this.value = num;
	}
	
	public void decrement() {
		this.value--;
	}
	
	public int getValue() {
		return this.value;}
		
	/*======================= Bitwise methods =======================*/
	
	public boolean getBitValue(int bitNumber)
	{
		if ((this.value & (1 << bitNumber)) != 0)
			return true;
		else 
			return false;
	}
	
	public void setBit(int bitNumber) {
		this.value |= (1 << bitNumber);
	}
	
	public void resetBit(int bitNumber) {
		this.value &= ~(1 << bitNumber);
	}

	/*=============================================*/
	
	public void printValue() {
		System.out.println(this.value);
	}
}