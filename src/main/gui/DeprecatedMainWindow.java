package main.gui;

import java.awt.Component;
import java.awt.EventQueue;

import javax.swing.JFrame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.jgrapht.graph.ListenableDirectedGraph;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.JTextArea;
import java.awt.Color;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

@Deprecated
class DeprecatedMainWindow {

	private JFrame frame;
	private JTextField edges_density;
	private JTextField amount_of_nodes;
	private JTextField required_path;
	private JTextField max_weight_edge;
	private JGraphAdapterDemo graph_up;
	private JGraphAdapterDemo graph_down;
	private final Controller controller;
	
	private JPanel panel_4,panel_5;
	//	/**
//	 * Launch the application.
//	 */
//	public static void main(String[] args) {
//		EventQueue.invokeLater(new Runnable() {
//			public void run() {
//				try {
//					MainWindow window = new MainWindow();
//					window.frame.setVisible(true);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		});
//	}

	/**
	 * Create the application.
	 */
	public DeprecatedMainWindow(int edges_density, int amount_of_nodes, int max_weight_edge , int required_path,ListenableDirectedGraph graph_up,ListenableDirectedGraph graph_down,Controller controller) {
		this.graph_up   = new  JGraphAdapterDemo();
		this.graph_down = new  JGraphAdapterDemo();
		this.graph_up.init(graph_up);
		this.graph_down.init(graph_down);
		
		this.controller = controller;
		
		initialize();
		
		this.edges_density.setText(Integer.toString(edges_density));
		this.amount_of_nodes.setText(Integer.toString(amount_of_nodes));
		this.max_weight_edge.setText(Integer.toString(max_weight_edge));
		this.required_path.setText(Integer.toString(required_path));

		
		frame.setVisible(true);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				System.out.println("frame");
				JFrame frame = (JFrame) e.getComponent();
				
				for (Component c :frame.getContentPane().getComponents())
					c.dispatchEvent(e);
				
			}
		});
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
			}
		});
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JPanel panel = new JPanel();
		panel.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				System.out.println("OK");
				int width = frame.getWidth();
				int height = frame.getHeight();
				
				panel.setSize((40*width)/100,height);
			}
		});
		panel.setBounds(0, 0, 186, 271);
		frame.getContentPane().add(panel);
		panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
		
		
		JPanel panel_2 = new JPanel();
		panel_2.setBounds(0, 0, 186, 135);
		panel.add(panel_2);
		panel_2.setLayout(null);
		
		JLabel lblGraphGeneration = new JLabel("Graph Generation");
		lblGraphGeneration.setBounds(12, 12, 141, 15);
		panel_2.add(lblGraphGeneration);
		
		JLabel lblEdgesDensity = new JLabel("Edges Density");
		lblEdgesDensity.setBounds(7, 72, 108, 17);
		panel_2.add(lblEdgesDensity);
		
		edges_density = new JTextField();
		edges_density.setBounds(136, 71, 38, 19);
		panel_2.add(edges_density);
		edges_density.setColumns(10);
		
		JLabel label = new JLabel("Amount of Nodes");
		label.setBounds(7, 39, 133, 17);
		panel_2.add(label);
		
		amount_of_nodes = new JTextField();
		amount_of_nodes.setColumns(10);
		amount_of_nodes.setBounds(136, 40, 38, 19);
		panel_2.add(amount_of_nodes);
		
		JLabel label_3 = new JLabel("Max Weight Edge");
		label_3.setBounds(7, 101, 123, 17);
		panel_2.add(label_3);
		
		max_weight_edge = new JTextField();
		max_weight_edge.setColumns(10);
		max_weight_edge.setBounds(136, 101, 38, 19);
		panel_2.add(max_weight_edge);
		
		JPanel panel_3 = new JPanel();
		panel_3.setBounds(0, 135, 186, 135);
		panel.add(panel_3);
		panel_3.setLayout(null);
		
		JLabel label_1 = new JLabel("Model Parameters");
		label_1.setBounds(24, 5, 150, 15);
		panel_3.add(label_1);
		
		required_path = new JTextField();
		required_path.setColumns(10);
		required_path.setBounds(136, 32, 38, 19);
		panel_3.add(required_path);
		
		JLabel label_2 = new JLabel("Required Paths");
		label_2.setBounds(7, 32, 133, 17);
		panel_3.add(label_2);
		
		JButton btnCalcular = new JButton("Calcular");
		btnCalcular.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnCalcular.setEnabled(false);
				controller.solveNewModel(Integer.parseInt(amount_of_nodes.getText()), Integer.parseInt(edges_density.getText()),Integer.parseInt(required_path.getText()) , Integer.parseInt(max_weight_edge.getText()));
				btnCalcular.setEnabled(true);
			}
		});
		btnCalcular.setBounds(41, 98, 117, 25);
		panel_3.add(btnCalcular);
		
		JPanel panel_1 = new JPanel();
		panel_1.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				System.out.println("paneeeeellll");
				int width = frame.getWidth();
				int height = frame.getHeight();
				
				panel_1.setBounds((40*width)/100,0,(60*width)/100,height);
			}
		});
		panel_1.setBounds(188, 0, 260, 271);
		frame.getContentPane().add(panel_1);
		panel_1.setLayout(new BoxLayout(panel_1, BoxLayout.Y_AXIS));
		
		panel_4 = new JPanel();
		panel_1.add(panel_4);
		
		//@Nando
//		JGraphAdapterDemo demo = new JGraphAdapterDemo();
//		demo.init(graph_up);
		panel_4.add(graph_up);
		
		panel_5 = new JPanel();
		panel_1.add(panel_5);
		
		//@Nando
//		JGraphAdapterDemo demo_5 = new JGraphAdapterDemo();
//		demo_5.init(graph_down);
		panel_5.add(graph_down);
	}
	
	public void setGraphs(ListenableDirectedGraph graph_up,ListenableDirectedGraph graph_down){
		this.graph_up = new JGraphAdapterDemo();
		this.graph_down = new JGraphAdapterDemo();
		
		this.graph_up.init(graph_up);
		this.graph_down.init(graph_down);
		
		panel_4.removeAll();
		panel_5.removeAll();
		
		panel_4.add(this.graph_up);
		panel_5.add(this.graph_down);

		panel_4.revalidate();
		panel_4.repaint();
		
		panel_5.revalidate();
		panel_5.repaint();
		
	}
}
