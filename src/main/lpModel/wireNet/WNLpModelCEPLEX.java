package main.lpModel.wireNet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.AllDirectedPaths;
import org.jgrapht.graph.GraphPathImpl;
import org.jgrapht.graph.ListenableDirectedWeightedGraph;

import ilog.concert.*;
import ilog.cplex.*;
import ilog.cplex.CpxModel.ModelIterator;
import lpsolve.LpSolve;
import lpsolve.LpSolveException;
import main.Parameters;
import main.util.Edge;
import main.util.Pair;
import main.util.Vertex;

public class WNLpModelCEPLEX {
	
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

	/** Variables of current lp model */
	private IloIntVar[] edgeVars;
	private IloIntVar[] pathVars;		
	private IloIntVar[] twoPathsVars;

	private WNLpModelCEPLEX(
			double SHARED_EDGE_PONDERATION, boolean STRONG_SHARED_EDGE, int AMOUNT_OF_VERTEX, int AMOUNT_OF_PATH,int MAX_WEIGHT_EDGE, 
			int EDGE_DENSITY,ListenableDirectedWeightedGraph<Vertex,Edge> graph, 
			Vertex[] vertexs, Edge[] edges) throws IOException, IloException
	{
		this.AMOUNT_OF_VERTEX = AMOUNT_OF_VERTEX;
		this.AMOUNT_OF_PATH   = AMOUNT_OF_PATH;
		this.MAX_WEIGHT_EDGE  = MAX_WEIGHT_EDGE; 
		this.EDGE_DENSITY     = EDGE_DENSITY;
		this.graph            = graph;
		this.vertexs          = vertexs;
		this.edges            = edges;
		
		 Pair<Boolean,ListenableDirectedWeightedGraph<Vertex,Edge>> solution = solve(SHARED_EDGE_PONDERATION,STRONG_SHARED_EDGE);
		 this.resultGraph = solution.b;		 
		 this.graphMetrics =  new WNMetrics(graph, vertexs[0], vertexs[this.AMOUNT_OF_VERTEX-1]);
		 this.resultMetrics = new WNMetrics(resultGraph, vertexs[0], vertexs[this.AMOUNT_OF_VERTEX-1]);

	}
	
	
	/**
	 * Generate a default lp model
	 * @throws IOException 
	 * @throws LpSolveException 
	 * @throws IloException 
	 * 
	 * */
	public static WNLpModelCEPLEX DefaultLPModel() throws IOException, IloException
	{
//		WNLpFormat lpModel = new WNLpFormat(
//										Parameters.SHARED_EDGE_PONDERATION,false,Parameters.AMOUNT_OF_NODES, Parameters.AMOUNT_OF_REQUIRED_PATHS,
//				  						Parameters.MAX_WEIGHT_EDGE, Parameters.EDGE_DENSITY, Parameters.DEFAULT_GRAPH, 
//				  						Parameters.Vertexs, Parameters.Edges);	
		WNLpModelCEPLEX lpModel = new WNLpModelCEPLEX(
				Parameters.SHARED_EDGE_PONDERATION,false,Parameters.AMOUNT_OF_NODES, Parameters.AMOUNT_OF_REQUIRED_PATHS,
					Parameters.MAX_WEIGHT_EDGE, Parameters.EDGE_DENSITY, Parameters.DEFAULT_GRAPH, 
					Parameters.Vertexs, Parameters.Edges);		

		return lpModel;
	}
	
	/**
	 * Generate a default lp model
	 * @throws LpSolveException 
	 * @throws IloException 
	 * 
	 * */
	public static WNLpModelCEPLEX LpModelFromGraph(
											double SHARED_EDGE_PONDERATION,boolean STRONG_SHARED_EDGE ,int AMOUNT_OF_VERTEX, int AMOUNT_OF_PATH,int MAX_WEIGHT_EDGE, 
											int EDGE_DENSITY,ListenableDirectedWeightedGraph<Vertex,Edge> graph, 
											Vertex[] vertexs, Edge[] edges) 
											throws IOException, IloException
	{
		WNLpModelCEPLEX lpModel = new WNLpModelCEPLEX(SHARED_EDGE_PONDERATION, STRONG_SHARED_EDGE, AMOUNT_OF_VERTEX, AMOUNT_OF_PATH,
									  MAX_WEIGHT_EDGE, EDGE_DENSITY, graph, 
									  vertexs, edges);	
		
		return lpModel;
	}
	
