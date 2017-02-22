package main;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.jgrapht.ListenableGraph;
import org.jgrapht.graph.ListenableDirectedWeightedGraph;
import org.jgrapht.graph.ListenableUndirectedWeightedGraph;
import org.xml.sax.SAXException;

import main.lpModel.dtNet.DTNMetrics;
import main.lpModel.wireNet.WNMetrics;
import main.util.Edge;
import main.util.Metrics;
import main.util.Vertex;
import main.util.Availabity.Expr;
import main.util.Availabity.ExprAnd;
import main.util.Availabity.ExprConstant;
import main.util.Availabity.ExprList;
import main.util.Availabity.ExprNot;
import main.util.Availabity.ExprOr;
import main.util.Availabity.ExprVar;
import main.util.XMLGraphParser.XMLGraphParser;

public class Paper {

	static String STATE_SEPARATOR = "_";
	static double ESL_AVAILABILITY = 0.5;
	static double ISL_AVAILABILITY = 0.9;
	
	private static Vertex getNode(ListenableGraph<Vertex, Edge> graph, String label) {
		for(Vertex v:graph.vertexSet())
			if(v.name.equals(label))
				return v;
			
		return null;			
	}    
	
	private static Vertex getNode(ListenableGraph<Vertex, Edge> graph, int i, int j) {
		for(Vertex v:graph.vertexSet())
			if(v.name.equals("N" + i +  STATE_SEPARATOR + j))
				return v;
			
		return null;			
	}    
	
	/**
	 * This method computes the average of an array of double.
	 * */
	public static double arrayAverage(double[] arr){
		double aux=0;
		for(int i=0; i < arr.length; i++)
			aux += arr[i];
		
		return aux / arr.length;
	}
	
	/**
	 * Given an ExprVar e and ExprOr el returns  List<Expr> = [ExprAnd[e,se] for se in el.componets] 
	 * 
	 * */
	
	public static List<Expr> append(ExprVar e, List<Expr> el){
		List<Expr> resultBody = new LinkedList<Expr>();
		for (Expr expr: el){
			List<Expr> body = new LinkedList<Expr>(); 
			if(expr instanceof ExprAnd){
				body.add(e); 
				body.addAll(((ExprAnd) expr).components); 
			}else{
				body.add(e); 
				body.add(expr);
			}
			resultBody.add(new ExprAnd(body));
		}
		return resultBody;
		
	}

	/***
	 * This method compute the successful delivery probability for a node in a train like
	 * formation using REACTIVE forwarding.
	 * 	 
	 * */
	public static Expr reactDeliveryProbabilityExpr(int node, int k, int amountOfNodes, int kStartESL){
		if(k >= kStartESL + amountOfNodes || node >= amountOfNodes)
			//base case
			return new ExprConstant(false);
		
		if(node + kStartESL == k){
			/*
			 If we want to send a message from n in time n, n can send it by its own ESL.
			 Also, if its ESL fail, it can send this to next node in the train. So, the
			 succesfull delivery probability is: 
			 	ESL_node + (!ESL_NODE . ISL_node_node+1_k . deliveryProbabilityExpr(node+1,k+1,amountOfNodes,kStartESL))
			 */
			LinkedList<Expr> orBody = new LinkedList<Expr>();
			LinkedList<Expr> andBody = new LinkedList<Expr>();
			ExprVar vESL = new ExprVar("ESL" + node, ESL_AVAILABILITY);
			
			andBody.add(new ExprNot(vESL));
			andBody.add(new ExprVar("ISL" + node + (node+1) + k, ISL_AVAILABILITY));
			andBody.add(reactDeliveryProbabilityExpr(node + 1, k + 1, amountOfNodes,kStartESL));
			
			orBody.add(vESL);
			orBody.add(new ExprAnd(andBody));
			
			return new ExprOr(orBody);
		}else
			//we continue until node's ESL happen.
			return reactDeliveryProbabilityExpr(node, k + 1,amountOfNodes,kStartESL );
		
	}
	
