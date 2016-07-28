package main.util;

import java.util.Observable;

public class Report extends Observable{

	private StringBuilder st;
	
	public Report(){
		st = new StringBuilder();
	}

	public synchronized void write(String str){
		st.append(str);
		setChanged();
		notifyObservers();
	}
	
//	public synchronized void clear(){
//		st = new StringBuilder();
//		setChanged();
//		notifyObservers();
//	}

	public synchronized String read(int from){
		return st.substring(from);
	}

	public synchronized void writeln(String str) {
		st.append(str + "\n");
		setChanged();
		notifyObservers();		
	}
	
	public static void  main(String args[]){
		System.out.println("OK");
		System.exit(0);
	}
}

