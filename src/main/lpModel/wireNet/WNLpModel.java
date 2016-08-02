package main.lpModel.wireNet;
import lpsolve.*;
import main.Parameters;
import main.util.Edge;
import main.util.Pair;
import main.util.Vertex;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.jgrapht.GraphPath;
import org.jgrapht.graph.GraphPathImpl;
import org.jgrapht.graph.ListenableDirectedWeightedGraph;
import org.jgrapht.alg.AllDirectedPaths;

/**
 * Represent an unmutable linnear programming model and its solution if it is feasible
 * for a wire network.
 * 
 * */
public class WNLpModel{
	
	private static final int MAX_ATTEMPT_RANDOM_GRAPH = 5000;

	/** Amount of graph's vertexs */
	public final int AMOUNT_OF_VERTEX;
	
	/** Amount of required graph's path from node 0 to node AMOUNT_OF_VERTEX -1 */
	public final int AMOUNT_OF_PATH;
	
	/** Maximum Weight for an edge. The graph's edges will have weight from 1 to MAX_WEIGHT_EDGE inclusively */
	public final int MAX_WEIGHT_EDGE;
	
	/** Density of graph's edges. it will be taken into account for generate a random graph */
	public final int EDGE_DENSITY;
	
	/** The graph for current model */
	public final ListenableDirectedWeightedGraph<Vertex,Edge> graph;
	
	/** The linnear programming model entry graph */
	public final ListenableDirectedWeightedGraph<Vertex,Edge> resultGraph;
	
	/** The entry graph vertexs */
	public final Vertex[] vertexs;
	
	/** The entry graph edges */
	public final Edge[] edges;

	/** The entry graph metrics */
	public final WNMetrics graphMetrics;
	
	/** The result graph metrics */
	public final WNMetrics resultMetrics;


	private WNLpModel(
			double SHARED_EDGE_PONDERATION, boolean STRONG_SHARED_EDGE, int AMOUNT_OF_VERTEX, int AMOUNT_OF_PATH,int MAX_WEIGHT_EDGE, 
			int EDGE_DENSITY,ListenableDirectedWeightedGraph<Vertex,Edge> graph, 
			Vertex[] vertexs, Edge[] edges) throws IOException, LpSolveException
	{
		this.AMOUNT_OF_VERTEX = AMOUNT_OF_VERTEX;
		this.AMOUNT_OF_PATH   = AMOUNT_OF_PATH;
		this.MAX_WEIGHT_EDGE  = MAX_WEIGHT_EDGE; 
		this.EDGE_DENSITY     = EDGE_DENSITY;
		this.graph            = graph;
		this.vertexs          = vertexs;
		this.edges            = edges;
		
		 Pair<Integer,ListenableDirectedWeightedGraph<Vertex,Edge>> solution = solve(SHARED_EDGE_PONDERATION,STRONG_SHARED_EDGE);
		 this.resultGraph = solution.b;		 
		 this.graphMetrics =  new WNMetrics(graph, vertexs[0], vertexs[this.AMOUNT_OF_VERTEX-1]);
		 this.resultMetrics = new WNMetrics(resultGraph, vertexs[0], vertexs[this.AMOUNT_OF_VERTEX-1]);

	}
	
	
	/**
	 * Generate a default lp model
	 * @throws IOException 
	 * @throws LpSolveException 
	 * 
	 * */
	public static WNLpModel DefaultLPModel() throws IOException, LpSolveException
	{
//		WNLpFormat lpModel = new WNLpFormat(
//										Parameters.SHARED_EDGE_PONDERATION,false,Parameters.AMOUNT_OF_NODES, Parameters.AMOUNT_OF_REQUIRED_PATHS,
//				  						Parameters.MAX_WEIGHT_EDGE, Parameters.EDGE_DENSITY, Parameters.DEFAULT_GRAPH, 
//				  						Parameters.Vertexs, Parameters.Edges);	
		WNLpModel lpModel = new WNLpModel(
				Parameters.SHARED_EDGE_PONDERATION,false,Parameters.AMOUNT_OF_NODES, Parameters.AMOUNT_OF_REQUIRED_PATHS,
					Parameters.MAX_WEIGHT_EDGE, Parameters.EDGE_DENSITY, Parameters.DEFAULT_GRAPH, 
					Parameters.Vertexs, Parameters.Edges);		

		return lpModel;
	}
	
