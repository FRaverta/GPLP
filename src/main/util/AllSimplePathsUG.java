package main.util;


import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jgrapht.GraphPath;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.GraphPathImpl;
import org.jgrapht.graph.ListenableUndirectedWeightedGraph;

/** 
 * Class for compute all simple path (those don't have loops) in an undirected weighted graph.
 * The entry graph mustn't be a MultiGraph
 * */
public class AllSimplePathsUG<N, E>{
	
	UndirectedGraph<N,E> originGraph;
	
    private Map<N,MemNode> nodeToMemNode;
    
	public AllSimplePathsUG(UndirectedGraph<N,E> originGraph){
		this.originGraph = originGraph;
	}
	
	public List<GraphPath<N, E>> computePaths(N source, N target){;
		//check parameters
	
		if(!originGraph.containsVertex(source))
			throw new IllegalArgumentException("Source node must be in graph");
		if(!originGraph.containsVertex(target))
			throw new IllegalArgumentException("Target node must be in graph");
		for(N nfrom: originGraph.vertexSet())
			for(N nto: originGraph.vertexSet())
				if(!nfrom.equals(nto))
					if(originGraph.getAllEdges(nfrom, nto).size() > 1)
						throw new IllegalArgumentException("The entry graph must'n be a multigraph.\n"
															+"There must be only one edge between " + nfrom +"and " +nto);
		
		nodeToMemNode = new HashMap<N,MemNode>();
		
		for(N n: originGraph.vertexSet()){
			MemNode memNode = new MemNode(n);
			nodeToMemNode.put(n,memNode);
		}
		
		List<List<E>> pathsAsEdgeList = getAllSinglePath(source,target,new LinkedList<N>(Arrays.asList(source)));
		List<GraphPath<N,E>> paths = new LinkedList<GraphPath<N,E>>();
		for(List<E> edgeList: pathsAsEdgeList){
			double weight = 0;
			for(E e: edgeList){
				weight += originGraph.getEdgeWeight(e);
				paths.add(new GraphPathImpl<N,E>(originGraph, source, target, edgeList, weight));
			}
		}
		return paths;
	} 	
	
	
	
	class MemNode{
		public final N value;
		//null = non calculate, empty list = calculate and result was empty, non empty list = calculate and result was non empty
		private List<List<E>> result;
		
		MemNode(N n){
			this.value = n;
		}
		
		public List<List<E>> getResult(){
			return result;
		}
		
		public void setResultAsEmpty(){
			result = new LinkedList<List<E>>();
		}
		
		public void addResults(List<List<E>> p){
			if(result == null)
				result = new LinkedList<List<E>>();
			result.addAll(p);
		}
		
		public void addResult(List<E> p){
			if(result == null)
				result = new LinkedList<List<E>>();
			result.add(p);
		}
	}
	
	 private List<List<E>> getAllSinglePath(N source, N target, List<N> visitedList){
		 if(source == target)
				 return new LinkedList<List<E>>(); 
		 else{
			 //if paths from source to target has been calculated already, return that.
			 if(this.nodeToMemNode.get(source).result != null)
				 return this.nodeToMemNode.get(source).result;
			 //otherwise compute it.
			 else{
				 List<N> neighbords = new LinkedList<N>();
				 for(E e: originGraph.edgesOf(source))
			 		neighbords.add(((originGraph.getEdgeTarget(e) != source)? originGraph.getEdgeTarget(e):originGraph.getEdgeSource(e)));
				 
				 if(neighbords.isEmpty())
					 this.nodeToMemNode.get(source).setResultAsEmpty();
				 else
					 for(N n: neighbords){
						 if(!visitedList.contains(n)){
							 if(n.equals(target))
								 nodeToMemNode.get(source).addResult(new LinkedList<E>(Arrays.asList(this.originGraph.getEdge(source, target))) ); 
							 else{
								 LinkedList<N> newVisitedList = new LinkedList<N>(visitedList);
								 newVisitedList.add(n);
								 nodeToMemNode.get(source).addResults(concatEdgeToPath(originGraph.getEdge(source, n),getAllSinglePath(n,target,newVisitedList)));
							 }
						 }
					 }				 
			 }	
			 if(nodeToMemNode.get(source).result == null)
				 nodeToMemNode.get(source).setResultAsEmpty();
			 return nodeToMemNode.get(source).result;
		 }

	 }

	private List<List<E>> concatEdgeToPath(E e,List<List<E>> rightSidePaths) {
		List<List<E>> result = new LinkedList<List<E>>();
		for(List<E> p:rightSidePaths){
			List<E> edges = new LinkedList<E>(p);			
			edges.add(0,e);
			result.add(edges);	
		}
		return result;
	}

	private GraphPath<N,E> buildPath(List<N> visitedList) {
		 List<E> edgeList = new LinkedList<E>();
		 double weight = 0;
		 for(int i=0; i<visitedList.size()-1;i++){
			 E e = originGraph.getEdge(visitedList.get(i),visitedList.get(i+1));
			 edgeList.add(e);
			 weight += originGraph.getEdgeWeight(e); 
		 }
		 
		 return new GraphPathImpl<N,E>(originGraph,visitedList.get(0),visitedList.get(visitedList.size()-1),edgeList,weight);
	}
	
	public static void main(String args[]){
		ListenableUndirectedWeightedGraph<Vertex,Edge> g = new ListenableUndirectedWeightedGraph<Vertex,Edge> (Edge.class);
		Vertex[] vertexs = new Vertex[4];
		Edge[] edges = new Edge[6];
		
		for(int i=0;i<vertexs.length;i++){
			vertexs[i]=new Vertex(Integer.toString(i));
			g.addVertex(vertexs[i]);
		}
		
		edges[0] = new Edge("01",vertexs[0],vertexs[1],1);
		edges[1] = new Edge("13",vertexs[1],vertexs[3],1);
		edges[2] = new Edge("23",vertexs[2],vertexs[3],1);
		edges[3] = new Edge("02",vertexs[0],vertexs[2],1);
		edges[4] = new Edge("03",vertexs[0],vertexs[3],1);
		edges[5] = new Edge("12",vertexs[1],vertexs[2],1);
		
		for(int i=0;i<edges.length;i++){
			g.addEdge(edges[i].from, edges[i].to,edges[i]);
			g.setEdgeWeight(edges[i],edges[i].weight);
		}
		
		AllSimplePathsUG<Vertex,Edge> pathFinder = new AllSimplePathsUG<Vertex,Edge>(g);
	
		System.out.println(	pathFinder.computePaths(vertexs[0],vertexs[3]).toString());
		System.out.println(	pathFinder.computePaths(vertexs[0],vertexs[2]).toString());
		System.out.println(	pathFinder.computePaths(vertexs[0],vertexs[0]).toString());

	}
}
