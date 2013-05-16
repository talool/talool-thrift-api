package com.talool.server;

import java.util.List;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.THttpClient;
import org.apache.thrift.transport.TTransportException;
import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.talool.api.thrift.CTokenAccess_t;
import com.talool.api.thrift.CustomerServiceConstants;
import com.talool.api.thrift.CustomerService_t;
import com.talool.api.thrift.Customer_t;
import com.talool.api.thrift.DealAcquire_t;
import com.talool.api.thrift.DealOffer_t;
import com.talool.api.thrift.Location_t;
import com.talool.api.thrift.Merchant_t;
import com.talool.api.thrift.SearchOptions_t;
import com.talool.api.thrift.ServiceException_t;
import com.talool.api.thrift.Sex_t;
import com.talool.api.thrift.SocialAccount_t;
import com.talool.api.thrift.SocialNetwork_t;
import com.talool.core.AcquireStatusType;
import com.talool.core.service.ServiceException;

/**
 * Integration tests for the Thrift service . A running Thrift server needs to
 * be running on the servletUrl
 * 
 * @author clintz
 * 
 */
public class ServiceIntegrationTest
{
	// private static final String TEST_URL = "http://api.talool.com/1.1";
	private static final String TEST_URL = "http://localhost:8082/1.1";

	private static final String MERCHANT_NAME = "The Kitchen";
	private static final int MERCHANT_DEAL_CNT = 6;
	private static final int MERCHANT_ACQUIRE_CNT = 2;

	private static final String DEAL_OFFER_TITLE = "Payback Book Test Book #1";

	private static final String TEST_USER = "christopher.justin@gmail.com";
	private static final String TEST_USER_PASS = "pass123";
	private static final String TEST_USER_FIRST = "Chris";
	private static final String TEST_USER_LAST = "Lintz";

	private static final Location_t BOULDER_LOCATION = new Location_t(-105.2700, 40.0150);
	private static final Location_t DENVER_LOCATION = new Location_t(-104.9842, 39.7392);

	private static THttpClient tHttpClient;
	private static TProtocol protocol;
	private static CustomerService_t.Client client;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@BeforeClass
	public static void setUpThrift() throws TTransportException
	{
		tHttpClient = new THttpClient(TEST_URL);
		protocol = new TBinaryProtocol(tHttpClient);
		client = new CustomerService_t.Client(protocol);
	}

	@After
	public void cleanupClient() throws TTransportException
	{
		// ensures headers are cleared, so client calls are forced to set the header
		// when appropriate
		tHttpClient.setCustomHeaders(null);
	}

	@Test
	public void testRegisterCustomer()
			throws ServiceException_t, TException
	{
		final long now = System.currentTimeMillis();
		Customer_t customer = new Customer_t();
		customer.setFirstName("Chris-" + now);
		customer.setLastName("Lintz-" + now);
		customer.setSex(Sex_t.M);
		customer.setEmail("chris-" + System.currentTimeMillis() + "@talool.com");

		CTokenAccess_t accessToken = client.createAccount(customer, "pass123");

		Assert.assertNotNull(accessToken.getToken());
		Assert.assertEquals(customer.getEmail(), accessToken.getCustomer().getEmail());
		Assert.assertEquals(customer.getLastName(), accessToken.getCustomer().getLastName());
		Assert.assertNotNull(accessToken.getCustomer().getCustomerId());
		Assert.assertEquals(customer.getSex(), accessToken.getCustomer().getSex());

	}

	@Test
	public void testDuplicateRegisterCustomer()
			throws ServiceException_t, TException
	{
		thrown.expect(ServiceException_t.class);
		thrown.expect(new ServiceExceptionMatcher(ServiceException.Type.EMAIL_ALREADY_TAKEN.getCode()));

		Customer_t customer = new Customer_t();
		customer.setFirstName(TEST_USER_FIRST);
		customer.setLastName(TEST_USER_LAST);
		customer.setSex(Sex_t.M);
		customer.setEmail(TEST_USER);

		client.createAccount(customer, TEST_USER_PASS);

	}

