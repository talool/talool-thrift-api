package com.talool.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;
import com.talool.api.thrift.Activity_t;
import com.talool.api.thrift.CTokenAccess_t;
import com.talool.api.thrift.Category_t;
import com.talool.api.thrift.CustomerService_t;
import com.talool.api.thrift.Customer_t;
import com.talool.api.thrift.DealAcquire_t;
import com.talool.api.thrift.DealOffer_t;
import com.talool.api.thrift.Deal_t;
import com.talool.api.thrift.ErrorCode_t;
import com.talool.api.thrift.Gift_t;
import com.talool.api.thrift.Location_t;
import com.talool.api.thrift.Merchant_t;
import com.talool.api.thrift.PaymentDetail_t;
import com.talool.api.thrift.SearchOptions_t;
import com.talool.api.thrift.ServiceException_t;
import com.talool.api.thrift.SocialAccount_t;
import com.talool.api.thrift.SocialNetwork_t;
import com.talool.api.thrift.TNotFoundException_t;
import com.talool.api.thrift.TServiceException_t;
import com.talool.api.thrift.TUserException_t;
import com.talool.api.thrift.Token_t;
import com.talool.api.thrift.TransactionResult_t;
import com.talool.cache.TagCache;
import com.talool.core.AccountType;
import com.talool.core.Customer;
import com.talool.core.Deal;
import com.talool.core.DealAcquire;
import com.talool.core.DealOffer;
import com.talool.core.FactoryManager;
import com.talool.core.Merchant;
import com.talool.core.activity.Activity;
import com.talool.core.gift.Gift;
import com.talool.core.service.ActivityService;
import com.talool.core.service.CustomerService;
import com.talool.core.service.InvalidInputException;
import com.talool.core.service.NotFoundException;
import com.talool.core.service.ServiceException;
import com.talool.core.service.TaloolService;
import com.talool.core.social.CustomerSocialAccount;
import com.talool.core.social.SocialNetwork;
import com.talool.payment.TransactionResult;
import com.talool.service.util.Constants;
import com.talool.service.util.ExceptionUtil;
import com.talool.service.util.TokenUtil;
import com.talool.thrift.ThriftUtil;

/**
 * 
 * Thrift implementation of the Talool Customer service
 * 
 * @author clintz
 */
public class CustomerServiceThriftImpl implements CustomerService_t.Iface
{
	private static final Logger LOG = LoggerFactory.getLogger(TaloolServiceImpl.class);

	private static final int CATEGORY_REFRESH_INTERVAL = 600000;

	private static final transient TaloolService taloolService = FactoryManager.get()
			.getServiceFactory().getTaloolService();

	private static final transient CustomerService customerService = FactoryManager.get()
			.getServiceFactory().getCustomerService();

	private static final transient ActivityService activityService = FactoryManager.get()
			.getServiceFactory().getActivityService();

	private static final ImmutableList<Merchant_t> EMPTY_MERCHANTS = ImmutableList.of();
	private static final ImmutableList<Category_t> EMPTY_CATEGORIES = ImmutableList.of();
	private static final ImmutableList<Activity_t> EMPTY_ACTIVITIES = ImmutableList.of();

	private volatile List<Category_t> categories = EMPTY_CATEGORIES;

	// a thread local convenience
	private static ResponseTimer responseTimer = new ResponseTimer();

	private CategoryThread categoryThread;

	private static String[] EAGER_DEAL_PROPS = { "image", "merchant.locations" };

	private class CategoryThread extends Thread
	{

		@Override
		public void run()
		{
			while (true)
			{
				try
				{
					categories = ConversionUtil.convertToThriftCategories(TagCache.get().getCategories());
					if (CollectionUtils.isEmpty(categories))
					{
						LOG.info("Loaded empty categories, sleeping for 1 secs then retry");
						sleep(1000);
					}
					else
					{
						LOG.info("Loaded " + categories.size() + " total categories.  Refreshing in " + CATEGORY_REFRESH_INTERVAL + " ms");
						sleep(CATEGORY_REFRESH_INTERVAL);
					}
				}
				catch (Exception e)
				{
					LOG.error(e.getLocalizedMessage(), e);
				}

			}

		}

	}

	public CustomerServiceThriftImpl()
	{
		super();

		categoryThread = new CategoryThread();
		categoryThread.setName("ThriftCategoryThread");
		categoryThread.setDaemon(true);
		categoryThread.start();
		LOG.info("Started CategoryThread");

	}