	/**
	 * Generate a default lp model
	 * @throws LpSolveException 
	 * 
	 * */
	public static WNLpModel LpModelFromGraph(
											double SHARED_EDGE_PONDERATION,boolean STRONG_SHARED_EDGE ,int AMOUNT_OF_VERTEX, int AMOUNT_OF_PATH,int MAX_WEIGHT_EDGE, 
											int EDGE_DENSITY,ListenableDirectedWeightedGraph<Vertex,Edge> graph, 
											Vertex[] vertexs, Edge[] edges) 
											throws IOException, LpSolveException
	{
		WNLpModel lpModel = new WNLpModel(SHARED_EDGE_PONDERATION, STRONG_SHARED_EDGE, AMOUNT_OF_VERTEX, AMOUNT_OF_PATH,
									  MAX_WEIGHT_EDGE, EDGE_DENSITY, graph, 
									  vertexs, edges);	
		
		return lpModel;
	}
	
	public static WNLpModel generateLpModel(int amount_of_vertex,int edge_density,int amount_of_path,int max_weight_edge) throws IOException, LpSolveException{
		LinkedList<Vertex> listVertexs = new LinkedList<Vertex>();
		LinkedList<Edge> listEdges     = new LinkedList<Edge>();
		ListenableDirectedWeightedGraph<Vertex,Edge> graph = generateGraph(amount_of_vertex,edge_density,max_weight_edge,amount_of_path,listVertexs,listEdges);
		
		//check if random graph could be generated
		if(graph == null)
			return null;
		
		Vertex[] vertexs = new Vertex[amount_of_vertex];
		Edge[] edges = new Edge[listEdges.size()];
		vertexs = listVertexs.toArray(vertexs);
		edges = listEdges.toArray(edges);
		
		WNLpModel model = new WNLpModel(0,false,amount_of_vertex, amount_of_path, max_weight_edge, edge_density, graph, vertexs, edges);
		return model;
	}
	
	private static ListenableDirectedWeightedGraph<Vertex,Edge> generateGraph(int amountOfNodes, int density, int max_weight_edge,int amount_of_path,List<Vertex> outVertex, List<Edge> outEdges){
		int k;
		List<GraphPath<Vertex,Edge>> paths;
		ListenableDirectedWeightedGraph<Vertex,Edge> graph = null;
		
		for(k=0; k<MAX_ATTEMPT_RANDOM_GRAPH;k++){
			while(!outVertex.isEmpty())outVertex.remove(0);
			while(!outEdges.isEmpty())outEdges.remove(0);
			graph = new ListenableDirectedWeightedGraph<Vertex,Edge>(Edge.class);			
			
			for(int i=0; i<amountOfNodes; i++){
				Vertex v = new Vertex("V"+i);
				outVertex.add(v);
				graph.addVertex(v);
			}
			
			Random rn  = new Random();
			Random rnw = new Random();
			for(int i=0; i<amountOfNodes; i++)
				for(int j=0; j<amountOfNodes; j++){
					if(i != j && rn.nextFloat()*100 < density){
						Edge e = new Edge("E"+outEdges.size(), outVertex.get(i),outVertex.get(j),rnw.nextInt(max_weight_edge) + 1 ); 
						graph.addEdge(e.from,e.to,e);					
						graph.setEdgeWeight(e, e.weight);
						outEdges.add(e);
					}
				}
			
			paths = getAllSinglePath(graph,outVertex.get(0),outVertex.get(amountOfNodes - 1));
			if(paths.size() >= amount_of_path)
				break;
		}
		if(k<MAX_ATTEMPT_RANDOM_GRAPH){
			Parameters.report.writeln("The Graph has been generated");
			return graph;
		}else 
			return null;
	}
	
