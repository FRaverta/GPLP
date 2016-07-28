//package main.lpModel;
//
//
//import org.jgrapht.graph.ListenableDirectedWeightedGraph;
//import main.lpModel.wireNet.Metrics;
//import main.util.Edge;
//import main.util.Vertex;
//
///*
// * Una posibilidad para que todos extiendan de esta es declarar un ModelWriter y un ComputeMetrics y pasarlos como parametros
// * o pasarle las clases. 
// * */
//public interface LpModel {
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
//}
