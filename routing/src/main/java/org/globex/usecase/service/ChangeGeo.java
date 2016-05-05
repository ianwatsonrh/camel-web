package org.globex.usecase.service;

import org.globex.Account;

public class ChangeGeo {

	public Account changeGeo(Account account) {
		String oldGeo = account.getCompany().getGeo();
		String newGeo = GeoEnum.valueOf(oldGeo).getLocation();
		account.getCompany().setGeo(newGeo);
        return account;
	}
	
}
