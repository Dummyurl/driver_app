package com.rideeaze.payment.service;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name="Result")
public class LoginStatus implements IResult{

	@Element(required=false)
	public boolean Success;
	
	@Element(required=false)
    public String SessionCode;
	
	@Element(required=false)
    public String FirstName;
	
	@Element(required=false)
    public String LastName;
	
	@Element(required=false)
    public String Address;
	
	@Element(required=false)
    public String PrintName;
	
	@Element(required=false)
    public String PhoneNo;
	
	@Element(required=false)
    public String UserId;
	
	public LoginStatus(@Element(name="Success") Boolean success, 
			@Element(name="SessionCode") String sessionCode, 
			@Element(name = "FirstName") String firstName, 
			@Element(name = "LastName") String lastName,
			@Element(name = "Address") String address,
			@Element(name = "PrintName") String printName,
			@Element(name = "PhoneNo") String phoneNo,
			@Element(name = "UserId") String userId) {
		Success = success;
		SessionCode = sessionCode;
		FirstName = firstName;
		LastName = lastName;
		Address = address;
		PrintName = printName;
		PhoneNo = phoneNo;
		UserId= userId;
		
	}
    
}
