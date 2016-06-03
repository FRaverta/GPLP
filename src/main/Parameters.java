package main;

import org.jgrapht.graph.ListenableDirectedWeightedGraph;

public final class Parameters {
	
	/** Default example parameters */
	public static final int EDGE_DENSITY = 50;
	public static final int AMOUNT_OF_NODES = 4;
	public static final int AMOUNT_OF_REQUIRED_PATHS = 2;
	public static final int MAX_WEIGHT_EDGE = 2;
	public static final Edge[] Edges = new Edge[5];
	public static final Vertex[] Vertexs = new Vertex[AMOUNT_OF_NODES];
	public static final ListenableDirectedWeightedGraph<Vertex,Edge> DEFAULT_GRAPH = createDefaultGraph();
	
	/** Example1 example parameters */
	public static final int E1_EDGE_DENSITY = 50;
	public static final int E1_AMOUNT_OF_NODES = 14;
	public static final int E1_AMOUNT_OF_REQUIRED_PATHS = 2;
	public static final int E1_MAX_WEIGHT_EDGE = 4;
	public static final Edge[] E1_Edges = new Edge[18];
	public static final Vertex[] E1_Vertexs = new Vertex[E1_AMOUNT_OF_NODES];
	public static final ListenableDirectedWeightedGraph<Vertex,Edge> E1_GRAPH = createE1Graph();

	/** Example2 example parameters */
	public static final int E2_EDGE_DENSITY = 50;
	public static final int E2_AMOUNT_OF_NODES = 5;
	public static final int E2_AMOUNT_OF_REQUIRED_PATHS = 2;
	public static final int E2_MAX_WEIGHT_EDGE = 4;
	public static final Edge[] E2_Edges = new Edge[7];
	public static final Vertex[] E2_Vertexs = new Vertex[E2_AMOUNT_OF_NODES];
	public static final ListenableDirectedWeightedGraph<Vertex,Edge> E2_GRAPH = createE2Graph();
	
	/*Report text*/
	public static final Report report = new Report();
	
	public static void ready(){}
	
	private static ListenableDirectedWeightedGraph<Vertex, Edge> createDefaultGraph() {
		ListenableDirectedWeightedGraph<Vertex, Edge> graph = new ListenableDirectedWeightedGraph<Vertex,Edge>(Edge.class);
		
		for(int i=0; i<AMOUNT_OF_NODES; i++){
			Vertexs[i] = new Vertex("V" + i);
			graph.addVertex(Vertexs[i]);
		}
		
		Edges[0] = new Edge("E0",Vertexs[0],Vertexs[2],2);
		Edges[1] = new Edge("E1",Vertexs[0],Vertexs[1],2);
		Edges[2] = new Edge("E2",Vertexs[1],Vertexs[2],1);
		Edges[3] = new Edge("E3",Vertexs[1],Vertexs[3],2);
		Edges[4] = new Edge("E4",Vertexs[2],Vertexs[3],2);
		
		graph.addEdge(Vertexs[0],Vertexs[2],Edges[0]);
		graph.addEdge(Vertexs[0],Vertexs[1],Edges[1]);
		graph.addEdge(Vertexs[1],Vertexs[2],Edges[2]);
		graph.addEdge(Vertexs[1],Vertexs[3],Edges[3]);
		graph.addEdge(Vertexs[2],Vertexs[3],Edges[4]);

		
		graph.setEdgeWeight(Edges[0], Edges[0].weight);
		graph.setEdgeWeight(Edges[1], Edges[1].weight);
		graph.setEdgeWeight(Edges[2], Edges[2].weight);
		graph.setEdgeWeight(Edges[3], Edges[3].weight);
		graph.setEdgeWeight(Edges[4], Edges[4].weight);
		
		return graph;

	}
	
