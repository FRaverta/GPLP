package main.util.XMLGraphParser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.jgrapht.graph.ListenableDirectedWeightedGraph;

import main.util.Edge;
import main.util.Vertex;

public class GraphXMLExporter {
	
	public static void generateGraphmlFromGraph(ListenableDirectedWeightedGraph<Vertex,Edge> graph, File file) throws IOException{
		BufferedWriter output = new BufferedWriter(new FileWriter(file));
		output.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		output.write("<graphml xmlns=\"http://graphml.graphdrawing.org/xmlns\"\n");
		output.write("    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n");
		output.write("    xsi:schemaLocation=\"http://graphml.graphdrawing.org/xmlns\n");
		output.write("      http://graphml.graphdrawing.org/xmlns/1.0/graphml.xsd\">\n");
		output.newLine();

		//write keys
		String tab = "  ";
		
		//write graph
		String name = file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf("/") + 1);
		output.write(tab + "<graph id=\" " + name + "\" edgedefault=\"directed\"> \n"); tab = "    ";
		output.newLine();
		
		//write nodes
		for(Vertex v: graph.vertexSet())
			output.write(tab+"<node id=\"" + v.name + "\"/>\n");
		output.newLine();

		//write edges
		for(Edge e: graph.edgeSet()){
			output.write(tab + "<edge id=\"" + e.name + "\" source=\"" + e.from +"\" target=\"" + e.to +"\">\n"); tab= "      ";
			output.write(tab + "<data key=\"weight\">" + e.weight + "</data>\n");
			output.write(tab + "<data key=\"capacity\">" + e.capacity + "</data>\n");
			output.write(tab + "<data key=\"a\">" + e.availability + "</data>\n"); tab = "    ";
			output.write(tab + "</edge>\n"); 
		}
		output.newLine();
		tab = "  "; 
		output.write(tab + "</graph>\n"); tab = ""; 
		output.write(tab + "</graphml>\n");
		
		output.close();
	}

}