	public ListenableDirectedWeightedGraph<Vertex,Edge> generateGraphFromSolution(double[] solution){
		 ListenableDirectedWeightedGraph<Vertex,Edge> resultGraph = new ListenableDirectedWeightedGraph<Vertex,Edge>(Edge.class);		
		
		for(int i=0; i<vertexs.length; i++)
			resultGraph.addVertex(vertexs[i]);
		
		for(int i=0; i<edges.length; i++)
			if(solution[i] == 1.00 ){
				resultGraph.addEdge(edges[i].from,edges[i].to, edges[i]);
				resultGraph.setEdgeWeight(edges[i], edges[i].weight);
			}
		
		return resultGraph;
	}
	
	/**
	 * Solve current Linnear Programming model. 
	 * 
	 * */
	private Pair<Integer,ListenableDirectedWeightedGraph<Vertex,Edge>> solve(double SHARED_EDGE_PONDERATION, boolean STRONG_SHARED_EDGE) throws IOException, LpSolveException{
		Parameters.report.writeln("Creating LP Model. . .");
		
		String prefix = "lpmodel";
	    String suffix = ".tmp";
	    
	    File tempFile = File.createTempFile(prefix, suffix);
	    tempFile.deleteOnExit();
	    
	    FileWriter writer = new FileWriter(tempFile);
	    String stringLpModel = generateLPFormatString2(SHARED_EDGE_PONDERATION,STRONG_SHARED_EDGE);
	    writer.append(stringLpModel);
	    
		Parameters.report.writelnGreen("The LP model has been created: ");
		Parameters.report.writeString(stringLpModel);
	    
	    writer.flush();
	    writer.close();

		Parameters.report.writeln("Solving Lp Model. . .");

	    
	    //Solve lp model	
    	LpSolve solver = LpSolve.readLp(tempFile.getCanonicalFile().getAbsolutePath(),1,"");
    	int solverResult = solver.solve();
    	System.out.println("Solver Result= " + solverResult ) ;

	      
	      ListenableDirectedWeightedGraph<Vertex,Edge> resultGraph = generateGraphFromSolution(solver.getPtrVariables());
	      

	      
	      Parameters.report.writeln("The Lp model was solved: ");
	      // print solution
	      Parameters.report.writeln("Value of objective function: " + solver.getObjective());
	      double[] var = solver.getPtrVariables();		     
	      for (int i = 0; i < var.length; i++) {
	    	  Parameters.report.writeln("Value of var[" + solver.getColName(i+1) + "] = " + var[i]);
	      }

	      // delete the problem and free memory
	      solver.deleteLp();
	      
	      return new Pair<Integer,ListenableDirectedWeightedGraph<Vertex,Edge>>(solverResult,resultGraph);
	}
	
