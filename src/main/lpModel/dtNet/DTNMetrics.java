package main.lpModel.dtNet;

import java.util.LinkedList;
import java.util.List;

import org.jgrapht.GraphPath;
import org.jgrapht.graph.ListenableDirectedWeightedGraph;

import main.Parameters;
import main.util.Edge;
import main.util.Metrics;
import main.util.Vertex;
import main.util.Availabity.Expr;
import main.util.Availabity.ExprAnd;
import main.util.Availabity.ExprOr;
import main.util.Availabity.ExprVar;

public class DTNMetrics extends Metrics {
	
	/**This metrics measure the DTNetwork probability of there will be paths for transport a minimum flow f from S to T*/
	public final double flow_availability;
	public final double minFlow;
	
	public DTNMetrics(ListenableDirectedWeightedGraph<Vertex,Edge> g,Vertex source, Vertex target,int minFlow) {
		super(g, source, target);
		this.minFlow = minFlow;
		flow_availability = calcFlowAvailability(g, source, target, minFlow);
		Parameters.report.writeln(this.toHTML());		
	}

	/**
	 * Dummy constructor for test propuses
	 * */
	public DTNMetrics() {
		super();
		flow_availability = 0;
		minFlow = 0;
	}

	public String toString(){
		StringBuilder st = new StringBuilder();
		
		st.append(super.toString());
		
		st.append("Availability of paths that delivery a flow  " + minFlow + " :" + flow_availability + "\n");

		st.append("----------------------------------------------------------+\n");

		return st.toString();
	}
	
	public String toHTML(){
		StringBuilder st = new StringBuilder();
		
		st.append(super.toHTML());
		
		st.append("Availability of paths that delivery a flow  " + minFlow + " :" + flow_availability + "<br>");

		st.append("----------------------------------------------------------<br>");

		return st.toString();
	}
	
	
	/*****************************************************************/
	//Methods for compute flow availability
	/*****************************************************************/
	
	/**
	 * Compute the availability of a graph configuration which can delivery minflow in any moment of time.
	 * We define a graph configuration as a graph state in which some (or all) paths are available. So the meaning
	 * of this metric is how much is the chance that I observe the System and it can delivery minFlow from Source to Target.
	 * 
	 * */
	private double calcFlowAvailability(ListenableDirectedWeightedGraph<Vertex, Edge> g,Vertex source, Vertex target, int minFlow) {			
		Expr e = genFlowAvailableConfigurationExpr(g,source,target,minFlow);
		
		return H(e);
	}
	
	/**
	 * Given a list of elements compute all possible combination of those elements without repetition neither order.
	 * Eg: [A,B,C] = [[A],[B],[C],[A,B],[A,C],[B,C],[A,B,C]] 
	 * */
	private static LinkedList<LinkedList> getCombination(LinkedList  s){
		if(s.isEmpty()){
			LinkedList<LinkedList> aux = new LinkedList<LinkedList>();	aux.add(new LinkedList<LinkedList>()); 
			return aux;
		}else{
			LinkedList<LinkedList> ss = (LinkedList<LinkedList>) s.clone();
			Object o=  ss.pop(); 
			LinkedList<LinkedList> recResult = getCombination(ss);
			LinkedList<LinkedList> left = concat(o,recResult);
			LinkedList<LinkedList> result = new LinkedList<LinkedList>();
			result.addAll(left);result.addAll(recResult);
			return result;
		}	
	}

	/**
	 * This method append an element o to head of each element of combination argument.
	 * It is equivalent to (map : o) 
	 * 
	 * eg: concat(A , [[BC],[B],[C],[]]) =  [[ABC],[AB],[AC],[A]].  
	 * 
	 * */
	private static LinkedList<LinkedList> concat(Object o,LinkedList<LinkedList> combination) {
		LinkedList<LinkedList> result = new LinkedList<LinkedList>();
		for(LinkedList l: combination){
			LinkedList aux = new LinkedList();
			aux.add(o);
			aux.addAll(l);
			result.add(aux);
		}	
		
		return result;		
	}
	
	/**
	 * This method generate a boolean Expression e = Or[c: c is a valid configuration of system]. 
	 * In other words, since a model should guarantee a minFlow for transporting from source to target, It generate an orexpr
	 * with all system valid configuration (those that can transport minFlow from Source to Target).
	 * 
	 * */
	public Expr genFlowAvailableConfigurationExpr(ListenableDirectedWeightedGraph<Vertex, Edge> g,Vertex source, Vertex target,int requiredFlow){
		//get all paths from source to target
		LinkedList<GraphPath<Vertex,Edge>> paths = new LinkedList<GraphPath<Vertex,Edge>>( getAllSinglePath(g,source,target)); 
				
		//get all possible configuration of enabled paths
		LinkedList<LinkedList> configurations = getCombination(paths);
		
		
		/**
		 * For each path combination from above step  generate a graph that contains the edges
		 * that these paths enable and compute max flow for each graph.
		 * If computed flow >= requiredFlow, it mean that current configuration is valid 
		 * so generate an expr = AND(Ei:Ei in Pi..Pj). Otherwise current configuration is 
		 * discarding.
		*/
		List<Expr> validConfigurations = new LinkedList<Expr>(); 
		for(LinkedList<GraphPath<Vertex,Edge>> l: configurations){
			ListenableDirectedWeightedGraph<Vertex, Edge> gAux = new ListenableDirectedWeightedGraph<Vertex, Edge>(Edge.class);
			for(Vertex v: g.vertexSet())
				gAux.addVertex(v);
			for(GraphPath<Vertex,Edge> p:l)
				for(Edge e: p.getEdgeList())
					gAux.addEdge(e.from,e.to,e);
			if (calculateMaxFlow(gAux,source,target) >= requiredFlow)
				validConfigurations.add(genExprAndForAllEdges(gAux));
		}
		
		//generate a Expr Or(e: e was generated in the above step)
		Expr orConfigurations = new ExprOr(validConfigurations);
		
		return orConfigurations;
	}
	
