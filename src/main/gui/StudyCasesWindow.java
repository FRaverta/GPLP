package main.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class StudyCasesWindow extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextField textField;
	String[] studyCasesList = {"Default CE","Tree CE","Big Tree CE"};
	String[] seCriteriaList = {"None","Weak","Strong"};

	/**
	 * Launch the application.
	 */
//	public static void main(String[] args) {
//		try {
//			StudyCases dialog = new StudyCases();
//			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
//			dialog.setVisible(true);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

	/**
	 * Create the dialog.
	 */
	public StudyCasesWindow(NewWindow window) {
		super(window.frame);
		setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		this.setModal(true);
		setTitle("Run a study case");
		setResizable(false);
		setAlwaysOnTop(true);
		setBounds(100, 100, 392, 276);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		
		JLabel lblSelectStudyCase = new JLabel("Select study case");
		lblSelectStudyCase.setBounds(12, 12, 133, 28);
		contentPanel.add(lblSelectStudyCase);
		
		JComboBox<String> list = new JComboBox<String>(studyCasesList);
		list.setBounds(189, 18, 193, 33);
		contentPanel.add(list);
		
		JComboBox<String> list_1 = new JComboBox<String>(seCriteriaList);
		list_1.setBounds(189, 57, 193, 33);
		contentPanel.add(list_1);
		
		JLabel label = new JLabel("Shared edges criteria");
		label.setBounds(12, 60, 183, 28);
		contentPanel.add(label);
		
		JLabel lblWeighing = new JLabel("Weighing");
		lblWeighing.setBounds(67, 119, 78, 15);
		contentPanel.add(lblWeighing);
		
		textField = new JTextField();
		textField.setBounds(189, 113, 67, 28);
		contentPanel.add(textField);
		textField.setColumns(10);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
				okButton.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e){
						window.solveStudyCase(list.getSelectedIndex(), list_1.getSelectedIndex(),Double.parseDouble(textField.getText().equals("")?"0":textField.getText()));
					}
				});
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
				cancelButton.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e){
						window.hideStudyCaseWindow();
					}
				}
				);
			}
		}
	}
}