	@Override
	public CTokenAccess_t createAccount(final Customer_t customer, final String password)
			throws ServiceException_t, TException
	{
		CTokenAccess_t token = null;

		if (LOG.isDebugEnabled())
		{
			LOG.debug("createAccount received for :" + customer);
		}

		try
		{
			// test of account exits
			if (taloolService.emailExists(AccountType.CUS, customer.getEmail()))
			{
				LOG.error("Email already taken: " + customer.getEmail());
				throw new ServiceException_t(ErrorCode.EMAIL_ALREADY_TAKEN.getCode(),
						ErrorCode.EMAIL_ALREADY_TAKEN.getMessage());
			}
		}
		catch (ServiceException e)
		{
			LOG.error("Problem registering customer: " + e, e);
			throw new ServiceException_t(e.getErrorCode().getCode(), e.getMessage());
		}

		try
		{

			final Customer taloolCustomer = ConversionUtil.convertFromThrift(customer);
			if (customer.getSocialAccounts() != null)
			{
				for (final SocialAccount_t sac : customer.getSocialAccounts().values())
				{
					final CustomerSocialAccount taloolSocialAccount = ConversionUtil.convertFromThrift(sac, taloolCustomer);
					taloolCustomer.addSocialAccount(taloolSocialAccount);
				}

			}

			customerService.createAccount(taloolCustomer, password);

			final Customer_t updatedCustomer = ConversionUtil.convertToThrift(taloolCustomer);

			token = TokenUtil.createTokenAccess(updatedCustomer);

		}
		catch (Exception e)
		{
			LOG.error("Problem registering customer: " + e, e);
			throw new ServiceException_t(1000, e.getLocalizedMessage());
		}

		return token;
	}

	private void beginRequest(final String method)
	{
		if (!ServiceApiConfig.get().logApiMethodResponseTimes())
		{
			return;
		}

		responseTimer.get().watch.reset();
		responseTimer.get().method = method;
		responseTimer.get().watch.start();
	}

	private void endRequest()
	{
		if (!ServiceApiConfig.get().logApiMethodResponseTimes())
		{
			return;
		}

		responseTimer.get().watch.stop();

		LOG.debug(responseTimer.get().method + "/" + responseTimer.get().watch.getTime());

	}

	@Override
	public CTokenAccess_t authenticate(final String email, final String password)
			throws ServiceException_t, TException
	{
		CTokenAccess_t token = null;
		Customer customer = null;
		Customer_t thriftCust = null;

		beginRequest("authenticate");

		if (LOG.isDebugEnabled())
		{
			LOG.debug("authenticate received for :" + email);
		}

		try
		{
			try
			{
				customer = customerService.authenticateCustomer(email, password);
			}
			catch (ServiceException e)
			{
				LOG.error("Problem authenticating customer: " + e, e);
				throw new ServiceException_t(e.getErrorCode().getCode(), e.getMessage());
			}

			if (customer == null)
			{
				throw new ServiceException_t(ErrorCode.EMAIL_OR_PASS_INVALID.getCode(), ErrorCode.EMAIL_OR_PASS_INVALID.getMessage());
			}
			try
			{
				thriftCust = ConversionUtil.convertToThrift(customer);
				token = TokenUtil.createTokenAccess(thriftCust);
				return token;
			}
			catch (Exception e)
			{
				LOG.error("Problem converting customer: " + e, e);
				throw new ServiceException_t(1000, e.getMessage());
			}
		}
		catch (ServiceException_t e)
		{
			// yes this catch is here simply so we can have a finally and endRequest
			// cleanly
			throw e;
		}
		finally
		{
			endRequest();
		}

	}

	@Override
	public boolean customerEmailExists(final String email) throws ServiceException_t, TException
	{

		if (LOG.isDebugEnabled())
		{
			LOG.debug("customerEmailExists received: " + email);
		}

		try
		{
			return taloolService.emailExists(AccountType.CUS, email);
		}

		catch (Exception ex)
		{
			LOG.error("Problem saving customer: " + ex, ex);
			throw new ServiceException_t(1033, "Problem customerEmailExists");
		}
	}

