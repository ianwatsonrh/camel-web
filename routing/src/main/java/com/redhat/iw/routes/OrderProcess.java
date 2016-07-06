package com.redhat.iw.routes;

import org.apache.camel.BeanInject;
import org.apache.camel.Endpoint;
import org.apache.camel.EndpointInject;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.rest.RestBindingMode;
import org.globex.Account;
import org.globex.usecase.AccountAggregator;
import org.globex.usecase.service.CustomerWSImpl;

import com.redhat.iw.AccountFactory;

public class OrderProcess extends RouteBuilder {

	@BeanInject
	CustomerWSImpl customer;
	
	@EndpointInject (ref = "orderInputQueue")
			//uri = "file://src/data/inbox?noop=true&include=(.)*.json")
	Endpoint orderInput;
	
	@EndpointInject (uri = "direct:callRestEndpoint")
	Endpoint restEndpoint;
	
	@EndpointInject (uri = "direct:callWSEndpoint")
	Endpoint wsEndpoint;
	
	
	
	@Override
	public void configure() throws Exception {
		from(orderInput).routeId("OrderProcess")
			.log("Order Process body is -> ${body}")
			.unmarshal().json(Account.class,null)
			.multicast().aggregationStrategy(new AccountAggregator()).parallelProcessing()
				.to(restEndpoint)
				.to(wsEndpoint)
			.end()
			.to("direct:dbInsert");
		
		
		from(wsEndpoint).routeId("InvokeWS")
			.log("Invoking WS endpoint with body -> ${body}")
			.to("cxf:bean:customerWebservice");
		
		
		from(restEndpoint).routeId("InvokeRS").tracing()
			.log("Invoking RS endpoint with body -> ${body}")
			.setHeader("Content-Type",constant("application/json"))
			.setHeader("Exchange.HTTP_PATH",constant("/customer/enrich"))
			.to("cxfrs:bean:customerRestServiceClient").id("customerRestServiceClient")
			.log("Response from RS endpoint is -> ${body}");
		
		
		restConfiguration().component("jetty").port(9111).bindingMode(RestBindingMode.auto);
		rest("/customer").post("/enrich").type(Account.class).route().beanRef("changeGeo");
		
		
		
		from("direct:testRestService")
			.beanRef("accountFactory","createAccount")
			.log("Rest service test Body is now -> " + simple("${body}"))
			.marshal().json(JsonLibrary.Jackson)
			.log("Rest service test is marhsalled and is now -> " + simple("${body}"))
			.to("http4://ianwatson-OSX.local:9111/customer/enrich")
			.log("Invoked body is now -> " + simple("${body}"));
		
		from("timer://foo?fixedRate=true&period=5s")
			.to("direct:testRestService");
		
	}

}
