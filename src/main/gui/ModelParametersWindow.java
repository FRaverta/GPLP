package main.gui;

import java.awt.BorderLayout;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeListener;//

import org.jgrapht.graph.ListenableDirectedWeightedGraph;

import main.util.Edge;
import main.util.Vertex;

public class ModelParametersWindow extends JDialog{
	
	private final MainWindow window;
	private final ListenableDirectedWeightedGraph<Vertex, Edge> graph;
	private JComboBox fromBox;
	private JComboBox toBox;
	private JSpinner kTextField;
	private JSpinner sTextField;
	private JSpinner cTextField;
	private Vertex[] nodeList;

//	public static void main(String args[]){
//		EventQueue.invokeLater(new Runnable() {
//		public void run() {
//			try {
//				ModelParametersWindow w = new ModelParametersWindow();
//				w.setVisible(true);
//				
//			} catch (Exception e) {
//				e.printStackTrace();
//			}//
//		}
//	});
//	}
	
	private Container createOkCancelPanel(){
		Container c = new JPanel();
		c.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		JButton okButton = new JButton("Ok");
		JButton cancelButton = new JButton("Cancel");
		c.add(cancelButton);		
		c.add(okButton);
		
		okButton.setActionCommand("OK");
		getRootPane().setDefaultButton(okButton);
		okButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				nodeList = (Vertex[]) swap(nodeList,0, fromBox.getSelectedIndex());
				nodeList = (Vertex[]) swap(nodeList,nodeList.length-1, toBox.getSelectedIndex());
				if(window.wnModel) 
					window.runWNModel(graph, nodeList,((Integer)kTextField.getValue()).intValue() );
				else
					window.runDTNModel(graph, nodeList,((Integer)kTextField.getValue()).intValue(),((Integer)cTextField.getValue()),((Integer)sTextField.getValue()) );
			}
		});
		
		cancelButton.setActionCommand("Cancel");
		cancelButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				window.hideModelParametersWindow();
				
			}
		});
		
		return c;
	}
	
	private Container createOptionPanel(boolean c, boolean s){
		Container optionContainer = new Container();
		//Constant for put a component one under other
		int yStartNextComponent = 0;
		
		
		optionContainer.setLayout(new GridBagLayout());
		
		GridBagConstraints gbc = new GridBagConstraints();
		
		

		JLabel from = new JLabel("From");
		JLabel to = new JLabel("To");
		JLabel k = new JLabel("K");
		JLabel fromHelpIcon = new JLabel(); fromHelpIcon.setIcon(new ImageIcon(LoadAndSetModelWindow.class.getResource("/main/gui/resources/helpIcon25.png")));
		JLabel toHelpIcon = new JLabel();   toHelpIcon.setIcon(new ImageIcon(LoadAndSetModelWindow.class.getResource("/main/gui/resources/helpIcon25.png")));
		JLabel kHelpIcon = new JLabel();	kHelpIcon.setIcon(new ImageIcon(LoadAndSetModelWindow.class.getResource("/main/gui/resources/helpIcon25.png")));
		
		fromBox = new JComboBox(nodeList);
		toBox = new JComboBox(nodeList);
		kTextField = new JSpinner(new SpinnerNumberModel(2,0,Integer.MAX_VALUE,1)); kTextField.setPreferredSize(new Dimension(100,20));

		/**
		 * Left side components: these are labels for the user can interpret which value he should put in center fields
		 */
		gbc.gridx = 0;
		gbc.gridy = yStartNextComponent;yStartNextComponent++;
		gbc.gridwidth  = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.5;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets= new Insets(0, 15, 0, 10);
		optionContainer.add(from,gbc);
		
		gbc.gridx = 0;
		gbc.gridy = yStartNextComponent;yStartNextComponent++;
		gbc.gridwidth  = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.5;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets= new Insets(0, 15, 0, 10);
		optionContainer.add(to,gbc);
		
		gbc.gridx = 0;
		gbc.gridy = yStartNextComponent;yStartNextComponent++;
		gbc.gridwidth  = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.5;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets= new Insets(0, 15, 0, 10);
		optionContainer.add(k,gbc);
		
		if(c){
			JLabel cLabel = new JLabel("C");
			gbc.gridx = 0;
			gbc.gridy = yStartNextComponent;yStartNextComponent++;
			gbc.gridwidth  = 1;
			gbc.gridheight = 1;
			gbc.weightx = 0.5;
			gbc.weighty = 1;
			gbc.fill = GridBagConstraints.BOTH;
			optionContainer.add(cLabel,gbc);
		}
		
		if(s){
			JLabel sLabel = new JLabel("S");
			gbc.gridx = 0;
			gbc.gridy = yStartNextComponent;yStartNextComponent++;
			gbc.gridwidth  = 1;
			gbc.gridheight = 1;
			gbc.weightx = 0.5;
			gbc.weighty = 1;
			gbc.fill = GridBagConstraints.BOTH;
			optionContainer.add(sLabel,gbc);
		}
		
		/**
		 * Center components: these are fields that user should complete
		 */
		yStartNextComponent = 0;
		gbc.gridx = 1;
		gbc.gridy = yStartNextComponent;yStartNextComponent++;
		gbc.gridwidth  = 7;
		gbc.gridheight = 1;
		gbc.weightx = 0.5;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets= new Insets(0, 0, 2, 20);
		optionContainer.add(fromBox,gbc);
		
		gbc.gridx = 1;
		gbc.gridy = yStartNextComponent;yStartNextComponent++;
		gbc.gridwidth  = 7;
		gbc.gridheight = 1;
		gbc.weightx = 0.5;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets= new Insets(0, 0, 2, 20);
		optionContainer.add(toBox,gbc);
		
		gbc.gridx = 1;
		gbc.gridy = yStartNextComponent;yStartNextComponent++;
		gbc.gridwidth  = 7;
		gbc.gridheight = 1;
		gbc.weightx = 0.5;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets= new Insets(0, 0, 2, 20);
		optionContainer.add(kTextField,gbc);
		
		if(c){
			cTextField = new JSpinner(new SpinnerNumberModel(0,0,Integer.MAX_VALUE,1)); kTextField.setPreferredSize(new Dimension(100,20));
			gbc.gridx = 1;
			gbc.gridy = yStartNextComponent;yStartNextComponent++;
			gbc.gridwidth  = 7;
			gbc.gridheight = 1;
			gbc.weightx = 0.5;
			gbc.weighty = 1;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets= new Insets(0, 0, 2, 20);
			optionContainer.add(cTextField,gbc);
		}
		
		if(s){
			sTextField = new JSpinner(new SpinnerNumberModel(Integer.MAX_VALUE,0,Integer.MAX_VALUE,1)); kTextField.setPreferredSize(new Dimension(100,20));
			gbc.gridx = 1;
			gbc.gridy = yStartNextComponent;yStartNextComponent++;
			gbc.gridwidth  = 7;
			gbc.gridheight = 1;
			gbc.weightx = 0.5;
			gbc.weighty = 1;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets= new Insets(0, 0, 2, 20);
			optionContainer.add(sTextField,gbc);
		}
		
		
		/**
		 * Right components: these are help icons that give information to users when he pass over it the mouse. 
		 return optionContainer;
	}*/
		yStartNextComponent=0;
		
		gbc.gridx = 8;
		gbc.gridy = yStartNextComponent;yStartNextComponent++;
		gbc.gridwidth  = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.5;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		fromHelpIcon.setToolTipText("Chose the source node.");
		optionContainer.add(fromHelpIcon,gbc);
		
		gbc.gridx = 8;
		gbc.gridy = yStartNextComponent;yStartNextComponent++;
		gbc.gridwidth  = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.5;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		toHelpIcon.setToolTipText("Chose the target node.");
		optionContainer.add(toHelpIcon,gbc);
		
		gbc.gridx = 8;
		gbc.gridy = yStartNextComponent;yStartNextComponent++;
		gbc.gridwidth  = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.5;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		kHelpIcon.setToolTipText("Put amount of required paths.");
		optionContainer.add(kHelpIcon,gbc);
		
		if(c){
			JLabel cHelpIcon = new JLabel();	cHelpIcon.setIcon(new ImageIcon(LoadAndSetModelWindow.class.getResource("/main/gui/resources/helpIcon25.png")));
			gbc.gridx = 8;
			gbc.gridy = yStartNextComponent;yStartNextComponent++;
			gbc.gridwidth  = 1;
			gbc.gridheight = 1;
			gbc.weightx = 0.5;
			gbc.weighty = 1;
			gbc.fill = GridBagConstraints.BOTH;
			cHelpIcon.setToolTipText("Put minimum capacity required.");
			optionContainer.add(cHelpIcon,gbc);
		}
		
		if(s){
			JLabel sHelpIcon = new JLabel();	sHelpIcon.setIcon(new ImageIcon(LoadAndSetModelWindow.class.getResource("/main/gui/resources/helpIcon25.png")));
			gbc.gridx = 8;
			gbc.gridy = yStartNextComponent;yStartNextComponent++;
			gbc.gridwidth  = 1;
			gbc.gridheight = 1;
			gbc.weightx = 0.5;
			gbc.weighty = 1;
			gbc.fill = GridBagConstraints.BOTH;
			sHelpIcon.setToolTipText("Put maximum amount of allowed shared edges between any pair of paths.");
			optionContainer.add(sHelpIcon,gbc);
		}
		
		return optionContainer;
	}
	
	
	public ModelParametersWindow(MainWindow window,boolean c, boolean s,ListenableDirectedWeightedGraph<Vertex, Edge> graph){		
		//Set this JDialog as modal for MainWindow, with default size, title and always on top. 
		super(window.frame);
		this.window= window;
		this.graph = graph;
		Set<Vertex> vertexs = graph.vertexSet();
		this.nodeList = (Vertex[]) vertexs.toArray(new Vertex[vertexs.size()]);
		this.setModal(true);
		setTitle("Model Parameters");
		setAlwaysOnTop(true);
		setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		setBounds(100, 100, 300 , 200 + ((c)? 40:0) + ((s)? 40:0));
		
		//Set layout as border layout. In Center there will the options and in south the ok,cancel buttons. 
		this.setLayout(new BorderLayout());		
		Container centerContainer = createOptionPanel(c,s);
		this.add(centerContainer,BorderLayout.CENTER);
		Container okCancelPanel = createOkCancelPanel();		
		this.add(okCancelPanel,BorderLayout.SOUTH);
	
	}
	
	private Object[] swap(Object[] arr,int i,int j){
		Object aux = arr[i];
		arr[i] = arr[j];
		arr[j] = aux;
		
		return arr;
	}

}