	/***
	 * This method compute the successful delivery probability for a node in a train like
	 * formation using PROACTIVE forwarding.
	 * 	 
	 * 
	 * return -  expected ExprOr or ExprConstant(false)
	 * */
	public static LinkedList<Expr> proactDeliveryProbabilityExpr(int node, int k, LinkedList<Integer> visitedNodes ,int amountOfNodes, int kStartESL){
		LinkedList<Expr> paths = new LinkedList<Expr>();

		//ISL		
		if (node - 1 >= 0  && !visitedNodes.contains(node - 1)){
			LinkedList<Expr> r;
			LinkedList<Integer> aux = (LinkedList<Integer>) visitedNodes.clone();  aux.add(node);
			
			if (!((r = proactDeliveryProbabilityExpr(node-1,k + 1,aux,amountOfNodes,kStartESL)).isEmpty())) 
				paths.addAll(append(new ExprVar("ISL" + node + (node - 1) + k, ISL_AVAILABILITY), r));  
		}
		
		if (node + 1 < amountOfNodes  && !visitedNodes.contains(node + 1)){
			LinkedList<Expr> r;
			LinkedList<Integer> aux = (LinkedList<Integer>) visitedNodes.clone();  aux.add(node);
			
			if (!((r = proactDeliveryProbabilityExpr(node+1,k + 1,aux,amountOfNodes,kStartESL)).isEmpty())) 
				paths.addAll(append(new ExprVar("ISL" + node + (node + 1) + k, ISL_AVAILABILITY), r));  
		}
		
		//ESL
		if (k == kStartESL + node)
			paths.add(new ExprVar("ESL" + node, ESL_AVAILABILITY));
		
		//STORAGE
		if( k + 1 < kStartESL + amountOfNodes)
			 paths.addAll(proactDeliveryProbabilityExpr(node,k + 1,visitedNodes,amountOfNodes,kStartESL));
			
		return paths;		
	}
	
	/***
	 * This method compute the successful delivery probability for a node in a train like
	 * formation using PROACTIVE-REACTIVE forwarding.
	 * 	 
	 * 
	 * return -  expected ExprOr or ExprConstant(false)
	 * */
	public static LinkedList<Expr> proactReactDeliveryProbabilityExpr(int node, int k, LinkedList<Integer> visitedNodes ,int amountOfNodes, int kStartESL){
		LinkedList<Expr> paths = new LinkedList<Expr>();
		
		Expr res;
		if(node == 0 && (res = reactDeliveryProbabilityExpr(node, k, amountOfNodes, kStartESL)) instanceof ExprOr)
			paths.addAll(((ExprOr)res).components);
		else{	
			//ISL		
			if (node - 1 >= 0  && !visitedNodes.contains(node - 1)){
				LinkedList<Expr> r;
				LinkedList<Integer> aux = (LinkedList<Integer>) visitedNodes.clone();  aux.add(node);
				
				if (!((r = proactReactDeliveryProbabilityExpr(node-1,k + 1,aux,amountOfNodes,kStartESL)).isEmpty())) 
					paths.addAll(append(new ExprVar("ISL" + node + (node - 1) + k, ISL_AVAILABILITY), r));  
			}
			
			if (node + 1 < amountOfNodes  && !visitedNodes.contains(node + 1)){
				LinkedList<Expr> r;
				LinkedList<Integer> aux = (LinkedList<Integer>) visitedNodes.clone();  aux.add(node);
				
				if (!((r = proactReactDeliveryProbabilityExpr(node+1,k + 1,aux,amountOfNodes,kStartESL)).isEmpty())) 
					paths.addAll(append(new ExprVar("ISL" + node + (node + 1) + k, ISL_AVAILABILITY), r));  
			}
		
			
			//STORAGE
			if( k + 1 < kStartESL)
				 paths.addAll(proactReactDeliveryProbabilityExpr(node,k + 1,visitedNodes,amountOfNodes,kStartESL));
		}
		return paths;		
	}

//	
//	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException{
////		ListenableDirectedWeightedGraph<Vertex, Edge> graph = XMLGraphParser.parseDirectedXMLGraph("/home/nando/development/Doctorado/Ej1/src/test/test_availability/data/ejpaper1.graphml");
//		int AMOUNT_OF_STATES = 4;
//		int AMOUNT_OF_NODES = 5;
//
//		ListenableDirectedWeightedGraph<Vertex, Edge> graph = new ListenableDirectedWeightedGraph<Vertex, Edge>(Edge.class);
//		
//		//Add nodes
//		for(int i=0; i<AMOUNT_OF_STATES; i++)
//			for(int j=0; j<AMOUNT_OF_NODES; j++)
//				graph.addVertex(new Vertex("N" + j + STATE_SEPARATOR +   i));	
//		
//		graph.addVertex(new Vertex("T"));
//		
//		//create inter state links
//		int name = 0;
//		for(int i=0; i<AMOUNT_OF_STATES-1; i++)
//			for(int j=0; j<AMOUNT_OF_NODES-1; j++){
//		        Vertex source = getNode(graph, j, i);
//		        Vertex target = getNode(graph, j, i+1);
//				Edge e = new Edge("E" + (name++), source, target , 0, Edge.MAX_CAPACITY, 1);
//				graph.addEdge(source, target, e);
//			}
//		
//		//create ISLs
//		for(int i=0; i<AMOUNT_OF_STATES; i++){
//			
//			for(int j=0; j<AMOUNT_OF_NODES-2; j++){
//		        Vertex source = getNode(graph, j, i);
//		        Vertex target = getNode(graph, (j+1), i);
//			
//		        Edge e = new Edge("E" + (name), source, target , 0, Edge.MAX_CAPACITY, 0.75);
//				graph.addEdge(source, target, e);
//				
//				Edge eSymetric = new Edge("E" + (name++), target, source, 0, Edge.MAX_CAPACITY, 0.75);
//				graph.addEdge(target, source, eSymetric);
//
//			}
//					
//		}
//		
//		//create ESL
//		for(int i=0; i<AMOUNT_OF_STATES; i++){
//	        Vertex source = getNode(graph, i, i);
//	        Vertex target = getNode(graph, (AMOUNT_OF_NODES - 1), i);
//			Edge e = new Edge("E" + (name++), source, target , 0, Edge.MAX_CAPACITY, 0.75);
//			graph.addEdge(source, target, e);
//		}
//
//		//create Earth to target links (ETL)
//		for(int i=0; i<AMOUNT_OF_STATES; i++){
//	        Vertex source = getNode(graph,AMOUNT_OF_NODES - 1, i);
//	        Vertex target = getNode(graph,"T");
//			Edge e = new Edge("E" + (name++), source, target , 0, Edge.MAX_CAPACITY, 1);
//			graph.addEdge(source, target, e);
//		}
//		
//		System.out.println(graph.toString());
//		
//		
//        Vertex source = getNode(graph,3,0);
//        Vertex target = getNode(graph,"T");
//        
//		DTNMetrics dummyObject = new DTNMetrics();
//        Double res =  dummyObject.computeAvailability(graph,source,target);
//        System.out.println(res);
//	
//	}
//	
//	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException{
//		Expr expr = reactDeliveryProbabilityExpr(0,0,3,3);
//		expr = expr.reduce();
//		System.out.println(expr.toString());
//		System.out.println(Metrics.H(expr));
//		
//	}
	
//	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException{
//		Expr expr = new ExprOr(proactReactDeliveryProbabilityExpr(1,0,new LinkedList<Integer>(),2,2));
//		expr = expr.reduce();
//		System.out.println(expr.toString());		
//		System.out.println(Metrics.H(expr));
//		
//	}
		