	private static ListenableDirectedWeightedGraph<Vertex, Edge> createE1Graph() {
		ListenableDirectedWeightedGraph<Vertex, Edge> graph = new ListenableDirectedWeightedGraph<Vertex,Edge>(Edge.class);
		
		for(int i=0; i<E1_AMOUNT_OF_NODES; i++){
			E1_Vertexs[i] = new Vertex("V" + i);
			graph.addVertex(E1_Vertexs[i]);
		}
//nivel-0		
		E1_Edges[0] = new Edge("E0",E1_Vertexs[0],E1_Vertexs[1],4);
		E1_Edges[1] = new Edge("E1",E1_Vertexs[0],E1_Vertexs[2],4);
		E1_Edges[2] = new Edge("E2",E1_Vertexs[0],E1_Vertexs[3],4);
//level 1		
		E1_Edges[3] = new Edge("E3",E1_Vertexs[1],E1_Vertexs[4],4);
		E1_Edges[4] = new Edge("E4",E1_Vertexs[1],E1_Vertexs[5],4);

		E1_Edges[5] = new Edge("E5",E1_Vertexs[2],E1_Vertexs[6],4);
		E1_Edges[6] = new Edge("E6",E1_Vertexs[2],E1_Vertexs[7],4);
		
		E1_Edges[7] = new Edge("E7",E1_Vertexs[3],E1_Vertexs[8],4);
		E1_Edges[8] = new Edge("E8",E1_Vertexs[3],E1_Vertexs[9],4);
//level 2
		E1_Edges[9] = new Edge("E9",E1_Vertexs[4],E1_Vertexs[10],4);
		E1_Edges[10] = new Edge("E10",E1_Vertexs[5],E1_Vertexs[10],4);

		E1_Edges[11] = new Edge("E11",E1_Vertexs[6],E1_Vertexs[11],4);		
		E1_Edges[12] = new Edge("E12",E1_Vertexs[7],E1_Vertexs[11],4);
		
		E1_Edges[13] = new Edge("E13",E1_Vertexs[8],E1_Vertexs[12],4);		
		E1_Edges[14] = new Edge("E14",E1_Vertexs[9],E1_Vertexs[12],4);
//level 3
		E1_Edges[15] = new Edge("E15",E1_Vertexs[10],E1_Vertexs[13],4);		
		E1_Edges[16] = new Edge("E16",E1_Vertexs[11],E1_Vertexs[13],4);
		E1_Edges[17] = new Edge("E17",E1_Vertexs[12],E1_Vertexs[13],4);

		for(int i=0; i<E1_Edges.length; i++){
			graph.addEdge(E1_Edges[i].from,E1_Edges[i].to,E1_Edges[i]);	
			graph.setEdgeWeight(E1_Edges[i], E1_Edges[i].weight);
		}
		
		
		return graph;

	}
	
	private static ListenableDirectedWeightedGraph<Vertex, Edge> createE2Graph() {
		ListenableDirectedWeightedGraph<Vertex, Edge> graph = new ListenableDirectedWeightedGraph<Vertex,Edge>(Edge.class);
		
		for(int i=0; i<E2_Vertexs.length; i++){
			E2_Vertexs[i] = new Vertex("V"+i);
			graph.addVertex(E2_Vertexs[i]);
		}
		
		E2_Edges[0] = new Edge("E0",E2_Vertexs[0],E2_Vertexs[1],6);
		E2_Edges[1] = new Edge("E1",E2_Vertexs[1],E2_Vertexs[3],5);
		E2_Edges[2] = new Edge("E2",E2_Vertexs[3],E2_Vertexs[4],5);
		E2_Edges[3] = new Edge("E3",E2_Vertexs[2],E2_Vertexs[4],9);
		E2_Edges[4] = new Edge("E4",E2_Vertexs[0],E2_Vertexs[2],3);
		E2_Edges[5] = new Edge("E5",E2_Vertexs[0],E2_Vertexs[4],12);
		E2_Edges[6] = new Edge("E6",E2_Vertexs[2],E2_Vertexs[1],2);
		
		for(int i=0; i<E2_Edges.length; i++){
			graph.addEdge(E2_Edges[i].from,E2_Edges[i].to,E2_Edges[i]);	
			graph.setEdgeWeight(E2_Edges[i], E2_Edges[i].weight);
		}
		
		return graph;

	}



}
