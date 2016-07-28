package main.lpModel.dtNet;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.AllDirectedPaths;
import org.jgrapht.graph.GraphPathImpl;
import org.jgrapht.graph.ListenableDirectedWeightedGraph;

import lpsolve.LpSolve;
import lpsolve.LpSolveException;
import main.Parameters;
import main.lpModel.dtNet.dtnParser.parser;
import main.lpModel.wireNet.WNMetrics;
import main.lpModel.wireNet.WNLpFormat;
import main.util.Edge;
import main.util.Pair;
import main.util.Vertex;

public class DTNLpModel{

	/** Amount of graph's vertexs */
	public final int AMOUNT_OF_VERTEX;
	
	/** Amount of required graph's path from node 0 to node AMOUNT_OF_VERTEX -1 */
	public final int AMOUNT_OF_PATH;
	
	/** Amount of required graph's capacity from node 0 to node AMOUNT_OF_VERTEX -1 */
	public final int REQUIRED_CAPACITY;
	
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
	public final DTNMetrics graphMetrics;
	
	/** The result graph metrics */
	public final DTNMetrics resultMetrics;

	public final int s = 0;

	private DTNLpModel(
			int AMOUNT_OF_VERTEX, int AMOUNT_OF_PATH,int REQUIRED_CAPACITY, int MAX_WEIGHT_EDGE, 
			int EDGE_DENSITY,ListenableDirectedWeightedGraph<Vertex,Edge> graph, 
			Vertex[] vertexs, Edge[] edges) throws IOException, LpSolveException
	{
		this.AMOUNT_OF_VERTEX  = AMOUNT_OF_VERTEX;
		this.AMOUNT_OF_PATH    = AMOUNT_OF_PATH;
		this.REQUIRED_CAPACITY = REQUIRED_CAPACITY;
		this.MAX_WEIGHT_EDGE   = MAX_WEIGHT_EDGE; 
		this.EDGE_DENSITY      = EDGE_DENSITY;
		this.graph             = graph;
		this.vertexs           = vertexs;
		this.edges             = edges;
		
		 Pair<Integer,ListenableDirectedWeightedGraph<Vertex,Edge>> solution = solve();
		 this.resultGraph = solution.b;		 
		 this.graphMetrics =  new DTNMetrics(graph, vertexs[0], vertexs[this.AMOUNT_OF_VERTEX-1],REQUIRED_CAPACITY);
		 this.resultMetrics = new DTNMetrics(resultGraph, vertexs[0], vertexs[this.AMOUNT_OF_VERTEX-1],REQUIRED_CAPACITY);

	}


	public static DTNLpModel generateFromFile(String path, int requiredPaths, int requiredCapacity) throws Exception{
		DTNetwork net = parser.parseFromFile(path);
		ListenableDirectedWeightedGraph<Vertex, Edge> graph = net.toListenableDirectedWeightedGraph();
	
		DTNLpModel model = new DTNLpModel(
										net.AMOUNT_OF_NODES * net.AMOUNT_OF_INTERVALS + 1, requiredPaths,requiredCapacity,
										0, 0 , graph, net.vertexs, net.edges
									);
		return model;
	}
	
	public ListenableDirectedWeightedGraph<Vertex,Edge> generateGraphFromSolution(LpSolve solver) throws LpSolveException{
		double[] varValues = solver.getPtrVariables();
		
		 ListenableDirectedWeightedGraph<Vertex,Edge> resultGraph = new ListenableDirectedWeightedGraph<Vertex,Edge>(Edge.class);		
		
		for(int i=0; i<vertexs.length; i++)
			resultGraph.addVertex(vertexs[i]);
		
		
		for(int i=0; i<edges.length; i++)
		/*
			The get_nameindex function returns the index (column/row number) 
			of the given column/row name. Note that this index starts from 1. 
			Some API routines expect zero-based indexes and thus this value must 
			then be corrected with -1.
		 */
			if(varValues[solver.getNameindex("E" + i, false)-1] == 1.00 ){
				resultGraph.addEdge(edges[i].from,edges[i].to, edges[i]);
				resultGraph.setEdgeWeight(edges[i], edges[i].weight);
			}
		
		return resultGraph;
	}
	
