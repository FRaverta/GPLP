package gui;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jgrapht.graph.ListenableDirectedGraph;
import org.jgrapht.graph.ListenableDirectedWeightedGraph;

import main.Controller;
import main.Edge;
import main.JGraphAdapterDemo;
import main.Vertex;

public class NewWindow {

	private Controller controller;
	private JGraphAdapterDemo  rightUpGraph,rightDownGraph;
	protected JFrame frame;
	private NewSampleWindow sampleWindow;
	private StudyCasesWindow studyCasesWindow;
	
	/**
	 * Launch the application.
	 */
//	public static void main(String[] args) {
//		EventQueue.invokeLater(new Runnable() {
//			public void run() {
//				try {
//					NewWindow window = new NewWindow();
//					window.frame.setVisible(true);
//					
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		});
//	}

	/**
	 * Create the application.
	 * @param resultGraph 
	 * @param graph 
	 */
	public NewWindow(Controller ctr, ListenableDirectedWeightedGraph<Vertex, Edge> graph, ListenableDirectedWeightedGraph<Vertex, Edge> resultGraph) {
		controller = ctr;
		initialize(graph,resultGraph);
		frame.setVisible(true);

	}
	/**
	 * Initialize the contents of the frame.
	 * @param resultGraph 
	 * @param graph 
	 */
	private void initialize(ListenableDirectedWeightedGraph<Vertex, Edge> graph, ListenableDirectedWeightedGraph<Vertex, Edge> resultGraph) {
		frame = new JFrame();
		frame.setSize(600, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("FRaverta");
		 //leftPanel.setBackground(Color.red);
				
		
		//UpPanel
		JMenuBar  upPanel = new JMenuBar(); 
		upPanel.setBackground(Color.orange); 
		//upPanel.setLayout(new BoxLayout(upPanel,BoxLayout.X_AXIS)); 		
		JMenu menu = new JMenu("Menu"); 
		
		{
		//boton new
		JMenuItem newItem = new JMenuItem("New random model");
		menu.add(newItem);upPanel.add(menu);
		newItem.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){				
					sampleWindow.setVisible(true);
					
				}
			});
		}
		
		{
		//boton load tree topology
		JMenuItem newItem = new JMenuItem("Run a study case");
		menu.add(newItem);upPanel.add(menu);
		newItem.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){				
					studyCasesWindow.setVisible(true);					
				}
			});
		}
		
//		{
//		//boton load tree topology
//		JMenuItem newItem = new JMenuItem("Load Tree Topology");
//		menu.add(newItem);upPanel.add(menu);
//		newItem.addActionListener(new ActionListener(){
//				public void actionPerformed(ActionEvent e){				
//					controller.loadExample1(false);
//					
//				}
//			});
//		}
//		{
//		//boton load tree topology
//		JMenuItem newItem = new JMenuItem("Load example 2");
//		menu.add(newItem);upPanel.add(menu);
//		newItem.addActionListener(new ActionListener(){
//				public void actionPerformed(ActionEvent e){				
//					controller.loadExample2(false);
//					
//				}
//			});
//		}
//		
//		{
//		//boton load tree topology
//		JMenuItem newItem = new JMenuItem("Load Tree Topology pse");
//		menu.add(newItem);upPanel.add(menu);
//		newItem.addActionListener(new ActionListener(){
//				public void actionPerformed(ActionEvent e){				
//					controller.loadExample1(true);
//					
//				}
//			});
//		}
//		
//		{
//		//boton load tree topology
//		JMenuItem newItem = new JMenuItem("Load example 2 pse");
//		menu.add(newItem);upPanel.add(menu);
//		newItem.addActionListener(new ActionListener(){
//				public void actionPerformed(ActionEvent e){				
//					controller.loadExample2(true);
//					
//				}
//			});
//		}
//		
//		{
//		//boton load tree topology
//		JMenuItem newItem = new JMenuItem("Load default example");
//		menu.add(newItem);upPanel.add(menu);
//		newItem.addActionListener(new ActionListener(){
//				public void actionPerformed(ActionEvent e){				
//					controller.loadDefault(false);
//					
//				}
//			});
//		}
//		
//		{
//		//boton load tree topology
//		JMenuItem newItem = new JMenuItem("Load default example pse");
//		menu.add(newItem);upPanel.add(menu);
//		newItem.addActionListener(new ActionListener(){
//				public void actionPerformed(ActionEvent e){				
//					controller.loadDefault(true);
//					
//				}
//			});
//		}
		
		
		//leftPanel
		ReportPanel  leftPanel = new ReportPanel();
		
		//rightUpPanel
		rightUpGraph =new JGraphAdapterDemo(); //rightUpPanel.setBackground(Color.blue);
		rightUpGraph.init(graph);
		
		//rightUpPanel
		rightDownGraph =new JGraphAdapterDemo(); //rightUpPanel.setBackground(Color.blue);
		rightDownGraph.init(resultGraph);
		
		frame.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		
