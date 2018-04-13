package io.tudresden.fbs.axis.beans;

public class Workflow {
	public String name;
	public String context;
	
	public boolean isValid() {
		return name != null 
				&& context != null 
				&& !name.trim().isEmpty() 
				&& !context.trim().isEmpty(); 
	}

}