	/**
	 * Solve current Linnear Programming model. 
	 * 
	 * */
	private Pair<Integer,ListenableDirectedWeightedGraph<Vertex,Edge>> solve() throws IOException, LpSolveException{
		Parameters.report.writeln("Creating LP Model\n. . .");
		
		String prefix = "lpmodel";
	    String suffix = ".tmp";
	    
	    File tempFile = File.createTempFile(prefix, suffix);
	    tempFile.deleteOnExit();
	    
	    FileWriter writer = new FileWriter(tempFile);
	    String stringLpModel = generateLPFormatString();
	    writer.append(stringLpModel);
	    
		Parameters.report.writeln("The LP model has been created: ");
		Parameters.report.writeln(stringLpModel);
	    
	    writer.flush();
	    writer.close();

		Parameters.report.writeln("Solving Lp Model\n. . .");

	    
	    //Solve lp model	
    	LpSolve solver = LpSolve.readLp(tempFile.getCanonicalFile().getAbsolutePath(),1,"");
    	int solverResult = solver.solve();
    	System.out.println("Solver Result= " + solverResult ) ;

//    	solverResult = solver.solve();
//    	System.out.println("Solver Result= " + solverResult ) ;
//    	
//    	solverResult = solver.solve();
//    	System.out.println("Solver Result= " + solverResult ) ;
	      
	      ListenableDirectedWeightedGraph<Vertex,Edge> resultGraph = generateGraphFromSolution(solver);
	      

	      
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
	 
/**Generate LpFormat model for min cost graph to guarantee k paths from S to T and a minimum flow of C */	
//	public String generateLPFormatString(){
//		//look for all simple path between node 0 and node 3
//		List<GraphPath<Vertex,Edge>> paths = getAllSinglePath(graph, vertexs[0],vertexs[vertexs.length - 1 ]);
//
//		StringBuilder st = new StringBuilder();
//		
//		st.append("min:");
//		
//		//plus between all enables PATHS and its cost
////		for(int i=0; i<paths.size();i++)
////			st.append(" +" + paths.get(i).getWeight()+" P"+i );
//
//		//plus between all enables Edges and its cost
//		for(int i=0; i<edges.length;i++)
//			st.append(" +" + 10 * edges[i].weight +" E"+i );
//		
//		st.append("  - c");
//		
//		st.append(";\n");
//		
//		int constraintNumber=1;
//		
//		//amount of path constraint
//		st.append("r_" + constraintNumber + ":"); constraintNumber++;
//		for(int i=0; i<paths.size();i++)
//			st.append(" +P"+i);
//		st.append(" >= " + AMOUNT_OF_PATH + ";\n");
//		
//		//constraint about each path: If Pi is enable so Pi's edges must be enabled also.
//		for(int i=0;i<paths.size();i++){
//			st.append("r_"+ constraintNumber + ":"); constraintNumber++;
//			st.append("+" + paths.get(i).getEdgeList().size() + " P"+ i + " <=");
//			for(int j=0; j<paths.get(i).getEdgeList().size(); j++)
//				st.append(" +" + paths.get(i).getEdgeList().get(j).name);
//			st.append(";\n");
//		}
//		
//		//constraint about each edge: Ei is enabled only if there is at least one enabled path that contains its.
//		for(int i=0; i<edges.length; i++){
//			st.append("r_"+ constraintNumber + ":" + " E" + i +" <="); constraintNumber++;
//			StringBuilder aux = new StringBuilder();
//			for(int j=0;j<paths.size();j++)	
//				if(paths.get(j).getEdgeList().contains(edges[i]))
//					aux.append(" +P"+ j);
//			if(aux.length() == 0)
//				st.append(" 0");
//			else
//				st.append(aux.toString());
//			st.append(";\n");
//		}
//		
//		/**
//		 * Edges capacity constraints and link variable of enable edge(Ei) with variable of edge flow (ci)
//		 *  ci <= C(Ei) Ei where C(Ei) is capacity of Ci
//		 **/
//		for(int i=0; i<edges.length; i++){	
////			if(!(edges[i].capacity == Integer.MAX_VALUE)){
//				st.append("r_"+ constraintNumber + ": c" + i + " <= +" + edges[i].capacity + " E" + i + ";\n"); constraintNumber++;
////			}
//		}
//
//		for(int i=0; i<edges.length; i++)
//			if(edges[i].capacity < Integer.MAX_VALUE){
//				st.append("r_"+ constraintNumber + ": c" + i + " <= " + edges[i].capacity + ";\n"); constraintNumber++;
//			}
//		
//		/***
//		 * Flow model constraint
//		 ***/
//		
//		//source node flow constraint
////		st.append("r_"+ constraintNumber + ": c0= c \n") ; constraintNumber++;
//		st.append("r_"+ constraintNumber + ": c =" + getOutflow(vertexs[0]) + ";\n"); constraintNumber++;
//		
//		//flow continuity constraint
//		for(int i=1; i<vertexs.length-1; i++){
//			st.append("r_"+ constraintNumber + ":" + getInflow(vertexs[i]) + " =" + getOutflow(vertexs[i]) + ";\n"); constraintNumber++;
//		}
//		//target node flow constraint
//		st.append("r_"+ constraintNumber + ": c =" + getInflow(vertexs[vertexs.length - 1]) + ";\n"); constraintNumber++;
//		
//		//the network capacity c is greater or equal to a minimum required capacity C
//		st.append("r_"+ constraintNumber + ": c >= " + REQUIRED_CAPACITY + ";\n"); constraintNumber++;
//			
//		//constraint that all variables are boolean.				
//		st.append("bin");
//		for(int i=0; i<paths.size();i++)
//			st.append(" P" + i + ",");
//
//		for(int i=0; i<graph.edgeSet().size();i++)
//			st.append(" E" + i + ",");
//		
//		
//		st.deleteCharAt(st.length()-1);		
//		st.append(";");
//
//		System.out.println(st.toString());
//		return st.toString();
//
//	}
	
	public String generateLPFormatString(){
	//look for all simple path between node 0 and node 3
	List<GraphPath<Vertex,Edge>> paths = getAllSinglePath(graph, vertexs[0],vertexs[vertexs.length - 1 ]);

	StringBuilder st = new StringBuilder();
	
	st.append("min:");
	
	//plus between all enables PATHS and its cost
//	for(int i=0; i<paths.size();i++)
//		st.append(" +" + paths.get(i).getWeight()+" P"+i );

	//plus between all enables Edges and its cost
	for(int i=0; i<edges.length;i++)
		st.append(" +" + edges[i].weight +" E"+i );
	
	st.append(" ;\n");
	
	int constraintNumber=1;
	
	//amount of path constraint
	st.append("r_" + constraintNumber + ":"); constraintNumber++;
	for(int i=0; i<paths.size();i++)
		st.append(" +P"+i);
	st.append(" >= " + AMOUNT_OF_PATH + ";\n");
	
	//constraint about each path: If Pi is enable so Pi's edges must be enabled also.
	for(int i=0;i<paths.size();i++){
		st.append("r_"+ constraintNumber + ":"); constraintNumber++;
		st.append("+" + paths.get(i).getEdgeList().size() + " P"+ i + " <=");
		for(int j=0; j<paths.get(i).getEdgeList().size(); j++)
			st.append(" +" + paths.get(i).getEdgeList().get(j).name);
		st.append(";\n");
	}
	
	//constraint about each edge: Ei is enabled only if there is at least one enabled path that contains its.
	for(int i=0; i<edges.length; i++){
		st.append("r_"+ constraintNumber + ":" + " E" + i +" <="); constraintNumber++;
		StringBuilder aux = new StringBuilder();
		for(int j=0;j<paths.size();j++)	
			if(paths.get(j).getEdgeList().contains(edges[i]))
				aux.append(" +P"+ j);
		if(aux.length() == 0)
			st.append(" 0");
		else
			st.append(aux.toString());
		st.append(";\n");
	}
	
	/** Constraint about two-paths variables*/
	for(int i=0; i<paths.size(); i++)
		for(int j=i+1; j<paths.size(); j++){
			int sharedEdge = amountSE(paths.get(i), paths.get(j));
			if( sharedEdge > 0){		
				st.append("r_"+ constraintNumber +":"); constraintNumber++;
				st.append(" P" + i + " " + "+" + "P" + j + " <= +1 +P" + i + "P" + j + ";\n" );
		
			}
		}
	
	/** Constraint about paths can share S edges two and two */
	for(int i=0; i<paths.size(); i++)
		for(int j=i+1; j<paths.size(); j++){
			st.append("r_"+ constraintNumber +":" + sharedEdges(paths.get(i),paths.get(j)) + " P" + i + "P" + j + " <= " + s + " ;\n");
			constraintNumber++;
		}
	
	/**
	 * Edges capacity constraints and link variable of enable edge(Ei) with variable of edge flow (ci)
	 *  ci <= C(Ei) Ei where C(Ei) is capacity of Ci
	 **/
	for(int i=0; i<edges.length; i++){	
		if(!(edges[i].capacity == Integer.MAX_VALUE)){
			st.append("r_"+ constraintNumber + ": c" + i + " <= +" + edges[i].capacity + " E" + i + ";\n"); constraintNumber++;
		}
	}
	
	/***
	 * Flow model constraint
	 ***/
	
	//source node flow constraint
	st.append("r_"+ constraintNumber + ": c =" + getOutflow(vertexs[0]) + ";\n"); constraintNumber++;
	
	//flow continuity constraint
	for(int i=1; i<vertexs.length-1; i++){
		st.append("r_"+ constraintNumber + ":" + getInflow(vertexs[i]) + " =" + getOutflow(vertexs[i]) + ";\n"); constraintNumber++;
	}
	//target node flow constraint
	st.append("r_"+ constraintNumber + ": c =" + getInflow(vertexs[vertexs.length - 1]) + ";\n"); constraintNumber++;
	
	//the network capacity c is greater or equal to a minimum required capacity C
	st.append("r_"+ constraintNumber + ": c >= " + REQUIRED_CAPACITY + ";\n"); constraintNumber++;
	
	//constraint that all variables are boolean.				
	st.append("bin");
	for(int i=0; i<paths.size();i++)
		st.append(" P" + i + ",");

	for(int i=0; i<graph.edgeSet().size();i++)
		st.append(" E" + i + ",");
	
	
	st.deleteCharAt(st.length()-1);		
	st.append(";");

	System.out.println(st.toString());
	return st.toString();

}
	
	private String getOutflow(Vertex vertex) {
		StringBuilder st = new StringBuilder();
		
		for(int i=0; i<edges.length;i++)
			if(edges[i].from.equals(vertex))
				st.append(" +c" + i );
		
		return (st.toString().isEmpty())? " 0": st.toString();			
	}
	
	private String getInflow(Vertex vertex) {
		StringBuilder st = new StringBuilder();
		
		for(int i=0; i<edges.length;i++)
			if(edges[i].to.equals(vertex))
				st.append(" +c" + i );
		
		return (st.toString().isEmpty())? " 0": st.toString();
			
	}





	public static List<GraphPath<Vertex, Edge>> getAllSinglePath(ListenableDirectedWeightedGraph<Vertex,Edge> graph, Vertex source, Vertex target){
		AllDirectedPaths<Vertex,Edge> pathFinder = new AllDirectedPaths<Vertex,Edge>(graph);
		List<GraphPath<Vertex, Edge>> paths = pathFinder.getAllPaths(source,target,true,null);
		
//		Edge sourceToTarget = graph.getEdge(source,target);
//		if (sourceToTarget != null ){ 			
//			LinkedList<Edge> edgeList = new LinkedList<Edge>(); edgeList.add(sourceToTarget);
//			paths.add(new GraphPathImpl<Vertex,Edge>(graph, source, target, edgeList, sourceToTarget.weight));
//		
//		}
		
		List<GraphPath<Vertex, Edge>> filterPath = new LinkedList<GraphPath<Vertex, Edge>>();
		for(GraphPath<Vertex, Edge> p: paths){
			List<Edge> edgeList = p.getEdgeList(); 
			int i;
			for(i=0; i< edgeList.size()-2;i++){
				String interval = edgeList.get(i).from.name.substring(edgeList.get(i).from.name.indexOf("_") + 1);			
				String cmp = edgeList.get(i+1).to.name.substring(edgeList.get(i+1).to.name.indexOf("_") + 1);
				if(interval.equals(cmp))
						break;
			}
			if (i ==  edgeList.size() - 2 )
				filterPath.add(p);
		}
		
		return filterPath;
	}

	
	private static int amountSE(GraphPath<Vertex,Edge> pi, GraphPath<Vertex,Edge> pj){
		int result = 0;
		for(Edge ePi: pi.getEdgeList() )
			for(Edge ePj: pj.getEdgeList())
				if(ePi.equals(ePj))
					result++;

		return result;
	}
	
	public static void main(String[] args) throws LpSolveException, IOException{		
		Parameters.report.writeln("Creating LP Model\n. . .");
		
		String prefix = "lpmodel";
	    String suffix = ".tmp";
	    
	    File tempFile = File.createTempFile(prefix, suffix);
	    tempFile.deleteOnExit();
	    
	    FileWriter writer = new FileWriter(tempFile);
	    
		StringBuilder st = new StringBuilder();
		
		st.append("min: +x0 +x1 +x2 +x3 ;\n");
		

		
		int constraintNumber=1;
		
		//amount of path constraint
		st.append("r_" + constraintNumber + ": 1 <= +x0 +x1 +x2 +x3 ;\n"); constraintNumber++;
		st.append("\n" + "bin x0, x1, x2, x3 ;");
		
		System.out.println(st.toString());
	    String stringLpModel = st.toString(); 
	    writer.append(stringLpModel);
	    
		Parameters.report.writeln("The LP model has been created: ");
		Parameters.report.writeln(stringLpModel);
	    
	    writer.flush();
	    writer.close();

		Parameters.report.writeln("Solving Lp Model\n. . .");

	    
	    //Solve lp model	
    	LpSolve solver = LpSolve.readLp("/home/nando/Desktop/a.lp",1,"");
//    	solver.strSetObjFn("0.1");
//    	int solverResult = solver.solve();
//    	System.out.println("Solver Result= " + solverResult ) ;

//    	solverResult = solver.solve();
//    	System.out.println("Solver Result= " + solverResult ) ;
//    	
//    	solverResult = solver.solve();
//    	System.out.println("Solver Result= " + solverResult ) ;
	      	      

	      
	      Parameters.report.writeln("The Lp model was solved: ");
	      // print solution
	      for(int j=0; j<10; j++){
	    	  System.out.println("***************************** Solution " + j +" *****************************");
	    	  int solverResult = solver.solve();
	    	  System.out.println("Solver Result= " + solverResult ) ;
	    	  System.out.println("Value of objective function: " + solver.getObjective());
	      	double[] var = solver.getPtrVariables();		     
	      	for (int i = 0; i < var.length; i++) {
	      		System.out.println("Value of var[" + solver.getColName(i+1) + "] = " + var[i]);
	      	}
	      	System.out.println("*****************************************************************************");
	      }

	      // delete the problem and free memory
	      solver.deleteLp();
		
	}
	
	private int sharedEdges(GraphPath<Vertex,Edge> pi, GraphPath<Vertex,Edge> pj){
		int amountSharedEdges=0;
		
		for(Edge e: pi.getEdgeList())
			//because DTNLinks has weight equal to zero
			if( e.weight==0 && !e.to.equals(vertexs[vertexs.length-1]) && pj.getEdgeList().contains(e))
				amountSharedEdges++;
		
		return amountSharedEdges;
	}

//	  public static void main(String[] args) {
//		    try {
//		      // Create a problem with 4 variables and 0 constraints
//		      LpSolve solver = LpSolve.makeLp(0, 4);
//
//		      // add constraints
//		      solver.strAddConstraint("3 2 2 1", LpSolve.LE, 4);
//		      solver.strAddConstraint("0 4 3 1", LpSolve.GE, 3);
//
//		      // set objective function
//		      solver.strSetObjFn("2 3 -2 3");
//		     System.out.println("anaosndoiansd\n\n"); solver.printConstraints(1);;System.out.println("anaosndoiansd\n\n");
//		      // solve the problem
//		      solver.solve();
//
//		      // print solution
//		      System.out.println("Value of objective function: " + solver.getObjective());
//		      double[] var = solver.getPtrVariables();
//		      for (int i = 0; i < var.length; i++) {
//		        System.out.println("Value of var[" + i + "] = " + var[i]);
//		      }
//
//		      // delete the problem and free memory
//		      solver.deleteLp();
//		    }
//		    catch (LpSolveException e) {
//		       e.printStackTrace();
//		    }
//	  }
}
