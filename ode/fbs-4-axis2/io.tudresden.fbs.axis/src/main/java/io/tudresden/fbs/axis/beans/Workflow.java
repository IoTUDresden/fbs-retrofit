package io.tudresden.fbs.axis.beans;

public class Workflow {
	private String name;
	private String context;
	
	public boolean isValid() {
		return name != null 
				&& context != null 
				&& !name.trim().isEmpty() 
				&& !context.trim().isEmpty(); 
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

}