	@Deprecated
	@Override
	/**
	 * Deprecated - see  getMerchantAcquiresWithLocation(SearchOptions_t searchOptions, Location_t location) 
	 */
	public List<Merchant_t> getMerchantAcquires(final SearchOptions_t searchOptions)
			throws ServiceException_t, TException
	{

		final Token_t token = TokenUtil.getTokenFromRequest(true);
		List<Merchant> merchants = null;
		List<Merchant_t> thriftMerchants = null;

		beginRequest("getMerchantAcquires");

		if (LOG.isDebugEnabled())
		{
			if (searchOptions != null)
			{
				LOG.debug(String.format("CustomerId %s getMerchantAcquires with searchOptions %s",
						token.getAccountId(), searchOptions.toString()));
			}
			else
			{
				LOG.debug(String.format(
						"CustomerId %s getMerchantAcquiress with no searchOptions",
						token.getAccountId()));
			}

		}

		try
		{
			merchants = customerService.getMerchantAcquires(UUID.fromString(token.getAccountId()),
					ConversionUtil.convertFromThrift(searchOptions));

			thriftMerchants = ConversionUtil.convertToThriftMerchants(merchants);

			if (LOG.isDebugEnabled() && thriftMerchants != null)
			{
				long byteTotal = 0;
				for (Merchant_t merchant : thriftMerchants)
				{
					byteTotal += ThriftUtil.serialize(merchant, Constants.PROTOCOL_FACTORY).length;
				}

				LOG.debug(String.format("getMerchantAcquires - serializing %d merchants totalBytes %d (%s)", thriftMerchants.size(),
						byteTotal,
						FileUtils.byteCountToDisplaySize(byteTotal)));

			}

			return CollectionUtils.isEmpty(thriftMerchants) ? EMPTY_MERCHANTS : thriftMerchants;
		}
		catch (Exception ex)
		{
			LOG.error("Problem getMerchantAcquires for customer " + token.getAccountId(), ex);
			throw new ServiceException_t(1087, "Problem getting merchants");
		}
		finally
		{
			endRequest();
		}

	}

	@Override
	public List<DealAcquire_t> getDealAcquires(final String merchantId,
			final SearchOptions_t searchOptions) throws ServiceException_t, TException
	{
		final Token_t token = TokenUtil.getTokenFromRequest(true);
		List<DealAcquire> dealAcquires = null;

		beginRequest("getDealAcquires");

		if (LOG.isDebugEnabled())
		{
			if (searchOptions != null)
			{
				LOG.debug(String.format(
						"CustomerId %s getting deal acquires for merchantId %s and searchOptions %s",
						token.getAccountId(), merchantId, searchOptions));
			}
			else
			{
				LOG.debug(String.format(
						"CustomerId %s getting deal acquires for merchantId %s and no searchOptions",
						token.getAccountId(), merchantId));
			}

		}

		try
		{
			dealAcquires = customerService.getDealAcquires(UUID.fromString(token.getAccountId()),
					UUID.fromString(merchantId), null);

			if (CollectionUtils.isEmpty(dealAcquires))
			{
				LOG.error("No deals available for merchant: " + merchantId);
				throw new ServiceException_t(1088, "No deals available for merchant");
			}

			return ConversionUtil.convertToThriftDealAcquires(dealAcquires);

		}
		catch (ServiceException_t se)
		{
			throw se;
		}
		catch (Exception ex)
		{
			LOG.error("There was a problem retrieving deals for merchant: " + merchantId, ex);
			throw new ServiceException_t(1087, "There was a problem retrieving deals for merchant");
		}
		finally
		{
			endRequest();
		}

	}

	@Override
	public String redeem(final String dealAcquireId, final Location_t location)
			throws ServiceException_t, TException
	{
		final Token_t token = TokenUtil.getTokenFromRequest(true);
		String redemptionCode = null;

		beginRequest("redeem");

		if (LOG.isDebugEnabled())
		{
			LOG.debug(String.format("CustomerId %s redeeming dealAcquireId %s", token.getAccountId(),
					dealAcquireId));
		}

		final UUID customerUuid = UUID.fromString(token.getAccountId());
		final UUID dealAcquireUuid = UUID.fromString(dealAcquireId);

		try
		{
			redemptionCode = customerService.redeemDeal(UUID.fromString(dealAcquireId), customerUuid,
					ConversionUtil.convertFromThrift(location));
		}
		catch (ServiceException e)
		{
			LOG.error("There was a problem redeeming deal : " + dealAcquireId, e);
			endRequest();
			throw new ServiceException_t(e.getErrorCode().getCode(), e.getMessage());
		}

		try
		{
			final DealAcquire dealAcquire = customerService.getDealAcquire(dealAcquireUuid);

			Activity activity = ActivityFactory.createRedeem(dealAcquire, customerUuid);
			activityService.save(activity);

			// if we have a gift, we have a bi-directional activity (for friend that
			// gave gift)
			final Gift gift = dealAcquire.getGift();
			if (gift != null)
			{
				activity = ActivityFactory.createFriendGiftReedem(dealAcquire);
				activityService.save(activity);
			}

			return redemptionCode;
		}
		catch (ServiceException e)
		{
			LOG.error("There was a problem redeeming deal : " + dealAcquireId, e);
			throw new ServiceException_t(e.getErrorCode().getCode(), e.getMessage());
		}
		finally
		{
			endRequest();
		}

	}

	@Override
	// TODO Change this method to getDealOffers based on some business criteria
	public List<DealOffer_t> getDealOffers() throws ServiceException_t, TException
	{
		List<DealOffer> dealOffers = null;

		try
		{
			dealOffers = taloolService.getDealOffers();
		}
		catch (ServiceException e)
		{
			LOG.error(e.getMessage(), e);
			throw new ServiceException_t(e.getErrorCode().getCode(), e.getMessage());
		}

		return ConversionUtil.convertToThrift(dealOffers);
	}

