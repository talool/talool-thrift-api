package com.talool.server;

import java.util.List;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.THttpClient;
import org.apache.thrift.transport.TTransportException;

import com.talool.api.thrift.CTokenAccess_t;
import com.talool.api.thrift.CustomerServiceConstants;
import com.talool.api.thrift.CustomerService_t;
import com.talool.api.thrift.Customer_t;
import com.talool.api.thrift.Deal_t;
import com.talool.api.thrift.Merchant_t;
import com.talool.api.thrift.ServiceException_t;
import com.talool.api.thrift.Sex_t;
import com.talool.api.thrift.SocialAccount_t;
import com.talool.api.thrift.SocialNetwork_t;

public class ThriftServiceIntegrationTest
{
	// static String servletUrl = "http://www.talool.com/api";
	// static String servletUrl = "http://api.talool.com/1.1";
	static String servletUrl = "http://localhost:8080/1.1";

	public static void main(String args[]) throws TTransportException, TException
	{

		Long now = System.currentTimeMillis();
		// String email = "christopher" + now + "justin@gmail.com";
		String email = "christopher.justin@gmail.com";

		THttpClient thc = new THttpClient(servletUrl);
		TProtocol loPFactory = new TBinaryProtocol(thc);
		CustomerService_t.Client client = new CustomerService_t.Client(loPFactory);

		CTokenAccess_t accessToken = client.authenticate(email, "pass123");

		thc.setCustomHeader(CustomerServiceConstants.CTOKEN_NAME, accessToken.getToken());
		List<Merchant_t> merchants = client.getMerchants();

		for (Merchant_t merc : merchants)
		{
			System.out.println(merc);

			List<Deal_t> deals = client.getDeals(merc.getMerchantId());

			for (Deal_t deal : deals)
			{
				System.out.println(deal);
			}
		}

		Customer_t customer = new Customer_t();
		customer.setFirstName("Billy" + now);
		customer.setLastName("Lintz" + now);
		customer.setSex(Sex_t.F);
		customer.setEmail(email);

		try
		{
			accessToken = client.createAccount(customer, "pass123");

			System.out.println("Email Exists: " + client.customerEmailExists(email));

			accessToken = client.authenticate(email, "pass123");

			System.out.println("Customer: " + accessToken.getCustomer());
			System.out.println("Token: " + accessToken.getToken());

			Customer_t cust = accessToken.getCustomer();

			// set header on all calls!
			thc.setCustomHeader(CustomerServiceConstants.CTOKEN_NAME, accessToken.getToken());

			// test adding social account
			SocialAccount_t twitterAccount = new SocialAccount_t();
			twitterAccount.setLoginId("twitter-login");
			twitterAccount.setSocalNetwork(SocialNetwork_t.Twitter);
			twitterAccount.setToken("twitter-token");

			client.addSocialAccount(twitterAccount);

		}
		catch (ServiceException_t e)
		{
			System.out.println(String.format("Error registering %d %s", e.getErrorCode(),
					e.getErrorDesc()));
		}

	}
}
