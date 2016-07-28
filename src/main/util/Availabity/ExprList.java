package main.util.Availabity;

import java.util.LinkedList;
import java.util.List;

public abstract class ExprList extends Expr {
	public List<Expr> components;
	
	public ExprList() {
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public List<ExprVar> atoms() {
		List<ExprVar> vars = new LinkedList<ExprVar>();
		for(Expr e:this.components)
			vars.addAll(e.atoms());		
		
		return vars;
	}
	

}
