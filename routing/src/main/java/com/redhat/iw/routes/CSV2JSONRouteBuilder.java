package com.redhat.iw.routes;

import org.apache.camel.Endpoint;
import org.apache.camel.EndpointInject;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.cdi.Uri;

public class CSV2JSONRouteBuilder extends RouteBuilder {

	@EndpointInject	(uri = "dozer:transformCSV?unmarshalId=csv&marshalId=myjson&mappingFile=transformations/csv2json.xml&targetModel=org.globex.Account")
	Endpoint transform;
	
	@EndpointInject (uri = "{{fileInput}}")
	Endpoint input;
	
	@EndpointInject (uri = "{{fileOutput}}")
	Endpoint output;
	
	@EndpointInject (uri = "{{fileError}}")
	Endpoint error;
	
	@Override
	public void configure() throws Exception {
		// TODO Auto-generated method stub
		onException(java.lang.IllegalArgumentException.class)
			.handled(true)
			.to(error);
		
		from(input).routeId("CSV2JSON")
			.split(body(String.class).tokenize("\n"))
				.to(transform)
				.to(output);	
	}

}