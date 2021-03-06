package main.lpModel.dtNet;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.jgrapht.graph.ListenableDirectedWeightedGraph;
import org.jgrapht.graph.ListenableUndirectedWeightedGraph;

import main.lpModel.dtNet.dtnParser.Field;
import main.lpModel.dtNet.dtnParser.State;
import main.util.Edge;
import main.util.Vertex;

public class DTNetwork {
	
	/** Constat for represent a default availability(non fail probability) for a DTN link */
	private static final double DEFAULT_AVAILABILITY = 0.5;
	
	public final int AMOUNT_OF_NODES;
	public final int AMOUNT_OF_INTERVALS;
	public final List<State> STATES;
	
	//borrar
	public Edge[] edges;
	public Vertex[] vertexs;
	
	public DTNetwork(Integer nodes, Integer intervals, List<State> states) {
		if( nodes == null ||  nodes <= 0 )
			throw new IllegalArgumentException("The amount of nodes should be > 0");
		if( nodes == null || intervals <= 0)
			throw new IllegalArgumentException("The amount of intervals should be > 0");
		if(states == null || states.isEmpty())
			throw new IllegalArgumentException("The amount of states should be > 0");
		else
			if(states.size() < intervals)
				throw new IllegalArgumentException("The amount of described intervals should be " + intervals);
		
		this.AMOUNT_OF_NODES = nodes;
		this.AMOUNT_OF_INTERVALS = intervals;
		this.STATES = Collections.unmodifiableList(states);
	}
	
	public  ListenableDirectedWeightedGraph<Vertex,Edge> toListenableDirectedWeightedGraph(){
		ListenableDirectedWeightedGraph<Vertex,Edge> result = new ListenableDirectedWeightedGraph<Vertex, Edge>(Edge.class);
		Vertex[][] vertexs = new Vertex[AMOUNT_OF_INTERVALS][AMOUNT_OF_NODES];
		List<Edge> edges = new LinkedList<Edge>();
		
		//create vertex
		for(int k=0; k < AMOUNT_OF_INTERVALS; k++)
			for(int i=0; i < AMOUNT_OF_NODES; i++ ){
				vertexs[k][i] = new Vertex("V" + i + "_" + k);
				result.addVertex(vertexs[k][i]);
			}
		//create edges for each described state
		for(int k=0; k < AMOUNT_OF_INTERVALS; k++){
			State s = STATES.get(k);
			for(int i=0; i < AMOUNT_OF_NODES; i++)
				for(int j=0; j < AMOUNT_OF_NODES; j++ )
					if( s.links[i][j].enable ){
						//create an edge with default availability
						Edge e = new Edge("E" + edges.size(),vertexs[k][i],vertexs[k][j], s.links[i][j].weight,STATES.get(k).LENGTH,DEFAULT_AVAILABILITY);
						edges.add(e);
						result.addEdge(e.from, e.to,e);
						result.setEdgeWeight(e, e.weight);
					}
		}
			
		//create inter-state edges EXEPT FOR THE LAST VERTEX
		for(int i=0; i < AMOUNT_OF_NODES -1 ; i++)	
			for(int k=0; k < AMOUNT_OF_INTERVALS-1; k++){
				//create an edge with no limit in capacity and no limit in capacity
				Edge e = new Edge("E" + edges.size(),vertexs[k][i],vertexs[k+1][i], STATES.get(k).LENGTH,Edge.MAX_CAPACITY,1);
				edges.add(e);
				result.addEdge(e.from, e.to,e);
				result.setEdgeWeight(e, e.weight);
			}
		

		Vertex targetVertex = new Vertex("T");
		result.addVertex(targetVertex);
		for(int k=0; k < AMOUNT_OF_INTERVALS; k++){		
			//Create an edge with no limit in capacity an always available because it isn't in the real DTN network (it is a added by representation link )
			Edge e = new Edge("E" + edges.size(),vertexs[k][AMOUNT_OF_NODES-1],targetVertex, 0,Edge.MAX_CAPACITY,1);
			edges.add(e);
			result.addEdge(e.from, e.to,e);
			result.setEdgeWeight(e, e.weight);
		}
							
		
		//borrar
		this.vertexs = new Vertex[AMOUNT_OF_INTERVALS*AMOUNT_OF_NODES+1];
		for(int k=0; k<this.AMOUNT_OF_INTERVALS;k++)
			for(int i=0; i<this.AMOUNT_OF_NODES;i++)
				this.vertexs[k*AMOUNT_OF_NODES + i] = vertexs[k][i];
		
		this.vertexs[this.vertexs.length -1 ] = targetVertex;
		
		this.edges = new Edge[edges.size()];
		this.edges = edges.toArray(this.edges);
		 
		
		return result;
	}
	
