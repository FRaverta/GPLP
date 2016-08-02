package main.gui;

import java.awt.Font;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import main.Parameters;
import main.util.Report;

public class ReportPanel implements Observer {

	private JTextPane textArea;
	public  final JScrollPane scrollPane;
	int readedChar;
	
	public ReportPanel(){
		textArea = new JTextPane();
		textArea.setContentType("text/html");
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
		String msg = r.readHtml(this.readedChar);
		readedChar += msg.length();
		//textArea.insert(msg);
		try {
			HTMLDocument doc = (HTMLDocument)this.textArea.getDocument();
			HTMLEditorKit editorKit = (HTMLEditorKit)textArea.getEditorKit();
			editorKit.insertHTML(doc, doc.getLength(), msg, 0, 0, null);
			textArea.setCaretPosition(doc.getLength());
//			HTMLDocument doc=(HTMLDocument) textArea.getStyledDocument();
//			doc.insertAfterEnd(doc.getCharacterElement(doc.getLength()),msg);
//			doc.insertString(readedChar, msg, null);
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		textArea.update(textArea.getGraphics());
	}

	
}

