package com.redhat.iw.routes;

import org.apache.camel.BeanInject;
import org.apache.camel.Endpoint;
import org.apache.camel.EndpointInject;
import org.apache.camel.builder.RouteBuilder;

import com.redhat.iw.beans.DLQBean;

public class DLQRouteBuilder extends RouteBuilder {

	@EndpointInject(ref = "orderDQL")
	Endpoint dlq;
	
	@BeanInject ("dlqSQLBean")
	DLQBean sqlBean;
	
	//@EndpointInject( uri = "sql:{{insertDQLSQL}}?dataSource=usecaseDB")
	@EndpointInject (uri = "jpa:com.redhat.iw.beans.DLQEntity")
	Endpoint database;
	
	@Override
	public void configure() throws Exception {
	
		from(dlq).routeId("DLQRoute")
			.log("GOT FROM ERROR QUEUE")
			.bean(sqlBean,"error")
			.log(">>>>> Body is now -> ${body}")
			.to(database);
		
	}

}
