import java.awt.event.*;


public interface PulseSource
{
    final static byte BURST_MODE = 0;
    final static byte CONTINOUS_MODE = 1;

    void addActionListener(ActionListener pl);
    void removeActionListener(ActionListener pl);

    void startGeneration();
    void stopGeneration();
    
    void setMode(byte mode);
    byte getMode() ;
    
    void setPulseDelay(int ms) ;
    int getPulseDelay() ;
    void setPulseCount(int burst) ;
}
