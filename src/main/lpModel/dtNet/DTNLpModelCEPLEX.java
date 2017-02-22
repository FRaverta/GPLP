package main.lpModel.dtNet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.AllDirectedPaths;
import org.jgrapht.graph.GraphPathImpl;
import org.jgrapht.graph.ListenableDirectedWeightedGraph;
import org.jgrapht.graph.ListenableUndirectedWeightedGraph;

import ilog.concert.IloException;
import ilog.concert.IloIntExpr;
import ilog.concert.IloIntVar;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import ilog.concert.IloNumVarType;
import ilog.cplex.IloCplex;
import lpsolve.LpSolve;
import lpsolve.LpSolveException;
import main.Parameters;
import main.lpModel.LpModel;
import main.lpModel.dtNet.dtnParser.parser;
import main.lpModel.wireNet.WNMetrics;
import main.lpModel.wireNet.WNLpModel;
import main.util.AllSimplePathsUG;
import main.util.Edge;
import main.util.Pair;
import main.util.Vertex;
import main.util.Availabity.Expr;
import main.util.Availabity.ExprAnd;
import main.util.Availabity.ExprOr;
import main.util.Availabity.ExprVar;
import main.util.XMLGraphParser.GraphXMLExporter;

public class DTNLpModelCEPLEX extends LpModel{

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
	public final ListenableUndirectedWeightedGraph<Vertex,Edge> graph;
	
	/** The linnear programming model entry graph */
	public final ListenableDirectedWeightedGraph<Vertex,Edge> resultGraph;
	
	/** The entry graph vertexs */
	public final Vertex[] vertexs;
	
	/** The entry graph edges */
	public final Edge[] edges;
	
	/** The entry graph edges */
	public Edge[] interNodeEdges;

	/** The entry graph metrics */
//	public final DTNMetrics graphMetrics;
	
	/** The result graph metrics */
	public final DTNMetrics resultMetrics;

	/** Amount of allow shared edges between two paths */
	public final int MAX_SHARED_EDGES;
	
	/** Variables of current lp model */
	private IloIntVar[] edgeVars;
	private IloIntVar[] pathVars;		
	private IloIntVar[] twoPathsVars;
	private IloNumVar[] availabilityVars;
	private IloIntVar[] configurationVars;

	private DTNLpModelCEPLEX(
			int AMOUNT_OF_VERTEX, int AMOUNT_OF_PATH,int REQUIRED_CAPACITY, int MAX_SHARED_EDGES, 
			int MAX_WEIGHT_EDGE, int EDGE_DENSITY,ListenableUndirectedWeightedGraph<Vertex,Edge> graph, 
			Vertex[] vertexs, Edge[] edges) throws IOException, LpSolveException, IloException
	{
		this.AMOUNT_OF_VERTEX  = AMOUNT_OF_VERTEX;
		this.AMOUNT_OF_PATH    = AMOUNT_OF_PATH;
		this.REQUIRED_CAPACITY = REQUIRED_CAPACITY;
		this.MAX_SHARED_EDGES  = MAX_SHARED_EDGES;
		this.MAX_WEIGHT_EDGE   = MAX_WEIGHT_EDGE; 
		this.EDGE_DENSITY      = EDGE_DENSITY;
		this.graph             = graph;
		this.vertexs           = vertexs;
		this.edges             = edges;
		
		 Pair<Boolean, ListenableDirectedWeightedGraph<Vertex, Edge>> solution = solve();
		 this.resultGraph = solution.b;		 
//		 this.graphMetrics =  new DTNMetrics(graph, vertexs[0], vertexs[this.AMOUNT_OF_VERTEX-1],REQUIRED_CAPACITY);
		 this.resultMetrics = new DTNMetrics(resultGraph, vertexs[0], vertexs[this.AMOUNT_OF_VERTEX-1],REQUIRED_CAPACITY);

	}


