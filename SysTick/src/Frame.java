import java.awt.EventQueue;

import javax.swing.JFrame;import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JCheckBox;
import javax.swing.BoxLayout;
import javax.swing.SwingConstants;
import javax.swing.JTextField;
import java.awt.Component;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import java.awt.TextArea;
import java.awt.Panel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JToggleButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JSlider;
import javax.swing.JSeparator;
import javax.swing.JSpinner;

public class Frame extends JFrame {

	private JPanel contentPane;
	private JTextField CVRtextField;
	private JTextField RVRtextField;
	private JTextField CVR_ValueDisp;
	private JTextField CVR_Disp;
	private JTextField RVR_ValueDisp;
	private JTextField RVR_Disp;
	private JTextField CSR_ValueDisp;
	private JTextField CSR_Disp;
	private JTextField CSRtextField;
	private JTextField txtBurstTickSlider;
	private JTextField txtSetCountDelay;

	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Frame frame = new Frame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		});
	}

	
	public Frame() {
		
		SysTick cortexM01 = new SysTick();
		
		Generator g = new Generator(5, 500, PulseSource.BURST_MODE);
		g.start();
		g.startGeneration();
		
		/*--------------------- LAMBDA FOR GENERATOR -------------------------*/
		
		g.addActionListener(e->{
			
			if (g.getMode() == PulseSource.BURST_MODE &&
							 	  cortexM01.isEnableFlag()) {
				cortexM01.tickInternal();
				Generator.decrement();
				System.out.println("Burst tick");
			}
			
			else if (g.getMode() == PulseSource.CONTINOUS_MODE && 
										   cortexM01.isEnableFlag()) {
				cortexM01.tickInternal();
				System.out.println("Continues tick");
			}
			
			if(cortexM01.isInterruptFlag() && cortexM01.isInterrupt()) 
				JOptionPane.showMessageDialog(null, "SysTick counted to 0!");
			
			cortexM01.setInterruptedFalse();
			CSR_ValueDisp.setText(String.valueOf(cortexM01.getCSRforGUI()));
			CVR_ValueDisp.setText(String.valueOf(cortexM01.getCVR()));
		});
		
		
		
		/*--------------------- FRAME LAYOUT  -------------------------*/
		
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 452, 473);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(10, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(new GridLayout(2, 2, 0, 0));
		
		JPanel panel_1 = new JPanel();
		contentPane.add(panel_1);
		
		panel_1.setLayout(new GridLayout(2, 1, 0, 3));
		
		JPanel panel_11 = new JPanel();
		panel_1.add(panel_11);
		panel_11.setLayout(new GridLayout(3, 1, 0, 0));
		
		JPanel panel_111 = new JPanel();
		panel_11.add(panel_111);
		
		JPanel panel_12 = new JPanel();
		panel_1.add(panel_12);
	
		JPanel panel_2 = new JPanel();
		contentPane.add(panel_2);
		
		Graph graph = new Graph(500);
		
		
		
		/*--------------------- WINDOW COMPONENTS  -------------------------*/
		
		
		
		/*--------------------- DELAY SPINNER  -------------------------*/
		
		
		JSpinner DelaySpinner = new JSpinner();
		DelaySpinner.setBounds(150, 47, 54, 30);
		panel_2.add(DelaySpinner);
		
		DelaySpinner.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				// TODO Auto-generated method stub
				int delay = DelaySpinner.getValue().hashCode();
				g.setPulseDelay(delay);
				graph.reload(delay);
				}
		    });
		

		
		/*--------------------- EXTERNAL TICK BTN  -------------------------*/
		
		
		JButton TickBtn = new JButton("External Tick");
		TickBtn.setBounds(47, 181, 120, 19);
		
		TickBtn.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == TickBtn &&
				  cortexM01.isInternalActive()) {
					
					System.out.println("External Tick");
					cortexM01.tickExternal();
					
					if(cortexM01.isInterruptFlag() && cortexM01.isInterrupt()) 
						JOptionPane.showMessageDialog(null, "SysTick counted to 0!");
					
					cortexM01.setInterruptedFalse();
					CSR_ValueDisp.setText(String.valueOf(cortexM01.getCSRforGUI()));
					CVR_ValueDisp.setText(String.valueOf(cortexM01.getCVR()));
					
				}
			}
		});
		panel_2.setLayout(null);
		panel_2.add(TickBtn);
		
		
		
		/*--------------------- BURST TICK SLIDER -------------------------*/

		
		JSlider BurstTicksSlider = new JSlider();
		BurstTicksSlider.setBounds(30, 122, 156, 19);
		panel_2.add(BurstTicksSlider);
		BurstTicksSlider.setValue(g.getPulseCount());
		
		BurstTicksSlider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				// TODO Auto-generated method stub
				g.setPulseCount(BurstTicksSlider.getValue());
				g.reloadBurst();
				txtBurstTickSlider.setText("BURST TICKS: " + g.getPulseCount());
				}
		    });
		
		txtBurstTickSlider = new JTextField();
		txtBurstTickSlider.setText("BURST TICKS: " + g.getPulseCount());
		txtBurstTickSlider.setBounds(47, 87, 120, 25);
		panel_2.add(txtBurstTickSlider);
		txtBurstTickSlider.setColumns(10);
		
		
		
		/*--------------------- RELOAD BURS BUTTON  -------------------------*/
		
		
		JButton BurstBtn = new JButton("Reload Burst");
		BurstBtn.setBounds(47, 151, 120, 19);
		panel_2.add(BurstBtn);
		
		BurstBtn.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				
				if (e.getSource() == BurstBtn) {
					g.reloadBurst();
				}
			}
		});
		
		txtSetCountDelay = new JTextField();
		txtSetCountDelay.setText("Set count delay in ms");
		txtSetCountDelay.setBounds(20, 47, 125, 30);
		panel_2.add(txtSetCountDelay);
		txtSetCountDelay.setColumns(10);
		
		
		
		/*--------------------- MODE BUTTON  -------------------------*/
		
		
		JButton ModeBtn = new JButton("Burst Mode");
		ModeBtn.setBounds(10, 10, 194, 25);
		panel_2.add(ModeBtn);
		
		ModeBtn.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				
				if (e.getSource() == ModeBtn) {
					
					if (g.getMode() == PulseSource.BURST_MODE) {
						g.setMode(PulseSource.CONTINOUS_MODE);
						ModeBtn.setText("Continous Mode");
					}
					else {
						g.setMode(PulseSource.BURST_MODE);
						ModeBtn.setText("Burst Mode");
					}
				}
			}
		});
		
		
		
		/*--------------------- CHECKBOXES -------------------------*/
		
		
		/*--------------------- Enable checkbox -------------------------*/
		
		
		panel_12.setLayout(null);
		
		JCheckBox chckbxEnable = new JCheckBox("Enable");
		chckbxEnable.setBounds(42, 18, 127, 25);
		panel_12.add(chckbxEnable);
		
		chckbxEnable.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				if (chckbxEnable.isSelected()) {
					cortexM01.setEnable();
				}
					
				else {
					cortexM01.setDisable();
				}
			}
		});
		
		
		
		/*--------------------- Source Internal checkbox -------------------------*/
		
		
		JCheckBox chckbxSource = new JCheckBox("Source External");
		chckbxSource.setBounds(42, 45, 148, 25);
		panel_12.add(chckbxSource);
		
		chckbxSource.addChangeListener(new ChangeListener() {
			
			
			public void stateChanged(ChangeEvent e) {
				if (chckbxSource.isSelected()) {
					cortexM01.setSourceInternal();
					g.stopGeneration();
					
					//Prepare components
					TickBtn.setEnabled(true);
					ModeBtn.setEnabled(false);
					BurstBtn.setEnabled(false);
					DelaySpinner.setEnabled(false);
					txtSetCountDelay.setEnabled(false);
					txtBurstTickSlider.setEnabled(false);
					BurstTicksSlider.setEnabled(false);
					
				}
					
				else {
					cortexM01.setSourceExternal();
					g.startGeneration();
					
					//Prepare components
					TickBtn.setEnabled(false);
					ModeBtn.setEnabled(true);
					BurstBtn.setEnabled(true);
					DelaySpinner.setEnabled(true);
					txtSetCountDelay.setEnabled(true);
					txtBurstTickSlider.setEnabled(true);
					BurstTicksSlider.setEnabled(true);
					
				}

				if (cortexM01.isEnableFlag())
					chckbxEnable.setSelected(true);
				else
					chckbxEnable.setSelected(false);
			}
		});
		
		
		
		/*--------------------- Tickint checkbox -------------------------*/
		
		
		JCheckBox chckbxTickint = new JCheckBox("Interupt Enable");
		chckbxTickint.setBounds(42, 72, 139, 25);
		panel_12.add(chckbxTickint);
		
		chckbxTickint.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				if (chckbxTickint.isSelected())
					cortexM01.setInterruptEnable();
				else
					cortexM01.setInterruptDisable();
				
				CSR_ValueDisp.setText(String.valueOf(cortexM01.getCSRforGUI()));
			}
		});
			
		
		
		/*--------------------- LAYOUT -------------------------*/
		
		
			JPanel panel_4 = new JPanel();
			contentPane.add(panel_4);
			panel_4.setLayout(null);
			
			JPanel panel_3 = new JPanel();
			panel_3.setBounds(0, 24, 204, 176);
			panel_4.add(panel_3);
			panel_3.setLayout(new GridLayout(8, 2, 0, 3));
			
			
			
			/*--------------------- CVR -------------------------*/

			
			CVRtextField = new JTextField();
			panel_3.add(CVRtextField);
			CVRtextField.setColumns(1);
			
			JButton sendCVRbtn = new JButton("Send value to CVR register");
			panel_3.add(sendCVRbtn);
			
			sendCVRbtn.addActionListener(new ActionListener() {
				
				public void actionPerformed(ActionEvent e) {
					
					if (CVRtextField.getText().isEmpty()) {
						JOptionPane.showMessageDialog(null, "You should send a number!",
															"WARNING", JOptionPane.WARNING_MESSAGE);
					}
					
					else {
						String txt = CVRtextField.getText();
						int cvrValue = Integer.parseInt(txt);
						
						if (e.getSource() == sendCVRbtn) {
							cortexM01.setCVR(cvrValue);
							CVR_ValueDisp.setText(String.valueOf(cortexM01.getCVR()));
							CVRtextField.setText("");
						}
					}
				}
			});
			
			

			/*--------------------- RVR -------------------------*/

			
			RVRtextField = new JTextField();
			panel_3.add(RVRtextField);
			RVRtextField.setColumns(1);
			
			
			JButton sendRVRbtn = new JButton("Send value to RVR register");
			panel_3.add(sendRVRbtn);
			
			sendRVRbtn.addActionListener(new ActionListener() {
				
				public void actionPerformed(ActionEvent e) {
				
					if (RVRtextField.getText().isEmpty()) {
						JOptionPane.showMessageDialog(null, "You should send a number",
															"WARNING", JOptionPane.WARNING_MESSAGE);
					}
					
					else {
						String txt = RVRtextField.getText();
						int rvrValue = Integer.parseInt(txt);
						
						if (e.getSource() == sendRVRbtn) {
							cortexM01.setRVR(rvrValue);
							RVR_ValueDisp.setText(String.valueOf(cortexM01.getRVR()));
							RVRtextField.setText("");
						}
					}
				}
			});
			

			
			/*--------------------- CSR -------------------------*/
			
			
			CSRtextField = new JTextField();
			CSRtextField.setColumns(1);
			panel_3.add(CSRtextField);
			
			JButton sendCSRbtn = new JButton("Send value to CSR register");
			panel_3.add(sendCSRbtn);
			
			sendCSRbtn.addActionListener(new ActionListener() {
				
				public void actionPerformed(ActionEvent e) {
					
					if (CSRtextField.getText().isEmpty()) {
						JOptionPane.showMessageDialog(null, "You should send a number",
															"WARNING", JOptionPane.WARNING_MESSAGE);
					}
					
					else {
						String txt = CSRtextField.getText();
						int csrValue = Integer.parseInt(txt);
						
						if (e.getSource() == sendCSRbtn) {
							cortexM01.setCSR(csrValue);
							CSR_ValueDisp.setText(String.valueOf(cortexM01.getCSR()));
							CSRtextField.setText("");
							
							// Update checkbox states
							
							if (cortexM01.isInternalActive())
								chckbxSource.setSelected(true);
							else
								chckbxSource.setSelected(false);
							
							if (cortexM01.isEnableFlag())
								chckbxEnable.setSelected(true);
							else
								chckbxEnable.setSelected(false);
							
							if (cortexM01.isInterruptFlag())
								chckbxTickint.setSelected(true);
							else
								chckbxTickint.setSelected(false);
						}
					}
				}
			});
		

			
		/*--------------------- ADD REST OF COMPONENTS -------------------------*/
			
			
		CVR_ValueDisp = new JTextField();
		CVR_ValueDisp.setText("0");
		CVR_ValueDisp.setEditable(false);
		CVR_ValueDisp.setColumns(7);
		panel_111.add(CVR_ValueDisp);
		
		CVR_Disp = new JTextField();
		CVR_Disp.setText("CVR");
		CVR_Disp.setEditable(false);
		CVR_Disp.setColumns(5);
		panel_111.add(CVR_Disp);
		
		Panel panel_112 = new Panel();
		panel_11.add(panel_112);
		
		RVR_ValueDisp = new JTextField();
		RVR_ValueDisp.setText("0");
		RVR_ValueDisp.setEditable(false);
		RVR_ValueDisp.setColumns(7);
		panel_112.add(RVR_ValueDisp);
		
		RVR_Disp = new JTextField();
		RVR_Disp.setText("RVR");
		RVR_Disp.setEditable(false);
		RVR_Disp.setColumns(5);
		panel_112.add(RVR_Disp);
		
		Panel panel_113 = new Panel();
		panel_11.add(panel_113);
		
		CSR_ValueDisp = new JTextField();
		CSR_ValueDisp.setText("0");
		CSR_ValueDisp.setEditable(false);
		CSR_ValueDisp.setColumns(7);
		panel_113.add(CSR_ValueDisp);
		
		CSR_Disp = new JTextField();
		CSR_Disp.setText("CSR");
		CSR_Disp.setEditable(false);
		CSR_Disp.setColumns(5);
		panel_113.add(CSR_Disp);
		
		JPanel panel = new JPanel();
		contentPane.add(panel);
		panel.setLayout(null);
		
		graph.setLayout(null);
		graph.setBounds(10, 10, 214, 210);
		panel.add(graph);	
	}
}