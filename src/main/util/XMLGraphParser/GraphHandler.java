package main.util.XMLGraphParser;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import org.xml.sax.Attributes;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.jgrapht.graph.ListenableDirectedWeightedGraph;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import main.util.Edge;
import main.util.Vertex;

//issue-> check unique identifier 
public class GraphHandler extends DefaultHandler {

	private LinkedList<Edge> edges;
	private LinkedList<Vertex> vertexs;
	
	
	/**For drive parsing*/
//	boolean parsing_graphml;
//	boolean parsing_key;
//	boolean parsing_graph;
	private boolean parsing_edge;
	private boolean parsing_node;
	private boolean parsing_data;
	private boolean next_is_mttf;
	private boolean  next_is_mttr;
	private boolean next_is_a;
	private String element_id;
//	private String type_of_edge;
	private String edge_source;
	private String edge_target;
	private double mttr;
	private double mttf;
	private double a;
	
	/**Atributes for future use*/
	private int edge_weight;
	private int edge_capacity;
	
	public GraphHandler() {
		
		edges = new LinkedList<Edge>();
		vertexs = new LinkedList<Vertex>();
		
		parsing_edge = false;
		parsing_node = false;
		parsing_data = false;		
		next_is_mttf = false;
		next_is_mttr = false;
		next_is_a = false;
		mttr = -1;
		mttf = -1;
		a = -1;
		
		/**Atributes for future use*/
		edge_weight = 0;
		edge_capacity = 0;	
	}
	
	
	public void startElement(String s, String s1, String elementName, Attributes attributes) throws SAXException {
//		type_of_edge = attributes.getValue("edgedefault");
		
		
		if(elementName.equalsIgnoreCase("node")){
			parsing_node = true;
			element_id = attributes.getValue("id");

			if(element_id == null)
				throw new SAXException("Node declaration must have id \n");	
			
		}
		
		if(elementName.equalsIgnoreCase("edge")){
			parsing_edge = true;
			element_id =  attributes.getValue("id");
			edge_source = attributes.getValue("source");
			edge_target = attributes.getValue("target");
			if(element_id == null)
				throw new SAXException("Edge declaration must have id \n");
			if(edge_source == null || edge_target == null)
				throw new SAXException("Edge " + element_id + " declaration must have source and target vertex \n");
		}
		
		if(elementName.equals("data")){
			if(parsing_edge){
				parsing_data = true;
				String keyValue = attributes.getValue("key");
				if(keyValue == null)
					throw new SAXException("In data declaration there must be a key field\n");
				switch (keyValue){
					case "mttf" : next_is_mttf = true;break;
					case "mttr" : next_is_mttr = true;break;
					case "a"	: next_is_a = true;break;
					case "groups": break; 
					case "delay": break; 
					
					default: throw new SAXException("In data declaration, key field value must be ''mttf'', ''mttr'' or 'a'. (key ''groups'' and 'delays' are ingnored) \n");
				}
			}
		}
	}
	
	public void endElement(String s, String s1, String element) throws SAXException{
		if(parsing_node){
			Vertex v = new Vertex(element_id);
			vertexs.add(v);
			parsing_node = false;
			System.out.println("add node " + v.name);
		}
		
		//end edge 
		if(parsing_edge && !parsing_data){
			if(a >= 0){
				Vertex source = searchSourceVertex(edge_source);
				Vertex target = searchTargetVertex(edge_target);
				Edge e = new Edge(element_id, source, target, edge_weight, edge_capacity, a);
				edges.add(e);
			}else if(mttr > 0 && mttf > 0){
				Vertex source = searchSourceVertex(edge_source);
				Vertex target = searchTargetVertex(edge_target);
				Edge e = new Edge(element_id, source, target, edge_weight, edge_capacity, calcAvailability(mttr,mttf));
				edges.add(e);
			}
				else{
					Vertex source = searchSourceVertex(edge_source);
					Vertex target = searchTargetVertex(edge_target);
					//Add edge with availability=1
					Edge e = new Edge(element_id, source, target, edge_weight, edge_capacity,1);
					edges.add(e);
				}
			mttr = -1;
			mttf = -1;
			a = -1;
			parsing_edge = false;
		}
		
		if(parsing_data){
			parsing_data = false;
			next_is_mttf = false;
			next_is_mttr = false;
			next_is_a = false;
		}
	}




	public void characters(char ch[], int start, int length) throws SAXException {
		String aux = new String(ch, start, length);
		if(next_is_mttr)
			mttr = Double.parseDouble(aux);
		else if(next_is_mttf)
			mttf =  Double.parseDouble(aux); 
			else if(next_is_a)
				a =  Double.parseDouble(aux); 
	}
	
	private Vertex searchTargetVertex(String id) throws SAXException{
		Vertex result;
		if ((result=searchVertex(id)) == null)
			throw new SAXException("Target node " + id + " missing in Edge: " + element_id + " declaration. Target node must be declared before");
		
		return result;
	}

	private Vertex searchSourceVertex(String id) throws SAXException{
		Vertex result;
		if ((result=searchVertex(id)) == null)
			throw new SAXException("Source node " + id + " missing in Edge: " + element_id + " declaration. Target node must be declared before");
		
		return result;		
	}
	
	private Vertex searchVertex(String id){
		for(Vertex v: vertexs)
			if(v.name.equals(id))
				return v;
		
		return null;
		
	}
	
	private double calcAvailability(double mttr, double mttf) {
        double mtbf = mttf + mttr;
        return  mttf / mtbf;
	}
	
	/**
	 * Parse graph from a XML document in graphml format. 
	 * Current implementation ignore field 'key' declaration and it always return directed graph.
	 * The format expected could be expresed as the follow:
	 * 
	 *  <graph id="id_graph">
	 *  	(<node id="id_node"/>)*
	 *  	 (<edge id="e0" source="n0" target="n1">
			  	(<data key="mttf">float value</data>)?
				(<data key="mttr">float value</data>)?
				(<data key="a">float value</data>)?
			</edge>)*
		</graph>
	 * */
	public static ListenableDirectedWeightedGraph<Vertex, Edge> parseXMLGraph(String path) throws ParserConfigurationException, SAXException, IOException{
        GraphHandler handler = new GraphHandler();
		// parse
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();
        parser.parse(new File(path), handler);
        
		ListenableDirectedWeightedGraph<Vertex, Edge> graph = new ListenableDirectedWeightedGraph<Vertex,Edge>(Edge.class);
		
		for(Vertex v: handler.vertexs)
			graph.addVertex(v);
		
		for(Edge e: handler.edges){
			graph.addEdge(e.from, e.to, e);
			graph.setEdgeWeight(e, e.weight);
		}
		
		return graph;

	}
	
	public static void main(String args[]) throws ParserConfigurationException, SAXException, IOException{
		ListenableDirectedWeightedGraph<Vertex, Edge> graph = GraphHandler.parseXMLGraph("/home/nando/development/Doctorado/Ej1/src/test_availability/data/g13.graphml");
		System.out.println(graph.toString());
	}
	

}
