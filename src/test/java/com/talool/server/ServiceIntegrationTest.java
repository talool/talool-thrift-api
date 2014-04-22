package com.talool.server;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.time.DateUtils;
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

import com.talool.api.thrift.AcquireStatus_t;
import com.talool.api.thrift.Activity_t;
import com.talool.api.thrift.CTokenAccessResponse_t;
import com.talool.api.thrift.CTokenAccess_t;
import com.talool.api.thrift.Category_t;
import com.talool.api.thrift.CoreConstants;
import com.talool.api.thrift.CustomerServiceConstants;
import com.talool.api.thrift.CustomerService_t;
import com.talool.api.thrift.Customer_t;
import com.talool.api.thrift.DealAcquire_t;
import com.talool.api.thrift.DealOfferGeoSummariesResponse_t;
import com.talool.api.thrift.DealOfferGeoSummary_t;
import com.talool.api.thrift.DealOffer_t;
import com.talool.api.thrift.Deal_t;
import com.talool.api.thrift.EmailResponse_t;
import com.talool.api.thrift.Gift_t;
import com.talool.api.thrift.Location_t;
import com.talool.api.thrift.MerchantLocation_t;
import com.talool.api.thrift.Merchant_t;
import com.talool.api.thrift.MerchantsResponse_t;
import com.talool.api.thrift.PropertyConstants;
import com.talool.api.thrift.SearchOptions_t;
import com.talool.api.thrift.ServiceException_t;
import com.talool.api.thrift.Sex_t;
import com.talool.api.thrift.SocialAccount_t;
import com.talool.api.thrift.SocialNetwork_t;
import com.talool.api.thrift.ValidateCodeResponse_t;
import com.talool.service.ActivityEmailTemplateType;
import com.talool.service.ErrorCode;

/**
 * Integration tests for the Thrift service . A running Thrift server needs to
 * be running on the servletUrl
 * 
 * @author clintz
 * 
 *         TODO - create testGetAcquiredMerchantsLocation that tests mutliple
 *         locations not being null
 */

@Ignore
public class ServiceIntegrationTest
{

	// private static final String TEST_URL = "http://localhost:8082/1.1";

	// dev-api1
	private static final String TEST_URL = "http://dev-api.talool.com/1.1";
	// private static final String TEST_URL = "http://api.talool.com/1.1";

	// private static final String TEST_URL = "http://localhost:8082/1.1";

	private static final String MERCHANT_KITCHEN = "The Kitchen";
	private static final int MERCHANT_DEAL_CNT = 6;
	private static final int MERCHANT_ACQUIRE_CNT = 2;

	private static final String MERCHANT_CENTRO = "Centro Latin Kitchen";

	private static final String DEAL_OFFER_TITLE = "Payback Book Test Book #1";

	private static final String TEST_USER = "christopher.justin@gmail.com";
	private static final String TEST_USER_PASS = "pass123";
	private static final String TEST_USER_FIRST = "Chris";
	private static final String TEST_USER_LAST = "Lintz";

	public static final String GCM_DEVICE_TOKEN = "GcmDeviceToken";

	/**
	 * Generic deviceId for mobile
	 */
	public static final String DEVICE_ID = "DeviceId";

	/**
	 * Apple push notification device token
	 */
	public static final String APN_DEVICE_TOKEN = "ApnDeviceToken";

	private static final Location_t Boulder_CO = new Location_t(-105.2700, 40.0150);
	private static final Location_t Denver_CO = new Location_t(-104.9842, 39.7392);
	private static final Location_t Binghamton_NY = new Location_t(-75.98, 42.23);
	private static final Location_t Rochester_NY = new Location_t(-77.6114, 43.1656);

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
	public void testCategories() throws ServiceException_t, TException
	{
		List<Category_t> categories = client.getCategories();
		Map<String, Boolean> expectedCategores = new HashMap<String, Boolean>();
		expectedCategores.put("Food", false);
		expectedCategores.put("Shopping Services", false);
		expectedCategores.put("Fun", false);
		expectedCategores.put("Nightlife", false);

		for (Category_t cat : categories)
		{
			expectedCategores.put(cat.name, true);
		}

		for (Entry<String, Boolean> entry : expectedCategores.entrySet())
		{
			Assert.assertEquals("Category " + entry.getKey() + " not found", true, entry.getValue());

		}

	}

