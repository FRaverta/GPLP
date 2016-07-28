package main.lpModel.dtNet.dtnParser;

import java.util.List;

public class State {
	
	public final int AMOUNT_OF_NODES;
	public final int LENGTH;
	public final Field[][] links; 
			
	public State(Integer nodes, Integer length, List<Field> decls) {
		
		if( nodes == null ||  nodes <= 0 )
			throw new IllegalArgumentException("The amount of nodes should be > 0");
		
		if( length == null ||  length <= 0 )
			throw new IllegalArgumentException("The intervals length should be > 0");
		
		if( nodes == null ||  nodes <= 0 )
			throw new IllegalArgumentException("The amount of decls should be " + (nodes * nodes));

		AMOUNT_OF_NODES = nodes;
		LENGTH = length;		
		links = new Field[nodes][nodes];
		for(int i=0; i < nodes; i++)
			for(int j=0; j<nodes; j++)
				links[i][j] = decls.get(i*nodes + j);
		
	}

}