	public static WNLpModelCEPLEX generateLpModel(int amount_of_vertex,int edge_density,int amount_of_path,int max_weight_edge) throws IOException, IloException{
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
		
		WNLpModelCEPLEX model = new WNLpModelCEPLEX(0,false,amount_of_vertex, amount_of_path, max_weight_edge, edge_density, graph, vertexs, edges);
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
	 * @throws IloException 
	 * 
	 * */
	private Pair<Boolean,ListenableDirectedWeightedGraph<Vertex,Edge>> solve(double SHARED_EDGE_PONDERATION, boolean STRONG_SHARED_EDGE) throws IOException, IloException{
		Parameters.report.writeln("Creating LP Model. . .");
			    
        // Create the modeler/solver object
        IloCplex cplex = generateModel(SHARED_EDGE_PONDERATION,STRONG_SHARED_EDGE);
	    
		Parameters.report.writelnGreen("The LP model has been created: ");

		{
		// write model to file
		String prefix = "lpmodel";
	    String suffix = ".lp";
	    
	    File tempFile = File.createTempFile(prefix, suffix);
	    tempFile.deleteOnExit();
	    cplex.exportModel(tempFile.getAbsolutePath());
	    BufferedReader bf = new BufferedReader(new FileReader(tempFile));
	    String ln;
	    while((ln=bf.readLine())!=null)
			Parameters.report.writeln(ln);
		}
		
		Parameters.report.writeln("Solving Lp Model. . .");

	    
	    //Solve lp model			 
    	boolean solverResult = cplex.solve();
    	System.out.println("Solver Result= " + solverResult );
//   Parameters.report.writeString(solveResultHTMLMessage(solverResult));

	      
    	ListenableDirectedWeightedGraph<Vertex,Edge> resultGraph = generateGraphFromSolution(cplex.getValues(edgeVars));
    	  		      
    	  if(solverResult){
		      
		      Parameters.report.writeln("The Lp model was solved: ");
		      // print solution
		      Parameters.report.writeln("Value of objective function: " + cplex.getObjValue());		     		     
		      for (int i = 0; i < edgeVars.length; i++)
		    	  Parameters.report.writeln("Value of var[" + edgeVars[i].getName() + "] = " + cplex.getValue(edgeVars[i]));
		      for (int i = 0; i < pathVars.length; i++)
		    	  Parameters.report.writeln("Value of var[" + pathVars[i].getName() + "] = " + cplex.getValue(pathVars[i]));
		      if(SHARED_EDGE_PONDERATION > 0 && STRONG_SHARED_EDGE)
		    	  for (int i = 0; i < twoPathsVars.length; i++)
		    		  Parameters.report.writeln("Value of var[" + twoPathsVars[i].getName() + "] = " + cplex.getValue(twoPathsVars[i]));
		     
		}	

	      // delete the problem and free memory
	      cplex.end();
	      
	      return new Pair<Boolean,ListenableDirectedWeightedGraph<Vertex,Edge>>(solverResult,resultGraph);
	}
	
	
	/***
	 * 
	 * @param shared_edge_ponderation
	 * @param strong_weighting
	 * @return
	 * @throws IloException 
	 *
	 */
	public IloCplex generateModel(double shared_edge_ponderation, boolean strong_weighting) throws IloException{
		//look for all simple path between node 0 and node 3
		List<GraphPath<Vertex,Edge>> paths = getAllSinglePath(graph, vertexs[0],vertexs[vertexs.length - 1 ]);
		
		//generate an empty lp model
        IloCplex model = new IloCplex();
        
        //generate one variable for each edge
        {
        String[]    varName = new String[edges.length];
        for(int i=0; i<edges.length; i++)
        	varName[i] = edges[i].name;
        edgeVars = model.boolVarArray(edges.length, varName);
        }
        
        
        //generate one variable for each path
        {
    	String[]    varName = new String[paths.size()];    
        for(int i=0; i<paths.size(); i++)
        	varName[i] = "P" + i;
        pathVars = model.boolVarArray(paths.size(), varName);
        }
        
        //if the model penalize for shared edges between any two paths , generate two path variables (like pipj i!=j)
        if(shared_edge_ponderation > 0 && strong_weighting){
	        //create two paths variables
			{
	        LinkedList<String>  varNameList = new LinkedList<String>();
			for(int i=0; i<paths.size(); i++)
				for(int j=i+1; j<paths.size(); j++){
					int sharedEdge = amountSE(paths.get(i), paths.get(j));
					if( sharedEdge > 0){
						varNameList.add("P" + i + "P" + j);
					}
				}
			 String[] varName = varNameList.toArray(new String[varNameList.size()]); 
	         twoPathsVars = model.boolVarArray(varName.length, varName);
            }
        }
        
        //create objetive function
        {
        IloLinearNumExpr fObjetive = model.linearNumExpr();        
        for(int i=0;i<edges.length; i++)
        	fObjetive.addTerm(edges[i].weight, edgeVars[i]);    

		/*
		 * weak weigthting: add terms to lp's target function for each edge like this: shared_edge_ponderation * Ei. 
		 */
        if(shared_edge_ponderation > 0  && !strong_weighting){			
			for(int i=0; i < edgeVars.length; i++){
				fObjetive.addTerm(-shared_edge_ponderation, edgeVars[i] );
			}
		}
        
		/* 
		 * strong weigthting: For each two path pi and pj where pi!=pj,
		 * add variables to lp model pipj which meaning is path pi and pj are both available. 
		 * And add for each of theses new variables terms like shared_edge_ponderation * ShE(pi,pj) * pipj, 
		 * where ShE(pi,pj) is the function that compute the shared edges between two paths.   
		 */
		if(shared_edge_ponderation > 0 && strong_weighting){	        
			int k = 0;
			for(int i=0; i<paths.size(); i++)
				for(int j=i+1; j<paths.size(); j++){
					int sharedEdge = amountSE(paths.get(i), paths.get(j));
					if( sharedEdge > 0){
						fObjetive.addTerm(shared_edge_ponderation * sharedEdge, twoPathsVars[k]);
						k++;
					}
				}
					
		}
        
        model.addMinimize(fObjetive);
        }
        
        /******************************************/
        /***************Constraints****************/
        /******************************************/        
        int constraint = 0;       
        
        //P0 + ... + Pn >= AMOUNT_OF_PATH
        if(paths.size()>0){
	        IloNumExpr expr = model.prod(1,pathVars[0]);
	        for(int i=1; i<paths.size();i++)
	        	expr = model.sum(expr,model.prod(1, pathVars[i]));
			model.addGe(expr,this.AMOUNT_OF_PATH,"c"+constraint); constraint++;
        }
        
        
        for(int i=0;i<paths.size();i++){
        	GraphPath<Vertex,Edge> p = paths.get(i);
        	IloNumExpr expr = model.prod(p.getEdgeList().size(),pathVars[i]);
        	for(Edge e: p.getEdgeList())
        		expr = model.sum(expr, model.prod(-1 ,  edgeVars[getEdgeIndex(e)]));
        	model.addLe(expr,0,"c" + constraint); constraint++;
        }		
        

		//constraint about strong shared edges (if Pi & Pj then PiPj)
		if(shared_edge_ponderation > 0 && strong_weighting){
			int k = 0;
			for(int i=0; i<paths.size(); i++)
				for(int j=i+1; j<paths.size(); j++){
					int sharedEdge = amountSE(paths.get(i), paths.get(j));
					if( sharedEdge > 0){		
						IloNumExpr expr = model.prod(1,pathVars[i]);
						expr = model.sum(expr, model.prod(1 , pathVars[j]));
						expr = model.sum(expr, model.prod(-1 , twoPathsVars[k]));
						model.addLe(expr,1,"c" + constraint); constraint++;
						k++;
					}
				}
		}
		
        return model;
	}
	
	/**
	 * Search edge e in array edges.
	 * The search is performed by reference.
	 * */
	private int getEdgeIndex(Edge e) {
		for(int i=0; i<edges.length;i++)
			if(edges[i] == e)
				return i;
		return -1;
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
	
	public static WNLpModelCEPLEX testLpModel(ListenableDirectedWeightedGraph<Vertex,Edge> graph, int amount_of_path,Vertex[] vertexs, Edge[] edges) throws IOException, IloException{
		return new WNLpModelCEPLEX(0,false,graph.vertexSet().size(), amount_of_path, Integer.MAX_VALUE, 100, graph, vertexs,edges);
	}
	
	private static int amountSE(GraphPath<Vertex,Edge> pi, GraphPath<Vertex,Edge> pj){
		int result = 0;
		for(Edge ePi: pi.getEdgeList() )
			for(Edge ePj: pj.getEdgeList())
				if(ePi.equals(ePj))
					result++;

		return result;
	}

		public static void solveMe() {
			int n = 4; //cargos
	        int m = 3; //compartments
	        double[] p = {310.0, 380.0, 350.0, 285.0}; //profit
	        double[] v = {480.0, 650.0, 580.0, 390.0}; //volume per ton of cargo
	        double[] a = {18.0, 15.0, 23.0, 12.0}; //available weight
	        double[] c = {10.0, 16.0, 8.0}; //capacity of compartment
	        double[] V = {6800.0, 8700.0, 5300.0}; //volume capacity of 
	        try {
	        	// define new model
	        	IloCplex cplex = new IloCplex();
	        	// variables
	        	IloNumVar[][] x = new IloNumVar[n][];
	        	for (int i=0; i<n; i++) {
	        		x[i] = cplex.numVarArray(m, 0, Double.MAX_VALUE);
	        	}
	        	IloNumVar y = cplex.numVar(0,Double.MAX_VALUE);
	        	// expressions
	        	IloLinearNumExpr[] usedWeightCapacity = new IloLinearNumExpr[m];
	        	IloLinearNumExpr[] usedVolumeCapacity = new IloLinearNumExpr[m];
	        	for (int j=0; j<m; j++) {
	        		usedWeightCapacity[j] = cplex.linearNumExpr();
	        		usedVolumeCapacity[j] = cplex.linearNumExpr();
	        		for (int i=0; i<n; i++) {
	        			usedWeightCapacity[j].addTerm(1.0, x[i][j]);
	        			usedVolumeCapacity[j].addTerm(v[i],x[i][j]);
	        		}
	        	}
	        	IloLinearNumExpr objective = cplex.linearNumExpr();
	        	for (int i=0; i<n; i++) {
	        		for (int j=0; j<m; j++) {
	        			objective.addTerm(p[i],x[i][j]);
	        		}
	        	}
	        	// define objective
	        	cplex.addMaximize(objective);
	        	// constraints
	        	for (int i=0; i<n; i++) {
	        		cplex.addLe(cplex.sum(x[i]), a[i]);
	        	}
	        	for (int j=0; j<m; j++) {
	        		cplex.addLe(usedWeightCapacity[j], c[j]);
	        		cplex.addLe(usedVolumeCapacity[j],V[j]);
	        		cplex.addEq(cplex.prod(1/c[j], usedWeightCapacity[j]), y);
	        	}
	        	
//				cplex.setParam(IloCplex.Param.Simplex.Display, 0);
	        	
	        	// solve model
	        	if (cplex.solve()) {
	        		System.out.println("obj = "+cplex.getObjValue());
	        	}
	        	else {
	        		System.out.println("problem not solved");
	        	}
	        	
	        	cplex.end();
	        }
	        catch (IloException exc) {
	        	exc.printStackTrace();
	        }
		}
		
		public static void main(String args[]){
			solveMe();
		}
		
		/**
		 * Computes combinatoria. How many groups of k elements could be formed with n elements. Whithout repetition and whitout permutagtion 
		 * */
		int nOverK(int n, int k) {
			  return fact(n) / (fact(k) * fact(n - k));
		}

		int fact(int n) {
		  if(n<0)
			  throw new IllegalArgumentException("Factorial methods is only defined by non negative integer");
		  int result = 1;
		  if(n == 0 || n == 1 )
			  return result;
			
		  for(int i = n; i > 1; i--)
			  result = result * i;
		  
		  return result;
		}

}


