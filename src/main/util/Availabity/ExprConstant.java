package main.util.Availabity;

import java.util.LinkedList;
import java.util.List;

public class ExprConstant extends Expr{
	public final boolean value;
	
	public ExprConstant(boolean value) {
		this.value = value;
	}

	@Override
	public Expr reduce() {		
		return this;
	}

	@Override
	public List<ExprVar> atoms() {
		return new LinkedList<ExprVar>();		
	}

	@Override
	public Expr subs(ExprVar v, ExprConstant w) {
		return this;
	}
	
	public String toString(){
		return (value)?"True":"False";
	}

}
