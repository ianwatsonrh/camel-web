package com.redhat.iw.routes;

import java.io.FileInputStream;
import java.util.Map;

import org.apache.camel.CamelExecutionException;
import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.component.cxf.DataFormat;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.model.DataFormatDefinition;
import org.apache.camel.model.ModelHelper;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.apache.xbean.spring.context.ClassPathXmlApplicationContext;
import org.globex.Account;
import org.globex.Company;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;

public class DozerTest extends CamelSpringTestSupport {

	@Override
	protected AbstractApplicationContext createApplicationContext() {
		return new ClassPathXmlApplicationContext("META-INF/spring/camel-context.xml");
	}
	
	@Produce(uri = "file://src/data/inbox?fileName=customers.csv&noop=true")
	protected ProducerTemplate template;
	
	@EndpointInject(uri = "mock:activemq:orders.incoming")
    protected MockEndpoint resultEndpoint;
	
	@EndpointInject(uri = "mock:dozer:transformCSV")
	protected MockEndpoint dozerEndpoint;
	
	
	@Test
	public void testDozer() throws InterruptedException {
		
		dozerEndpoint.expectedBodiesReceived("Fail");
		
		template.sendBody("Robocops,NA,true,Bill,Smith,100 N Park Ave.,Phoenix,AZ,85017,200-555-1000"
				  +System.lineSeparator()+
				  "MountainBikers,SA,true,George,Jungle,1101 Smith St.,Raleigh,NC,27519,600-555-7000");
		
		Thread.sleep(5000);
		
		dozerEndpoint.assertIsSatisfied();
	}
	
	
	@Test
	public void testFullRoute() throws CamelExecutionException, Exception {
		
		resultEndpoint.expectedBodiesReceived(
				"{\"company\":{\"name\":\"Robocops\",\"geo\":\"NA\",\"active\":true},\"contact\":{\"firstName\":\"Bill\",\"lastName\":\"Smith\",\"streetAddr\":\"100 N Park Ave.\",\"city\":\"Phoenix\",\"state\":\"AZ\",\"zip\":\"85017\",\"phone\":\"200-555-1000\"},\"clientId\":0,\"salesRepresentative\":null}",
				"{\"company\":{\"name\":\"MountainBikers\",\"geo\":\"SA\",\"active\":true},\"contact\":{\"firstName\":\"George\",\"lastName\":\"Jungle\",\"streetAddr\":\"1101 Smith St.\",\"city\":\"Raleigh\",\"state\":\"NC\",\"zip\":\"27519\",\"phone\":\"600-555-7000\"},\"clientId\":0,\"salesRepresentative\":null}"
			);
		resultEndpoint.expectedMessageCount(2);
		
		template.sendBody("Robocops,NA,true,Bill,Smith,100 N Park Ave.,Phoenix,AZ,85017,200-555-1000"
						  +System.lineSeparator()+
						  "MountainBikers,SA,true,George,Jungle,1101 Smith St.,Raleigh,NC,27519,600-555-7000");
		
		Thread.sleep(5000);
		
		resultEndpoint.assertIsSatisfied();
	}
	
	@Override
	public boolean isUseAdviceWith() {
		return true;
	}
	
	@Before
	public void mockEndpoints() throws Exception {
		AdviceWithRouteBuilder advice = new AdviceWithRouteBuilder() {
			
			@Override
			public void configure() throws Exception {				
				mockEndpoints();			
			}
		};
		
		
		context.getRouteDefinition("CSV2JSON").adviceWith(context, advice);
		//context.startRoute("CSV2JSON");
		context.start();
		
		String xml = ModelHelper.dumpModelAsXml(context, context.getRouteDefinition("CSV2JSON"));
		System.out.println("XML -> " + xml);
	}

	private String readFile(String filePath) throws Exception {
        String content;
        FileInputStream fis = new FileInputStream(filePath);
        try {
            content = createCamelContext().getTypeConverter().convertTo(String.class, fis);
        } finally {
            fis.close();
        }
        return content;
    }
	
}
