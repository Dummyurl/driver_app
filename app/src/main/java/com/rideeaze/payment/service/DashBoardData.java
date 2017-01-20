package com.rideeaze.payment.service;

import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

public class DashBoardData {
	@Element
	public double TodaysTotalValue;
	
	@ElementList
    public List<String> RecentTransactions=new ArrayList<String>();
	
//	public DashBoardData(@Element(name="TodaysTotalValue") double x, @Element(name="RecentTransactions") ArrayList<String> y){
//		TodaysTotalValue = x;
//		RecentTransactions = y;
//	}
}
