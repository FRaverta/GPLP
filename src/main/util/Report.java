package main.util;

import java.util.Observable;

public class Report extends Observable{

	private StringBuilder htmlSt;
	
	public Report(){
		htmlSt = new StringBuilder();		
		htmlSt.append("<html> <P ALIGN=left>");		
	}

	public synchronized void write(String str){
		htmlSt.append(str);
		setChanged();
		notifyObservers();
	}
	
//	public synchronized void clear(){
//		st = new StringBuilder();
//		setChanged();
//		notifyObservers();
//	}

	public synchronized String readHtml(int from){
		return htmlSt.substring(from);
	}

	public synchronized void writeln(String str) {
		htmlSt.append(str + "<br>");
		setChanged();
		notifyObservers();		
	}
	
	public synchronized void writelnRed(String str) {
		htmlSt.append("<font color=\"red\">" + str + "</font><br>");
		setChanged();
		notifyObservers();		
	}

	public synchronized void writelnGreen(String str) {
		htmlSt.append("<font color=\"green\">" + str + "</font><br>");
		setChanged();
		notifyObservers();		
	}
	
	public static void  main(String args[]){
		System.out.println("OK");
		System.exit(0);
	}

	//This method read an string and format as HTML
	public void writeString(String str) {
		int i = -1;
		while((i=str.indexOf("\n")) != -1){
			//purge \n and write into reporter in HTML format	
			String aux = (i>0)? str.substring(0,i-1): "";
			str = (i+1<str.length())?str.substring(i+1):"";
			this.writeln(aux);
		}
		this.write(str);
	}
}

