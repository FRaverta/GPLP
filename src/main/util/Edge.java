package main.util;

import org.jgrapht.graph.DefaultWeightedEdge;

public class Edge extends DefaultWeightedEdge {
	
	public final String name;
	
	public final Vertex from;
	
	public final Vertex to;
	
	public final int weight;
	
	/** Limits availability of component represented by current edge. Its a probability measure, so it's value belong [0,1]*/
	public final double availability;
	
	/** The amount of data that support current edge in some time intervals 
	 * 	In DTN, the amount of data that support current edge.
	 */
	public final int capacity;


	public Edge(String name,Vertex from, Vertex to, int weight){		
		this(name, from, to, weight,Integer.MAX_VALUE,(weight>0)?1:0.5);
	}
	
	public Edge(String name, Vertex from, Vertex to, int weight, int capacity, double availability) {
		this.name = name;
		this.from = from;
		this.to = to;
		this.weight = weight;
		this.capacity = capacity;
		this.availability = availability;
	}
	
	public Edge(String name, Vertex from, Vertex to, int weight, int capacity) {
		this(name, from, to, weight,capacity,(weight>0)?1:0.5);
	}
	
	
	public String toString(){
		return "["+name + ",f:" + from + ",t:" + to + ",w:" + weight+",a:" + availability + "]";
	}
}
