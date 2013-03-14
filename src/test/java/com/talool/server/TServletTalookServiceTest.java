package com.talool.server;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.THttpClient;
import org.apache.thrift.transport.TTransportException;

import com.talool.api.thrift.CTokenAccess_t;
import com.talool.api.thrift.CustomerServiceConstants;
import com.talool.api.thrift.CustomerService_t;
import com.talool.api.thrift.Customer_t;
import com.talool.api.thrift.ServiceException_t;
import com.talool.api.thrift.Sex_t;
import com.talool.api.thrift.SocialAccount_t;
import com.talool.api.thrift.SocialNetwork_t;

public class TServletTalookServiceTest
{
	// static String servletUrl = "http://www.talool.com/api";
	static String servletUrl = "http://localhost:8080/api";

	public static void main(String args[]) throws TTransportException, TException
	{

		THttpClient thc = new THttpClient(servletUrl);
		TProtocol loPFactory = new TBinaryProtocol(thc);
		CustomerService_t.Client client = new CustomerService_t.Client(loPFactory);

		Customer_t customer = new Customer_t();
		customer.setFirstName("Christopher");
		customer.setLastName("Lintz");
		customer.setSex(Sex_t.M);
		customer.setEmail("christopher5.justin@gmail.com");

		thc.setCustomHeader("ttok", "mytokenq348977823890234");

		try
		{
			// client.registerCustomer(customer, "pass123");
			CTokenAccess_t accessToken = client.authenticate("christopher5.justin@gmail.com", "pass123");

			System.out.println("Customer: " + accessToken.getCustomer());
			System.out.println("Token: " + accessToken.getToken());

			// test update account
			SocialAccount_t sac = accessToken.getCustomer().getSocialAccounts().get(SocialNetwork_t.Facebook);
			Customer_t cust = accessToken.getCustomer();

			// set header on all calls!
			thc.setCustomHeader(CustomerServiceConstants.CTOKEN_NAME, accessToken.getToken());

			sac.setLoginId("cupdate-123");

			client.save(cust);

			// test adding social account
			SocialAccount_t twitterAccount = new SocialAccount_t();
			twitterAccount.setLoginId("twitter-login");
			twitterAccount.setSocalNetwork(SocialNetwork_t.Twitter);
			twitterAccount.setToken("twitter-token");
			cust.getSocialAccounts().put(SocialNetwork_t.Twitter, twitterAccount);
			client.save(cust);

			// test adding
			SocialAccount_t pinterest = new SocialAccount_t();
			pinterest.setLoginId("pinterest-login");
			pinterest.setSocalNetwork(SocialNetwork_t.Pinterest);
			pinterest.setToken("pinterest-token");

			client.addSocialAccount(pinterest);

		}
		catch (ServiceException_t e)
		{
			System.out.println(String.format("Error registering %d %s", e.getErrorCode(), e.getErrorDesc()));
		}

	}
}