	public  ListenableUndirectedWeightedGraph<Vertex,Edge> toListenableUndirectedWeightedGraph(){
		ListenableUndirectedWeightedGraph<Vertex,Edge> result = new ListenableUndirectedWeightedGraph<Vertex, Edge>(Edge.class);
		Vertex[][] vertexs = new Vertex[AMOUNT_OF_INTERVALS][AMOUNT_OF_NODES];
		List<Edge> edges = new LinkedList<Edge>();
		
		//create vertex
		for(int k=0; k < AMOUNT_OF_INTERVALS; k++)
			for(int i=0; i < AMOUNT_OF_NODES; i++ ){
				vertexs[k][i] = new Vertex("V" + i + "_" + k);
				result.addVertex(vertexs[k][i]);
			}
		//create edges for each described state
		for(int k=0; k < AMOUNT_OF_INTERVALS; k++){
			State s = STATES.get(k);
			for(int i=0; i < AMOUNT_OF_NODES; i++)
				for(int j=0; j < AMOUNT_OF_NODES; j++ )
					if( s.links[i][j].enable ){
						//create an edge with default availability
						Edge e = new Edge("E" + edges.size(),vertexs[k][i],vertexs[k][j], s.links[i][j].weight,STATES.get(k).LENGTH,DEFAULT_AVAILABILITY);
						edges.add(e);
						result.addEdge(e.from, e.to,e);
						result.setEdgeWeight(e, e.weight);
						//TODO: for this moment only disable symetric edge (non add twice). Maybe we need check if (i,j)&&(j,i) are edges from graph
						s.links[j][i] = new Field(0);
					}
		}
			
		//create inter-state edges EXEPT FOR THE LAST VERTEX
		for(int i=0; i < AMOUNT_OF_NODES -1 ; i++)	
			for(int k=0; k < AMOUNT_OF_INTERVALS-1; k++){
				//create an edge with no limit in capacity and no limit in capacity
				Edge e = new Edge("E" + edges.size(),vertexs[k][i],vertexs[k+1][i], STATES.get(k).LENGTH,Edge.MAX_CAPACITY,1);
				edges.add(e);
				result.addEdge(e.from, e.to,e);
				result.setEdgeWeight(e, e.weight);
			}
		

		Vertex targetVertex = new Vertex("T");
		result.addVertex(targetVertex);
		for(int k=0; k < AMOUNT_OF_INTERVALS; k++){		
			//Create an edge with no limit in capacity an always available because it isn't in the real DTN network (it is a added by representation link )
			Edge e = new Edge("E" + edges.size(),vertexs[k][AMOUNT_OF_NODES-1],targetVertex, 0,Edge.MAX_CAPACITY,1);
			edges.add(e);
			result.addEdge(e.from, e.to,e);
			result.setEdgeWeight(e, e.weight);
		}
							
		
		//borrar
		this.vertexs = new Vertex[AMOUNT_OF_INTERVALS*AMOUNT_OF_NODES+1];
		for(int k=0; k<this.AMOUNT_OF_INTERVALS;k++)
			for(int i=0; i<this.AMOUNT_OF_NODES;i++)
				this.vertexs[k*AMOUNT_OF_NODES + i] = vertexs[k][i];
		
		this.vertexs[this.vertexs.length -1 ] = targetVertex;
		
		this.edges = new Edge[edges.size()];
		this.edges = edges.toArray(this.edges);
		 
		
		return result;
	}

}
