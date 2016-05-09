package com.redhat.iw.routes;

import org.apache.camel.Endpoint;
import org.apache.camel.EndpointInject;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.cdi.Uri;

import com.redhat.iw.processors.FailureProcessor;

public class CSV2JSONRouteBuilder extends RouteBuilder {

	@EndpointInject	(ref = "dozerTransform")
	Endpoint transform;
	
	@EndpointInject (uri = "{{fileInput}}")
	Endpoint input;
	
	@EndpointInject (ref = "orderInputQueue")
	Endpoint output;
	
	@EndpointInject (ref = "orderDQL")
	Endpoint error;
	
	@Override
	public void configure() throws Exception {
		// TODO Auto-generated method stub
		onException(java.lang.IllegalArgumentException.class)
			.handled(true)
			.log("A illegal argument exception has occurred")
			.process(new FailureProcessor())
			.to(error);
		
		
		from(input).routeId("CSV2JSON")
			.split(body(String.class).tokenize("\n"))
				.to(transform)
				.to(output);	
	}

}