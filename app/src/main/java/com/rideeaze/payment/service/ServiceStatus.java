package com.rideeaze.payment.service;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root
public class ServiceStatus<T> {
	
	
	@Element(required=false)
	public Boolean isSuccess;
	
	@Element(required=false)
    public String message; 
	
	@Element(required=false)
	public T content;
	
	@Element(required=false)
	public int responseCode;
	
	public ServiceStatus(@Element(name="isSuccess") Boolean x, @Element(name="message") String y, @Element(name = "content") T result, @Element(name = "responseCode") int exception){
		isSuccess = x;
		message = y;
		responseCode = exception;
		content = result;
	}
	
}