	@Test
	public void testRegisterCustomerByEmail() throws ServiceException_t, TException
	{

		final long now = System.currentTimeMillis();
		Customer_t customer = new Customer_t();
		customer.setFirstName("Chris-" + now);
		customer.setLastName("Lintz-" + now);
		customer.setSex(Sex_t.M);
		customer.setEmail("christopher.justin" + System.currentTimeMillis() + "@gmail.com");

		customer.setBirthDate(DateUtils.addYears(Calendar.getInstance().getTime(), -35).getTime());

		CTokenAccess_t accessToken = client.createAccount(customer, "pass123");

		Assert.assertNotNull(accessToken.getToken());
		Assert.assertEquals(customer.getEmail(), accessToken.getCustomer().getEmail());
		Assert.assertEquals(customer.getLastName(), accessToken.getCustomer().getLastName());
		Assert.assertNotNull(accessToken.getCustomer().getCustomerId());
		Assert.assertEquals(customer.getSex(), accessToken.getCustomer().getSex());

	}

	@Test
	public void testFacebookLogin() throws TException
	{
		CTokenAccessResponse_t response = client.loginFacebook("630941208", null);
		System.out.println(response);
	}

	@Test
	public void testSocialAccounts() throws ServiceException_t, TException
	{
		long now = System.currentTimeMillis();
		String email = "chris-" + now + "@talool.com";
		String facebookId = "fbloginid-" + now;
		String firstName = "Chris-" + now;
		String lastName = "Lintz-" + now;
		String password = "pass123";

		Customer_t customer = new Customer_t();
		customer.setFirstName(firstName);
		customer.setLastName(lastName);
		customer.setSex(Sex_t.M);
		customer.setEmail(email);

		Map<SocialNetwork_t, SocialAccount_t> socialAccounts = new HashMap<SocialNetwork_t, SocialAccount_t>();
		socialAccounts.put(SocialNetwork_t.Facebook, new SocialAccount_t(SocialNetwork_t.Facebook, facebookId));
		customer.setSocialAccounts(socialAccounts);

		// create account
		CTokenAccess_t accessToken = client.createAccount(customer, password);

		customer = accessToken.getCustomer();

		Assert.assertNotNull(accessToken.getToken());
		Assert.assertEquals(customer.getEmail(), accessToken.getCustomer().getEmail());
		Assert.assertEquals(customer.getLastName(), accessToken.getCustomer().getLastName());
		Assert.assertNotNull(accessToken.getCustomer().getCustomerId());
		Assert.assertEquals(customer.getSex(), accessToken.getCustomer().getSex());

		// test social accounts
		Assert.assertEquals(1, customer.getSocialAccountsSize());
		Assert.assertEquals(facebookId, customer.getSocialAccounts().get(SocialNetwork_t.Facebook).getLoginId());
		Assert.assertNotNull(facebookId, customer.getSocialAccounts().get(SocialNetwork_t.Facebook).getCreated());

		// test removing social account
		tHttpClient.setCustomHeader(CustomerServiceConstants.CTOKEN_NAME, accessToken.getToken());

		client.removeSocialAccount(SocialNetwork_t.Facebook);

		accessToken = client.authenticate(email, password);
		Assert.assertEquals(0, accessToken.getCustomer().getSocialAccountsSize());

		// add another one
		client.addSocialAccount(new SocialAccount_t(SocialNetwork_t.Facebook, facebookId));
		accessToken = client.authenticate(email, password);
		Assert.assertEquals(1, accessToken.getCustomer().getSocialAccountsSize());
		Assert.assertEquals(facebookId, customer.getSocialAccounts().get(SocialNetwork_t.Facebook).getLoginId());
		Assert.assertNotNull(facebookId, customer.getSocialAccounts().get(SocialNetwork_t.Facebook).getCreated());

	}

