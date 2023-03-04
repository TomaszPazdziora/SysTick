import java.awt.AWTEventMulticaster;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.TimeUnit;
import javax.swing.JButton;
import javax.swing.JFrame;

public class Generator extends Thread implements PulseSource{
	
	ActionListener listener;
	
	private boolean alive;
	private boolean on;
	
	private byte mode;
	
	private int pulseDelay;
	private int pulseCount;
	
	private static int ticksToStop;
	
	public Generator() {
		
	}
	
	public Generator(int pulseCount, int pulseDelay, byte mode) {
		this.pulseCount = pulseCount;
		this.pulseDelay = pulseDelay;
		this.mode = mode;
	}
	
	public void addActionListener(ActionListener l) {
		listener = AWTEventMulticaster.add(listener, l);
	}
	
	public void removeActionListener(ActionListener l) {
		listener = AWTEventMulticaster.remove(listener, l);
	}
	
	public void run() 
	{
		alive = true;
		reloadBurst();
		
			while(alive)
			{
				if((on && ticksToStop > 0) || ((on && mode == CONTINOUS_MODE)))
				{
					try {
						Thread.sleep(getPulseDelay());
					}
					catch (InterruptedException e) {
						e.printStackTrace();
					}
					if (listener == null){
						System.out.println("Tick");
						
						if (mode == BURST_MODE)
							decrement();
						}
						
					else 
						listener.actionPerformed(new ActionEvent(this, 
																 ActionEvent.ACTION_PERFORMED, 
																 "tick"));
				}	
			
				else
					{
					try {
						Thread.sleep(1);
					}
						catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
	}
	
	public void killThread() {
		alive = false;
	}
	
	public void startGeneration() {
		on = true;
	}
	
	public void stopGeneration() {
		on = false;
	}
	
	public boolean checkOn() {
		return on;
	}
	

	@Override
	public void setPulseDelay(int ms) {
		// TODO Auto-generated method stub
		pulseDelay = ms;
	}

	@Override
	public int getPulseDelay() {
		// TODO Auto-generated method stub
		return pulseDelay;
	}

	@Override
	public void setPulseCount(int burst) {
		// TODO Auto-generated method stub
		pulseCount = burst;
	}

	@Override
	public void setMode(byte mode) {
		// TODO Auto-generated method stub
		this.mode = mode;
	}

	@Override
	public byte getMode() {
		// TODO Auto-generated method stub
		return mode;
	}
	
	public int getPulseCount() {
		return pulseCount;
	}
	
	public static void decrement() {
		--ticksToStop;
	}
	
	public void reloadBurst() {
		ticksToStop = getPulseCount();
	}
}