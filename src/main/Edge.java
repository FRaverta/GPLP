package main;

import org.jgrapht.graph.DefaultWeightedEdge;

public class Edge extends DefaultWeightedEdge {
	
	public final String name;
	public final Vertex from;
	public final Vertex to;
	public final int weight;
	
	public Edge(String name,Vertex from, Vertex to, int weight){
		this.name = name;
		this.from = from;
		this.to = to;
		this.weight = weight;
	}
	
	public String toString(){
		return "["+name + ",f:" + from + ",t:" + to + ",w:" + weight+"]";
	}
}
