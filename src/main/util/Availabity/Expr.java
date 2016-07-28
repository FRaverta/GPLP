package main.util.Availabity;

import java.util.List;

public abstract class Expr {
		
	public Expr() {
		// TODO Auto-generated constructor stub
	}
	
	public abstract Expr reduce();
	
	public abstract List<ExprVar> atoms();
	
	public abstract Expr subs(ExprVar v, ExprConstant w);
	
}