	/***
	 * 
	 * @param shared_edge_ponderation
	 * @param strong_weighting
	 * @return
	 * @deprecated method create names for edges. Now method belive in edges names
	 */
	@Deprecated
	public String generateLPFormatString(double shared_edge_ponderation, boolean strong_weighting){
		//look for all simple path between node 0 and node 3
		List<GraphPath<Vertex,Edge>> paths = getAllSinglePath(graph, vertexs[0],vertexs[vertexs.length - 1 ]);

		StringBuilder st = new StringBuilder();
		
		st.append("min:");
		//plus between all enables edges and its cost
		for(int i=0; i<edges.length;i++)
			st.append(" +" + graph.getEdgeWeight(edges[i]) +" E"+i );
		
//		if(shared_edge_ponderation > 0){			
//			for(int i=0;i<edges.length;i++){
//				st.append(" -"+ shared_edge_ponderation + " E"+i);
//			}
//		}
//		st.append(";\n");
//		if(shared_edge_ponderation > 0){			
//			for(int i=0;i<edges.length;i++){
//				boolean b = false;
//				for(int j=0; j<paths.size();j++){
//					if(paths.get(j).getEdgeList().contains(edges[i])){
//						st.append(" +" + shared_edge_ponderation+ " E" + i + " P" + j);
//						b=true;					
//					}
//					
//				}
//				
////				if(b){st.append(" -" + "E" + i + " " + shared_edge_ponderation);}				
//			}
//		}
		
		/*
		 * weak weigthting: add terms to lp's target function for each edge like this: shared_edge_ponderation * Ei. 
		 */
		if(shared_edge_ponderation > 0 && !strong_weighting){
			System.out.println(shared_edge_ponderation);
			for(int i=0;i<edges.length;i++)
				st.append(" -" + shared_edge_ponderation+ " E" + i);
		}

		/* 
		 * strong weigthting: For each two path pi and pj where pi!=pj,
		 * add variables to lp model pipj which meaning is path pi and pj are both available. 
		 * And add for each of theses new variables terms like shared_edge_ponderation * ShE(pi,pj) * pipj, 
		 * where ShE(pi,pj) is the function that compute the shared edges between two paths.   
		 */
		if(shared_edge_ponderation > 0 && strong_weighting){
			for(int i=0; i<paths.size(); i++)
				for(int j=i+1; j<paths.size(); j++){
					int sharedEdge = amountSE(paths.get(i), paths.get(j));
					if( sharedEdge > 0)
						st.append(" +" + (shared_edge_ponderation * sharedEdge) + " P" + i + "P" + j);
				}
					
		}

		
		
		st.append(";\n");
		
		int constraintNumber=1;
		
		//amount of path constraint
		st.append("r_" + constraintNumber + ":"); constraintNumber++;
		for(int i=0; i<paths.size();i++)
			st.append(" +P"+i);
		st.append(" >= " + AMOUNT_OF_PATH + ";\n");
		
		//constraint about each path
		for(int i=0;i<paths.size();i++){
			st.append("r_"+ constraintNumber + ":"); constraintNumber++;
			st.append("+" + paths.get(i).getEdgeList().size() + " P"+ i + " <=");
			for(int j=0; j<paths.get(i).getEdgeList().size(); j++)
				st.append(" +" + paths.get(i).getEdgeList().get(j).name);
			st.append(";\n");
		}
		
		
		//constraint about strong shared edges 
		if(strong_weighting)
			for(int i=0; i<paths.size(); i++)
				for(int j=i+1; j<paths.size(); j++){
					int sharedEdge = amountSE(paths.get(i), paths.get(j));
					if( sharedEdge > 0){		
						st.append("r_"+ constraintNumber +":"); constraintNumber++;
						st.append(" P" + i + " " + "+" + "P" + j + " <= +1 +P" + i + "P" + j + ";\n" );
				
					}
				}
		
		st.append("\n");
		
		//constraint that all variables are boolean.				
		st.append("bin");
		for(int i=0; i<paths.size();i++)
			st.append(" P" + i + ",");

		for(int i=0; i<graph.edgeSet().size();i++)
			st.append(" E" + i + ",");
		
		if(strong_weighting)
			for(int i=0; i<paths.size(); i++)
				for(int j=i+1; j<paths.size(); j++)
					st.append(" P" + i + "P" + j + ",");
		
		st.deleteCharAt(st.length()-1);		
		st.append(";");
		
		System.out.println(st.toString());
		return st.toString();

	}
	
	
	/***
	 * 
	 * @param shared_edge_ponderation
	 * @param strong_weighting
	 * @return
	 *
	 */
	public String generateLPFormatString2(double shared_edge_ponderation, boolean strong_weighting){
		//look for all simple path between node 0 and node 3
		List<GraphPath<Vertex,Edge>> paths = getAllSinglePath(graph, vertexs[0],vertexs[vertexs.length - 1 ]);

		StringBuilder st = new StringBuilder();
		
		st.append("min:");
		//plus between all enables edges and its cost
		for(Edge e: edges)
			st.append(" +" + graph.getEdgeWeight(e) +" "+ e.name );
		
//		if(shared_edge_ponderation > 0){			
//			for(int i=0;i<edges.length;i++){
//				st.append(" -"+ shared_edge_ponderation + " E"+i);
//			}
//		}
//		st.append(";\n");
//		if(shared_edge_ponderation > 0){			
//			for(int i=0;i<edges.length;i++){
//				boolean b = false;
//				for(int j=0; j<paths.size();j++){
//					if(paths.get(j).getEdgeList().contains(edges[i])){
//						st.append(" +" + shared_edge_ponderation+ " E" + i + " P" + j);
//						b=true;					
//					}
//					
//				}
//				
////				if(b){st.append(" -" + "E" + i + " " + shared_edge_ponderation);}				
//			}
//		}
		
		/*
		 * weak weigthting: add terms to lp's target function for each edge like this: shared_edge_ponderation * Ei. 
		 */
		if(shared_edge_ponderation > 0 && !strong_weighting){
			System.out.println(shared_edge_ponderation);
			for(Edge e: edges )
				st.append(" -" + shared_edge_ponderation+ " " + e.name);
		}

		/* 
		 * strong weigthting: For each two path pi and pj where pi!=pj,
		 * add variables to lp model pipj which meaning is path pi and pj are both available. 
		 * And add for each of theses new variables terms like shared_edge_ponderation * ShE(pi,pj) * pipj, 
		 * where ShE(pi,pj) is the function that compute the shared edges between two paths.   
		 */
		if(shared_edge_ponderation > 0 && strong_weighting){
			for(int i=0; i<paths.size(); i++)
				for(int j=i+1; j<paths.size(); j++){
					int sharedEdge = amountSE(paths.get(i), paths.get(j));
					if( sharedEdge > 0)
						st.append(" +" + (shared_edge_ponderation * sharedEdge) + " P" + i + "P" + j);
				}
					
		}

		
		
		st.append(";\n");
		
		int constraintNumber=1;
		
		//amount of path constraint
		st.append("r_" + constraintNumber + ":"); constraintNumber++;
		for(int i=0; i<paths.size();i++)
			st.append(" +P"+i);
		st.append(" >= " + AMOUNT_OF_PATH + ";\n");
		
		//constraint about each path
		for(int i=0;i<paths.size();i++){
			st.append("r_"+ constraintNumber + ":"); constraintNumber++;
			st.append("+" + paths.get(i).getEdgeList().size() + " P"+ i + " <=");
			for(int j=0; j<paths.get(i).getEdgeList().size(); j++)
				st.append(" +" + paths.get(i).getEdgeList().get(j).name);
			st.append(";\n");
		}
		
		
		//constraint about strong shared edges 
		if(strong_weighting)
			for(int i=0; i<paths.size(); i++)
				for(int j=i+1; j<paths.size(); j++){
					int sharedEdge = amountSE(paths.get(i), paths.get(j));
					if( sharedEdge > 0){		
						st.append("r_"+ constraintNumber +":"); constraintNumber++;
						st.append(" P" + i + " " + "+" + "P" + j + " <= +1 +P" + i + "P" + j + ";\n" );
				
					}
				}
		
		st.append("\n");
		
		//constraint that all variables are boolean.				
		st.append("bin");
		for(int i=0; i<paths.size();i++)
			st.append(" P" + i + ",");

		for(Edge e: edges)
			st.append(" " + e.name + ",");
		
		if(strong_weighting)
			for(int i=0; i<paths.size(); i++)
				for(int j=i+1; j<paths.size(); j++)
					st.append(" P" + i + "P" + j + ",");
		
		st.deleteCharAt(st.length()-1);		
		st.append(";");
		
		System.out.println(st.toString());
		return st.toString();

	}
	
