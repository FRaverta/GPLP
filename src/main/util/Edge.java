package main.util;

import org.jgrapht.graph.DefaultWeightedEdge;

public class Edge extends DefaultWeightedEdge {
	
	/**Constant that indicates no capacity limit in edge*/
	public static int MAX_CAPACITY = Integer.MAX_VALUE;
	
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
		this(name, from, to, weight,MAX_CAPACITY,1);
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
		this(name, from, to, weight,capacity,1);
	}
	
	
	public String toString(){
		return "["+name + ",f:" + from + ",t:" + to + ",w:" + weight+",a:" + availability + "]";
	}
	
	//********************************************************************************
	//TODO-> delete from here 
	
	public boolean isInterNodeLink(){
		int fromindex_ = from.name.indexOf('_'); 
		int toindex_ = to.name.indexOf('_');
		if(fromindex_>=0 && toindex_>=0 && this.from.name.substring(0, fromindex_).equals(to.name.substring(0,toindex_)))
			return false;
		return true;
	}
	
	public int varIndex;
}