	@Override
	public void purchaseDealOffer(final String dealOfferId) throws ServiceException_t, TException
	{
		final Token_t token = TokenUtil.getTokenFromRequest(true);

		try
		{
			final UUID customerUuid = UUID.fromString(token.getAccountId());
			final UUID dealOfferUuid = UUID.fromString(dealOfferId);
			customerService.createDealOfferPurchase(customerUuid, dealOfferUuid);

			final DealOffer dealOffer = taloolService.getDealOffer(dealOfferUuid);

			// create a purchase activity
			final Activity activity = ActivityFactory.createPurchase(dealOffer, customerUuid);

			activityService.save(activity);
		}
		catch (ServiceException e)
		{
			LOG.error(e.getMessage(), e);
			throw new ServiceException_t(e.getErrorCode().getCode(), e.getMessage());
		}

	}

	@Override
	public List<Merchant_t> getMerchantsWithin(final Location_t location, final int maxMiles, final SearchOptions_t searchOptions)
			throws ServiceException_t, TException
	{
		List<Merchant> merchants = null;

		beginRequest("getMerchantsWithin");

		try
		{
			merchants = taloolService.getMerchantsWithin(ConversionUtil.convertFromThrift(location), maxMiles,
					ConversionUtil.convertFromThrift(searchOptions));

		}
		catch (ServiceException e)
		{
			LOG.error(e.getMessage(), e);
			throw new ServiceException_t(e.getErrorCode().getCode(), e.getMessage());
		}
		finally
		{
			endRequest();
		}

		final List<Merchant_t> thriftMerchants = ConversionUtil.convertToThriftMerchants(merchants);

		if (LOG.isDebugEnabled() && thriftMerchants != null)
		{
			long byteTotal = 0;
			for (Merchant_t merchant : thriftMerchants)
			{
				byteTotal += ThriftUtil.serialize(merchant, Constants.PROTOCOL_FACTORY).length;
			}

			LOG.debug(String.format("getMerchantsWithin - serializing %d merchants totalBytes %d (%s)", thriftMerchants.size(),
					byteTotal,
					FileUtils.byteCountToDisplaySize(byteTotal)));

		}

		return CollectionUtils.isEmpty(thriftMerchants) ? EMPTY_MERCHANTS : thriftMerchants;

	}

	@Override
	public void addFavoriteMerchant(final String merchantId) throws ServiceException_t, TException
	{
		final Token_t token = TokenUtil.getTokenFromRequest(true);

		beginRequest("addFavoriteMerchant");

		try
		{
			customerService.addFavoriteMerchant(UUID.fromString(token.getAccountId()), UUID.fromString(merchantId));
		}
		catch (ServiceException e)
		{
			throw new ServiceException_t(e.getErrorCode().getCode(), e.getMessage());
		}
		finally
		{
			endRequest();
		}

	}

	@Override
	public void removeFavoriteMerchant(final String merchantId) throws ServiceException_t, TException
	{
		final Token_t token = TokenUtil.getTokenFromRequest(true);

		beginRequest("removeFavoriteMerchant");

		try
		{
			customerService.removeFavoriteMerchant(UUID.fromString(token.getAccountId()), UUID.fromString(merchantId));
		}
		catch (ServiceException e)
		{
			throw new ServiceException_t(e.getErrorCode().getCode(), e.getMessage());
		}
		finally
		{
			endRequest();
		}

	}

	@Override
	public List<Merchant_t> getFavoriteMerchants(final SearchOptions_t searchOptions) throws ServiceException_t, TException
	{
		final Token_t token = TokenUtil.getTokenFromRequest(true);
		List<Merchant> merchants = null;

		beginRequest("getFavoriteMerchants");

		try
		{
			merchants = customerService
					.getFavoriteMerchants(UUID.fromString(token.getAccountId()), ConversionUtil.convertFromThrift(searchOptions));

			return ConversionUtil.convertToThriftMerchants(merchants);
		}
		catch (ServiceException e)
		{
			throw new ServiceException_t(e.getErrorCode().getCode(), e.getMessage());
		}
		finally
		{
			endRequest();
		}

	}

	@Override
	public List<Category_t> getCategories() throws ServiceException_t, TException
	{
		return categories;
	}

