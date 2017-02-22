package main.util;

public class Vertex {
	public final String name;
	
	public Vertex(String name){
		this.name = name;
	}
	
	public String toString(){
		return name;
	}
//	
//	public boolean equals(Object obj){
//		System.out.println("LPM");
//		if(!(obj instanceof Vertex))
//			return false;
//		else
//			return name.equals(((Vertex) obj).name);
//		
//	}

	

}