	public static List<GraphPath<Vertex, Edge>> getAllSinglePath(ListenableDirectedWeightedGraph<Vertex,Edge> graph, Vertex source, Vertex target){
		AllDirectedPaths<Vertex,Edge> pathFinder = new AllDirectedPaths<Vertex,Edge>(graph);
		List<GraphPath<Vertex, Edge>> paths = pathFinder.getAllPaths(source,target,true,null);
		
		Edge sourceToTarget = graph.getEdge(source,target);
		if (sourceToTarget != null ){ 			
			LinkedList<Edge> edgeList = new LinkedList<Edge>(); edgeList.add(sourceToTarget);
			paths.add(new GraphPathImpl<Vertex,Edge>(graph, source, target, edgeList, sourceToTarget.weight));
		
		}
		return paths;
	}
	
	public static WNLpModel testLpModel(ListenableDirectedWeightedGraph<Vertex,Edge> graph, int amount_of_path,Vertex[] vertexs, Edge[] edges) throws IOException, LpSolveException{
		return new WNLpModel(0,false,graph.vertexSet().size(), amount_of_path, Integer.MAX_VALUE, 100, graph, vertexs,edges);
	}
	
	private static int amountSE(GraphPath<Vertex,Edge> pi, GraphPath<Vertex,Edge> pj){
		int result = 0;
		for(Edge ePi: pi.getEdgeList() )
			for(Edge ePj: pj.getEdgeList())
				if(ePi.equals(ePj))
					result++;

		return result;
	}
//	public static void main(String[] args) throws IOException{
//		LpModel model = new LpModel();
//				
//	}
//	
//	  public static void main(String[] args) throws IOException
//	  {
//	    String prefix = "lpmodel";
//	    String suffix = ".tmp";
//	     
//	    exampleGraph();
//	    
//	    File tempFile = File.createTempFile(prefix, suffix);
//	    tempFile.deleteOnExit();
//	    System.out.format("Canonical filename: %s\n", tempFile.getCanonicalFile());
//	    
//	    FileWriter writer = new FileWriter(tempFile);
//	    //Model for graph first example.
//	    
////	    writer.append(exampleGraph());
//	    writer.append(
//	    				"min: +1 E0 +1 E1 +2 E2 +1 E3 +1 E4;\n"+
//						"r_1: +P0 +P1 +P2 >= 2;\n"+
//						"r_2: +2 P0 <= +E0 +E4;\n"+
//						"r_3: +2 P1 <= +E1 +E3;\n"+
//						"r_4: +3 P2 <= +E1 +E2 +E4;\n"+
//						"\n"+	
//						"bin E0, E1, E2, E3, E4, P0, P1, P2;"
//					);
////	    writer.append(
////	    				"max: 143 x + 60 y;\n"+
////	    				"120 x + 210 y <= 15000;\n"+
////	    				"110 x + 30 y <= 4000;\n"+
////	    				"x + y <= 75;\n"
////	    				);
//
////	    writer.append(
////	    				"min: -x1 -2 x2 +0.1 x3 +3 x4; \n"+
////	    				"r_1: +x1 +x2 <= 5; \n"+
////						"r_2: +2 x1 -x2 >= 0; \n"+
////						"r_3: -x1 +3 x2 >= 0; \n"+
////						"r_4: +x3 +x4 >= 0.5; \n"+
////						"\n"+	
////						"bin x3, x4;\n"
////					);
//	    writer.flush();
//	    writer.close();
//	    try{
//	    	LpSolve solver = LpSolve.readLp(tempFile.getCanonicalFile().getAbsolutePath(),1,"");
//	    	solver.solve();
//		      // print solution
//		      System.out.println("Value of objective function: " + solver.getObjective());
//		      double[] var = solver.getPtrVariables();		     
//		      for (int i = 0; i < var.length; i++) {
//		        System.out.println("Value of var[" + solver.getColName(i+1) + "] = " + var[i]);
//		      }
//		      
//		      
//		      // delete the problem and free memory
//		      solver.deleteLp();
//	    }catch(LpSolveException e){
//	    	System.out.println(e.toString());
//	    }
//	}
	
/*
	  public static void main(String[] args) {
	    try {
	      // Create a problem with 4 variables and 0 constraints
	      LpSolve solver = LpSolve.makeLp(0, 4);

	      // add constraints
	      solver.strAddConstraint("3 2 2 1", LpSolve.LE, 4);
	      solver.strAddConstraint("0 4 3 1", LpSolve.GE, 3);

	      // set objective function
	      solver.strSetObjFn("2 3 -2 3");
	      
	      solver.setMaxim();
	      // solve the problem
	      solver.solve();

	      // print solution
	      System.out.println("Value of objective function: " + solver.getObjective());
	      double[] var = solver.getPtrVariables();
	      for (int i = 0; i < var.length; i++) {
	        System.out.println("Value of var[" + i + "] = " + var[i]);
	      }

	      // delete the problem and free memory
	      solver.deleteLp();
	    }
	    catch (LpSolveException e) {
	       e.printStackTrace();
	    }
	  }
*/

}
