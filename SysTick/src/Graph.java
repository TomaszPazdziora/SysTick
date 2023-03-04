import java.awt.*;
import javax.swing.*;

public class Graph extends JPanel {
    
	private static final long serialVersionUID = 1L;
	private int[] data = {0, 0, 0, 0, 0, 0};
    private int maxValue = 0;
    private final int width = 190;
    private final int height = 160;
    
    private int scalled = 1;
    
    private final int minX = 30;
    private final int maxX = 180;
    
    private final int minY = 160;
    private final int maxY = 40;
    
    private int xstep = (maxX - minX) / data.length;
    private int ystep = 0;
    
    public void scaleGraph(int max) {
    	if (max >= (minY - maxY)) {
        	int counter = 2;
	        while(true) {
		        if (max / counter < (minY - maxY)) {
		            scalled = counter;
		            break;
		        }	
		        counter++;
	        }
        }
    }


    public Graph(int period) {
        setPreferredSize(new Dimension(width, height));
        scaleGraph(period);
        
        maxValue = period;
        
        for (int i = 0; i < data.length; i++) {
        	data[i] = period;
        }
    }
    
    public void reloadMaxValue() {
    	int max = 0;
    	for (int i = 0; i < data.length; i++) {
        	if(max < data[i])
        		max = data[i];
        }
    	scaleGraph(max);
    	maxValue = max;
    }
    
    public void reload(int v) {
    	// get last index
    	int len = data.length - 1;
    	// shift every value left
    	for(int i = 0; i < len; i++) {
    		data[i] = data[i + 1];
    	}
    	data[len] = v;
    	
    	paintComponent(getGraphics());
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // axis legend
        g.setFont(new Font ("Verdana", 0, 10));
        g.drawString("Generator", minX - 30, maxY - 25);
        g.drawString("period [ms]", minX - 30, maxY - 10);
        g.drawString("Probe", maxX - 30, minY + 15);
        
        // x axis
        g2d.drawLine(minX, minY, maxX, minY);
        // y axis
        g2d.drawLine(minX, minY, minX, maxY);
        
        reloadMaxValue();
        
        ystep = (maxY - minY) / (maxValue / scalled);

        // start from point first data point
        int x = minX;
        int y = minY + (data[0] / scalled) * ystep;
        g.drawString("" + data[0], x, y - 5);
        
        for (int i = 1; i < data.length; i++) {
            int x2 = x + xstep;
            int y2 = minY + (data[i] / scalled) * ystep;
            g2d.drawLine(x, y, x2, y2);
            g.drawString("" + data[i], x2, y2 - 5);
            
            x = x2;
            y = y2;
        }
    }
}