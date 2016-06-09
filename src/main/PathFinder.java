package main;

/**
 * 
 * THIS CLASS IS NOT BEING USED
 */
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import org.jgrapht.DirectedGraph;
import org.jgrapht.GraphPath;
import org.jgrapht.graph.GraphPathImpl;

public class PathFinder {
	

Stack<Vertex> connectionPath = new Stack<Vertex>();
List<GraphPath<Vertex, Edge>>  paths = new LinkedList<GraphPath<Vertex, Edge>>();
DirectedGraph<Vertex,Edge> graph;

public PathFinder(DirectedGraph<Vertex,Edge> graph){
	this.graph = graph;
}

public List<GraphPath<Vertex, Edge>> getAllPaths(Vertex node, Vertex targetNode) {
	connectionPath.push(node);
	findAllPaths(node,targetNode);
	return paths;
}

// Push to connectionsPath the object that would be passed as the parameter 'node' into the method below
public void findAllPaths(Vertex node, Vertex targetNode) {
	
	List<Vertex> nextNodes = new LinkedList<Vertex>();
	for(Edge e: graph.outgoingEdgesOf(node))
		nextNodes.add(e.to);
	
    for (Vertex nextNode : nextNodes) {
    	
       if (nextNode.equals(targetNode)) {
           Stack<Vertex> temp = new Stack<Vertex>();
           for (Vertex node1 : connectionPath)
               temp.add(node1);
           temp.add(targetNode);
           List<Edge> edges = new LinkedList<Edge>();
           while(temp.size()>1){
        	   edges.add(graph.getEdge(temp.firstElement(), temp.get(1)));
        	   temp.remove(0);
           }
           GraphPath<Vertex, Edge> newPath = new GraphPathImpl<Vertex, Edge>(graph,edges.get(0).from,edges.get(edges.size()-1).to,edges,getWeight(edges));
           paths.add(newPath);
           
       } else if (!connectionPath.contains(nextNode)) {
           connectionPath.push(nextNode);
           findAllPaths(nextNode, targetNode);
           connectionPath.pop();
        }
    }
}

    public double getWeight(List<Edge> edges){
    	double result=0;
    	for(Edge e: edges)
    		result += e.weight;
    	
    	return result;
    }


}

