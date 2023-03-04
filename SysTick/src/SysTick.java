public class SysTick implements Cortex_M0_SysTick_Interface{
	
	// Create SysTick's registers
	private Register Syst_CSR = new Register();
	private Register Syst_RVR = new Register();
	private Register Syst_CVR = new Register();
	
	// CSR bit meaning variables
	private final short COUNTFLAG_BIT_NUMBER = 16;
	private final short CLKSOURCE_BIT_NUMBER = 2;
	private final short TICKINT_BIT_NUMBER = 1;
	private final short ENABLE_BIT_NUMBER = 0;
	
	// Boolean fields
	private boolean isInterrupted = false;
	private boolean isDisabledOnNextWrap = false;
	private boolean isInternalActive = false;
	private boolean isExternalActive = false;
	
	// Other fields
	private final int LAST_CVR_ANAVIBLE_VALUE = 16777216; 
	
	/*======================= Interface Methods =======================*/
	
	@Override
	public void tickInternal() {
			tick();
	}

	@Override
	public void tickExternal() {
			tick();
	}
	
	/*======================= Register's set Methods =======================*/
	
	@Override
	public void setRVR(int value) {
		
		if (value > LAST_CVR_ANAVIBLE_VALUE - 1)
			Syst_RVR.setValue(value - LAST_CVR_ANAVIBLE_VALUE);	
		else if (value >= 0)
			Syst_RVR.setValue(value);
		else if (value < 0)
			Syst_RVR.setValue((1<<24) + value);
	}

	@Override
	public void setCVR(int value) {
		Syst_CVR.setValue(0);
		resetCountFlag();
	}

	@Override
	public void setCSR(int value) {
		Syst_CSR.setValue(value);
	}
	
	/*======================= RESET =======================*/
	@Override
	public void reset() {
		Syst_CSR.setValue(0);
	}

	/*======================= Other set Methods =======================*/
	
	@Override
	public void setEnable() {
		Syst_CSR.setBit(ENABLE_BIT_NUMBER);
		
		// remember clock source state in case of switching clk source
		saveClockState(true);
	}

	@Override
	public void setDisable() {
		Syst_CSR.resetBit(ENABLE_BIT_NUMBER);
		
		// remember clock source state in case of switching clk source
		saveClockState(false);
	}

	@Override
	public void setSourceExternal() {
		Syst_CSR.resetBit(CLKSOURCE_BIT_NUMBER);
		
		// reload external source state 
		reloadClockState(isExternalActive);
	}
	
	@Override
	public void setSourceInternal() {
		Syst_CSR.setBit(CLKSOURCE_BIT_NUMBER);
		
		// reload external source state 
		reloadClockState(isInternalActive);
	}

	@Override
	public void setInterruptEnable() {
		Syst_CSR.setBit(TICKINT_BIT_NUMBER);
	}
	
	@Override
	public void setInterruptDisable() {
		Syst_CSR.resetBit(TICKINT_BIT_NUMBER);
	}
	
	//////////////////////////// My set methods //////////////////////////////////
	
	public void setCountFlag(){
		Syst_CSR.setBit(COUNTFLAG_BIT_NUMBER);
	}
	
	public void resetCountFlag(){
		Syst_CSR.resetBit(COUNTFLAG_BIT_NUMBER);
	}
	
	/*======================= Register get Methods =======================*/
	
	@Override
	public int getRVR() {
		return Syst_RVR.getValue();
	}
	
	@Override
	public int getCVR() {
		return Syst_CVR.getValue();
	}
	
	@Override
	public int getCSR() {
		int csrBuffer = Syst_CSR.getValue();
		Syst_CSR.resetBit(COUNTFLAG_BIT_NUMBER);
		return csrBuffer;
	}
	
	
	/*======================= Other get Methods =======================*/
	
	@Override
	public boolean getEnabled() {
		return getCSRBitValueAndResetCountflag(ENABLE_BIT_NUMBER);
	}

	@Override
	public boolean getSource() {
		return getCSRBitValueAndResetCountflag(CLKSOURCE_BIT_NUMBER);
	}

	@Override
	public boolean getCountFlag() {
		return getCSRBitValueAndResetCountflag(COUNTFLAG_BIT_NUMBER);
	}
	
	@Override
	public boolean getInterrupt() {
		return getCSRBitValueAndResetCountflag(TICKINT_BIT_NUMBER);
	}
	
	/*======================= Register is Methods =======================*/

	@Override
	public boolean isCountFlag() {
		return Syst_CSR.getBitValue(COUNTFLAG_BIT_NUMBER);
	}

	@Override
	public boolean isEnableFlag() {
		return Syst_CSR.getBitValue(ENABLE_BIT_NUMBER);
	}

	@Override
	public boolean isInterruptFlag() {
		return Syst_CSR.getBitValue(TICKINT_BIT_NUMBER);
	}

	@Override
	public boolean isInterrupt() {
		return isInterrupted;
	}
	
	
	
//////////////////////////// My methods //////////////////////////////////
	
	public boolean isInternalActive() {
		return Syst_CSR.getBitValue(CLKSOURCE_BIT_NUMBER);
	}
	
	public void setInterruptedFalse() {
		isInterrupted = false;
	}
	
	public int getCSRforGUI()	{
		return Syst_CSR.getValue();
	}
	
	public boolean getCSRBitValueAndResetCountflag(int bitNumber) {
		boolean buffer = Syst_CSR.getBitValue(bitNumber);
		Syst_CSR.resetBit(COUNTFLAG_BIT_NUMBER);
		return buffer;
	}
	
	public void saveClockState(boolean state) {
		if (isInternalActive() == true)
			isInternalActive = state;
		else
			isExternalActive = state;
	}
	
	public void reloadClockState(boolean isIntOrIsExtField) {
		if(isIntOrIsExtField == true)
			setEnable();
		else
			setDisable();
	}
	
	public void tick() {
		
		if (getRVR() == 0)
		{
			isDisabledOnNextWrap = true;
		}
		
		if (Syst_CVR.getValue() == 0 && isDisabledOnNextWrap == false)
		{
			isInterrupted = true;
			Syst_CVR.setValue(Syst_RVR.getValue());
			return;
		}
		
		if (isEnableFlag() && Syst_CVR.getValue() > 0)
		{
			Syst_CVR.decrement();
			if (Syst_CVR.getValue() == 0)
			{
				setCountFlag();
			}
		}
		
		if (getRVR() != 0)
		{
			isDisabledOnNextWrap = false;
		}
	}
}