	@Test
	public void testTokenRequired() throws ServiceException_t, TException
	{
		SearchOptions_t searchOptions = new SearchOptions_t();
		searchOptions.setMaxResults(1000).setPage(0).setSortProperty("merchant.name").setAscending(true);

		thrown.expect(ServiceException_t.class);
		thrown.expect(new ServiceExceptionMatcher(101));

		client.getMerchantAcquires(searchOptions);

	}

	@Test
	public void testMerchantAcquires() throws ServiceException_t, TException
	{
		CTokenAccess_t tokenAccess = client.authenticate(TEST_USER, TEST_USER_PASS);

		// remember to set the header!
		tHttpClient.setCustomHeader(CustomerServiceConstants.CTOKEN_NAME, tokenAccess.getToken());

		SearchOptions_t searchOptions = new SearchOptions_t();
		searchOptions.setMaxResults(1000).setPage(0).setSortProperty("merchant.name").setAscending(true);
		List<Merchant_t> merchants = client.getMerchantAcquires(searchOptions);

		Assert.assertEquals(MERCHANT_ACQUIRE_CNT, merchants.size());

		for (Merchant_t merc : merchants)
		{
			List<DealAcquire_t> deals = client.getDealAcquires(merc.getMerchantId(), null);

			if (merc.getName().equals("Centro Latin Kitchen"))
			{
				Assert.assertEquals(2, deals.size());
			}
			else if (merc.getName().equals(MERCHANT_NAME))
			{
				Assert.assertEquals(MERCHANT_DEAL_CNT, deals.size());
			}
			else
			{
				throw new AssertionError("Unexpected merchant " + merc.getName());
			}

			for (DealAcquire_t deal : deals)
			{

				System.out.println(deal);
			}
		}

	}

	@Ignore
	// TODO FILL IN INTEGRATION TEST
	public void testAddSocialAccounts()
			throws ServiceException_t, TException
	{
		// test adding social account
		SocialAccount_t twitterAccount = new SocialAccount_t();
		twitterAccount.setLoginId("twitter-login");
		twitterAccount.setSocalNetwork(SocialNetwork_t.Twitter);

		client.addSocialAccount(twitterAccount);
	}

	@Test
	public void testFavoriteMerchants() throws ServiceException_t, TException
	{
		SearchOptions_t searchOptions = new SearchOptions_t();
		searchOptions.setSortProperty("merchant.name");
		searchOptions.setPage(0);
		searchOptions.setAscending(true);
		searchOptions.setMaxResults(10);

		// lets first cleanup any lingering favorites (should be zero)
		CTokenAccess_t tokenAccess = client.authenticate(TEST_USER, TEST_USER_PASS);
		tHttpClient.setCustomHeader(CustomerServiceConstants.CTOKEN_NAME, tokenAccess.getToken());
		List<Merchant_t> favoriteMerchants = client.getFavoriteMerchants(searchOptions);
		for (Merchant_t merc : favoriteMerchants)
		{
			client.removeFavoriteMerchant(merc.merchantId);
		}

		// favorite all merchants acquired
		List<Merchant_t> merchantAcquires = client.getMerchantAcquires(null);
		Assert.assertEquals(MERCHANT_ACQUIRE_CNT, merchantAcquires.size());

		for (Merchant_t merc : merchantAcquires)
		{
			client.addFavoriteMerchant(merc.getMerchantId());
		}

		favoriteMerchants = client.getFavoriteMerchants(searchOptions);

		Assert.assertEquals(merchantAcquires.size(), favoriteMerchants.size());

		// now lets remove the favorites
		for (Merchant_t merc : favoriteMerchants)
		{
			client.removeFavoriteMerchant(merc.merchantId);
		}

		// should be zero
		favoriteMerchants = client.getFavoriteMerchants(searchOptions);
		Assert.assertEquals(0, favoriteMerchants.size());

	}

