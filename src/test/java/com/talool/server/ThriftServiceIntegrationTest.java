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
import com.talool.api.thrift.DealAcquire_t;
import com.talool.api.thrift.Merchant_t;
import com.talool.api.thrift.SearchOptions_t;
import com.talool.api.thrift.ServiceException_t;
import com.talool.api.thrift.Sex_t;
import com.talool.api.thrift.SocialAccount_t;
import com.talool.api.thrift.SocialNetwork_t;
import com.talool.api.thrift.SortType_t;

public class ThriftServiceIntegrationTest
{
	// static String servletUrl = "http://api.talool.com/1.1";

	static String servletUrl = "http://localhost:8080/1.1";

	public static CTokenAccess_t testRegisterCustomer(CustomerService_t.Client client)
			throws ServiceException_t, TException
	{
		final long now = System.currentTimeMillis();
		Customer_t customer = new Customer_t();
		customer.setFirstName("Chris-" + now);
		customer.setLastName("Lintz-" + now);
		customer.setSex(Sex_t.M);
		customer.setEmail("chris-" + System.currentTimeMillis() + "@talool.com");

		CTokenAccess_t accessToken = client.createAccount(customer, "pass123");

		return accessToken;

	}

	public static void testAddSocialAccounts(final CustomerService_t.Client client)
			throws ServiceException_t, TException
	{
		// test adding social account
		SocialAccount_t twitterAccount = new SocialAccount_t();
		twitterAccount.setLoginId("twitter-login");
		twitterAccount.setSocalNetwork(SocialNetwork_t.Twitter);

		client.addSocialAccount(twitterAccount);
	}

	public static CTokenAccess_t testAuthenticate(final CustomerService_t.Client client,
			final String email, String pass) throws ServiceException_t, TException
	{
		return client.authenticate(email, "pass123");
	}

	public static void main(String args[]) throws TTransportException, TException
	{

		String email = "christopher.justin@gmail.com";
		THttpClient thc = new THttpClient(servletUrl);
		TProtocol loPFactory = new TBinaryProtocol(thc);
		CustomerService_t.Client client = new CustomerService_t.Client(loPFactory);

		CTokenAccess_t accessToken = testAuthenticate(client, email, "pass123");

		thc.setCustomHeader(CustomerServiceConstants.CTOKEN_NAME, accessToken.getToken());

		SearchOptions_t searchOptions = new SearchOptions_t();
		searchOptions.setMaxResults(1).setPage(0).setSortProperty("name").setSortType(SortType_t.Asc);
		List<Merchant_t> merchants = client.getMerchantAcquires(searchOptions);

		for (Merchant_t merc : merchants)
		{
			System.out.println(merc);

			List<DealAcquire_t> deals = client.getDealAcquires(merc.getMerchantId(), null);

			for (DealAcquire_t deal : deals)
			{
				System.out.println(deal);
			}
		}

		try
		{
			accessToken = testRegisterCustomer(client);

			System.out.println("Email Exists: " + client.customerEmailExists(email));
			accessToken = client.authenticate(accessToken.getCustomer().getEmail(), "pass123");

			System.out.println("Customer: " + accessToken.getCustomer());
			System.out.println("Token: " + accessToken.getToken());

			Customer_t cust = accessToken.getCustomer();

		}
		catch (ServiceException_t e)
		{
			System.out.println(String.format("Error registering %d %s", e.getErrorCode(),
					e.getErrorDesc()));
		}

	}
}
