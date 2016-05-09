package com.redhat.iw.routes;

import org.apache.camel.BeanInject;
import org.apache.camel.Endpoint;
import org.apache.camel.EndpointInject;
import org.apache.camel.builder.RouteBuilder;
import org.globex.usecase.ProcessorBean;

public class DBInsert extends RouteBuilder {

	@EndpointInject( uri = "direct:dbInsert")
	Endpoint input;
	
	@BeanInject ("sqlBean")
	ProcessorBean process;
	
	@EndpointInject( uri = "sql:{{insertSQL}}?dataSource=usecaseDB")
	Endpoint database;
	
	@Override
	public void configure() throws Exception {
		
		from(input).routeId("DBInsert").startupOrder(500)
			.log("Inserting into DB")
			.bean(process,"defineNamedParameters")
			.to(database);
					
	}	
}
