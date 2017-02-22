package main.util.Availabity;

import java.util.List;

public class ExprNot extends Expr{
	
	Expr body;
	
	public ExprNot(Expr body) {
		this.body = body;
	}

	@Override
	public Expr reduce() {
		Expr newBody = body.reduce();
		if(newBody instanceof ExprConstant )
			if (((ExprConstant)newBody).value)
				return new ExprConstant(false);
			else
				return new ExprConstant(true);
		
		return new ExprNot(newBody);
	}

	@Override
	public List<ExprVar> atoms() {
		return body.atoms();
	}

	@Override
	public Expr subs(ExprVar v, ExprConstant w) {
		Expr newBody = this.body.subs(v, w); 
		return new ExprNot(newBody);
	} 
	
	public String toString(){
		return "Not[" + body.toString() + "]"; 
	}
}
