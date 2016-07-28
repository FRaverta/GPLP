package main.util.Availabity;

import java.util.LinkedList;
import java.util.List;

public class ExprVar extends Expr {

	/**The variable's name*/
	public final String name;
	
	/**Field for current application. Availability of edge that is represented by current ExprVar*/
	public final double  value;
	
	public ExprVar(String name, double value) {
		this.name = name;
		this.value = value;
	}

	@Override
	public Expr reduce() {
		return this;
	}

	@Override
	public List<ExprVar> atoms() {
		LinkedList<ExprVar> result = new LinkedList<ExprVar>();
		result.add(this);
		
		return result;
	}

	@Override
	public Expr subs(ExprVar v, ExprConstant w) {
		if(v.name.equals(this.name))
			return w;
		else 
			return this;
	}
	
	public String toString(){
		return this.name;
	}

}
