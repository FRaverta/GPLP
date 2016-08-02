package test.test_availability;

import static org.junit.Assert.*;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.jgrapht.graph.ListenableDirectedWeightedGraph;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;

import main.Parameters;
import main.lpModel.wireNet.WNMetrics;
import main.util.Edge;
import main.util.Vertex;
import main.util.XMLGraphParser.XMLGraphParser;


/**
 * Test cases for Availability computation implementation.
 * Its include the following test from optlib: g1,g3,g4,g5,g14.
 * It doesn't include test g2 and g14 because current implementation doesn't have in account multigraphs. 
 *
 * */
public class TestAvailability {
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Parameters.ready();
	}
	
	@Test
	public void test1() {
		WNMetrics dummyObject = new WNMetrics();
		double res = dummyObject.computeAvailability(Parameters.E4_GRAPH,Parameters.E4_Vertexs[0] , Parameters.E4_Vertexs[Parameters.E4_AMOUNT_OF_NODES-1]);
		assertEquals("Error in availability metric computation for simple bridge graph (s<|>T)",0.268,res,0.01);		
	}
	
	@Test
	public void test2() {
		WNMetrics dummyObject = new WNMetrics();
		double res = dummyObject.computeAvailability(Parameters.E5_GRAPH,Parameters.E5_Vertexs[0] , Parameters.E5_Vertexs[Parameters.E5_AMOUNT_OF_NODES-1]);
		assertEquals("Error in availability metric computation for simple bridge graph (s<|>T)",0.945,res,0.01);		
	}

	@Test
	public void test3(){
        ListenableDirectedWeightedGraph<Vertex, Edge> graph;
		try {
			graph = XMLGraphParser.parseXMLGraph("/home/nando/development/Doctorado/Ej1/src/test/test_availability/data/g1.graphml");
	        Vertex source = getNode(graph,"n0");
	        Vertex target = getNode(graph,"n2");
			WNMetrics dummyObject = new WNMetrics();
	        Double res =  dummyObject.computeAvailability(graph,source,target);
	        assertEquals("Error in availability metric computation for src/test/test_availability/data/g1.graphml",0.985,res,0.01);
		} catch (ParserConfigurationException e) {
			fail("Parser Configuration error: " + e.toString());
		} catch (SAXException e) {
			fail("Data parsing error: " + e.toString());
		} catch (IOException e) {
			fail("IO error: " + e.toString());
		}
	}

	@Test
	public void test4(){
        ListenableDirectedWeightedGraph<Vertex, Edge> graph;
		try {
			graph = XMLGraphParser.parseXMLGraph("/home/nando/development/Doctorado/Ej1/src/test/test_availability/data/g3.graphml");
	        Vertex source = getNode(graph,"n0");
	        Vertex target = getNode(graph,"n3");
			WNMetrics dummyObject = new WNMetrics();
	        Double res =  dummyObject.computeAvailability(graph,source,target);
	        assertEquals("Error in availability metric computation for src/test/test_availability/data/g3.graphml",0.974,res,0.01);
		} catch (ParserConfigurationException e) {
			fail("Parser Configuration error: " + e.toString());
		} catch (SAXException e) {
			fail("Data parsing error: " + e.toString());
		} catch (IOException e) {
			fail("IO error: " + e.toString());
		}
	}
	
	@Test
	public void test5(){
        ListenableDirectedWeightedGraph<Vertex, Edge> graph;
		try {
			graph = XMLGraphParser.parseXMLGraph("/home/nando/development/Doctorado/Ej1/src/test/test_availability/data/g4.graphml");
	        Vertex source = getNode(graph,"n0");
	        Vertex target = getNode(graph,"n3");
			WNMetrics dummyObject = new WNMetrics();
	        Double res =  dummyObject.computeAvailability(graph,source,target);
	        assertEquals("Error in availability metric computation for src/test/test_availability/data/g4.graphml",0.98,res,0.01);
		} catch (ParserConfigurationException e) {
			fail("Parser Configuration error: " + e.toString());
		} catch (SAXException e) {
			fail("Data parsing error: " + e.toString());
		} catch (IOException e) {
			fail("IO error: " + e.toString());
		}
	}
	
	@Test
	public void test6(){
        ListenableDirectedWeightedGraph<Vertex, Edge> graph;
		try {
			graph = XMLGraphParser.parseXMLGraph("/home/nando/development/Doctorado/Ej1/src/test/test_availability/data/g5.graphml");
	        Vertex source = getNode(graph,"n0");
	        Vertex target = getNode(graph,"n3");
			WNMetrics dummyObject = new WNMetrics();
	        Double res =  dummyObject.computeAvailability(graph,source,target);
	        assertEquals("Error in availability metric computation for src/test/test_availability/data/g5.graphml",1,res,0.01);
		} catch (ParserConfigurationException e) {
			fail("Parser Configuration error: " + e.toString());
		} catch (SAXException e) {
			fail("Data parsing error: " + e.toString());
		} catch (IOException e) {
			fail("IO error: " + e.toString());
		}
	}
	
	@Test
	public void test7(){
        ListenableDirectedWeightedGraph<Vertex, Edge> graph;
		try {
			graph = XMLGraphParser.parseXMLGraph("/home/nando/development/Doctorado/Ej1/src/test/test_availability/data/g14.graphml");
	        Vertex source = getNode(graph,"n0");
	        Vertex target = getNode(graph,"n2");
	        if(source==null || target==null )
	        	fail("Source or target node equal to null");
			WNMetrics dummyObject = new WNMetrics();
	        Double res =  dummyObject.computeAvailability(graph,source,target);
	        assertEquals("Error in availability metric computation for src/test/test_availability/data/g14.graphml",0.9903567548,res,0.01);
		} catch (ParserConfigurationException e) {
			fail("Parser Configuration error: " + e.toString());
		} catch (SAXException e) {
			fail("Data parsing error: " + e.toString());
		} catch (IOException e) {
			fail("IO error: " + e.toString());
		}
	}
	
	private Vertex getNode(ListenableDirectedWeightedGraph<Vertex, Edge> graph, String label) {
		for(Vertex v:graph.vertexSet())
			if(v.name.equals(label))
				return v;
			
		return null;			
	}
}	     



