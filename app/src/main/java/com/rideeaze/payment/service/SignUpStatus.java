package com.rideeaze.payment.service;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name="Result")
public class SignUpStatus {
	
	@Element(required=false)
	public String Message;
	
	@Element(required=false)
    public Boolean Success;
	
	public SignUpStatus(@Element(name="Message") String message, @Element(name="Success") Boolean success) {
		Message = message;
		Success = success;
	}
}
