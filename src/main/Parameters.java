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
	public static final double SHARED_EDGE_PONDERATION = 0;
	
	/** Example1 example parameters */
	public static final int E1_EDGE_DENSITY = 50;
	public static final int E1_AMOUNT_OF_NODES = 14;
	public static final int E1_AMOUNT_OF_REQUIRED_PATHS = 3;
	public static final int E1_MAX_WEIGHT_EDGE = 4;
	public static final Edge[] E1_Edges = new Edge[18];
	public static final Vertex[] E1_Vertexs = new Vertex[E1_AMOUNT_OF_NODES];
	public static final ListenableDirectedWeightedGraph<Vertex,Edge> E1_GRAPH = createE1Graph();
	public static final double E1_SHARED_EDGE_PONDERATION = 10;
	
	/** Example2 example parameters */
	public static final int E2_EDGE_DENSITY = 50;
	public static final int E2_AMOUNT_OF_NODES = 5;
	public static final int E2_AMOUNT_OF_REQUIRED_PATHS = 2;
	public static final int E2_MAX_WEIGHT_EDGE = 4;
	public static final Edge[] E2_Edges = new Edge[7];
	public static final Vertex[] E2_Vertexs = new Vertex[E2_AMOUNT_OF_NODES];
	public static final ListenableDirectedWeightedGraph<Vertex,Edge> E2_GRAPH = createE2Graph();
	public static final double E2_SHARED_EDGE_PONDERATION = 0.5;
	

	/** Example2 example parameters */
	public static final int E3_EDGE_DENSITY = 50;
	public static final int E3_AMOUNT_OF_NODES = 20;
	public static final int E3_AMOUNT_OF_REQUIRED_PATHS = 4;
	public static final int E3_MAX_WEIGHT_EDGE = 4;
	public static final Edge[] E3_Edges = new Edge[36];
	public static final Vertex[] E3_Vertexs = new Vertex[E3_AMOUNT_OF_NODES];
	public static final ListenableDirectedWeightedGraph<Vertex,Edge> E3_GRAPH = createE3Graph();
	public static final double E3_SHARED_EDGE_PONDERATION = 0.5;

	
	
	
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
	
	private static ListenableDirectedWeightedGraph<Vertex, Edge> createE3Graph() {
		ListenableDirectedWeightedGraph<Vertex, Edge> graph = new ListenableDirectedWeightedGraph<Vertex,Edge>(Edge.class);
		
		for(int i=0; i<E3_AMOUNT_OF_NODES; i++){
			E3_Vertexs[i] = new Vertex("V" + i);
			graph.addVertex(E3_Vertexs[i]);
		}
//nivel-0		
		E3_Edges[0] = new Edge("E0",E3_Vertexs[0],E3_Vertexs[1],4);
		E3_Edges[1] = new Edge("E1",E3_Vertexs[0],E3_Vertexs[2],4);
		E3_Edges[2] = new Edge("E2",E3_Vertexs[0],E3_Vertexs[3],4);
//level 1		
		E3_Edges[3] = new Edge("E3",E3_Vertexs[1],E3_Vertexs[4],4);
		E3_Edges[4] = new Edge("E4",E3_Vertexs[1],E3_Vertexs[5],4);
		E3_Edges[5] = new Edge("E5",E3_Vertexs[1],E3_Vertexs[6],4);

		E3_Edges[6] = new Edge("E6",E3_Vertexs[2],E3_Vertexs[7],4);
		E3_Edges[7] = new Edge("E7",E3_Vertexs[2],E3_Vertexs[8],4);
		E3_Edges[8] = new Edge("E8",E3_Vertexs[2],E3_Vertexs[9],4);
		
		E3_Edges[9] = new Edge("E9",E3_Vertexs[3],E3_Vertexs[10],4);
		E3_Edges[10] = new Edge("E10",E3_Vertexs[3],E3_Vertexs[11],4);
		E3_Edges[11] = new Edge("E11",E3_Vertexs[3],E3_Vertexs[12],4);
//level 2
		E3_Edges[12] = new Edge("E12",E3_Vertexs[4],E3_Vertexs[13],4);
		E3_Edges[13] = new Edge("E13",E3_Vertexs[4],E3_Vertexs[14],4);
		
		E3_Edges[14] = new Edge("E14",E3_Vertexs[5],E3_Vertexs[13],4);
		E3_Edges[15] = new Edge("E15",E3_Vertexs[5],E3_Vertexs[14],4);
		
		E3_Edges[16] = new Edge("E16",E3_Vertexs[6],E3_Vertexs[13],4);
		E3_Edges[17] = new Edge("E17",E3_Vertexs[6],E3_Vertexs[14],4);
		//
		E3_Edges[18] = new Edge("E18",E3_Vertexs[7],E3_Vertexs[15],4);
		E3_Edges[19] = new Edge("E19",E3_Vertexs[7],E3_Vertexs[16],4);

		E3_Edges[20] = new Edge("E20",E3_Vertexs[8],E3_Vertexs[15],4);
		E3_Edges[21] = new Edge("E21",E3_Vertexs[8],E3_Vertexs[16],4);
		
		E3_Edges[22] = new Edge("E22",E3_Vertexs[9],E3_Vertexs[15],4);
		E3_Edges[23] = new Edge("E23",E3_Vertexs[9],E3_Vertexs[16],4);

		//		
		E3_Edges[24] = new Edge("E24",E3_Vertexs[10],E3_Vertexs[17],4);
		E3_Edges[25] = new Edge("E25",E3_Vertexs[10],E3_Vertexs[18],4);

		E3_Edges[26] = new Edge("E26",E3_Vertexs[11],E3_Vertexs[17],4);
		E3_Edges[27] = new Edge("E27",E3_Vertexs[11],E3_Vertexs[18],4);

		E3_Edges[28] = new Edge("E28",E3_Vertexs[12],E3_Vertexs[17],4);
		E3_Edges[29] = new Edge("E29",E3_Vertexs[12],E3_Vertexs[18],4);

//level 3
		E3_Edges[30] = new Edge("E30",E3_Vertexs[13],E3_Vertexs[19],4);		
		E3_Edges[31] = new Edge("E31",E3_Vertexs[14],E3_Vertexs[19],4);
		E3_Edges[32] = new Edge("E32",E3_Vertexs[15],E3_Vertexs[19],4);		
		E3_Edges[33] = new Edge("E33",E3_Vertexs[16],E3_Vertexs[19],4);
		E3_Edges[34] = new Edge("E34",E3_Vertexs[17],E3_Vertexs[19],4);		
		E3_Edges[35] = new Edge("E35",E3_Vertexs[18],E3_Vertexs[19],4);		

		for(int i=0; i<E3_Edges.length; i++){
			graph.addEdge(E3_Edges[i].from,E3_Edges[i].to,E3_Edges[i]);	
			graph.setEdgeWeight(E3_Edges[i], E3_Edges[i].weight);
		}
		
		
		return graph;

	}



}
