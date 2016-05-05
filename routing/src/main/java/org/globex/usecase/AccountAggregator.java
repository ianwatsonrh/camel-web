package org.globex.usecase;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.apache.cxf.message.MessageContentsList;
import org.globex.Account;
import org.globex.CorporateAccount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Aggregator implementation which extract the id and salescontact
 * from CorporateAccount and update the Account
 */
public class AccountAggregator implements AggregationStrategy {

	Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        if (oldExchange == null) {
            return newExchange;
        }
        
        if (oldExchange.getIn().getBody() instanceof Account) {
        	Account a = (Account) oldExchange.getIn().getBody();
        	CorporateAccount b = (CorporateAccount) ((MessageContentsList) newExchange.getIn().getBody()).get(0);
        	b.getCompany().setGeo(a.getCompany().getGeo());
        	newExchange.getIn().setBody(b);
        } else {
        	// instance of CorporateAccount
        	CorporateAccount a = (CorporateAccount) ((MessageContentsList) oldExchange.getIn().getBody()).get(0);
        	Account b = (Account) newExchange.getIn().getBody();
        	a.getCompany().setGeo(b.getCompany().getGeo());
        	newExchange.getIn().setBody(a);
        }
        
        return newExchange;
    }
    
}