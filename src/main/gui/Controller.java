//package main.gui;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.Set;
//
//import org.jgrapht.graph.ListenableDirectedWeightedGraph;
//
//import lpsolve.LpSolveException;
//import main.Parameters;
//import main.lpModel.dtNet.DTNLpModel;
//import main.lpModel.dtNet.DTNLpModelMultiFlow;
//import main.lpModel.dtNet.DTNetwork;
//import main.lpModel.dtNet.dtnParser.parser;
//import main.lpModel.wireNet.WNLpModel;
//import main.util.Edge;
//import main.util.Vertex;
//import main.util.XMLGraphParser.GraphXMLExporter;
//import main.util.XMLGraphParser.XMLGraphParser;
//
//@Deprecated
//public class Controller {
//	
//	private MainWindow gui;
//	private ListenableDirectedWeightedGraph<Vertex,Edge> graph;
//	private ListenableDirectedWeightedGraph<Vertex,Edge> resultGraph;
//
//	public Controller(){
//		try {
//			Parameters.ready();
//			Parameters.report.writeln("Welcome, I am a Software and I hope to help you");
//			Parameters.report.writeln("Default example model ");
//			Parameters.report.writeln("Amount of Nodes = " + Parameters.AMOUNT_OF_NODES);
//			WNLpModel model = WNLpModel.DefaultLPModel();
//			//gui = new MainWindow(Parameters.EDGE_DENSITY,Parameters.AMOUNT_OF_NODES,Parameters.MAX_WEIGHT_EDGE, Parameters.AMOUNT_OF_REQUIRED_PATHS,model.graph,model.resultGraph,this);
//			this.graph = model.graph; this.resultGraph = model.resultGraph;
//			gui = new MainWindow(this,model.graph,model.resultGraph);
//			Parameters.report.write(" ");
//
//		} catch (IOException e) {
//			e.printStackTrace();
//		} catch (LpSolveException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//	}
//	
//	public void solveNewModel(int amount_of_vertex,int edge_density,int amount_of_path,int max_weight_edge){
//		GenerateLpModelTask generateLPModelTask = new GenerateLpModelTask(amount_of_vertex,edge_density,amount_of_path,max_weight_edge,gui);
//		Thread t = new Thread(generateLPModelTask);
//		t.start();
//	}
//	
//	
//
//	
//	public void loadExample1(boolean strongSharedEge, double sharedEdegeWeighing){	
//		try {
//			WNLpModel model = WNLpModel.LpModelFromGraph(
//											sharedEdegeWeighing,strongSharedEge,Parameters.E1_AMOUNT_OF_NODES, Parameters.E1_AMOUNT_OF_REQUIRED_PATHS,
//											Parameters.E1_MAX_WEIGHT_EDGE, Parameters.E1_EDGE_DENSITY, Parameters.E1_GRAPH, 
//											Parameters.E1_Vertexs, Parameters.E1_Edges);
//			this.graph = model.graph; this.resultGraph = model.resultGraph;
//			gui.setGraphs(model.graph,model.resultGraph);
//
//		} catch (IOException e) {
//			e.printStackTrace();
//			Parameters.report.writeln(e.toString());
//		} catch (LpSolveException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			Parameters.report.writeln(e.toString());
//		}
//		finally{gui.activeMenu();}
//	}
//	
//	public void loadExample2(boolean strongSharedEge, double sharedEdegeWeighing){	
//		try {
//			WNLpModel model = WNLpModel.LpModelFromGraph(
//											sharedEdegeWeighing, strongSharedEge,Parameters.E2_AMOUNT_OF_NODES, Parameters.E2_AMOUNT_OF_REQUIRED_PATHS,
//											Parameters.E2_MAX_WEIGHT_EDGE, Parameters.E2_EDGE_DENSITY, Parameters.E2_GRAPH, 
//											Parameters.E2_Vertexs, Parameters.E2_Edges);
//			this.graph = model.graph; this.resultGraph = model.resultGraph;
//			gui.setGraphs(model.graph,model.resultGraph);
//			
//		} catch (IOException e) {
//			e.printStackTrace();
//			Parameters.report.writeln(e.toString());
//		} catch (LpSolveException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			Parameters.report.writeln(e.toString());
//		}	
//		finally{gui.activeMenu();}
//	}
//	
//	public void loadExample3(boolean strongSharedEge, double sharedEdegeWeighing){	
//		try {
//			WNLpModel model = WNLpModel.LpModelFromGraph(
//											sharedEdegeWeighing, strongSharedEge,Parameters.E3_AMOUNT_OF_NODES, Parameters.E3_AMOUNT_OF_REQUIRED_PATHS,
//											Parameters.E3_MAX_WEIGHT_EDGE, Parameters.E3_EDGE_DENSITY, Parameters.E3_GRAPH, 
//											Parameters.E3_Vertexs, Parameters.E3_Edges);
//			this.graph = model.graph; this.resultGraph = model.resultGraph;
//			gui.setGraphs(model.graph,model.resultGraph);
//			
//		} catch (IOException e) {
//			e.printStackTrace();
//			Parameters.report.writeln(e.toString());
//		} catch (LpSolveException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			Parameters.report.writeln(e.toString());
//		}	
//		finally{gui.activeMenu();}
//	}
//	
//	
//	public void loadDefault(boolean strongSharedEge, double sharedEdegeWeighing){	
//		try {
//			WNLpModel model = WNLpModel.LpModelFromGraph(
//											sharedEdegeWeighing, strongSharedEge,Parameters.AMOUNT_OF_NODES, Parameters.AMOUNT_OF_REQUIRED_PATHS,
//											Parameters.MAX_WEIGHT_EDGE, Parameters.EDGE_DENSITY, Parameters.DEFAULT_GRAPH, 
//											Parameters.Vertexs, Parameters.Edges);
//			this.graph = model.graph; this.resultGraph = model.resultGraph;
//			gui.setGraphs(model.graph,model.resultGraph);
//
//		} catch (IOException e) {
//			e.printStackTrace();
//			Parameters.report.writeln(e.toString());
//		} catch (LpSolveException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			Parameters.report.writeln(e.toString());
//		}	
//		finally{gui.activeMenu();}
//	}
//	
//	
//	class GenerateLpModelTask implements Runnable{
//		int amount_of_vertex;
//		int edge_density;
//		int amount_of_path;
//		int max_weight_edge;
//		MainWindow window;
//		
//		GenerateLpModelTask(int amount_of_vertex,int edge_density,int amount_of_path,int max_weight_edge,MainWindow window){
//			this.amount_of_vertex = amount_of_vertex;
//			this.edge_density = edge_density;
//			this.amount_of_path = amount_of_path;
//			this.max_weight_edge = max_weight_edge;
//			this.window = window;
//		}
//		public void run() {
//			try {
//				Parameters.report.writeln("***************************************");
//				Parameters.report.writeln("Creating  graph with:");
//				Parameters.report.writeln("      " + amount_of_vertex + " vertexs"); 
//				Parameters.report.writeln("      " + edge_density + "% edge density");
//				Parameters.report.writeln("      " + max_weight_edge +" max weight for an edge");
//				Parameters.report.writeln("      at least " + amount_of_path + " paths from V0 to V" + (amount_of_vertex -1));
//				Parameters.report.writeln(". . .");
//				
//				WNLpModel model = WNLpModel.generateLpModel(amount_of_vertex, edge_density,amount_of_path, max_weight_edge);
//				if(model == null){
//					Parameters.report.writeln(":( :( :( :( :( :(:( :(:( :(:( :(:( :(:( :( ");
//					Parameters.report.writeln("------We can't generate a random graph ----");
//					Parameters.report.writeln("-----------------Try Again ----------------");
//					Parameters.report.writeln(":( :( :( :( :( :(:( :(:( :(:( :(:( :(:( :( ");
//				}else{
//					graph = model.graph; resultGraph = model.resultGraph;
//					gui.setGraphs(model.graph,model.resultGraph);
//					}
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				Parameters.report.writeln(e.toString());
//				e.printStackTrace();
//			} catch (LpSolveException e) {
//				Parameters.report.writeln(e.toString());
//				e.printStackTrace();
//			}
//			finally{gui.activeMenu();}
//
//			
//		}
//		
//	}
//
//
//	public void runDTN() {
//		try {
//				DTNLpModel model = DTNLpModel.generateFromFile("/home/nando/development/Doctorado/Ej1/Examples/DTNExamples/DTN3",2,0);							
//				graph = model.graph; resultGraph = model.resultGraph;
//				gui.setGraphs(model.graph,model.resultGraph);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//	}
//	
//	public void runDTNMultiFlow() {
//		try {
//				DTNLpModelMultiFlow model = DTNLpModelMultiFlow.generateFromFile("/home/nando/development/Doctorado/Ej1/Examples/DTNExamples/DTN4",2,4);				
//				graph = model.graph; resultGraph = model.resultGraph;
//				gui.setGraphs(model.graph,model.resultGraph);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//	}
//	
//	public static void main(String args[]){
//		Controller c = new Controller();
//	}
//	
//	/******************************/
//	public void runWNFromGraph(ListenableDirectedWeightedGraph<Vertex,Edge> graph,Vertex[] vertex, Edge[] edges, int requiredPath,int sharedEdegeWeighing, boolean strongSharedEge){		
//		try {
//			WNLpModel model = WNLpModel.LpModelFromGraph(
//											sharedEdegeWeighing,strongSharedEge,graph.vertexSet().size(),requiredPath,
//											10, 10, graph, vertex, edges);
//			graph = model.graph; resultGraph = model.resultGraph;
//			gui.setGraphs(model.graph,model.resultGraph);
//
//		} catch (IOException e) {
//			e.printStackTrace();
//			Parameters.report.writelnRed("GPLP has experimented an internal error while it was generating LPModel : " + e.toString());
//		} catch (LpSolveException e) {
//			e.printStackTrace();
//			Parameters.report.writelnRed("GPLP has experimented an internal error while it was solving LPModel : " + e.toString());
//		}
//		finally{gui.activeMenu();}
//	}
//	
//	public void runWNFromGraph(ListenableDirectedWeightedGraph<Vertex,Edge> graph,Vertex[] vertex, int requiredPath){		
//		try {
//			Set<Edge> edgesSet = graph.edgeSet();
//			Edge[] edges =  edgesSet.toArray(new Edge[edgesSet.size()]);
//			WNLpModel model = WNLpModel.LpModelFromGraph(
//											0,false,graph.vertexSet().size(),requiredPath,
//											10, 10, graph, vertex, edges);
//			graph = model.graph; resultGraph = model.resultGraph;
//			gui.setGraphs(model.graph,model.resultGraph);
//
//		} catch (IOException e) {
//			e.printStackTrace();
//			Parameters.report.writelnRed("GPLP has experimented an internal error while it was generating LPModel : " + e.toString());
//		} catch (LpSolveException e) {
//			e.printStackTrace();
//			Parameters.report.writelnRed("GPLP has experimented an internal error while it was solving LPModel : " + e.toString());
//		}
//		finally{gui.activeMenu();}
//	}
//	
//	public void runDTNFromGraph(ListenableDirectedWeightedGraph<Vertex,Edge> graph,Vertex[] vertexs, int requiredPath, int requiredCapacity, int maxSharedEdges){		
//		try {
//			Set<Edge> edgesSet = graph.edgeSet();
//			Edge[] edges =  edgesSet.toArray(new Edge[edgesSet.size()]);
//			DTNLpModel model = DTNLpModel.modelFromGraph(vertexs.length,requiredPath,requiredCapacity,maxSharedEdges,graph,vertexs,edges);											
//			graph = model.graph; resultGraph = model.resultGraph;
//			gui.setGraphs(model.graph,model.resultGraph);
//		} catch (IOException e) {
//			e.printStackTrace();
//			Parameters.report.writelnRed("GPLP has experimented an internal error while it was generating LPModel : " + e.toString());
//		} catch (LpSolveException e) {
//			e.printStackTrace();
//			Parameters.report.writelnRed("GPLP has experimented an internal error while it was solving LPModel : " + e.toString());
//		}
//		finally{gui.activeMenu();}
//	}
//
//	public void saveResultGraph(File f) throws IOException {
//		GraphXMLExporter.generateGraphmlFromGraph(resultGraph, f);
//		
//	}
//	
//	public void saveGraph(File f) throws IOException {
//		GraphXMLExporter.generateGraphmlFromGraph(graph, f);
//		
//	}
//	
//}