//		gbc.gridx = 0;
//		gbc.gridy = 0;
//		//numer of column
//		gbc.gridwidth  = 2;
//		//number of rows
//		gbc.gridheight = 1;
//		gbc.weightx = 0.5;
//		gbc.weighty = 0.0;
//		gbc.fill = GridBagConstraints.HORIZONTAL;
		frame.setJMenuBar(upPanel);
		
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth  = 1;
		gbc.gridheight = 2;
		gbc.weightx = 0.5;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		frame.add(leftPanel.scrollPane,gbc);
		
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.gridwidth  = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets( 0,2,10,0 );
		frame.add(rightUpGraph,gbc);
		
		gbc.gridx = 1;
		gbc.gridy = 2;
		gbc.gridwidth  = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets( 10,2,1,0 );
		frame.add(rightDownGraph,gbc);	
		
		//Jdialog
		this.sampleWindow = new NewSampleWindow(this);
		this.studyCasesWindow = new StudyCasesWindow(this);
	}
	
	public void setGraphs(ListenableDirectedGraph graph_up,ListenableDirectedGraph graph_down){
		GridBagConstraints gbc = new GridBagConstraints();

		frame.remove(rightUpGraph);
		frame.remove(rightDownGraph);
		
		this.rightUpGraph = new JGraphAdapterDemo();
		this.rightDownGraph = new JGraphAdapterDemo();
		
		this.rightUpGraph.init(graph_up);
		this.rightDownGraph.init(graph_down);
		
		
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.gridwidth  = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets( 0,2,10,0 );
		frame.add(rightUpGraph,gbc);
		
		gbc.gridx = 1;
		gbc.gridy = 2;
		gbc.gridwidth  = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets( 10,2,1,0 );
		frame.add(rightDownGraph,gbc);		
		
		frame.revalidate();
		frame.repaint();

		
	}
	public void hideSampleWindow() {
		this.sampleWindow.setVisible(false);
	}
	
	public void hideStudyCaseWindow() {
		this.studyCasesWindow.setVisible(false);
	}
	
	public void solveNewModel(int amount_of_vertex,int edge_density,int amount_of_path,int max_weight_edge){
		sampleWindow.setVisible(false);
		this.frame.getJMenuBar().getMenu(0).setEnabled(false);
		controller.solveNewModel(amount_of_vertex, edge_density, amount_of_path, max_weight_edge);
	}
	
	public void solveStudyCase(int studyCase,int sharedEdgeOption, double sharedEdegeWeighing){
		studyCasesWindow.setVisible(false);
		this.frame.getJMenuBar().getMenu(0).setEnabled(false);
		switch (studyCase) {
			case 0 : controller.loadDefault((sharedEdgeOption==2)?true:false,sharedEdegeWeighing); break;
			case 1 : controller.loadExample1((sharedEdgeOption==2)?true:false,sharedEdegeWeighing); break;
			case 2 : controller.loadExample3((sharedEdgeOption==2)?true:false,sharedEdegeWeighing); break;
		}		
	}
	

	public void activeMenu(){
		this.frame.getJMenuBar().getMenu(0).setEnabled(true);
	}

	

}
