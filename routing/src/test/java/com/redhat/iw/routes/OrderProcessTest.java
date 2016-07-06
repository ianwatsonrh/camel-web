package com.redhat.iw.routes;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.camel.CamelContext;
import org.apache.camel.CamelExecutionException;
import org.apache.camel.EndpointInject;
import org.apache.camel.NamedNode;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.DefaultNodeIdFactory;
import org.apache.camel.spi.NodeIdFactory;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.apache.xbean.spring.context.ClassPathXmlApplicationContext;
import org.globex.Account;
import org.globex.Company;
import org.globex.Contact;
import org.globex.CorporateAccount;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;

public class OrderProcessTest extends CamelSpringTestSupport {

	@Override
	protected AbstractApplicationContext createApplicationContext() {
		return new ClassPathXmlApplicationContext("META-INF/spring/camel-context.xml");
	}
	
	@Produce(uri = "direct:dbInsert")
	protected ProducerTemplate template;
	
	@EndpointInject(uri = "mock:sql:{{insertSQL}}")
    protected MockEndpoint resultEndpoint;
	
	@Produce(uri = "direct:callRestEndpoint")
	protected ProducerTemplate restInvoke;
	
	@EndpointInject(uri = "mock:restResult")
    protected MockEndpoint restResultEndpoint;
	
	@Test 
	public void testRestService() {
		Account a = new Account();
		Company c = new Company();
		c.setGeo("NA");
		a.setCompany(c);
		
		restInvoke.sendBody(a);
		
		Account resultAccount = (Account) restResultEndpoint.getExchanges().get(0).getIn().getBody();
		assertEquals("NORTH_AMERICA",resultAccount.getCompany().getGeo());
				
	}
	
	@Test
	public void testFakeDBInsert() throws CamelExecutionException, Exception {
		CorporateAccount c = new CorporateAccount();
		Company company = new Company();
		company.setActive(true);
		company.setGeo("EU");
		company.setName("Name");
		c.setCompany(company);
		c.setId(123);
		c.setSalesContact("SalesContact");
		Contact contact = new Contact();
		contact.setCity("Birmingham");
		c.setContact(contact);
		
		template.sendBody(c);
		
		@SuppressWarnings("unchecked")
		Map<String, Object> resultMap = (Map<String,Object>)resultEndpoint.getExchanges().get(0).getIn().getBody();
		
		assertEquals(resultMap.get("SALES_CONTACT"),"SalesContact");
		assertEquals(resultMap.get("CONTACT_CITY"),"Birmingham");
		
		
		
	
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
				mockEndpointsAndSkip("sql:*");
				weaveById("customerRestServiceClient").after().to("mock:restResult");
			}
		};
		
		context.getRouteDefinition("InvokeRS").adviceWith(context, advice);
		//context.startRoute("CSV2JSON");
		
		//DefaultNodeIdFactory default = new DefaultNodeIdFactory();
		class MyFactory implements NodeIdFactory {
			
			CamelContext c;
			
			public void setContext(CamelContext c) {
				this.c = c;
			}
			
			@Override
			public String createId(NamedNode definition) {
				// TODO Auto-generated method stub
				System.out.println("prinitng -> " + definition);
				return null;
			}
			
		}
		
		MyFactory factory = new MyFactory();
		factory.setContext(context);
		context.setNodeIdFactory(factory);
		/*context.setNodeIdFactory(new NodeIdFactory() {
			
			
			@Override
			public String createId(NamedNode definition) {
				
				// TODO Auto-generated method stub
				System.out.println("Printing out " + definition.toString());
				return null;
			}
		});*/
		
		context.start();
		
	}
	
}