	public static void write (String filename, double[]x) throws IOException{
		  BufferedWriter outputWriter = null;
		  outputWriter = new BufferedWriter(new FileWriter(filename));
		  for (int i = 0; i < x.length; i++) {
		    outputWriter.write((i + 2) + " " + Double.toString(x[i]));
		    outputWriter.newLine();
		  }
		  outputWriter.flush();  
		  outputWriter.close();  
	}
	
	public static void write (String filename, String str) throws IOException{
	  BufferedWriter outputWriter = null;
	  outputWriter = new BufferedWriter(new FileWriter(filename));
	  outputWriter.write(str);
	  outputWriter.flush();  
	  outputWriter.close();  
	}
	
	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException{
		int MAX_SATELLITE_NUMBER = 9;
		double[] result = new double[MAX_SATELLITE_NUMBER - 2];
		double[] usedLinksByNet = new double[MAX_SATELLITE_NUMBER - 2];

		StringBuilder probaByNodeSB = new StringBuilder();

		for (int i=2; i < MAX_SATELLITE_NUMBER; i++){
			System.out.println(i);	
			double[] probabilityByNode = new double[i];
			double[] amountUsedLinksByNode = new double[i];
			for(int j = 0; j < i; j++){
				Expr expr = new ExprOr(proactDeliveryProbabilityExpr(j,0,new LinkedList<Integer>(),i, i));
				amountUsedLinksByNode[j] = amountOfDifferentsAtoms(expr.reduce());
				probabilityByNode[j] = Metrics.H(expr);
			}
			probaByNodeSB.append(arrToString(probabilityByNode) + "\n");
			usedLinksByNet[i-2] = arrayAverage(amountUsedLinksByNode);
			result[i-2] = arrayAverage(probabilityByNode);						
		}
		write("/home/nando/Desktop/paper/ProactivoPEE.txt",probaByNodeSB.toString());
		write("/home/nando/Desktop/paper/ProactivoPEEAvg.txt",result);
		write("/home/nando/Desktop/paper/ProactivoLinksAVG.txt",usedLinksByNet);

		
		
		
//		System.out.println(expr.toString());	
//		expr = expr.reduce();
//		System.out.println(expr.toString());
//		System.out.println(Metrics.H(expr));
		
	
	}

	private static String arrToString(double[] arr) {
		StringBuilder st = new StringBuilder();
		for(int i=0; i<arr.length;i++)
			st.append(arr[i] + "\n");
		
		return st.toString();
	}
	
	private static int amountOfDifferentsAtoms(Expr e){	
		return new HashSet<ExprVar>(e.atoms()).size();
	}
	
}
