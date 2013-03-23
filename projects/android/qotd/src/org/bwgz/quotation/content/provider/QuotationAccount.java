package org.bwgz.quotation.content.provider;

import android.accounts.Account;

public class QuotationAccount extends Account {
	static public String NAME = "quotation";
	static public String TYPE = "org.bwgz.quotation";
	
	public QuotationAccount() {
		super(NAME, TYPE);
	}
}