	public static DTNLpModelCEPLEX generateFromFile(String path, int requiredPaths, int requiredCapacity) throws Exception{
		DTNetwork net = parser.parseFromFile(path);
		ListenableUndirectedWeightedGraph<Vertex, Edge> graph = net.toListenableUndirectedWeightedGraph();
	
		DTNLpModelCEPLEX model = new DTNLpModelCEPLEX(
										net.AMOUNT_OF_NODES * net.AMOUNT_OF_INTERVALS + 1, requiredPaths,requiredCapacity,Integer.MAX_VALUE,
										0, 0 , graph, net.vertexs, net.edges
										);
		return model;
	}
	
	
	/**
	 * Generate lp model
	 * @throws LpSolveException 
	 * @throws IloException 
	 * 
	 * */
	public static DTNLpModelCEPLEX modelFromGraph(
											int AMOUNT_OF_VERTEX, int AMOUNT_OF_PATH,int REQUIRED_CAPACITY, 
											int MAX_SHARED_EDGES,ListenableUndirectedWeightedGraph<Vertex,Edge> graph, 
											Vertex[] vertexs, Edge[] edges) throws IOException, LpSolveException, IloException
	{
		DTNLpModelCEPLEX lpModel = new DTNLpModelCEPLEX(AMOUNT_OF_VERTEX,AMOUNT_OF_PATH,REQUIRED_CAPACITY,MAX_SHARED_EDGES,-1,-1,graph,vertexs,edges);	
		
		
		return lpModel;
	}
	
	private Pair<Boolean,ListenableDirectedWeightedGraph<Vertex,Edge>> solve() throws IOException, IloException{
		Parameters.report.writeln("Creating LP Model. . .");
			    
        // Create the modeler/solver object
        IloCplex cplex = generateModel();
	    
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
//		      for (int i = 0; i < pathVars.length; i++)
//		    	  Parameters.report.writeln("Value of var[" + pathVars[i].getName() + "] = " + cplex.getValue(pathVars[i]));
		     
		}	

	      // delete the problem and free memory
	      cplex.end();
	      
