package com.redhat.iw;

import org.globex.Account;
import org.globex.Company;
import org.globex.Contact;
import org.globex.CorporateAccount;

public class AccountFactory {

	public AccountFactory() {
		
	}
	
	public Account createAccount() {
		Account c = new Account();
		Company company = new Company();
		company.setActive(true);
		company.setGeo("EU");
		company.setName("Name");
		c.setCompany(company);
		Contact contact = new Contact();
		contact.setCity("Birmingham");
		c.setContact(contact);
		return c;
	}
	
}