	@Override
	public List<Merchant_t> getMerchantAcquiresByCategory(final int categoryId, final SearchOptions_t searchOptions)
			throws ServiceException_t,
			TException
	{
		final Token_t token = TokenUtil.getTokenFromRequest(true);
		List<Merchant> merchants = null;

		beginRequest("getMerchantAcquiresByCategory");

		try
		{
			merchants = customerService.getMerchantAcquires(UUID.fromString(token.getAccountId()), categoryId,
					ConversionUtil.convertFromThrift(searchOptions));

			return ConversionUtil.convertToThriftMerchants(merchants);
		}
		catch (ServiceException e)
		{
			throw new ServiceException_t(e.getErrorCode().getCode(), e.getMessage());
		}
		finally
		{
			endRequest();
		}

	}

	@Override
	public void removeSocialAccount(final SocialNetwork_t socialNetwork_t) throws ServiceException_t, TException
	{
		final Token_t token = TokenUtil.getTokenFromRequest(true);

		try
		{
			customerService.removeSocialAccount(UUID.fromString(token.getAccountId()),
					taloolService.getSocialNetwork(SocialNetwork.NetworkName.valueOf(socialNetwork_t.toString())));
		}
		catch (ServiceException e)
		{
			throw new ServiceException_t(e.getErrorCode().getCode(), e.getMessage());
		}

	}

	@Override
	public void addSocialAccount(final SocialAccount_t socialAccount_t) throws ServiceException_t,
			TException
	{
		final Token_t token = TokenUtil.getTokenFromRequest(true);

		if (LOG.isDebugEnabled())
		{
			LOG.debug(String.format("CustomerId %s adding social account %s", token.getAccountId(),
					socialAccount_t.toString()));
		}

		try
		{
			final Customer cust = customerService.getCustomerById(UUID.fromString(token.getAccountId()));

			final CustomerSocialAccount sac = FactoryManager.get().getDomainFactory()
					.newCustomerSocialAccount(socialAccount_t.getSocialNetwork().name());

			ConversionUtil.copyFromThrift(socialAccount_t, sac, cust);

			customerService.save(sac);
		}
		catch (ServiceException e)
		{
			LOG.error("Problem addSocialAccount for customerId: " + token.getAccountId(), e);
			throw new ServiceException_t(e.getErrorCode().getCode(), e.getMessage());
		}

	}

	@Override
	public String giftToFacebook(final String dealAcquireId, final String facebookId, final String receipientName)
			throws ServiceException_t,
			TException
	{
		final Token_t token = TokenUtil.getTokenFromRequest(true);
		UUID giftId = null;

		beginRequest("giftToFacebook");

		if (LOG.isDebugEnabled())
		{
			LOG.debug(String.format("CustomerId %s giftToFacebook dealAcquireId %s facebookId %s receipientName %s",
					token.getAccountId(),
					dealAcquireId, facebookId, receipientName));
		}

		try
		{
			giftId = customerService.giftToFacebook(UUID.fromString(token.getAccountId()), UUID.fromString(dealAcquireId),
					facebookId, receipientName);

			return giftId.toString();
		}
		catch (ServiceException e)
		{
			LOG.error("Problem giftToFacebook for customerId: " + token.getAccountId(), e);
			throw new ServiceException_t(e.getErrorCode().getCode(), e.getMessage());
		}
		finally
		{
			endRequest();
		}

	}

	@Override
	public String giftToEmail(final String dealAcquireId, final String email, final String receipientName)
			throws ServiceException_t,
			TException
	{
		final Token_t token = TokenUtil.getTokenFromRequest(true);
		UUID giftId = null;

		beginRequest("giftToEmail");

		if (LOG.isDebugEnabled())
		{
			LOG.debug(String.format("CustomerId %s giftToEmail dealAcquireId %s email %s receipientName %s",
					token.getAccountId(),
					dealAcquireId, email, receipientName));
		}

		try
		{

			giftId = customerService.giftToEmail(UUID.fromString(token.getAccountId()), UUID.fromString(dealAcquireId),
					email, receipientName);

			return giftId.toString();
		}
		catch (ServiceException e)
		{
			LOG.error("Problem giftToEmail for customerId: " + token.getAccountId(), e);
			throw new ServiceException_t(e.getErrorCode().getCode(), e.getMessage());
		}
		finally
		{
			endRequest();
		}

	}

	@Override
	public Gift_t getGift(final String giftId) throws ServiceException_t, TException
	{
		final Token_t token = TokenUtil.getTokenFromRequest(true);
		Gift_t thriftGift = null;

		beginRequest("getGift");

		if (LOG.isDebugEnabled())
		{
			LOG.debug(String.format("CustomerId %s getGifts", token.getAccountId()));
		}

		try
		{
			final Gift gift = customerService.getGift(UUID.fromString(giftId));
			thriftGift = ConversionUtil.convertToThrift(gift);
		}
		catch (ServiceException e)
		{
			LOG.error("Problem getGifts for customerId: " + token.getAccountId(), e);
			throw new ServiceException_t(e.getErrorCode().getCode(), e.getMessage());
		}
		finally
		{
			endRequest();
		}

		return thriftGift;

	}

