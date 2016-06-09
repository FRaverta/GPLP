package main;

import java.util.List;

import org.jgrapht.DirectedGraph;
import org.jgrapht.GraphPath;
import org.jgrapht.graph.ListenableDirectedWeightedGraph;

public class Metrics {
	
	public final int amount_of_edges;
	public final int amount_of_vertex;
	public final List<GraphPath<Vertex,Edge>> paths;
	public final int amount_of_path;
//	public final int amount_of_unreachable_vertex;
	
	/**
	 * eg. 3 path from source to target(see A,B,C) 
	 * If A shared 1 edge with B and 2 edges with C
	 * and B shared 1 edge with C so
	 * shared_edges_average= 4/cantidadAristas(A) + cantidadAristas(B) + cantidadAristas(C)
	 * */
	public final double shared_edges_average;
	
	public final double cost_average;
	
	public final double max_cost;
	
	public final double min_cost;
	
	/** "FAULT TOLERANCE METRICS" */
	
	/**
	 * Path availability after one random failure(paa1f) in average.
	 * This metrics measure the path availability after the most shared edge has an failure.
	 * Its value is the amount of available paths from source to target after discarding one edge in average.
	 *
	 * eg: 3 path from source to target(see A,B,C). 5 edges: A=v0, B=v1,v2, C= v1,v3,v4
	 * if v0 fails then there will be 2 available path.
	 * if v1 fails then there will be 1 available path.
	 * if v2 fails then there will be 2 available path.
	 * if v3 fails then there will be 2 available path.
	 * if v4 fails then there will be 2 available path.
	 *
	 *then paa1f= (2 + 1 + 2 + 2 + 2)/5 = 9/5
	 * */
	public final double paa1f;

	/**
	 * Fault tolerance after one failure in the worst case (paa1fwc).
	 * This metrics measure the fault tolerance after the most shared edge has an failure.
	 * Its value is the amount of available paths from source to traget after discarding the most shared edge.
	 * 
	 * eg: 3 path from source to target(see A,B,C). The most shared edge is v
	 * 1)
	 * If all paths shared v, and this edge fault
	 * then paa1fwc=0.
	 * 
	 * 2)
	 * If only two path shared v 
	 * then paa1fwc=1.
	 * */
	public final int paa1fwc;
	
	public Metrics(ListenableDirectedWeightedGraph<Vertex,Edge> g,Vertex source, Vertex target){
		this.amount_of_edges = g.edgeSet().size();
		this.amount_of_vertex = g.vertexSet().size();
		this.paths = LpModel.getAllSinglePath(g, source, target);
		this.amount_of_path = paths.size();
		//amount_of_unreachable_vertex
		
		shared_edges_average = calculateSharedEdgeAverage();
		this.cost_average = calculateCostAverage();
		this.min_cost = calculateMinCost();
		this.max_cost = calculateMaxCost();
		
		this.paa1f = calculatePaa1f(g);
		this.paa1fwc = calculatePaa1fwc(g);
		
		Parameters.report.writeln(this.toString());		
	}

	private int calculatePaa1fwc(ListenableDirectedWeightedGraph<Vertex, Edge> g) {
		int maxAmountOfUses = 0;
		Edge maxUsedEdge = null;
		
		for(Edge e: g.edgeSet()){
			int amountOfUses = 0;
			for(GraphPath<Vertex,Edge> p: paths)
				if(p.getEdgeList().contains(e)) amountOfUses++;
			
			if(amountOfUses > maxAmountOfUses){
				maxAmountOfUses = amountOfUses;
				maxUsedEdge = e;
			}
				
		}
		
		if(maxUsedEdge == null)
			return 0;
		else return this.amount_of_path - maxAmountOfUses;
	}

	private double calculatePaa1f(ListenableDirectedWeightedGraph<Vertex,Edge> g) {
		double result=0;
		
		for(Edge e: g.edgeSet())
			for(GraphPath<Vertex,Edge> p: paths)
				if(!p.getEdgeList().contains(e)) result++;
				
		return result/this.amount_of_edges;
	}

	private double calculateMinCost() {
		double min = Double.MAX_VALUE;
		for(GraphPath<Vertex,Edge> p: paths)
			if(calcPathCost(p) < min) min = calcPathCost(p);
		return min;
	}

	private double calculateMaxCost() {
		double max = Double.MIN_VALUE;
		for(GraphPath<Vertex,Edge> p: paths)
			if(calcPathCost(p) > max) max = calcPathCost(p);
		return max;
	}

	private double calculateCostAverage() {
		double cost = 0;
		for(GraphPath<Vertex,Edge> p: paths)
			cost += calcPathCost(p);
		
		cost = cost/this.amount_of_path;
		return cost;
	}

	private double calculateSharedEdgeAverage() {
		double shared = 0;
		for(int i=0; i<paths.size();i++)
			for(int j=i+1; j<paths.size();j++){
				GraphPath<Vertex, Edge> p0 = paths.get(i);
				GraphPath<Vertex, Edge> p1 = paths.get(j);
				for(Edge e: p0.getEdgeList())
					if(p1.getEdgeList().contains(e)) shared++;
					
			}
		
		int path_edges = 0;
		for(GraphPath<Vertex, Edge> p: paths)
			path_edges += p.getEdgeList().size();
		shared = shared / path_edges;
		return shared;
	}

	private double calcPathCost(GraphPath<Vertex,Edge> p){
		double r= 0;
		for(Edge e: p.getEdgeList())
			r+=e.weight;
		
		return r;
	}
	
	public String toString(){
		StringBuilder st = new StringBuilder();
		st.append("----------------------Metrics Report----------------------+\n");
		st.append("amount_of_edges: " +  amount_of_edges + "\n");
		st.append("amount_of_vertex: " + amount_of_vertex + "\n");
		st.append("amount_of_path: " + amount_of_path + "\n");
		st.append("shared_edges_average: " + shared_edges_average + "\n");
		st.append("cost_average: " + cost_average + "\n");
		st.append("max_cost: " + max_cost + "\n");
		st.append("min_cost: " + min_cost + "\n");

		st.append("Path availables after 1 failure in average " + paa1f + "\n");
		st.append("Path availables after 1 failure in the worst case " + paa1fwc + "\n");
		st.append("----------------------------------------------------------+\n");
		
		return st.toString();
	}
}
