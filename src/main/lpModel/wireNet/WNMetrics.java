package main.lpModel.wireNet;

import java.util.List;


import org.jgrapht.GraphPath;
import org.jgrapht.graph.ListenableDirectedWeightedGraph;

import main.Parameters;
import main.util.Edge;
import main.util.Metrics;
import main.util.Vertex;


public class WNMetrics extends Metrics {
		
	
	
	/**
	 * eg. 3 path from source to target(see A,B,C) 
	 * If A shared 1 edge with B and 2 edges with C
	 * and B shared 1 edge with C so
	 * shared_edges_average= 4/cantidadAristas(A) + cantidadAristas(B) + cantidadAristas(C)
	 * */
	public final double shared_edges_average;
	
	
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
	
	
	public WNMetrics(ListenableDirectedWeightedGraph<Vertex,Edge> g,Vertex source, Vertex target){
		super(g,source,target);
		
		shared_edges_average = calculateSharedEdgeAverage();		
		this.paa1f = calculatePaa1f(g);
		this.paa1fwc = calculatePaa1fwc(g);
		Parameters.report.writeln(this.toString());		
	}
	
	/**
	 * Dummy constructor for test proposes 
	 * */
	public WNMetrics(){
		super();
		paa1f = 0;
		paa1fwc = 0;
		shared_edges_average = 0;
		
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

	
	public String toString(){
		StringBuilder st = new StringBuilder();
		
		st.append(super.toString());
		
		st.append("Path availables after 1 failure in average " + paa1f + "\n");
		st.append("Path availables after 1 failure in the worst case " + paa1fwc + "\n");	

		st.append("----------------------------------------------------------+\n");

		return st.toString();
	}
	
//	public static void main(String args[]){
//		double A = 0.8;
//		double B = 0.9;
//		double C = 0.8;
//		double D = 0.9;
//		double E = 0.8;
//		double d = E * ( ( (A + C) - (A*C)) * ((B+D) - (B*D))) + (1 - E) * ((A*B + C*D) - (A*B*C*D));
//		System.out.println(d);
//	}
	


public List<GraphPath<Vertex, Edge>> getAllSinglePath(ListenableDirectedWeightedGraph<Vertex, Edge> g, Vertex source,Vertex target) {
	return WNLpFormat.getAllSinglePath(g, source, target);
}



//public static void main(String args[]){
////	double R1 = 0.4;
////	double R2 = 0.5;
////	double C1 = 0.3;
////	double C2 = 0.2;
////	double T = 0.6;
////	double Ha = (R1 * C1) + (R2 * C2) - (R1 * C1 * R2 * C2)  ;
////	System.out.println("Ha= " + Ha);
////	double Hb = (T * (1 - ((1-R1) * (1-R2))) * (1- ((1-C1) * (1-C2)))) + (1-T) * Ha  ;
////	System.out.println("Hb= " + Hb);
//	
//	LinkedList<LinkedList> s = new LinkedList<LinkedList>();
//
//	LinkedList<String> e = new LinkedList<String>(); e.add("A");
//	s.add(e);
//	
//	e = new LinkedList(); e.add("B");
//	s.add(e);
//	
//	e = new LinkedList(); e.add("C");
//	s.add(e);
//	
//	e = new LinkedList(); e.add("D");
//	s.add(e);
//	
//	e = new LinkedList(); e.add("E");
//	s.add(e);
//
//	
//	LinkedList l = getCombination(s);
//	System.out.println(l.toString());
//	System.out.println(l.size());
//}
//public static void main(String args[]){
//	WNMetrics m = new WNMetrics();
//	Expr e = m.genFlowAvailableConfigurationExpr(Parameters.E4_GRAPH,Parameters.E4_Vertexs[0],Parameters.E4_Vertexs[Parameters.E4_Vertexs.length -1],2);
//	System.out.println(e.toString());
//	double a = m.H(e);
//	System.out.println(a);
//}


}
