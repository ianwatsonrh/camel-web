package com.redhat.iw.routes;

import org.apache.camel.builder.RouteBuilder;

public class DLQRouteBuilder extends RouteBuilder {

	@EndpointInject
	Endpoint dlq;
	
	@Override
	public void configure() throws Exception {
	

		
	}

}
