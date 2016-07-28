package main.lpModel.dtNet;

import main.lpModel.dtNet.dtnParser.parser;

public class Demo {

	public static void main(String[] args) throws Exception {
		DTNetwork net = parser.parseFromFile("/home/nando/development/Doctorado/Ej1/Examples/DTNExamples/DTN1");
		System.out.println("Finish Parser");
		System.out.println(net.toListenableDirectedWeightedGraph().toString());
	}

}
