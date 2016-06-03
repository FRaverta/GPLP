package gui;

import java.awt.Dimension;
import java.awt.Font;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import main.Parameters;
import main.Report;

public class ReportPanel implements Observer {

	private JTextArea textArea;
	public  final JScrollPane scrollPane;
	int readedChar;
	
	public ReportPanel(){
		textArea = new JTextArea(20,30);
		textArea.setEditable(false);
		scrollPane = new JScrollPane(textArea);
		textArea.setFont(new Font("Serif",Font.PLAIN,14));
		Parameters.report.addObserver(this);
		//this.add(scrollPane);
		//this.setVisible(true);
	}
	
	@Override
	public void update(Observable o, Object arg) {		
		Report r = (Report) o;
		String msg = r.read(this.readedChar);
		readedChar += msg.length();
		textArea.append(msg);
		
		textArea.update(textArea.getGraphics());
	}

	
}