	@Override
	public DealAcquire_t acceptGift(final String giftId) throws ServiceException_t, TException
	{
		final Token_t token = TokenUtil.getTokenFromRequest(true);

		beginRequest("acceptGift");

		if (LOG.isDebugEnabled())
		{
			LOG.debug(String.format("CustomerId %s acceptGift giftId %s", token.getAccountId(), giftId));
		}

		try
		{
			final DealAcquire dac = customerService.acceptGift(UUID.fromString(giftId), UUID.fromString(token.getAccountId()));
			return ConversionUtil.convertToThrift(dac);
		}
		catch (ServiceException e)
		{
			LOG.error(String.format("Problem acceptGift for customerId %s giftId %s", token.getAccountId(), giftId), e);
			throw new ServiceException_t(e.getErrorCode().getCode(), e.getMessage());
		}
		finally
		{
			endRequest();
		}

	}

	@Override
	public void rejectGift(final String giftId) throws ServiceException_t, TException
	{
		final Token_t token = TokenUtil.getTokenFromRequest(true);

		beginRequest("rejectGift");

		if (LOG.isDebugEnabled())
		{
			LOG.debug(String.format("CustomerId %s rejectGift giftId %s", token.getAccountId(), giftId));
		}

		try
		{
			final UUID customerUuid = UUID.fromString(token.getAccountId());
			customerService.rejectGift(UUID.fromString(giftId), customerUuid);
		}
		catch (ServiceException e)
		{
			LOG.error(String.format("Problem rejectGift for customerId %s giftId %s", token.getAccountId(), giftId), e);
			throw new ServiceException_t(e.getErrorCode().getCode(), e.getMessage());
		}
		finally
		{
			endRequest();
		}

	}

	@Override
	public DealOffer_t getDealOffer(final String dealOfferId) throws ServiceException_t, TException
	{
		final Token_t token = TokenUtil.getTokenFromRequest(true);

		DealOffer dealOffer = null;
		DealOffer_t thriftDealOffer = null;

		beginRequest("getDealOffer");

		if (LOG.isDebugEnabled())
		{
			LOG.debug(String.format("CustomerId %s getDealOffer dealOfferId %s", token.getAccountId(), dealOfferId));
		}

		try
		{
			dealOffer = taloolService.getDealOffer(UUID.fromString(dealOfferId));
			thriftDealOffer = ConversionUtil.convertToThrift(dealOffer);

			return thriftDealOffer;
		}
		catch (ServiceException e)
		{
			LOG.error(e.getMessage(), e);
			throw new ServiceException_t(e.getErrorCode().getCode(), e.getMessage());
		}
		finally
		{
			endRequest();
		}
	}

	@Override
	public List<Deal_t> getDealsByDealOfferId(final String dealOfferId, final SearchOptions_t searchOptions)
			throws ServiceException_t, TException
	{
		TokenUtil.getTokenFromRequest(true);

		List<Deal> deals = null;

		beginRequest("getDealsByDealOfferId");

		try
		{
			deals = taloolService.getDealsByDealOfferId(UUID.fromString(dealOfferId),
					ConversionUtil.convertFromThrift(searchOptions), EAGER_DEAL_PROPS);

			return ConversionUtil.convertToThriftDeals(deals);
		}
		catch (ServiceException e)
		{
			LOG.error(e.getMessage(), e);
			throw new ServiceException_t(e.getErrorCode().getCode(), e.getMessage());
		}
		finally
		{
			endRequest();
		}
	}

	@Override
	public List<Activity_t> getActivities(final SearchOptions_t searchOptions) throws ServiceException_t, TException
	{
		final Token_t token = TokenUtil.getTokenFromRequest(true);

		beginRequest("getActivities");

		try
		{
			final List<Activity> activities = activityService.getActivities(UUID.fromString(token.getAccountId()),
					ConversionUtil.convertFromThrift(searchOptions));

			return CollectionUtils.isEmpty(activities) ? EMPTY_ACTIVITIES : ConversionUtil.convertToThriftActivites(activities);
		}
		catch (ServiceException e)
		{
			LOG.error(e.getMessage(), e);
			throw new ServiceException_t(e.getErrorCode().getCode(), e.getMessage());
		}
		finally
		{
			endRequest();
		}
	}

	@Override
	public void activityAction(final String activityId) throws ServiceException_t, TException
	{
		final Token_t token = TokenUtil.getTokenFromRequest(true);

		beginRequest("activityAction");

		if (LOG.isDebugEnabled())
		{
			LOG.debug(String.format("CustomerId %s activityAction activityId %s", token.getAccountId(), activityId));
		}

		try
		{
			activityService.activityAction(UUID.fromString(activityId));
		}
		catch (ServiceException e)
		{
			LOG.error(e.getMessage(), e);
			throw new ServiceException_t(e.getErrorCode().getCode(), e.getMessage());
		}
		finally
		{
			endRequest();
		}

	}