	@Test
	public void testDuplicateRegisterCustomer() throws ServiceException_t, TException
	{
		thrown.expect(ServiceException_t.class);
		thrown.expect(new ServiceExceptionMatcher(ErrorCode.EMAIL_ALREADY_TAKEN.getCode()));

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

			Assert.assertNotNull(merc.getCategory());
			if (merc.getName().equals(MERCHANT_CENTRO))
			{
				Assert.assertEquals(2, deals.size());
			}
			else if (merc.getName().equals(MERCHANT_KITCHEN))
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
	public void testAddSocialAccounts() throws ServiceException_t, TException
	{
		// test adding social account
		SocialAccount_t twitterAccount = new SocialAccount_t();
		twitterAccount.setLoginId("twitter-login");
		twitterAccount.setSocialNetwork(SocialNetwork_t.Twitter);

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
		CTokenAccess_t tokenAccess = client.authenticate("chris@talool.com", "pass123");
		tHttpClient.setCustomHeader(CustomerServiceConstants.CTOKEN_NAME, tokenAccess.getToken());

		client.addFavoriteMerchant("10ddf6d2-50e5-4f6c-bd64-1bea63a21fc8");

		// lets first cleanup any lingering favorites (should be zero)
		// CTokenAccess_t tokenAccess = client.authenticate(TEST_USER,
		// TEST_USER_PASS);
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
			if (merc.getName().equals(MERCHANT_KITCHEN))
			{
				selectedMerchant = merc;
			}
		}

		Assert.assertEquals(MERCHANT_ACQUIRE_CNT, merchantAcquires.size());

		// Step #5 - Get dealsAcquires by merchant
		List<DealAcquire_t> dealAcquires = client.getDealAcquires(selectedMerchant.getMerchantId(), null);
		int purchasedCnt = dealAcquires.size();

		Assert.assertEquals(MERCHANT_ACQUIRE_CNT, purchasedCnt);

		Assert.assertNotNull(dealAcquires.get(1).getDealAcquireId());
		Assert.assertNotNull(dealAcquires.get(0).getDealAcquireId());

		String redemptionCode = client.redeem(dealAcquires.get(0).getDealAcquireId(), Boulder_CO);
		Assert.assertNotNull(redemptionCode);

		redemptionCode = client.redeem(dealAcquires.get(1).getDealAcquireId(), Boulder_CO);

		Assert.assertNotNull(redemptionCode);

		// Step #6 - ensure purchases were counted

		dealAcquires = client.getDealAcquires(selectedMerchant.getMerchantId(), null);

		int redeemedCnt = 0;
		int totalAcs = 0;
		for (DealAcquire_t dac : dealAcquires)
		{
			totalAcs++;
			if (dac.getStatus().equals(AcquireStatus_t.REDEEMED))
			{
				redeemedCnt++;
			}
		}

		Assert.assertEquals(purchasedCnt, totalAcs);
		Assert.assertEquals(2, redeemedCnt);

	}

	@Test
	public void testGetAcquiredMerchantsLocation() throws ServiceException_t, TException
	{
		SearchOptions_t searchOptions = new SearchOptions_t();
		searchOptions.setSortProperty("merchant.name");
		searchOptions.setPage(0);
		searchOptions.setAscending(true);
		searchOptions.setMaxResults(10000);

		CTokenAccess_t tokenAccess = client.authenticate("chris@talool.com", "Walkon2013");
		tHttpClient.setCustomHeader(CustomerServiceConstants.CTOKEN_NAME, tokenAccess.getToken());

		List<Merchant_t> merchants = client.getMerchantAcquiresWithLocation(searchOptions, Boulder_CO);

		for (final Merchant_t merch : merchants)
		{
			if (merch.getName().contains("Dance"))
			{
				System.out.println(merch);
			}
			// System.out.println(merch.getName());
			if (merch.getName().startsWith("Backcountry"))
			{

				for (MerchantLocation_t loc : merch.getLocations())
				{
					System.out.println(loc.getLocationId() + ", " + loc.getMerchantImageUrl() + " , " + loc.getAddress().getCity());
				}

			}
		}
		Assert.assertTrue(merchants.size() > 0);

	}

