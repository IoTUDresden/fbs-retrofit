package io.tudresden.fbs.axis.beans;

public class FeedbackResponse {
	
	private boolean hasBeenSatisfied;
	private boolean hasBeenFinished;
	
	public FeedbackResponse() {	}
	
	public FeedbackResponse(boolean hasBeenFinished, boolean hasBeenSatisfied) {	
		this.hasBeenFinished = hasBeenFinished;
		this.hasBeenSatisfied = hasBeenSatisfied;
	}
	
	public boolean isHasBeenSatisfied() {
		return hasBeenSatisfied;
	}
	public void setHasBeenSatisfied(boolean hasBeenSatisfied) {
		this.hasBeenSatisfied = hasBeenSatisfied;
	}
	public boolean isHasBeenFinished() {
		return hasBeenFinished;
	}
	public void setHasBeenFinished(boolean hasBeenFinished) {
		this.hasBeenFinished = hasBeenFinished;
	}

}