	@Override
	public void activateCode(final String dealOfferid, final String code) throws ServiceException_t, TException
	{
		final Token_t token = TokenUtil.getTokenFromRequest(true);

		beginRequest("activateCode");

		if (LOG.isDebugEnabled())
		{
			LOG.debug(String.format("CustomerId %s activateCode dealOfferId %s code %s", token.getAccountId(), dealOfferid, code));
		}

		if (dealOfferid == null || code == null)

		{
			throw new ServiceException_t(ErrorCode.UNKNOWN.getCode(), "dealOfferId and code cannot be null");
		}

		try
		{
			customerService.activateCode(UUID.fromString(token.getAccountId()), UUID.fromString(dealOfferid), code);
		}
		catch (ServiceException e)
		{
			LOG.error(e.getMessage(), e);
			throw new ServiceException_t(e.getErrorCode().getCode(), e.getMessage());
		}
		finally
		{
			endRequest();
		}

	}

	@Override
	public List<Merchant_t> getMerchantAcquiresWithLocation(final SearchOptions_t searchOptions, final Location_t location) throws ServiceException_t,
			TException
	{
		final Token_t token = TokenUtil.getTokenFromRequest(true);
		List<Merchant> merchants = null;
		List<Merchant_t> thriftMerchants = null;

		beginRequest("getMerchantAcquiresWithLocation");

		if (LOG.isDebugEnabled())
		{
			if (searchOptions != null)
			{
				LOG.debug(String.format("CustomerId %s getMerchantAcquires with searchOptions %s",
						token.getAccountId(), searchOptions.toString()));
			}
			else
			{
				LOG.debug(String.format(
						"CustomerId %s getMerchantAcquiress with no searchOptions",
						token.getAccountId()));
			}

		}

		try
		{
			merchants = customerService.getMerchantAcquires(UUID.fromString(token.getAccountId()),
					ConversionUtil.convertFromThrift(searchOptions), ConversionUtil.convertFromThrift(location));

			thriftMerchants = ConversionUtil.convertToThriftMerchants(merchants);

			if (LOG.isDebugEnabled() && thriftMerchants != null)
			{
				long byteTotal = 0;
				for (Merchant_t merchant : thriftMerchants)
				{
					byteTotal += ThriftUtil.serialize(merchant, Constants.PROTOCOL_FACTORY).length;
				}

				LOG.debug(String.format("getMerchantAcquiresWithLocation - serializing %d merchants totalBytes %d (%s)", thriftMerchants.size(),
						byteTotal,
						FileUtils.byteCountToDisplaySize(byteTotal)));

			}

			return CollectionUtils.isEmpty(thriftMerchants) ? EMPTY_MERCHANTS : thriftMerchants;
		}
		catch (Exception ex)
		{
			LOG.error("Problem getMerchantAcquiresWithLocation for customer " + token.getAccountId(), ex);
			throw new ServiceException_t(1087, "Problem getting merchants");
		}
		finally
		{
			endRequest();
		}
	}

	@Override
	public void sendResetPasswordEmail(final String email) throws TServiceException_t, TUserException_t, TNotFoundException_t, TException
	{
		Customer customer = null;
		beginRequest("sendResetPasswordEmail");

		LOG.debug("sendResetPasswordEmail " + email);

		if (LOG.isDebugEnabled())
		{
			LOG.debug(String.format("sendResetPasswordEmail email " + email));
		}

		try
		{
			customer = customerService.getCustomerByEmail(email);

		}
		catch (ServiceException se)
		{
			LOG.error("Problem finding customer by email: " + email, se);
			throw new ServiceException_t(se.getErrorCode().getCode(), se.getMessage());
		}
		catch (InvalidInputException e)
		{
			LOG.warn("Invalid input on password reset " + email, e);
			throw ExceptionUtil.safelyTranslate(e);
		}

		if (customer == null)
		{
			throw new TNotFoundException_t("email", email);
		}

		try
		{
			if (customer.getSocialAccounts().get(taloolService.getSocialNetwork(SocialNetwork.NetworkName.Facebook)) != null)
			{
				throw new ServiceException_t(ErrorCode.UNKNOWN.getCode(), "Cannot change Facebook account passwords");
			}
		}
		catch (ServiceException se)
		{
			LOG.error("Problem checking for facebook account for user " + email, se);
			throw new ServiceException_t(se.getErrorCode().getCode(), se.getMessage());
		}

		try
		{
			customerService.createPasswordReset(customer);
		}
		catch (ServiceException se)
		{
			LOG.error("Problem generating password reset for user " + email, se);
			throw new ServiceException_t(se.getErrorCode().getCode(), se.getMessage());
		}

	}

