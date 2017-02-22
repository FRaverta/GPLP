package main.util.XMLGraphParser;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import org.xml.sax.Attributes;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.ListenableDirectedWeightedGraph;
import org.jgrapht.graph.ListenableUndirectedWeightedGraph;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import main.util.Edge;
import main.util.Vertex;

//issue-> check unique identifier 
public class XMLGraphParser extends DefaultHandler {

	private static Object WeightedGraph;
	private LinkedList<Edge> edges;
	private LinkedList<Vertex> vertexs;
	
	
	/**For drive parsing*/
//	boolean parsing_graphml;
//	boolean parsing_key;
//	boolean parsing_graph;
	private boolean parsing_edge;
	private boolean parsing_node;
	private boolean parsing_data;
	private boolean parsing_graph;
	private boolean next_is_mttf;
	private boolean next_is_mttr;
	private boolean next_is_a;
	private boolean next_is_weight;
	private boolean next_is_capacity;
	private String element_id;
	//true if current graph is directed, false in cc
	private boolean directed; 
//	private String type_of_edge;
	private String edge_source;
	private String edge_target;
	private double mttr;
	private double mttf;
	private double a;
	private int capacity;
	private int weight;
		
	public XMLGraphParser() {
		
		edges = new LinkedList<Edge>();
		vertexs = new LinkedList<Vertex>();
		
		parsing_edge = false;
		parsing_node = false;
		parsing_data = false;		
		next_is_mttf = false;
		next_is_mttr = false;
		next_is_a = false;
		next_is_weight = false;
		next_is_capacity = false;
		mttr = -1;
		mttf = -1;
		a = -1;
		
		/**Atributes for future use*/
		weight = 0;
		capacity = Edge.MAX_CAPACITY;	
	}
	
	
	public void startElement(String s, String s1, String elementName, Attributes attributes) throws SAXException {
//		type_of_edge = attributes.getValue("edgedefault");
		if(elementName.equalsIgnoreCase("graph")){
			parsing_graph = true;
			try{
				directed = (attributes.getValue("edgedefault").equals("directed")? true: false);
			}catch(NullPointerException e){throw new SAXException("Graph declaration must have a field 'edgedefault' \n");}			
		}
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
					case "weight"	: next_is_weight = true;break;
					case "capacity"	: next_is_capacity = true;break;
					case "groups": break; 
					case "delay": break; 
					
					default: throw new SAXException("In data declaration, key field value must be ''weight'',''capacity'',''mttf'', ''mttr'' or 'a'. (key ''groups'' and 'delays' are ingnored) \n");
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
				Edge e = new Edge(element_id, source, target , weight, capacity, a);
				edges.add(e);
				/**
				 * TODO: for this moment we parse undirected graph as directed. 
				 * Which for each edge (s,t) we created a symetric edge (t,s) with
				 * the same name, availability and weight. 
				 */
				if(!this.directed){
					Edge eSymetric =  new Edge(element_id, target, source, weight, capacity, a);
					edges.add(eSymetric);
				}
					
			}else if(mttr > 0 && mttf > 0){
				Vertex source = searchSourceVertex(edge_source);
				Vertex target = searchTargetVertex(edge_target);
				Edge e = new Edge(element_id, source, target, weight, capacity, calcAvailability(mttr,mttf));
				edges.add(e);
			}
				else{
					Vertex source = searchSourceVertex(edge_source);
					Vertex target = searchTargetVertex(edge_target);
					//Add edge with availability=1
					Edge e = new Edge(element_id, source, target, weight, capacity,1);
					edges.add(e);
				}
			mttr = -1;
			mttf = -1;
			a = -1;
			capacity = Edge.MAX_CAPACITY;
			weight = 0;
			parsing_edge = false;
		}
		
		if(parsing_data){
			parsing_data = false;
			next_is_mttf = false;
			next_is_mttr = false;
			next_is_a = false;
			next_is_weight = false;
			next_is_capacity = false;
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
				else if(next_is_weight)
					weight = Integer.parseInt(aux);
					else if(next_is_capacity)
						capacity = Integer.parseInt(aux);

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
	public static org.jgrapht.WeightedGraph<Vertex, Edge> parseXMLGraph(File f) throws ParserConfigurationException, SAXException, IOException{
        XMLGraphParser handler = new XMLGraphParser();
		// parse
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();
        parser.parse(f, handler);
        
        //TODO: for this moment we parse undirected graph as directed graph.
//        org.jgrapht.WeightedGraph<Vertex, Edge> graph = (handler.directed)?new ListenableDirectedWeightedGraph<Vertex,Edge>(Edge.class)
//        																   :new ListenableUndirectedWeightedGraph<Vertex,Edge>(Edge.class);
        org.jgrapht.WeightedGraph<Vertex, Edge> graph = new DefaultDirectedWeightedGraph<Vertex,Edge>(Edge.class);
		for(Vertex v: handler.vertexs)
			graph.addVertex(v);
		
		for(Edge e: handler.edges){
			graph.addEdge(e.from, e.to, e);
			graph.setEdgeWeight(e, e.weight);
		}
		
		return graph;

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
	public static ListenableDirectedWeightedGraph<Vertex, Edge> parseDirectedXMLGraph(String path) throws ParserConfigurationException, SAXException, IOException{
        File f = new File(path);
        ListenableDirectedWeightedGraph<Vertex, Edge> result =  new  ListenableDirectedWeightedGraph<Vertex, Edge>(parseXMLGraph(f));
		return result;
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
	@Deprecated //For this moment it parses undirected graph as directed graph. 
	public static ListenableUndirectedWeightedGraph<Vertex, Edge> parseUnDirectedXMLGraph(String path) throws ParserConfigurationException, SAXException, IOException{
        File f = new File(path);
        ListenableUndirectedWeightedGraph<Vertex, Edge> result =  new ListenableUndirectedWeightedGraph<Vertex, Edge>(parseXMLGraph(f));
		return result;
	}
	
	public static void main(String args[]) throws ParserConfigurationException, SAXException, IOException{
		ListenableDirectedWeightedGraph<Vertex, Edge> graph = XMLGraphParser.parseDirectedXMLGraph("/home/nando/development/Doctorado/Ej1/src/test_availability/data/g13.graphml");
		System.out.println(graph.toString());
	}
	

}
