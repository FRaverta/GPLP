package main.gui;

import java.io.File;
import java.io.IOException;

import org.jgrapht.graph.ListenableDirectedWeightedGraph;

import main.util.Edge;
import main.util.Vertex;

/**
 * Dummy class
 * It has been added to have two main classes: one with lp solver = LPSolve and other with lp solve = CEPLEX
 * 
 * */
public abstract class ControllerFather {

	public abstract void solveNewModel(int amount_of_vertex, int edge_density, int amount_of_path, int max_weight_edge);

	public abstract void runDTN();

	public abstract void runDTNMultiFlow();

	public abstract void loadDefault(boolean b, double sharedEdegeWeighing);

	public abstract void loadExample1(boolean b, double sharedEdegeWeighing);

	public abstract void loadExample3(boolean b, double sharedEdegeWeighing);

	public abstract void runWNFromGraph(ListenableDirectedWeightedGraph<Vertex, Edge> graph, Vertex[] vertexs, int requiredPath);

	public abstract void saveResultGraph(File f) throws IOException;

	public abstract void saveGraph(File f) throws IOException;

	public abstract void runDTNFromGraph(ListenableDirectedWeightedGraph<Vertex, Edge> graph, Vertex[] nodeList,
										int requiredPath, int requiredCapacity, int maxSharedEdges);
}
