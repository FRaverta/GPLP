package main.lpModel.dtNet.dtnParser;

public class Field {
	
	public final boolean enable;
	public final int weight;
	
	public Field(Integer value, Integer weight) {
		if( value == null ||  (value != 0 && value != 1) )
			throw new IllegalArgumentException("The field's value should be binary");
		if( weight == null || weight < 0)
			throw new IllegalArgumentException("The field's weight should be => 0");

		this.enable = (value == 0)? false : true;
		this.weight = weight;
		
	}

	public Field(Integer value) {
		if( value == null ||  (value != 0 && value != 1) )
			throw new IllegalArgumentException("The field's value should be binary");
		
		this.enable = (value == 0)? false : true;
		this.weight = 0;
	}
}
