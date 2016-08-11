package main.lpModel;


import java.io.IOException;
import java.util.List;

import org.jgrapht.graph.ListenableDirectedWeightedGraph;

import lpsolve.LpSolveException;
import main.lpModel.wireNet.WNMetrics;
import main.util.Edge;
import main.util.Metrics;
import main.util.Pair;
import main.util.Vertex;

/*
 * Una posibilidad para que todos extiendan de esta es declarar un ModelWriter y un ComputeMetrics y pasarlos como parametros
 * o pasarle las clases. 
 * */
public abstract class LpModel {
	
//
//	/** Amount of graph's vertexs */
//	public final int AMOUNT_OF_VERTEX;
//	
//	/** Amount of required graph's path from node 0 to node AMOUNT_OF_VERTEX -1 */
//	public final int AMOUNT_OF_PATH;
//	
//	/** Maximum Weight for an edge. The graph's edges will have weight from 1 to MAX_WEIGHT_EDGE inclusively */
//	public final int MAX_WEIGHT_EDGE;
//	
//	/** Density of graph's edges. it will be taken into account for generate a random graph */
//	public final int EDGE_DENSITY;
//	
//	/** The graph for current model */
//	public final ListenableDirectedWeightedGraph<Vertex,Edge> graph;
//	
//	/** The linnear programming model entry graph */
//	public final ListenableDirectedWeightedGraph<Vertex,Edge> resultGraph;
//	
//	/** The entry graph vertexs */
//	public final Vertex[] vertexs;
//	
//	/** The entry graph edges */
//	public final Edge[] edges;
//
//	/** The entry graph metrics */
//	public final Metrics graphMetrics;
//	
//	/** The result graph metrics */
//	public final Metrics resultMetrics;
//	
//	
//	protected LpModel(
//			int AMOUNT_OF_VERTEX, int AMOUNT_OF_PATH,int MAX_WEIGHT_EDGE, 
//			int EDGE_DENSITY,ListenableDirectedWeightedGraph<Vertex,Edge> graph, 
//			Vertex[] vertexs, Edge[] edges, List<Object> solverArgs) throws IOException, LpSolveException
//	{
//		this.AMOUNT_OF_VERTEX = AMOUNT_OF_VERTEX;
//		this.AMOUNT_OF_PATH   = AMOUNT_OF_PATH;
//		this.MAX_WEIGHT_EDGE  = MAX_WEIGHT_EDGE; 
//		this.EDGE_DENSITY     = EDGE_DENSITY;
//		this.graph            = graph;
//		this.vertexs          = vertexs;
//		this.edges            = edges;
//		
//		 Pair<Integer,ListenableDirectedWeightedGraph<Vertex,Edge>> solution = solve(solverArgs);
//		 this.resultGraph = solution.b;		 
//		 this.graphMetrics =  calcMetrics(graph, vertexs[0], vertexs[this.AMOUNT_OF_VERTEX-1]);
//		 this.resultMetrics = calcMetrics(resultGraph, vertexs[0], vertexs[this.AMOUNT_OF_VERTEX-1]);
//
//	}
//
//	protected abstract Pair<Integer, ListenableDirectedWeightedGraph<Vertex, Edge>> solve(List<Object> args) throws IOException, LpSolveException;
//	
//	protected abstract Metrics calcMetrics(ListenableDirectedWeightedGraph<Vertex,Edge> g,Vertex source, Vertex target);
	
	protected String solveResultHTMLMessage(int solverResult){
		switch (solverResult){
			case -2: return "<b> <font color=\"red\"> Out of memory </font> </b> <br>";	
			case 0:  return "<b> <font color=\"green\"> An optimal solution was obtained </font> </b> <br>";
			case 1:	 return "<font color=\"red\"> <b>  The model is sub-optimal. Only happens if there are integer variables and there is already an integer solution found. The solution is not guaranteed the most optimal one.</b> <br>"
						    +"    ->  A timeout occured (set via set_timeout or with the -timeout option in lp_solve)<br>"
						    +"    ->  set_break_at_first was called so that the first found integer solution is found (-f option in lp_solve)<br>"
						    +"	  ->  set_break_at_value was called so that when integer solution is found that is better than the specified value that it stops (-o option in lp_solve)<br>"
						    +"	  ->  set_mip_gap was called (-g/-ga/-gr options in lp_solve) to specify a MIP gap<br>"
						    +" 	  ->  An abort function is installed (put_abortfunc) and this function returned TRUE<br>"
						    +"	  ->  t some point not enough memory could not be allocated  </font><br>" ; 
			case 2:	return "<b> <font color=\"red\"> The model is infeasible </font> </b> <br> ";
			case 3: return "<b> <font color=\"red\"> The model is unbounded</font> </b> <br>";
			case 4: return "<b> <font color=\"red\">The model is degenerative</font> </b> <br>";
			case 5: return "<b> <font color=\"red\">Numerical failure encountered</font> </b> <br>";
			case 6: return "<b> <font color=\"red\">The abort routine returned TRUE. See put_abortfunc</font> </b> <br>";
			case 7: return "<b> <font color=\"red\">A timeout occurred. A timeout was set via set_timeout</font> </b> <br>";
			case 9: return "<b> <font color=\"red\">The model could be solved by presolve. This can only happen if presolve is active via set_presolve</font> </b> <br>";
			case 25: return "<b> <font color=\"red\">Accuracy error encountered</font> </b> <br>";
			default: return "";
		}
		
	}
}