/**
 * Este fue un intento de parametrizarlos. Renegue importando una clase y opte por hacerlos 
 * PARAMETRIZED TEST CASE
 * 
 * 
 * */

//@Parameters
//public static Collection<Object[]> data() {
//  String path = "/home/nando/development/Doctorado/Ej1/src/test_availability/data/g1.graphml";
//  double epsilon = 0.01;
//  Object[][] data = new Object[][] { {path + "g1.graphml","n0","n2",0.985,epsilon}, {path + "g1.graphml","n0","n1",0.922,epsilon} };
//  return Arrays.asList(data);
//}
//@Test
//public void test(String path,String vSource, String vTarget, double expectedValue , double epsilon){
//    ListenableDirectedWeightedGraph<Vertex, Edge> graph;
//	try {
//		// /home/nando/development/Doctorado/Ej1/src/test_availability/data/g1.graphml
//		graph = GraphHandler.parseXMLGraph(path);
//        Vertex source = getNode(graph,vSource);
//        Vertex target = getNode(graph,vTarget);
//        Double res =  Metrics.computeAvailability(graph,source,target);
//        assertEquals("Error in availability metric computation for " + path,expectedValue,res,epsilon);
//	} catch (ParserConfigurationException e) {
//		fail("Parser Configuration error: " + e.toString());
//	} catch (SAXException e) {
//		fail("Data parsing error: " + e.toString());
//	} catch (IOException e) {
//		fail("IO error: " + e.toString());
//	}
//}
