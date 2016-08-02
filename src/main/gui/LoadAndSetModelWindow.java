package main.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.ImageIcon;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class LoadAndSetModelWindow extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextField filePathTextField;
	String[] modelList = {"Wire Network Model", "Delay-Disruption Tolerant Network Model"};

//	/**
//	 * Launch the application.
//	 */
//	public static void main(String[] args) {
//		try {
//			LoadAndSetModelWindow dialog = new LoadAndSetModelWindow();
//			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
//			dialog.setVisible(true);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

	/**
	 * Create the dialog.
	 */
	public LoadAndSetModelWindow(MainWindow window) {
		super(window.frame);
		setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		this.setModal(true);
		setTitle("Load Graph & Set Model");
		setAlwaysOnTop(true);

		setBounds(100, 100, 700, 200);
		setResizable(false);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		
		filePathTextField = new JTextField();
		filePathTextField.setBounds(69, 25, 579, 39);
		contentPanel.add(filePathTextField);
		filePathTextField.setColumns(10);
		filePathTextField.setFont(new Font("Serif",Font.PLAIN,20));
		
		JLabel lblFile = new JLabel("File");
		lblFile.setBounds(25, 37, 70, 15);
		contentPanel.add(lblFile);
		
		JComboBox modelBox = new JComboBox(modelList);
		modelBox.setBounds(91, 76, 532, 33);
		contentPanel.add(modelBox);
		
		JLabel lblModel = new JLabel("Model");
		lblModel.setBounds(25, 85, 70, 15);
		contentPanel.add(lblModel);
		
		JLabel lblNewLabel = new JLabel();
		lblNewLabel.setToolTipText("Here there will be a small description about the choosed model + .... ...  and so on...");
		lblNewLabel.setIcon(new ImageIcon(LoadAndSetModelWindow.class.getResource("/main/gui/resources/helpIcon25.png")));
		lblNewLabel.setBounds(646, 76, 40, 33);
		contentPanel.add(lblNewLabel);
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
						window.loadGraphAndSolveModel(filePathTextField.getText(), modelBox.getSelectedIndex());
					}
				});
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
				cancelButton.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e){
						window.hideLoadAndSetModelWindow();
					}
				});
			}
		}
	}
}
