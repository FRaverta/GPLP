package main.util.Availabity;

import java.util.LinkedList;
import java.util.List;

public class ExprOr extends ExprList{
	
	public ExprOr() {
		// TODO Auto-generated constructor stub
	}
	
	public ExprOr(List<Expr> components) {
		this.components = components;
	}

	@Override
	public Expr subs(ExprVar v, ExprConstant w) {
		List<Expr> aux = new LinkedList<Expr>();
		
		for(Expr e: this.components)
			aux.add(e.subs(v, w));		
		
		return new ExprOr(aux);
	}

	public Expr reduce() {
		List<Expr> aux = new LinkedList<Expr>();
		
		for(Expr e: components){
			Expr re = e.reduce();
			if(re instanceof ExprConstant){
				if(((ExprConstant) re).value)
					return re;
			}else
				aux.add(re);
		}
		
		if(aux.size() == 0)
			return new ExprConstant(false);
		
		if(aux.size() == 1)
			return aux.get(0);
		else
			return new ExprOr(aux);
	}
	
	public String toString(){
		StringBuilder st = new StringBuilder();
		st.append("Or[");
		for(Expr e: components)
			st.append(e.toString() + ",");
		
		if(components.size() > 0)
			st.deleteCharAt(st.length()-1);
		
		st.append("]");
		return st.toString();
	}
}