	@Test
	public void testGetAcquiredMerchants() throws ServiceException_t, TException
	{
		SearchOptions_t searchOptions = new SearchOptions_t();
		searchOptions.setSortProperty("merchant.name");
		searchOptions.setPage(0);
		searchOptions.setAscending(true);
		searchOptions.setMaxResults(10);

		// CTokenAccess_t tokenAccess = client.authenticate(TEST_USER,
		// TEST_USER_PASS);
		// tHttpClient.setCustomHeader(CustomerServiceConstants.CTOKEN_NAME,
		// tokenAccess.getToken());

		CTokenAccess_t tokenAccess = client.authenticate("chris@talool.com", "Walkon2013");
		tHttpClient.setCustomHeader(CustomerServiceConstants.CTOKEN_NAME, tokenAccess.getToken());

		List<Merchant_t> merchs = client.getMerchantAcquires(null);
		for (Merchant_t merch : merchs)
		{
			if (merch.getName().contains("Dance"))
			{
				System.out.println(merch);
			}
		}

		List<Category_t> cats = client.getCategories();
		Category_t foodCat = null;
		for (Category_t catT : cats)
		{
			if (catT.getName().equals("Food"))
			{
				foodCat = catT;
				break;
			}
		}

		List<Merchant_t> merchants = client.getMerchantAcquiresByCategory(foodCat.getCategoryId(), searchOptions);
		Assert.assertEquals(2, merchants.size());

		Assert.assertEquals(MERCHANT_CENTRO, merchants.get(0).getName());
		Assert.assertNotNull(merchants.get(0).getCategory());

		Assert.assertEquals(MERCHANT_KITCHEN, merchants.get(1).getName());
		Assert.assertNotNull(merchants.get(1).getCategory());
	}

	@Test
	public void testValidateCodes() throws ServiceException_t, TException
	{
		CTokenAccess_t tokenAccess = client.authenticate("chris@talool.com", "pass123");
		tHttpClient.setCustomHeader(CustomerServiceConstants.CTOKEN_NAME, tokenAccess.getToken());

		// visions credit union
		ValidateCodeResponse_t response = client.validateCode("26JI0UR", "7a1b928b-aed3-4cd8-8ed8-bf758eafcaf0");
		Assert.assertTrue(response.isValid());
		Assert.assertEquals(response.getCodeType(), PropertyConstants.MERCHANT_CODE);

		// payback book
		response = client.validateCode("3NHDFUY", "231d6a36-1a40-44c6-ba25-402f42d05f6d");
		Assert.assertTrue(response.isValid());
		Assert.assertEquals(response.getCodeType(), PropertyConstants.ACTIVATION_CODE);

	}

	@Test
	public void testGetADealOffer() throws ServiceException_t, TException
	{
		CTokenAccess_t tokenAccess = client.authenticate(TEST_USER, TEST_USER_PASS);

		List<DealOffer_t> dealOffers = client.getDealOffers();

		tHttpClient.setCustomHeader(CustomerServiceConstants.CTOKEN_NAME, tokenAccess.getToken());

		DealOffer_t dealOfferResult = client.getDealOffer(dealOffers.get(0).getDealOfferId());

		Assert.assertEquals(dealOfferResult.getDealType(), dealOffers.get(0).getDealType());
		Assert.assertEquals(dealOfferResult.getSummary(), dealOffers.get(0).getSummary());
		Assert.assertEquals(dealOfferResult.getImageUrl(), dealOffers.get(0).getImageUrl());
		Assert.assertEquals(dealOfferResult.getTitle(), dealOffers.get(0).getTitle());
		Assert.assertEquals(dealOfferResult.getMerchant().getName(), dealOffers.get(0).getMerchant().getName());

	}

