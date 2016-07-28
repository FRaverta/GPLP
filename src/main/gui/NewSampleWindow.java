package main.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class NewSampleWindow extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextField amount_of_nodes;
	private JTextField edges_density;
	private JTextField max_weight_edge;
	private JTextField required_path;
	private NewWindow window;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			NewSampleWindow dialog = new NewSampleWindow(null);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 * @param frame 
	 */
	public NewSampleWindow(NewWindow window) {
		super(window.frame);
		setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		this.setModal(true);
		setTitle("Run new model");
		setResizable(false);
		setAlwaysOnTop(true);
		setBounds(100, 100, 277, 337);
		getContentPane().setLayout(null);
		contentPanel.setBounds(0, 12, 275, 252);
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel);
		contentPanel.setLayout(null);
		
		JLabel label = new JLabel("Graph Generation");
		label.setBounds(17, 0, 141, 15);
		contentPanel.add(label);
		
		JLabel label_1 = new JLabel("Amount of Nodes");
		label_1.setBounds(12, 27, 133, 17);
		contentPanel.add(label_1);
		
		amount_of_nodes = new JTextField();
		amount_of_nodes.setColumns(10);
		amount_of_nodes.setBounds(141, 28, 38, 19);
		contentPanel.add(amount_of_nodes);
		
		JLabel label_2 = new JLabel("Edges Density");
		label_2.setBounds(12, 60, 108, 17);
		contentPanel.add(label_2);
		
		edges_density = new JTextField();
		edges_density.setColumns(10);
		edges_density.setBounds(141, 59, 38, 19);
		contentPanel.add(edges_density);
		
		JLabel label_3 = new JLabel("Max Weight Edge");
		label_3.setBounds(12, 89, 123, 17);
		contentPanel.add(label_3);
		
		max_weight_edge = new JTextField();
		max_weight_edge.setColumns(10);
		max_weight_edge.setBounds(141, 89, 38, 19);
		contentPanel.add(max_weight_edge);
		
		JLabel label_4 = new JLabel("Model Parameters");
		label_4.setBounds(29, 128, 150, 15);
		contentPanel.add(label_4);
		
		JLabel label_5 = new JLabel("Required Paths");
		label_5.setBounds(12, 155, 133, 17);
		contentPanel.add(label_5);
		
		required_path = new JTextField();
		required_path.setColumns(10);
		required_path.setBounds(141, 155, 38, 19);
		contentPanel.add(required_path);
		
		JLabel label_6 = new JLabel("%");
		label_6.setBounds(181, 61, 70, 15);
		contentPanel.add(label_6);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setBounds(0, 273, 275, 35);
			getContentPane().add(buttonPane);
			buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
			{
				JButton okButton = new JButton("OK");
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
				okButton.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e){
						window.solveNewModel(Integer.parseInt(amount_of_nodes.getText()), Integer.parseInt(edges_density.getText()),Integer.parseInt(required_path.getText()) , Integer.parseInt(max_weight_edge.getText()));
					}
				});
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
				cancelButton.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e){
						window.hideSampleWindow();
					}
					}
				);
			}
		}
	}
}
