package main.util;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.jgrapht.GraphPath;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.ListenableDirectedWeightedGraph;

import main.Parameters;
import main.util.Edge;
import main.util.Vertex;
import main.util.Availabity.Expr;
import main.util.Availabity.ExprAnd;
import main.util.Availabity.ExprConstant;
import main.util.Availabity.ExprOr;
import main.util.Availabity.ExprVar;

import org.jgrapht.alg.flow.EdmondsKarpMaximumFlow;
import org.jgrapht.alg.interfaces.MaximumFlowAlgorithm.MaximumFlow;

public abstract class Metrics {
		
	public final int amount_of_edges;
	public final int amount_of_vertex;
	public final List<GraphPath<Vertex,Edge>> paths;
	public final int amount_of_path;
	
	public final double cost_average;
	
	public final double max_cost;
	
	public final double min_cost;
	
	/**Flow metric*/
	public final double max_flow;
	
	/** Asyntotic availability metrics*/
	private double availability;
	
	public Metrics(ListenableDirectedWeightedGraph<Vertex,Edge> g,Vertex source, Vertex target){
		this.amount_of_edges = g.edgeSet().size();
		this.amount_of_vertex = g.vertexSet().size();
		this.paths = getAllSinglePath(g, source, target);
		this.amount_of_path = paths.size();
		
		this.cost_average = calculateCostAverage();
		this.min_cost = calculateMinCost();
		this.max_cost = calculateMaxCost();
		
		this.max_flow = calculateMaxFlow(g,source,target);
		this.availability = computeAvailability(g, source, target);
	}

	/**
	 * Dummy method for test proposes
	 * */
	public Metrics(){
		this.amount_of_edges = 0;
		this.amount_of_vertex = 0;
		this.paths = null;
		this.amount_of_path = 0;
		
		this.cost_average = 0;
		this.min_cost = 0;
		this.max_cost = 0;
		
		this.max_flow = 0;
		this.availability = 0;
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

	private double calcPathCost(GraphPath<Vertex,Edge> p){
		double r= 0;
		for(Edge e: p.getEdgeList())
			r+=e.weight;
		
		return r;
	}
	
	protected double calculateMaxFlow(ListenableDirectedWeightedGraph<Vertex, Edge> g,Vertex source, Vertex target){
		DefaultDirectedWeightedGraph<Vertex,Edge> aux = new DefaultDirectedWeightedGraph<Vertex, Edge>(Edge.class);
		
		for(Vertex v: g.vertexSet())
			aux.addVertex(v);
		
		for(Edge e: g.edgeSet()){
			//add each edge from g to aux with capacity as weight
			Edge eCopy = new Edge(e.name,e.from,e.to,e.capacity);
			aux.addEdge(eCopy.from, eCopy.to, eCopy);
			aux.setEdgeWeight(eCopy, eCopy.weight);			
		}
		
		EdmondsKarpMaximumFlow<Vertex,Edge> maxFlowCalculator = new EdmondsKarpMaximumFlow<Vertex,Edge>(aux);
		MaximumFlow<Vertex, Edge> maxFlow = maxFlowCalculator.buildMaximumFlow(source, target);
		
		return maxFlow.getValue();
		
	}
	
	public double  computeAvailability(ListenableDirectedWeightedGraph<Vertex, Edge> g,Vertex source, Vertex target){
		Expr e = generateExprFromGraph(g, source, target);
				
		return H(e);		
	}
	
	public double H(Expr e){
		e = e.reduce();
		if(e instanceof ExprConstant)
			return (((ExprConstant)e).value)?1:0;
		else{
			ExprVar v = e.atoms().get(0);
			Expr left = e.subs(v, new ExprConstant(true));
			Expr right = e.subs(v, new ExprConstant(false));
			return v.value * (H(left)) + (1 - v.value) * H(right);
		}
		
	}
	
	public Expr generateExprFromGraph(ListenableDirectedWeightedGraph<Vertex,Edge> graph,Vertex source, Vertex target){
		List<GraphPath<Vertex, Edge>>  paths = getAllSinglePath(graph,source,target);
		HashMap<Edge,ExprVar> edgesToVar = new HashMap<Edge,ExprVar>();		
		List<Expr> orComponents = new LinkedList<Expr>(); 
		
		for(GraphPath<Vertex,Edge> p: paths){
			List<Expr> andComponents = new LinkedList<Expr>(); 
			List<Edge> edges = p.getEdgeList();
			for(Edge e: edges){
				ExprVar v = edgesToVar.get(e);
				if( v == null){
					v = new ExprVar(e.name,e.availability);
					edgesToVar.put(e,v);				
				}
				andComponents.add(v);
			}
			if(andComponents.size() > 1)
				orComponents.add(new ExprAnd(andComponents));
			else
				orComponents.add(andComponents.get(0));
		}
		
		return new ExprOr(orComponents);
	}
	
	public abstract List<GraphPath<Vertex,Edge>> getAllSinglePath(ListenableDirectedWeightedGraph<Vertex, Edge> g, Vertex source, Vertex target);
	
	public String toString(){
		StringBuilder st = new StringBuilder();
		st.append("----------------------Metrics Report----------------------+\n");
		st.append("amount_of_edges: " +  amount_of_edges + "\n");
		st.append("amount_of_vertex: " + amount_of_vertex + "\n");
		st.append("amount_of_path: " + amount_of_path + "\n");
		st.append("cost_average: " + cost_average + "\n");
		st.append("max_cost: " + max_cost + "\n");
		st.append("min_cost: " + min_cost + "\n");
		
		st.append("Max avalaible flow " + max_flow + "\n");
		st.append("Availability: " + availability + "\n");
		
		return st.toString();
	}
	
	public String toHTML(){
		StringBuilder st = new StringBuilder();
		st.append("---------------------- <b>Metrics Report</b> ----------------------<br/>");
		st.append("amount_of_edges: " +  amount_of_edges + "<br/>");
		st.append("amount_of_vertex: " + amount_of_vertex + "<br/>");
		st.append("amount_of_path: " + amount_of_path + "<br/>");
		st.append("cost_average: " + cost_average + "<br/>");
		st.append("max_cost: " + max_cost + "<br/>");
		st.append("min_cost: " + min_cost + "<br/>");
		
		st.append("Max avalaible flow " + max_flow + "<br/>");
		st.append("Availability: " + availability + "<br/>");
		
		return st.toString();
	}
	
//		public static void main(String args[]){
//			double A = 0.8;
//			double B = 0.9;
//			double C = 0.8;
//			double D = 0.9;
//			double E = 0.8;
//			double d = E * ( ( (A + C) - (A*C)) * ((B+D) - (B*D))) + (1 - E) * ((A*B + C*D) - (A*B*C*D));
//			System.out.println(d);
//		}
	
//		public static void main(String args[]){
//			double R1 = 0.4;
//			double R2 = 0.5;
//			double C1 = 0.3;
//			double C2 = 0.2;
//			double T = 0.6;
//			double Ha = (R1 * C1) + (R2 * C2) - (R1 * C1 * R2 * C2)  ;
//			System.out.println("Ha= " + Ha);
//			double Hb = (T * (1 - ((1-R1) * (1-R2))) * (1- ((1-C1) * (1-C2)))) + (1-T) * Ha  ;
//			System.out.println("Hb= " + Hb);
//			
//		}
//	}


}