	@Override
	public CTokenAccess_t resetPassword(final String customerId, final String resetPasswordCode, final String newPassword) throws TServiceException_t,
			TUserException_t, TNotFoundException_t, TException
	{
		Customer customer = null;
		CTokenAccess_t token = null;

		beginRequest("resetPassword");

		if (LOG.isDebugEnabled())
		{
			LOG.debug("resetPassword customerId " + customerId);
		}

		if (StringUtils.isEmpty(newPassword))
		{
			throw new TUserException_t(ErrorCode_t.PASS_REQUIRED);
		}

		if (StringUtils.isEmpty(resetPasswordCode))
		{
			throw new TUserException_t(ErrorCode_t.PASS_RESET_CODE_REQUIRED);
		}

		try
		{
			customer = customerService.getCustomerById(UUID.fromString(customerId));
		}
		catch (ServiceException se)
		{
			LOG.error("Problem resetPassword for customerId " + customerId, se);
			throw ExceptionUtil.safelyTranslate(se);
		}

		if (customer != null)
		{
			if (!customer.getResetPasswordCode().equals(resetPasswordCode))
			{
				throw new TUserException_t(ErrorCode_t.PASS_RESET_CODE_INVALID);
			}

			final Date now = Calendar.getInstance().getTime();
			if (now.getTime() > customer.getResetPasswordExpires().getTime())
			{
				LOG.warn(String.format("reset pass expired customerId %s now %s expiresTime %s", customerId, now, customer.getResetPasswordExpires()));
				throw new TServiceException_t(ErrorCode_t.PASS_RESET_CODE_EXPIRED);
			}

			try
			{ // encrypts and sets
				customer.setPassword(newPassword);
				customer.setResetPasswordCode(null);
				customerService.save(customer);
				token = TokenUtil.createTokenAccess(ConversionUtil.convertToThrift(customer));
			}
			catch (ServiceException se)
			{
				LOG.error("Problem setting new pass/saving customerId " + customerId, se);
				throw ExceptionUtil.safelyTranslate(se);
			}
		}
		else
		{
			LOG.warn("Customer not found customerId:" + customerId);
			throw new TNotFoundException_t("customer", customerId);
		}

		return token;

	}

	@Override
	public TransactionResult_t purchaseByCard(final String dealOfferId, final PaymentDetail_t paymentDetail) throws TServiceException_t,
			TUserException_t,
			TNotFoundException_t, TException
	{
		final Token_t token = TokenUtil.getTokenFromRequest(true);

		TransactionResult_t transactionResult_t = null;

		beginRequest("purchaseByCard");

		if (LOG.isDebugEnabled())
		{
			LOG.debug(String.format("purchaseByCard customerId %s dealOfferId %s", token.getAccountId(), dealOfferId));
		}

		try
		{
			final TransactionResult transactionResult = customerService.purchaseByCard(UUID.fromString(token.getAccountId()), UUID.fromString(dealOfferId),
					ConversionUtil.convertFromThrift(paymentDetail));
			transactionResult_t = ConversionUtil.convertToThrift(transactionResult);

		}
		catch (ServiceException e)
		{
			LOG.error("Problem purchaseByCard: " + e.getLocalizedMessage(), e);
			throw ExceptionUtil.safelyTranslate(e);
		}
		catch (NotFoundException e)
		{
			LOG.error("Problem purchaseByCard: " + e.getLocalizedMessage(), e);
			throw ExceptionUtil.safelyTranslate(e);
		}

		return transactionResult_t;

	}

	@Override
	public TransactionResult_t purchaseByCode(final String dealOfferId, final String paymentCode) throws TServiceException_t, TUserException_t,
			TNotFoundException_t,
			TException
	{
		final Token_t token = TokenUtil.getTokenFromRequest(true);

		TransactionResult_t transactionResult_t = null;

		beginRequest("purchaseByCode");

		if (LOG.isDebugEnabled())
		{
			LOG.debug(String.format("purchaseByCode customerId %s dealOfferId %s", token.getAccountId(), dealOfferId));
		}

		try
		{
			final TransactionResult transactionResult = customerService.purchaseByCode(UUID.fromString(token.getAccountId()), UUID.fromString(dealOfferId),
					paymentCode);
			transactionResult_t = ConversionUtil.convertToThrift(transactionResult);

		}
		catch (ServiceException e)
		{
			LOG.error("Problem purchaseByCode: " + e.getLocalizedMessage(), e);
			throw ExceptionUtil.safelyTranslate(e);
		}
		catch (NotFoundException e)
		{
			LOG.error("Problem purchaseByCode: " + e.getLocalizedMessage(), e);
			throw ExceptionUtil.safelyTranslate(e);
		}

		return transactionResult_t;

	}

}