	@Test
	public void testGetDealOffer() throws ServiceException_t, TException
	{
		CTokenAccess_t tokenAccess = client.authenticate("chris@talool.com", "Walkon2013");

		tHttpClient.setCustomHeader(CustomerServiceConstants.CTOKEN_NAME, tokenAccess.getToken());

		SearchOptions_t searchOptions = new SearchOptions_t();
		searchOptions.setSortProperty("title");
		searchOptions.setPage(0);
		searchOptions.setAscending(true);
		searchOptions.setMaxResults(1000);

		// taloolchris
		List<Deal_t> deals = client.getDealsByDealOfferId("84dc11d6-bae5-43db-a517-08f11614803d", null);

		// List<Deal_t> deals =
		// client.getDealsByDealOfferId("a067de54-d63d-4613-8d60-9d995765cd52",
		// searchOptions);

		for (final Deal_t deal : deals)
		{
			if (deal.getMerchant().getName().contains("Dance"))
			{
				System.out.println(deal);
			}

		}
	}

	@Test
	public void testGetDealsByDealOfferId() throws ServiceException_t, TException
	{
		// a2ae2cfa-4677-4e22-89c8-bc555e428ccc
		CTokenAccess_t tokenAccess = client.authenticate(TEST_USER, TEST_USER_PASS);

		tHttpClient.setCustomHeader(CustomerServiceConstants.CTOKEN_NAME, tokenAccess.getToken());

		SearchOptions_t searchOptions = new SearchOptions_t();
		searchOptions.setSortProperty("merchant.name");
		searchOptions.setPage(0);
		searchOptions.setAscending(true);
		searchOptions.setMaxResults(10);

		List<DealOffer_t> dealOffers = client.getDealOffers();
		DealOffer_t dealOffer = null;
		for (DealOffer_t dof : dealOffers)
		{
			if (dof.getTitle().equals(DEAL_OFFER_TITLE))
			{
				dealOffer = dof;
				break;
			}
		}
		List<Deal_t> deals = client.getDealsByDealOfferId(dealOffer.getDealOfferId(), searchOptions);

		Assert.assertEquals(4, deals.size());

		Deal_t deal = deals.get(0);

		Assert.assertNotNull(deal.getMerchant());
		Assert.assertTrue(deal.getMerchant().getLocations().size() > 0);

	}

	@Test
	public void testGetMerchantsWithin() throws ServiceException_t, TException
	{
		SearchOptions_t searchOptions = new SearchOptions_t();
		searchOptions.setSortProperty("merchant.name");
		searchOptions.setPage(0);
		searchOptions.setAscending(true);
		searchOptions.setMaxResults(10);

		List<Merchant_t> merchants = client.getMerchantsWithin(Boulder_CO, 1, searchOptions);

		Assert.assertEquals(2, merchants.size());

		// sorting with SearchOptions ensures these arrive in this order
		Merchant_t merchant = merchants.get(0);

		Assert.assertEquals(MERCHANT_CENTRO, merchant.getName());
		Assert.assertEquals(-105.2841748, merchant.getLocations().get(0).getLocation().getLongitude(), 0);
		Assert.assertEquals(40.0169992, merchant.getLocations().get(0).getLocation().getLatitude(), 0);

		for (MerchantLocation_t mloc : merchant.getLocations())
		{
			Assert.assertNotEquals(0.0, mloc.getDistanceInMeters());
		}

		merchant = merchants.get(1);

		Assert.assertEquals(MERCHANT_KITCHEN, merchant.getName());
		Assert.assertEquals(-105.281686, merchant.getLocations().get(0).getLocation().getLongitude(), 0);
		Assert.assertEquals(40.017663, merchant.getLocations().get(0).getLocation().getLatitude(), 0);

		for (MerchantLocation_t mloc : merchant.getLocations())
		{
			Assert.assertNotEquals(0.0, mloc.getDistanceInMeters());
		}

		// ensure no merchants are here
		merchants = client.getMerchantsWithin(Denver_CO, 10, searchOptions);
		Assert.assertEquals(0, merchants.size());

	}

