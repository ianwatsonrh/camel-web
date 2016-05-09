package com.redhat.iw.routes;

import org.apache.camel.BeanInject;
import org.apache.camel.Endpoint;
import org.apache.camel.EndpointInject;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.globex.Account;
import org.globex.usecase.AccountAggregator;
import org.globex.usecase.service.CustomerWSImpl;

public class OrderProcess extends RouteBuilder {

	@BeanInject
	CustomerWSImpl customer;
	
	@EndpointInject (uri = "file://src/data/inbox?noop=true&include=(.)*.json")
	Endpoint orderInput;
	
	@EndpointInject (uri = "direct:callRestEndpoint")
	Endpoint restEndpoint;
	
	@EndpointInject (uri = "direct:callWSEndpoint")
	Endpoint wsEndpoint;
	
	@Override
	public void configure() throws Exception {
		from(orderInput).routeId("OrderProcess")
			.log("Body is -> ${body}")
			.unmarshal().json(Account.class,null)
			.multicast().aggregationStrategy(new AccountAggregator()).parallelProcessing()
				.to(restEndpoint)
				.to(wsEndpoint)
			.end()
			.to("direct:dbInsert");
		
		
		from(wsEndpoint).routeId("InvokeWS")
			.log("Invoking WS endpoint with body -> ${body}")
			.to("cxf:bean:customerWebservice");
		
		
		from(restEndpoint).routeId("InvokeRS")
			.log("Invoking RS endpoint with body -> ${body}")
			.setHeader("Content-Type",constant("application/json"))
			.setHeader("Exchange.HTTP_PATH",constant("/customer/enrich"))
			.to("cxfrs:bean:customerRestServiceClient")
			.log("Response from RS endpoint is -> ${body}");
		
	}

}
