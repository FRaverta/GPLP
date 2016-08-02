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

public class SaveGraphWindow extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextField filePathTextField;
	private boolean saveOriginalGraph;

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
	public SaveGraphWindow(MainWindow window) {
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
		filePathTextField.setBounds(69, 44, 579, 39);
		contentPanel.add(filePathTextField);
		filePathTextField.setColumns(10);
		filePathTextField.setFont(new Font("Serif",Font.PLAIN,20));
		
		JLabel lblFile = new JLabel("File");
		lblFile.setBounds(12, 58, 70, 15);
		contentPanel.add(lblFile);
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
						if(saveOriginalGraph)
							window.saveGraph(filePathTextField.getText());
						else
							window.saveResultGraph(filePathTextField.getText());
					}
				});
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
				cancelButton.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e){
						window.hideGraphWindow();
					}
				});
			}
		}
	}

	public void setWichGraphSave(boolean saveOriginalGraph) {
		this.saveOriginalGraph = saveOriginalGraph;
		
	}
}