	      return new Pair<Boolean,ListenableDirectedWeightedGraph<Vertex,Edge>>(solverResult,resultGraph);
	}

	
	
	
	public IloCplex generateModel() throws IloException{
		//generate an empty lp model
        IloCplex model = new IloCplex();
        
        //TODO: Solo seria para nodos en k=0 y q no sea target. Now it is harcode
        //generate one variable for each n in {source} to target. 
        //for indicate the availability of path from each source node 
        //Where source={vertexs[0],...,vertexs[AMOUNT_OF_VERTEX - 2]} y target = vertexs[AMOUNT_OF_VERTEX - 1]
        {
        String[]    varName = new String[3];
        for(int i=0; i<3; i++)
        	varName[i] = "a" + vertexs[i].name + vertexs[vertexs.length-1].name;
//        	varName[i] = "a" + i + (AMOUNT_OF_VERTEX-1);

        availabilityVars = model.numVarArray(varName.length, 0.0, 1.0, IloNumVarType.Float, varName);
        }
        //generate one variable for each edge
        {
        int interNodeLinks=0;        
        for(Edge e: edges)
        	if(e.isInterNodeLink())
        		interNodeLinks++;
       
        String[]    varName = new String[interNodeLinks];        
        interNodeEdges = new Edge[interNodeLinks];
        int j=0;
        for(int i=0; i<edges.length; i++)
        	if(edges[i].isInterNodeLink()){
        		varName[j] = edges[i].name; edges[i].varIndex = j; interNodeEdges[j]=edges[i];j++; 
        	}
        edgeVars = model.boolVarArray(interNodeLinks, varName);
        }
        
        //generate one variable for each configuration(set of simultaneous enabled edges)
    	LinkedList<Edge> edgeList = new LinkedList<Edge>(Arrays.asList(interNodeEdges));
    	LinkedList<LinkedList> configurations = getCombination(edgeList);
        {
        String[]    varName = new String[configurations.size()];        		
        for(int i=0; i<configurations.size(); i++)
        	varName[i] = "c" + i;
        configurationVars = model.boolVarArray(configurations.size(), varName);   
        }
        
        //create objetive function
        {
        IloLinearNumExpr fObjetive = model.linearNumExpr();        
        for(int i=0;i<availabilityVars.length; i++)
        	fObjetive.addTerm(1/this.AMOUNT_OF_VERTEX - 1, availabilityVars[i]);    
        }
        
        /******************************************/
        /***************Constraints****************/
        /******************************************/        
        int constraint = 0;       
       

    //Constraint about configuration
    //ci - ei: ei in ci.edges = 0  
        
    for(LinkedList<Edge> c: configurations){
    	 IloNumExpr expr = model.prod(c.size(), configurationVars[configurations.indexOf(c)]);
    	 for(Edge e: c){
    		 expr = model.sum(expr,model.prod(-1,edgeVars[getEdgeIndex(e)]));
    	 }
		model.addLe(expr,0,"c"+constraint); constraint++;
    }
   
    //Only one configuration is enabled
    {
    IloNumExpr expr = model.numExpr();
    for(int i=1; i<configurationVars.length;i++)
    	expr = model.sum(expr,configurationVars[i]);
	model.addEq(expr,1,"c"+constraint); constraint++;
    }
    
	//dit - Availability(i,t,c0) * c0 -...- Availability(i,t,cm) cm = 0
    {
	    for(int f = 0; f < availabilityVars.length; f++){	
	    	IloNumExpr expr = model.prod(1,availabilityVars[f]);    	
	    	for(int i=0; i<configurations.size();i++)
	   		 	expr = model.sum(expr,model.prod(-computeAvailability(f,vertexs[AMOUNT_OF_VERTEX-1],configurations.get(i)),configurationVars[i]));
	    	
			model.addEq(expr,0,"c"+constraint); constraint++;
	    }
    }
	
	//Only one edge per node can be enable
    {
    	//TODO- FIX THIS HORRIBLE CODE
	    for(Vertex v: vertexs){		    	
	    	Set<Edge> auxSet = graph.edgesOf(v);
	    	List<Edge> vEdges = new LinkedList<Edge>(); 
	    	for(Edge e: auxSet)
	    		if(e.isInterNodeLink())
	    			vEdges.add(e);
	    	if(vEdges.size() >= 2){
		    	IloNumExpr expr = model.numExpr();
		    	for(Edge e: vEdges)
		    		expr = model.sum(expr,edgeVars[getEdgeIndex(e)]);
		    	model.addLe(expr,1,"c"+constraint); constraint++;
	    	}
	    }
    }
    
	System.out.println(model.toString());
    return model;
}
	
	/**
	 * This method computes the one available path probability from node vertexs[f]  to vertexs[vertexs.lenght -1]
	 * @param int f - index of node source in vertexs array
	 * @param LinkedList<Edge> configurations - list of all enabled edges in current configuration
	 * */
	private double computeAvailability(int f, Vertex vertex, LinkedList<Edge> configurations) {
		ListenableUndirectedWeightedGraph<Vertex,Edge> confGraph = new ListenableUndirectedWeightedGraph<Vertex,Edge> (Edge.class);
		
		for(Vertex v: vertexs)
			confGraph.addVertex(v);
		
		for(Edge e: edges)
			if(!e.isInterNodeLink())
				confGraph.addEdge(e.from, e.to,e);
		
		for(Edge e: configurations)
			confGraph.addEdge(e.from, e.to,e);
		
		
		//look for all simple path between node 0 and node 3
		List<GraphPath<Vertex,Edge>> paths = getAllSinglePath(confGraph, vertexs[0],vertexs[vertexs.length - 1 ]);
		List<Expr> disyungendos = new LinkedList<Expr>();
		for(GraphPath<Vertex,Edge> p:paths)
			disyungendos.add(genExprAndForAllEdges(p.getEdgeList()));
		
		
		//generate a Expr Or(e: e was generated in the above step)
		Expr orConfigurations = new ExprOr(disyungendos);
		
		return DTNMetrics.H(orConfigurations);

	}


	/**
	 * Given a graph g, this method return an ExprAnd where the coyuntos are g.edges
	 * */
	private ExprAnd genExprAndForAllEdges(List<Edge> atoms){
		List<Expr> coyuntos = new LinkedList<Expr>();
		for(Edge e:atoms)
			coyuntos.add(new ExprVar(e.name,e.availability));
		
		ExprAnd andExpr = new ExprAnd(coyuntos);
		return andExpr;
	}

	private String getOutflow(Vertex vertex) {
		StringBuilder st = new StringBuilder();
		
		for(Edge e: edges)
			if(e.from.equals(vertex))
				st.append(" +c" + e.name );
		
		return (st.toString().isEmpty())? " 0": st.toString();			
	}
	
	private String getInflow(Vertex vertex) {
		StringBuilder st = new StringBuilder();
		
		for(Edge e: edges)
			if(e.to.equals(vertex))
				st.append(" +c" + e.name );
		
		return (st.toString().isEmpty())? " 0": st.toString();
			
	}





	public static List<GraphPath<Vertex, Edge>> getAllSinglePath(ListenableUndirectedWeightedGraph<Vertex,Edge> graph, Vertex source, Vertex target){
		AllSimplePathsUG<Vertex,Edge> pathFinder = new AllSimplePathsUG<Vertex,Edge>(graph);
		List<GraphPath<Vertex, Edge>> paths = pathFinder.computePaths(source,target);
		
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
	
//	public static void main(String[] args) throws LpSolveException, IOException{		
//		Parameters.report.writeln("Creating LP Model\n. . .");
//		
//		String prefix = "lpmodel";
//	    String suffix = ".tmp";
//	    
//	    File tempFile = File.createTempFile(prefix, suffix);
//	    tempFile.deleteOnExit();
//	    
//	    FileWriter writer = new FileWriter(tempFile);
//	    
//		StringBuilder st = new StringBuilder();
//		
//		st.append("min: +x0 +x1 +x2 +x3 ;\n");
//		
//
//		
//		int constraintNumber=1;
//		
//		//amount of path constraint
//		st.append("r_" + constraintNumber + ": 1 <= +x0 +x1 +x2 +x3 ;\n"); constraintNumber++;
//		st.append("\n" + "bin x0, x1, x2, x3 ;");
//		
//		System.out.println(st.toString());
//	    String stringLpModel = st.toString(); 
//	    writer.append(stringLpModel);
//	    
//		Parameters.report.writeln("The LP model has been created: ");
//		Parameters.report.writeln(stringLpModel);
//	    
//	    writer.flush();
//	    writer.close();
//
//		Parameters.report.writeln("Solving Lp Model\n. . .");
//
//	    
//	    //Solve lp model	
//    	LpSolve solver = LpSolve.readLp("/home/nando/Desktop/a.lp",1,"");
////    	solver.strSetObjFn("0.1");
////    	int solverResult = solver.solve();
////    	System.out.println("Solver Result= " + solverResult ) ;
//
////    	solverResult = solver.solve();
////    	System.out.println("Solver Result= " + solverResult ) ;
////    	
////    	solverResult = solver.solve();
////    	System.out.println("Solver Result= " + solverResult ) ;
//	      	      
//
//	      
//	      Parameters.report.writeln("The Lp model was solved: ");
//	      // print solution
//	      for(int j=0; j<10; j++){
//	    	  System.out.println("***************************** Solution " + j +" *****************************");
//	    	  int solverResult = solver.solve();
//	    	  System.out.println("Solver Result= " + solverResult ) ;
//	    	  System.out.println("Value of objective function: " + solver.getObjective());
//	      	double[] var = solver.getPtrVariables();		     
//	      	for (int i = 0; i < var.length; i++) {
//	      		System.out.println("Value of var[" + solver.getColName(i+1) + "] = " + var[i]);
//	      	}
//	      	System.out.println("*****************************************************************************");
//	      }
//
//	      // delete the problem and free memory
//	      solver.deleteLp();
//		
//	}
	
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
	
	/**
	 * Given a list of elements compute all possible combination of those elements without repetition neither order.
	 * Eg: [A,B,C] = [[A],[B],[C],[A,B],[A,C],[B,C],[A,B,C]] 
	 * */
	private static LinkedList<LinkedList> getCombination(LinkedList  s){
		if(s.isEmpty()){
			LinkedList<LinkedList> aux = new LinkedList<LinkedList>();	aux.add(new LinkedList<LinkedList>()); 
			return aux;
		}else{
			LinkedList<LinkedList> ss = (LinkedList<LinkedList>) s.clone();
			Object o=  ss.pop(); 
			LinkedList<LinkedList> recResult = getCombination(ss);
			LinkedList<LinkedList> left = concat(o,recResult);
			LinkedList<LinkedList> result = new LinkedList<LinkedList>();
			result.addAll(left);result.addAll(recResult);
			return result;
		}	
	}
	

	/**
	 * This method append an element o to head of each element of combination argument.
	 * It is equivalent to (map : o) 
	 * 
	 * eg: concat(A , [[BC],[B],[C],[]]) =  [[ABC],[AB],[AC],[A]].  
	 * 
	 * */
	private static LinkedList<LinkedList> concat(Object o,LinkedList<LinkedList> combination) {
		LinkedList<LinkedList> result = new LinkedList<LinkedList>();
		for(LinkedList l: combination){
			LinkedList aux = new LinkedList();
			aux.add(o);
			aux.addAll(l);
			result.add(aux);
		}	
		
		return result;		
	}
	
	public ListenableDirectedWeightedGraph<Vertex,Edge> generateGraphFromSolution(double[] solution){
		 ListenableDirectedWeightedGraph<Vertex,Edge> resultGraph = new ListenableDirectedWeightedGraph<Vertex,Edge>(Edge.class);		
		
		for(int i=0; i<vertexs.length; i++)
			resultGraph.addVertex(vertexs[i]);
		
		for(Edge e: edges)
			if(!e.isInterNodeLink())
				resultGraph.addEdge(e.from, e.to,e);
		
		for(int i=0; i<interNodeEdges.length; i++)
			if(solution[i] == 1.00 ){
				resultGraph.addEdge(interNodeEdges[i].from,interNodeEdges[i].to, interNodeEdges[i]);
				resultGraph.setEdgeWeight(interNodeEdges[i], interNodeEdges[i].weight);
			}
		
		return resultGraph;
	}
	
	/**
	 * Search edge e in array edges.
	 * The search is performed by reference.
	 * */
	private int getEdgeIndex(Edge e) {
		for(int i=0; i<edges.length;i++)
			if(interNodeEdges[i] == e)
				return i;
		return -1;
	}
	
	public static void main(String args[]) throws Exception{
		 DTNetwork dtn = parser.parseFromFile("/home/nando/development/Doctorado/Ej1/Examples/DTNExamples/DTN5");
		 ListenableUndirectedWeightedGraph<Vertex,Edge> g = dtn.toListenableUndirectedWeightedGraph();
		 System.out.println(g.toString());
		

		DTNLpModelCEPLEX lpModel = new DTNLpModelCEPLEX(dtn.vertexs.length, 0, 0, 0, 0, 0, g, dtn.vertexs, dtn.edges);
		System.out.println(lpModel.resultGraph.toString());
		File f = new File("/home/nando/Desktop/graph1.xml");
		GraphXMLExporter.generateGraphmlFromGraph(lpModel.resultGraph, f);
	}
}
