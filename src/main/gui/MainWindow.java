package main.gui;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.xml.parsers.ParserConfigurationException;

import org.jgrapht.graph.ListenableDirectedGraph;
import org.jgrapht.graph.ListenableDirectedWeightedGraph;
import org.xml.sax.SAXException;

import main.Parameters;
import main.util.Edge;
import main.util.Vertex;
import main.util.XMLGraphParser.GraphXMLExporter;
import main.util.XMLGraphParser.XMLGraphParser;

public class MainWindow {

	private ControllerFather controller;
	private JGraphAdapterDemo  rightUpGraph,rightDownGraph;
	protected JFrame frame;
	private NewSampleWindow sampleWindow;
	private LoadAndSetModelWindow loadAndSetModelWindow;
	private SaveGraphWindow saveGraphWindow;
	private StudyCasesWindow studyCasesWindow;
	private ModelParametersWindow modelParametersWindow;
	
	
	/**
	 * This variables are used when a new model is required from a graph. These indicates which model should be generated 
	 */
	protected boolean wnModel = false;
	protected boolean dtnModel = false;	

	
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
	public MainWindow(ControllerFather ctr, ListenableDirectedWeightedGraph<Vertex, Edge> graph, ListenableDirectedWeightedGraph<Vertex, Edge> resultGraph) {
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
		frame.setTitle("GPLP");
		 //leftPanel.setBackground(Color.red);
				
		
		//UpPanel
		JMenuBar  upPanel = new JMenuBar(); 
		upPanel.setBackground(Color.orange); 
		//upPanel.setLayout(new BoxLayout(upPanel,BoxLayout.X_AXIS)); 		
		JMenu menu = new JMenu("Menu"); 
		
		{
		//boton new
		JMenuItem newItem = new JMenuItem("Load graph & set model");
		menu.add(newItem);upPanel.add(menu);
		newItem.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){				
					loadAndSetModelWindow.setVisible(true);
					
				}
			});
		}
		
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
		
		{
		//boton load tree topology
		JMenuItem newItem = new JMenuItem("Run DTN");
		menu.add(newItem);upPanel.add(menu);
		newItem.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){				
					controller.runDTN();
				}
			});
		}
		
		{
		//boton load tree topology
		JMenuItem newItem = new JMenuItem("Run DTN MultiFlow");
		menu.add(newItem);upPanel.add(menu);
		newItem.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){				
					controller.runDTNMultiFlow();
				}
			});
		}

		{
		//boton load tree topology
		JMenuItem newItem = new JMenuItem("Save original graph");
		menu.add(newItem);upPanel.add(menu);
		newItem.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){				
					saveGraphWindow.setWichGraphSave(true);
					saveGraphWindow.setVisible(true);
				}
			});
		}
		
		{
		//boton load tree topology
		JMenuItem newItem = new JMenuItem("Save result graph");
		menu.add(newItem);upPanel.add(menu);
		newItem.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){		
					saveGraphWindow.setWichGraphSave(false);
					saveGraphWindow.setVisible(true);
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
		this.loadAndSetModelWindow = new LoadAndSetModelWindow(this);
		this.saveGraphWindow = new SaveGraphWindow(this);
		
	     frame.addWindowListener(new WindowAdapter() {
	          public void windowClosing(WindowEvent e) {
	              sampleWindow.dispose();
	              studyCasesWindow.dispose();
	              System.out.println("a");
	          }
	     });
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
	
	/**
	 *Call only if a  cancel button of ModelParametersWindow 
	 * */
	public void hideModelParametersWindow() {
		this.modelParametersWindow.setVisible(false);
	}
	
	public void hideStudyCaseWindow() {
		this.studyCasesWindow.setVisible(false);
	}
	
	public void hideLoadAndSetModelWindow() {
		this.loadAndSetModelWindow.setVisible(false);
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

	public void desactiveMenu(){
		this.frame.getJMenuBar().getMenu(0).setEnabled(false);
	}
	
	public void loadGraphAndSolveModel(String path, int selectedIndex) {
		try{
			desactiveMenu();
			File f = new File(path);
			if(!f.exists()) { 
			    Parameters.report.writelnRed("Eror in read graph: The file " + path +" doesn't exist.");
			    loadAndSetModelWindow.setVisible(false);
			}
			else if(f.isDirectory()) { 
			    Parameters.report.writelnRed("Eror in read graph: " + path + " is a directory.");
			    loadAndSetModelWindow.setVisible(false);
			}else{
					//generate graph
					ListenableDirectedWeightedGraph<Vertex, Edge> graph = XMLGraphParser.parseDirectedXMLGraph(path);
					switch(selectedIndex){
						case 0:
					
								///put chosen to true and others to false TODO
								this.wnModel = true;
								this.dtnModel = false;
								//look up the window for enter model parameters
								modelParametersWindow = new ModelParametersWindow(this,false,false,graph);
								loadAndSetModelWindow.setVisible(false);
								modelParametersWindow.setVisible(true);
								//generate an solve lpmodel	
			//					controller.runWNFromGraph(graph, 1, true);
								break;
								
						case 1:
							///put chosen to true and others to false TODO
							this.dtnModel = true;
							this.wnModel = false;
							//look up the window for enter model parameters
							modelParametersWindow = new ModelParametersWindow(this,true,true,graph);
							loadAndSetModelWindow.setVisible(false);
							modelParametersWindow.setVisible(true);
							//generate an solve lpmodel	
		//					controller.runWNFromGraph(graph, 1, true);
							break;
				}
			}
		}catch(NullPointerException e){
			Parameters.report.writelnRed("Eror in read graph: A path to file is required.");
			loadAndSetModelWindow.setVisible(false);
		} catch (ParserConfigurationException|SAXException|IOException e) {
			Parameters.report.writelnRed("Eror parsing graph: " + e.toString());
			loadAndSetModelWindow.setVisible(false);
		}
		finally{activeMenu();}
	}
	
	public void runWNModel(ListenableDirectedWeightedGraph<Vertex, Edge> graph, Vertex[] vertexs, int requiredPath){
		modelParametersWindow.setVisible(false);
		controller.runWNFromGraph(graph, vertexs, requiredPath);
	}
	
	public void saveResultGraph(String path) {
		try{
			desactiveMenu();
		    saveGraphWindow.setVisible(false);
			File f = new File(path);
			if(f.exists()) { 
			    Parameters.report.writelnRed("Eror in save graph: The file " + path +" is already exist.");
			}
			else if(f.isDirectory()) { 
			    Parameters.report.writelnRed("Eror in save graph: " + path + " is a directory.");
			}else
					try{controller.saveResultGraph(f);Parameters.report.writelnGreen("The result graph was saved succesfully."); }
					catch(IOException e){Parameters.report.writelnRed("Error saving graph: " + e.toString());}
			
		
		}catch(NullPointerException e){
			Parameters.report.writelnRed("Eror in save graph: A path is required.");
			loadAndSetModelWindow.setVisible(false);
		}
		finally{activeMenu();}
	}
	
	public void saveGraph(String path) {
		try{
			desactiveMenu();
		    saveGraphWindow.setVisible(false);
			File f = new File(path);
			if(f.exists()) { 
			    Parameters.report.writelnRed("Eror in save graph: The file " + path +" is already exist.");
			}
			else if(f.isDirectory()) { 
			    Parameters.report.writelnRed("Eror in save graph: " + path + " is a directory.");
			}else
					try{controller.saveGraph(f);Parameters.report.writelnGreen("The graph was saved succesfully."); }
					catch(IOException e){Parameters.report.writelnRed("Error saving graph: " + e.toString());}
			
		
		}catch(NullPointerException e){
			Parameters.report.writelnRed("Eror in save graph: A path is required.");
			loadAndSetModelWindow.setVisible(false);
		}
		finally{activeMenu();}
	}
	
	public void hideGraphWindow() {
		this.saveGraphWindow.setVisible(false);
	}
	
	public void runDTNModel(ListenableDirectedWeightedGraph<Vertex, Edge> graph, Vertex[] nodeList, int requiredPath, Integer requiredCapacity, Integer maxSharedEdges) {
		controller.runDTNFromGraph(graph, nodeList, requiredPath, requiredCapacity,maxSharedEdges);
		modelParametersWindow.setVisible(false);
		
		
		
	}

	

}
