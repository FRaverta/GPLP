package test;

import java.util.List;

import org.jgrapht.GraphPath;
import org.jgrapht.graph.ListenableDirectedWeightedGraph;
import org.junit.Test;

import junit.framework.TestCase;
import main.util.Edge;
import main.util.Vertex;
import main.lpModel.wireNet.WNLpModel;

public class testLpModel extends TestCase {
	
//	@Test
//	public void test(){
//		ListenableDirectedWeightedGraph<Vertex, Edge> graph = new ListenableDirectedWeightedGraph<Vertex,Edge>(Edge.class);
//		
//		Vertex[] vertexs = new Vertex[5];
//		Edge[] edges= new Edge[7];
//		
//		for(int i=0; i<vertexs.length; i++){
//			vertexs[i] = new Vertex("V"+i);
//			graph.addVertex(vertexs[i]);
//		}
//		
//		edges[0] = new Edge("E0",vertexs[0],vertexs[1],6);
//		edges[1] = new Edge("E1",vertexs[1],vertexs[3],5);
//		edges[2] = new Edge("E2",vertexs[3],vertexs[4],5);
//		edges[3] = new Edge("E3",vertexs[2],vertexs[4],9);
//		edges[4] = new Edge("E4",vertexs[0],vertexs[2],3);
//		edges[5] = new Edge("E5",vertexs[0],vertexs[4],12);
//		edges[6] = new Edge("E6",vertexs[2],vertexs[1],2);
//		
//		for(int i=0; i<edges.length; i++)
//			graph.addEdge(edges[i].from,edges[i].to,edges[i]);		
//		
//		PathFinder p = new PathFinder(graph);
//		List<GraphPath<Vertex, Edge>> paths = p.getAllPaths(vertexs[0], vertexs[4]);
//		assert(paths.size() == 4);
//	}
	
	@Test
	public  void testLpModel1(){
		ListenableDirectedWeightedGraph<Vertex, Edge> graph = new ListenableDirectedWeightedGraph<Vertex,Edge>(Edge.class);
		
		Vertex[] vertexs = new Vertex[5];
		Edge[] edges= new Edge[7];
		
		for(int i=0; i<vertexs.length; i++){
			vertexs[i] = new Vertex("V"+i);
			graph.addVertex(vertexs[i]);
		}
		
		edges[0] = new Edge("E0",vertexs[0],vertexs[1],6);
		edges[1] = new Edge("E1",vertexs[1],vertexs[3],5);
		edges[2] = new Edge("E2",vertexs[3],vertexs[4],5);
		edges[3] = new Edge("E3",vertexs[2],vertexs[4],9);
		edges[4] = new Edge("E4",vertexs[0],vertexs[2],3);
		edges[5] = new Edge("E5",vertexs[0],vertexs[4],12);
		edges[6] = new Edge("E6",vertexs[2],vertexs[1],2);
		
		for(int i=0; i<edges.length; i++){
			graph.addEdge(edges[i].from,edges[i].to,edges[i]);	
			graph.setEdgeWeight(edges[i], edges[i].weight);
		}
		
		
		try{
			WNLpModel lpModel = WNLpModel.testLpModel(graph, 3, vertexs, edges);
		
			//entry graph assertion
			assertEquals("The entry graph and the result graph must have the same amount of  nodes", lpModel.resultGraph.vertexSet().size(),graph.vertexSet().size());
			assertEquals("Amount of paths error", WNLpModel.getAllSinglePath(graph, vertexs[0], vertexs[vertexs.length-1]).size(),4);
			
			//result graph assertion
			assertEquals("Amount of paths error", WNLpModel.getAllSinglePath(lpModel.resultGraph, vertexs[0], vertexs[vertexs.length-1]).size(),3);
			assertEquals("Amount of edges error", lpModel.resultGraph.edgeSet().size(),6);
			
			//entry graph metrics test			
			assertEquals("entry metrics- Amount of shared edges average error", 3.0 / 10.0, lpModel.graphMetrics.shared_edges_average);
			assertEquals("entry metrics- Cost average error", (12.0+12.0+15.0+16.0)/4.0, lpModel.graphMetrics.cost_average);
			assertEquals("entry metrics- Min cost average error", 12.0, lpModel.graphMetrics.min_cost);
			assertEquals("entry metrics- Max cost average error", 16.0, lpModel.graphMetrics.max_cost);
			assertEquals("entry metrics- Path availability after one random failure(paa1f) in average.", 18.0/7.0, lpModel.graphMetrics.paa1f);
			assertEquals("entry metrics- Fault tolerance after one failure in the worst case.",2 ,lpModel.graphMetrics.paa1fwc);

			//result metrics test
			assertEquals("result metrics- Amount of shared edges average error", 3.0/9.0, lpModel.resultMetrics.shared_edges_average);
			assertEquals("result metrics- Cost average error", (12.0+15.0+16.0)/3.0,lpModel.resultMetrics.cost_average);
			assertEquals("result metrics- Min cost error",12.0, lpModel.resultMetrics.min_cost);
			assertEquals("result metrics- Max cost error", 16.0, lpModel.resultMetrics.max_cost);
			assertEquals("result metrics- Path availability after one random failure(paa1f) in average.", 9.0/6.0,lpModel.resultMetrics.paa1f);
			assertEquals("result metrics- Fault tolerance after one failure in the worst case.", 1, lpModel.resultMetrics.paa1fwc);
		}catch(Exception e){assertTrue("Exception was throws during the LpModel creation\n" + e.toString(),false);}
		
		
		
	}

//	this.amount_of_edges = g.edgeSet().size();
//	this.amount_of_vertex = g.vertexSet().size();
//	this.paths = LpModel.getAllSinglePath(g, source, target);
//	this.amount_of_path = paths.size();
//	//amount_of_unreachable_vertex
//	
//	shared_edges_average = calculateSharedEdgeAverage();
//	this.cost_average = calculateCostAverage();
//	this.min_cost = calculateMinCost();
//	this.max_cost = calculateMaxCost();
//	
//	this.paa1f = calculatePaa1f(g);
//	this.paa1fwc = calculatePaa1fwc(g);
}
