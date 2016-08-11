package main.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.ImageIcon;
import java.awt.event.ActionListener;
import java.io.File;
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
		filePathTextField.setBounds(56, 23, 579, 39);
		contentPanel.add(filePathTextField);
		filePathTextField.setColumns(10);
		filePathTextField.setFont(new Font("Serif",Font.PLAIN,20));
		
		JLabel lblFile = new JLabel("File");
		lblFile.setBounds(12, 37, 70, 15);
		contentPanel.add(lblFile);
		
		JComboBox modelBox = new JComboBox(modelList);
		modelBox.setBounds(88, 74, 547, 33);
		contentPanel.add(modelBox);
		
		JLabel lblModel = new JLabel("Model");
		lblModel.setBounds(12, 83, 70, 15);
		contentPanel.add(lblModel);
		
		JLabel lblNewLabel = new JLabel();
		lblNewLabel.setToolTipText("Here there will be a small description about the choosed model + .... ...  and so on...");
		lblNewLabel.setIcon(new ImageIcon(LoadAndSetModelWindow.class.getResource("/main/gui/resources/helpIcon25.png")));
		lblNewLabel.setBounds(660, 76, 40, 33);
		contentPanel.add(lblNewLabel);
		
		JButton btnNewButton = new JButton();
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				 JFileChooser fc = new JFileChooser();
				  int returnVal = fc.showOpenDialog(LoadAndSetModelWindow.this);
		        if (returnVal == JFileChooser.APPROVE_OPTION) {
		            File file = fc.getSelectedFile();
		            LoadAndSetModelWindow.this.filePathTextField.setText(file.getAbsolutePath());
		        }
			}
		});
		btnNewButton.setIcon(new ImageIcon(LoadAndSetModelWindow.class.getResource("/main/gui/resources/openFileIcon.png")));
		btnNewButton.setBounds(647, 31, 39, 30);
		contentPanel.add(btnNewButton);
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