	@Test
	public void testDealRedemption() throws ServiceException_t, TException
	{
		final long now = System.currentTimeMillis();
		Customer_t customer = new Customer_t();
		customer.setFirstName("Chris-" + now);
		customer.setLastName("Lintz-" + now);
		customer.setSex(Sex_t.M);
		customer.setEmail("chris-" + System.currentTimeMillis() + "@talool.com");

		// Step #1 - Create a new user
		CTokenAccess_t accessToken = client.createAccount(customer, "pass123");

		// Step #2 - Browser deal offers
		List<DealOffer_t> dealOffers = client.getDealOffers();

		Assert.assertEquals(2, dealOffers.size());

		DealOffer_t selectedDealOffer = null;
		for (DealOffer_t dof : dealOffers)
		{
			if (dof.getTitle().equals(DEAL_OFFER_TITLE))
			{
				selectedDealOffer = dof;
				break;
			}
		}

		tHttpClient.setCustomHeader(CustomerServiceConstants.CTOKEN_NAME, accessToken.getToken());

		// Step #3 - Purchase a deal
		client.purchaseDealOffer(selectedDealOffer.getDealOfferId());

		// Step #4 - Get the merchants acquired
		Merchant_t selectedMerchant = null;
		List<Merchant_t> merchantAcquires = client.getMerchantAcquires(null);
		for (Merchant_t merc : merchantAcquires)
		{
			if (merc.getName().equals(MERCHANT_NAME))
			{
				selectedMerchant = merc;
			}
		}

		Assert.assertEquals(MERCHANT_ACQUIRE_CNT, merchantAcquires.size());

		// Step #5 - Get dealsAcquires by merchant
		List<DealAcquire_t> dealAcquires = client.getDealAcquires(selectedMerchant.getMerchantId(), null);
		int purchasedCnt = dealAcquires.size();

		Assert.assertEquals(MERCHANT_ACQUIRE_CNT, purchasedCnt);

		client.redeem(dealAcquires.get(0).getDealAcquireId(), BOULDER_LOCATION);
		client.redeem(dealAcquires.get(1).getDealAcquireId(), BOULDER_LOCATION);

		// Step #6 - ensure purchases were counted

		dealAcquires = client.getDealAcquires(selectedMerchant.getMerchantId(), null);

		int redeemedCnt = 0;
		int totalAcs = 0;
		for (DealAcquire_t dac : dealAcquires)
		{
			totalAcs++;
			if (dac.getStatus().equals(AcquireStatusType.REDEEMED.toString()))
			{
				redeemedCnt++;
			}
		}

		Assert.assertEquals(purchasedCnt, totalAcs);
		Assert.assertEquals(2, redeemedCnt);

	}

	@Test
	public void testGetMerchantsWithin() throws ServiceException_t, TException
	{
		SearchOptions_t searchOptions = new SearchOptions_t();
		searchOptions.setSortProperty("merchant.name");
		searchOptions.setPage(0);
		searchOptions.setAscending(true);
		searchOptions.setMaxResults(10);

		List<Merchant_t> merchants = client.getMerchantsWithin(BOULDER_LOCATION, 1, searchOptions);

		Assert.assertEquals(2, merchants.size());

		// sorting with SearchOptions ensures these arrive in this order
		Assert.assertEquals("Centro Latin Kitchen", merchants.get(0).getName());
		Assert.assertEquals(-105.2841748,
				merchants.get(0).getLocations().get(0).getLocation().getLongitude(), 0);
		Assert.assertEquals(40.0169992,
				merchants.get(0).getLocations().get(0).getLocation().getLatitude(), 0);

		Assert.assertEquals("The Kitchen", merchants.get(1).getName());
		Assert.assertEquals(-105.281686,
				merchants.get(1).getLocations().get(0).getLocation().getLongitude(), 0);
		Assert.assertEquals(40.017663,
				merchants.get(1).getLocations().get(0).getLocation().getLatitude(), 0);

		// ensure no merchants are here
		merchants = client.getMerchantsWithin(DENVER_LOCATION, 10, searchOptions);
		Assert.assertEquals(0, merchants.size());

	}

	@Test
	public void testAuthenticate() throws ServiceException_t, TException
	{
		CTokenAccess_t tokenAccess = client.authenticate(TEST_USER, TEST_USER_PASS);

		Assert.assertNotNull(tokenAccess.getToken());
		Assert.assertEquals(TEST_USER_FIRST, tokenAccess.getCustomer().getFirstName());
		Assert.assertEquals(TEST_USER_LAST, tokenAccess.getCustomer().getLastName());

	}
}