	/**
	 * Given a graph g, this method return an ExprAnd where the coyuntos are g.edges
	 * */
	private ExprAnd genExprAndForAllEdges(ListenableDirectedWeightedGraph<Vertex, Edge> gAux) {
		List<Expr> coyuntos = new LinkedList<Expr>();
		for(Edge e: gAux.edgeSet())
			coyuntos.add(new ExprVar(e.name,e.availability));
		
		ExprAnd andExpr = new ExprAnd(coyuntos);
		return andExpr;
	}
	
	/*****************************************************************/
	//END Methods for compute flow availability
	/*****************************************************************/


	@Override
	public List<GraphPath<Vertex, Edge>> getAllSinglePath(ListenableDirectedWeightedGraph<Vertex, Edge> g, 	Vertex source, Vertex target) {
		return DTNLpModel.getAllSinglePath(g, source, target);
	}
	
//public static void main(String args[]){
////	DTNMetrics m = new DTNMetrics();
//////	Expr e = m.genFlowAvailableConfigurationExpr(Parameters.E4_GRAPH,Parameters.E4_Vertexs[0],Parameters.E4_Vertexs[Parameters.E4_Vertexs.length -1],2);
//////	System.out.println(e.toString());
////	
////	ExprVar A = new ExprVar("A",0.4);
////	ExprVar B = new ExprVar("B",0.3);
////	ExprVar C = new ExprVar("C",0.5);
////	ExprVar D = new ExprVar("D",0.2);
////	ExprVar E = new ExprVar("E",0.6);
////	
////	List<Expr> d1 = new LinkedList<Expr>();
////	d1.add(A);d1.add(B);d1.add(C);d1.add(D);
////	
////	List<Expr> d2 = new LinkedList<Expr>();
////	d2.add(A);d2.add(B); d2.add(E); d2.add(C);d2.add(D);d2.add(E);
////
////	LinkedList<Expr> disyungendos = new LinkedList<Expr>();
////	disyungendos.add(new ExprAnd(d1));disyungendos.add(new ExprAnd(d2));
////	
////	Expr e = new ExprOr(disyungendos);
////	System.out.println(e.toString());
////	double a = m.H(e);
////	System.out.println(a);
//	char[] s= {'A','B','C','D','E'};
//	LinkedList l = new LinkedList();
//	for(char c: s)
//		l.add(c);
//	l=getCombination(l);
//	System.out.println(l.toString() + l.size() );
//}

public static void main(String args[]){
	//variables
	ExprVar AB = new ExprVar("AB",0.5);
	ExprVar AC = new ExprVar("AC",0.5);	
	ExprVar ACc = new ExprVar("ACc",0.5);
	ExprVar CB = new ExprVar("CB",0.5);
	ExprVar BD = new ExprVar("BD",0.5);
	ExprVar BDc = new ExprVar("BDc",0.5);
	ExprVar BD2 = new ExprVar("BD2",0.5);
	
	LinkedList<Expr> aux= new LinkedList<Expr>();

	//BDc * BD2
	aux.add(BDc);aux.add(BD2);  
	Expr e1 = new ExprAnd(aux);
	
	//BD + BDc * BD2
	aux= new LinkedList<Expr>();
	aux.add(BD); aux.add(e1);
	Expr e2 = new ExprOr(aux);
	
	//CB*(BD + BDc * BD2)
	aux= new LinkedList<Expr>();
	aux.add(CB); aux.add(e2);
	Expr e3 = new ExprAnd(aux);
	
	//AC*CB*(BD + BDc * BD2)
	aux= new LinkedList<Expr>();
	aux.add(AC); aux.add(e3);
	Expr leftOrExpr= new ExprAnd(aux);
	
	//ACc * AB * BD2
	aux= new LinkedList<Expr>();
	aux.add(ACc); aux.add(AB);aux.add(BD2);
	Expr rigthOrExpr= new ExprAnd(aux);
	
	aux= new LinkedList<Expr>();
	aux.add(leftOrExpr);aux.add(rigthOrExpr);
	Expr expr = new ExprOr(aux);
	
	System.out.println(H(expr));
}



}