	@Test
	public void testAuthenticate() throws ServiceException_t, TException
	{
		try
		{
			CTokenAccess_t tokenAccess = client.authenticate(TEST_USER, TEST_USER_PASS);
			Assert.assertNotNull(tokenAccess.getToken());
			Assert.assertEquals(TEST_USER_FIRST, tokenAccess.getCustomer().getFirstName());
			Assert.assertEquals(TEST_USER_LAST, tokenAccess.getCustomer().getLastName());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	@Test
	public void testGetGiftsByUser() throws ServiceException_t, TException
	{
		CTokenAccess_t tok = client.authenticate("chris@talool.com", "Walkon2013");
		tHttpClient.setCustomHeader(CustomerServiceConstants.CTOKEN_NAME, tok.getToken());

		try
		{
			// http://dev-www.talool.com/gift/5ec08aa5-4099-4663-b074-ce1e7878ae95
			Gift_t gift = client.getGift("3fac749c-901f-41ec-94ad-f33e5d2e647a");

			if (gift == null)
			{
				System.out.println("Chris");
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	@Test
	public void testGiftRequests() throws ServiceException_t, TException
	{
		long now = System.currentTimeMillis();
		String email = "giftGiver-" + now + "@talool.com";
		String facebookId = "fBgiftGiverId-" + now;
		String firstName = "Gift-" + now;
		String lastName = "Giver-" + now;
		String password = "pass123";

		Customer_t giftGiver = new Customer_t();
		giftGiver.setFirstName(firstName);
		giftGiver.setLastName(lastName);
		giftGiver.setSex(Sex_t.M);
		giftGiver.setEmail(email);

		try
		{
			client.createAccount(giftGiver, password);

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		String giftReceiverEmail = "giftReceiver-" + now + "@talool.com";
		String giftReceiverFbId = "fBGiftReceiverId-" + now;
		String giftReceiverFirst = "Gift-" + now;
		String giftReceiverLast = "Receiver";

		CTokenAccess_t tokenAccess = client.authenticate(email, password);
		tHttpClient.setCustomHeader(CustomerServiceConstants.CTOKEN_NAME, tokenAccess.getToken());

		List<DealOffer_t> dealOffers = client.getDealOffers();

		client.purchaseDealOffer(dealOffers.get(0).getDealOfferId());

		List<Merchant_t> merchantsAcquired = client.getMerchantAcquires(null);

		List<DealAcquire_t> dealAcquires = client.getDealAcquires(merchantsAcquired.get(0).getMerchantId(), null);

		String giftId = client.giftToFacebook(dealAcquires.get(0).getDealAcquireId(), giftReceiverFbId, giftReceiverFirst + " "
				+ giftReceiverLast);

		Assert.assertNotNull(giftId);

		Customer_t giftReceiver = new Customer_t();
		giftReceiver.setFirstName(giftReceiverFirst);
		giftReceiver.setLastName(giftReceiverLast);
		giftReceiver.setSex(Sex_t.M);
		giftReceiver.setEmail(giftReceiverEmail);

		Map<SocialNetwork_t, SocialAccount_t> socialAccounts = new HashMap<SocialNetwork_t, SocialAccount_t>();
		socialAccounts.put(SocialNetwork_t.Facebook, new SocialAccount_t(SocialNetwork_t.Facebook, giftReceiverFbId));
		giftReceiver.setSocialAccounts(socialAccounts);

		try
		{
			client.createAccount(giftReceiver, password);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		giftId = client.giftToEmail(dealAcquires.get(1).getDealAcquireId(), "someemail@" + "test.talool.com", "Someone");
		Assert.assertNotNull(giftId);

		// verify the gift made it to the user
		tokenAccess = client.authenticate(giftReceiverEmail, password);
		tHttpClient.setCustomHeader(CustomerServiceConstants.CTOKEN_NAME, tokenAccess.getToken());

		// verify there is activity
		List<Activity_t> acts = client.getActivities(null);
		Assert.assertEquals(1, acts.size());

		String _giftId = acts.get(0).getActivityLink().getLinkElement();
		client.acceptGift(_giftId);
		// Assert.assertTrue(acts.get(0).getTitle().contains(dac.get(0).getDeal().getTitle()));

		List<Merchant_t> giftedMerchants = client.getMerchantAcquires(null);
		// verify i have a dealAcquire and no gifts!
		Assert.assertEquals(1, giftedMerchants.size());
		Assert.assertEquals(dealAcquires.get(0).getDeal().getMerchant().getMerchantId(), giftedMerchants.get(0).getMerchantId());

		// Create the account so we can see the gifts for new user!

	}

	@Test
	public void testActivities() throws ServiceException_t, TException
	{
		CTokenAccess_t tokenAccess = client.authenticate(TEST_USER, TEST_USER_PASS);
		tHttpClient.setCustomHeader(CustomerServiceConstants.CTOKEN_NAME, tokenAccess.getToken());

		String pass = "pass123";
		String email = "jimmy" + System.currentTimeMillis() + "@gmail.com";

		Customer_t customer = new Customer_t();
		customer.setFirstName("jimmy");
		customer.setLastName("jikes");
		customer.setSex(Sex_t.M);

		customer.setEmail(email);

		CTokenAccess_t accessToken = client.createAccount(customer, pass);

		// test removing social account
		tHttpClient.setCustomHeader(CustomerServiceConstants.CTOKEN_NAME, accessToken.getToken());

		client.authenticate(email, pass);

		List<Activity_t> acts = client.getActivities(null);

		Assert.assertTrue(acts.size() > 0);

		Activity_t act = acts.get(0);

		Assert.assertFalse(act.actionTaken);

		client.activityAction(acts.get(0).getActivityId());

		acts = client.getActivities(null);

		act = acts.get(0);

		Assert.assertTrue(act.actionTaken);

	}

	@Test
	public void testGetMessages() throws ServiceException_t, TException
	{
		tHttpClient.setCustomHeader(DEVICE_ID, "deviceId123");
		tHttpClient.setCustomHeader(GCM_DEVICE_TOKEN, "gcmDeviceToken123");

		CTokenAccess_t tokenAccess = client.authenticate("chris@talool.com", "Walkon2013");
		tHttpClient.setCustomHeader(CustomerServiceConstants.CTOKEN_NAME, tokenAccess.getToken());

		List<Activity_t> acts = client.getMessages(null, Boulder_CO);

	}

	@Test
	public void testDealAcquiresForCustomer() throws ServiceException_t, TException
	{
		CTokenAccess_t tokenAccess = client.authenticate("chris@talool.com", "Walkon2013");
		tHttpClient.setCustomHeader(CustomerServiceConstants.CTOKEN_NAME, tokenAccess.getToken());

		List<Merchant_t> merchantsAcquired = client.getMerchantAcquires(null);

		Merchant_t theMerch = null;

		for (Merchant_t merc : merchantsAcquired)
		{
			if (merc.getName().startsWith("95"))
			{
				theMerch = merc;
			}
		}

		List<DealAcquire_t> dealAcquires = client.getDealAcquires(theMerch.getMerchantId(), null);

		for (DealAcquire_t dac : dealAcquires)
		{
			if (dac.getDealAcquireId().toString().equals("6a1f1663-4ab3-407e-a520-a3c379334082"))
			{
				System.out.println(dac.getGiftDetail());
			}
		}

		System.out.println(dealAcquires.size());
	}

	@Test
	public void testResetPasswordRequest() throws ServiceException_t, TException
	{
		try
		{

			client.sendResetPasswordEmail("chriwqdw@talool.com");

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	@Test
	public void testCodeActivation() throws ServiceException_t, TException
	{
		try
		{
			CTokenAccess_t tokenAccess = client.authenticate("chris@talool.com", "pass123");
			tHttpClient.setCustomHeader(CustomerServiceConstants.CTOKEN_NAME, tokenAccess.getToken());

			// client.activateCode("4d54d8ef-febb-4719-b9f0-a73578a41803", "A00OOZ");
			client.activateCode("4d54d8ef-febb-4719-b9f0-a73578a41803", "OOOOBZ0");

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	@Test
	public void testResetPassword() throws ServiceException_t, TException
	{
		// http: //
		// local-www.talool.com:8080/rpw/9aa5272e-20bd-45e6-a406-f408ec63914f/eN5dTlMIlZUHZGTy
		try
		{
			CTokenAccess_t token = client.resetPassword("614c2a04-1142-4ac9-a5f0-111ace872eec", "P6GrwZv1bJISxdmM", "walkon03");
			System.out.println("here");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	@Test
	public void testGetMerchantsByDealOfferId() throws ServiceException_t, TException
	{
		CTokenAccess_t tokenAccess = client.authenticate("chris@talool.com", "Walkon2013");

		tHttpClient.setCustomHeader(CustomerServiceConstants.CTOKEN_NAME, tokenAccess.getToken());

		SearchOptions_t searchOptions = new SearchOptions_t();
		searchOptions.setSortProperty("merchant.name");
		searchOptions.setPage(0);
		searchOptions.setAscending(false);
		searchOptions.setMaxResults(10);

		MerchantsResponse_t response = client.getMerchantsByDealOfferId("a3e08a7c-a47b-4043-a264-ee910f5db474", searchOptions);

		Assert.assertTrue(response.getMerchants().size() > 0);

	}

	@Test
	public void testDealOfferGeoSummaryWithin() throws ServiceException_t, TException
	{
		CTokenAccess_t tokenAccess = client.authenticate("chris@talool.com", "pass123");
		tHttpClient.setCustomHeader(CustomerServiceConstants.CTOKEN_NAME, tokenAccess.getToken());

		final SearchOptions_t searchOpts = new SearchOptions_t();
		searchOpts.setSortProperty("distanceInMeters");
		searchOpts.setAscending(false);
		searchOpts.setMaxResults(100);
		searchOpts.setPage(0);

		final SearchOptions_t fallbackSearchOpts = new SearchOptions_t();
		fallbackSearchOpts.setSortProperty("price");
		fallbackSearchOpts.setAscending(true);
		fallbackSearchOpts.setMaxResults(100);
		fallbackSearchOpts.setPage(0);

		// DealOfferGeoSummariesResponse_t response =
		// client.getDealOfferGeoSummariesWithin(Rochester_NY, 200, searchOpts);
		Location_t loc = new Location_t();

		DealOfferGeoSummariesResponse_t response = client.getDealOfferGeoSummariesWithin(null, 2000, searchOpts, fallbackSearchOpts);

		Assert.assertTrue(CollectionUtils.isNotEmpty(response.getDealOfferGeoSummaries()));

		for (DealOfferGeoSummary_t summary : response.getDealOfferGeoSummaries())
		{
			System.out.println("dealOffer title: " + summary.getDealOffer().getTitle());
			System.out.println("dealOffer price: " + summary.getDealOffer().getPrice());
			System.out.println("dealOffer distanceInMeters: " + summary.getDistanceInMeters());
			System.out.println("Total deals in dealOffer: " + summary.getLongMetrics().get(CoreConstants.METRIC_TOTAL_DEALS));
			System.out.println("Total merchants in dealOffer: " + summary.getLongMetrics().get(CoreConstants.METRIC_TOTAL_MERCHANTS));
			if (summary.getDealOffer().getProperties() != null)
			{
				System.out.println("properties: " + summary.getDealOffer().getProperties());
			}

		}

	}

	@Test
	public void testEmailBodyRequest() throws ServiceException_t, TException
	{
		String dealOfferPurchaseId = "713702c7-e783-4c0b-b26a-4fec36e78367";
		CTokenAccess_t tokenAccess = client.authenticate("chris@talool.com", "pass123");
		tHttpClient.setCustomHeader(CustomerServiceConstants.CTOKEN_NAME, tokenAccess.getToken());

		EmailResponse_t response = client.getEmailBody(ActivityEmailTemplateType.BasicFundRaiser.getTemplateIdAsString(),
				dealOfferPurchaseId);

		Assert.assertNotNull(response.getBody());
		Assert.assertNotNull(response.getSubject());

	}
}
