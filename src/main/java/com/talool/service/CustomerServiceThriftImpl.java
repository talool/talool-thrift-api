package com.talool.service;

import java.util.List;
import java.util.UUID;

import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import com.google.common.collect.ImmutableList;
import com.talool.api.thrift.CTokenAccess_t;
import com.talool.api.thrift.Category_t;
import com.talool.api.thrift.CustomerService_t;
import com.talool.api.thrift.Customer_t;
import com.talool.api.thrift.DealAcquire_t;
import com.talool.api.thrift.DealOffer_t;
import com.talool.api.thrift.Gift_t;
import com.talool.api.thrift.Location_t;
import com.talool.api.thrift.Merchant_t;
import com.talool.api.thrift.SearchOptions_t;
import com.talool.api.thrift.ServiceException_t;
import com.talool.api.thrift.SocialAccount_t;
import com.talool.api.thrift.SocialNetwork_t;
import com.talool.api.thrift.Token_t;
import com.talool.cache.TagCache;
import com.talool.core.AccountType;
import com.talool.core.Customer;
import com.talool.core.DealAcquire;
import com.talool.core.DealOffer;
import com.talool.core.DomainFactory;
import com.talool.core.FactoryManager;
import com.talool.core.Merchant;
import com.talool.core.gift.Gift;
import com.talool.core.gift.GiftStatus;
import com.talool.core.service.CustomerService;
import com.talool.core.service.ServiceException;
import com.talool.core.service.TaloolService;
import com.talool.core.social.CustomerSocialAccount;
import com.talool.core.social.SocialNetwork;
import com.talool.service.util.TokenUtil;

/**
 * 
 * Thrift implementation of the Talool Customer service
 * 
 * @author clintz
 */
public class CustomerServiceThriftImpl implements CustomerService_t.Iface
{
	private static final Logger LOG = LoggerFactory.getLogger(TaloolServiceImpl.class);

	private static final transient TaloolService taloolService = FactoryManager.get()
			.getServiceFactory().getTaloolService();

	private static final transient CustomerService customerService = FactoryManager.get()
			.getServiceFactory().getCustomerService();

	private static final transient DomainFactory domainFactory = FactoryManager.get().getDomainFactory();

	private static final ImmutableList<Merchant_t> EMPTY_MERCHANTS = ImmutableList.of();
	private static final ImmutableList<Category_t> EMPTY_CATEGORIES = ImmutableList.of();
	private static final ImmutableList<Gift_t> EMPTY_GIFTS = ImmutableList.of();

	private volatile List<Category_t> categories = EMPTY_CATEGORIES;

	private CategoryThread categoryThread;

	private GiftStatus[] PENDING_GIFT_ACCEPT = new GiftStatus[] { GiftStatus.PENDING };

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
						LOG.info("Loaded " + categories.size() + " total categories");
						sleep(60000);
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
				throw new ServiceException_t(ServiceException.Type.EMAIL_ALREADY_TAKEN.getCode(),
						ServiceException.Type.EMAIL_ALREADY_TAKEN.getMessage());
			}
		}
		catch (ServiceException e)
		{
			LOG.error("Problem registering customer: " + e, e);
			throw new ServiceException_t(e.getType().getCode(), e.getMessage());
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

	@Override
	public CTokenAccess_t authenticate(final String email, final String password)
			throws ServiceException_t, TException
	{
		CTokenAccess_t token = null;

		if (LOG.isDebugEnabled())
		{
			LOG.debug("authenticate received for :" + email);
		}

		try
		{
			final Customer customer = customerService.authenticateCustomer(email, password);
			final Customer_t thriftCust = ConversionUtil.convertToThrift(customer);
			token = TokenUtil.createTokenAccess(thriftCust);
			return token;
		}
		catch (Exception e)
		{
			LOG.error("Problem authenticatiing customer: " + e, e);
			throw new ServiceException_t(1000, e.getLocalizedMessage());
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

	@Override
	public List<Merchant_t> getMerchantAcquires(final SearchOptions_t searchOptions)
			throws ServiceException_t, TException
	{

		final Token_t token = TokenUtil.getTokenFromRequest(true);
		List<Merchant> merchants = null;
		List<Merchant_t> resultMerchants = null;

		if (LOG.isDebugEnabled())
		{
			if (searchOptions != null)
			{
				LOG.debug(String.format("CustomerId %s getting acquired merchants with searchOptions %s",
						token.getAccountId(), searchOptions.toString()));
			}
			else
			{
				LOG.debug(String.format(
						"CustomerId %s getting acquired merchants with no searchOptions",
						token.getAccountId()));
			}

		}

		try
		{
			merchants = customerService.getMerchantAcquires(UUID.fromString(token.getAccountId()),
					ConversionUtil.convertFromThrift(searchOptions));
		}
		catch (Exception ex)
		{
			LOG.error("Problem getting merchants for customer " + token.getAccountId(), ex);
			throw new ServiceException_t(1087, "Problem getting merchants");
		}

		resultMerchants = ConversionUtil.convertToThriftMerchants(merchants);

		return CollectionUtils.isEmpty(resultMerchants) ? EMPTY_MERCHANTS : resultMerchants;

	}

	@Override
	public List<DealAcquire_t> getDealAcquires(final String merchantId,
			final SearchOptions_t searchOptions) throws ServiceException_t, TException
	{
		final Token_t token = TokenUtil.getTokenFromRequest(true);
		List<DealAcquire> dealAcquires = null;

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
		}
		catch (Exception ex)
		{
			LOG.error("There was a problem retrieving deals for merchant: " + merchantId, ex);
			throw new ServiceException_t(1087, "There was a problem retrieving deals for merchant");
		}

		if (CollectionUtils.isEmpty(dealAcquires))
		{
			LOG.error("No deals available for merchant: " + merchantId);
			throw new ServiceException_t(1088, "No deals available for merchant");
		}

		return ConversionUtil.convertToThriftDealAcquires(dealAcquires);

	}

	@Override
	public void redeem(final String dealAcquireId, final Location_t location)
			throws ServiceException_t, TException
	{
		final Token_t token = TokenUtil.getTokenFromRequest(true);

		if (LOG.isDebugEnabled())
		{
			LOG.debug(String.format("CustomerId %s redeeming dealAcquireId %s", token.getAccountId(),
					dealAcquireId));
		}

		try
		{
			final DealAcquire dealAcquire = customerService.getDealAcquire(UUID.fromString(dealAcquireId));

			customerService.redeemDeal(dealAcquire, UUID.fromString(token.getAccountId()));
		}
		catch (ServiceException e)
		{
			LOG.error("There was a problem redeeming deal : " + dealAcquireId, e);
			throw new ServiceException_t(e.getType().getCode(), e.getMessage());
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
			throw new ServiceException_t(e.getType().getCode(), e.getMessage());
		}

		return ConversionUtil.convertToThrift(dealOffers);
	}

	@Override
	public void purchaseDealOffer(final String dealOfferId) throws ServiceException_t, TException
	{
		final Token_t token = TokenUtil.getTokenFromRequest(true);

		try
		{
			customerService.createDealOfferPurchase(UUID.fromString(token.getAccountId()), UUID.fromString(dealOfferId));
		}
		catch (ServiceException e)
		{
			throw new ServiceException_t(e.getType().getCode(), e.getMessage());
		}

	}

	@Override
	public List<Merchant_t> getMerchantsWithin(final Location_t location, final int maxMiles, final SearchOptions_t searchOptions)
			throws ServiceException_t, TException
	{
		List<Merchant> merchants = null;

		try
		{
			merchants = taloolService.getMerchantsWithin(ConversionUtil.convertFromThrift(location), maxMiles,
					ConversionUtil.convertFromThrift(searchOptions));
		}
		catch (ServiceException e)
		{
			throw new ServiceException_t(e.getType().getCode(), e.getMessage());
		}

		return ConversionUtil.convertToThriftMerchants(merchants);
	}

	@Override
	public void addFavoriteMerchant(final String merchantId) throws ServiceException_t, TException
	{
		final Token_t token = TokenUtil.getTokenFromRequest(true);

		try
		{
			customerService.addFavoriteMerchant(UUID.fromString(token.getAccountId()), UUID.fromString(merchantId));
		}
		catch (ServiceException e)
		{
			throw new ServiceException_t(e.getType().getCode(), e.getMessage());
		}

	}

	@Override
	public void removeFavoriteMerchant(final String merchantId) throws ServiceException_t, TException
	{
		final Token_t token = TokenUtil.getTokenFromRequest(true);

		try
		{
			customerService.removeFavoriteMerchant(UUID.fromString(token.getAccountId()), UUID.fromString(merchantId));
		}
		catch (ServiceException e)
		{
			throw new ServiceException_t(e.getType().getCode(), e.getMessage());
		}

	}

	@Override
	public List<Merchant_t> getFavoriteMerchants(final SearchOptions_t searchOptions) throws ServiceException_t, TException
	{
		final Token_t token = TokenUtil.getTokenFromRequest(true);
		List<Merchant> merchants = null;

		try
		{
			merchants = customerService
					.getFavoriteMerchants(UUID.fromString(token.getAccountId()), ConversionUtil.convertFromThrift(searchOptions));
		}
		catch (ServiceException e)
		{
			throw new ServiceException_t(e.getType().getCode(), e.getMessage());
		}

		return ConversionUtil.convertToThriftMerchants(merchants);
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

		try
		{
			merchants = customerService.getMerchantAcquires(UUID.fromString(token.getAccountId()), categoryId,
					ConversionUtil.convertFromThrift(searchOptions));
		}
		catch (ServiceException e)
		{
			throw new ServiceException_t(e.getType().getCode(), e.getMessage());
		}

		return ConversionUtil.convertToThriftMerchants(merchants);
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
			throw new ServiceException_t(e.getType().getCode(), e.getMessage());
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
			throw new ServiceException_t(e.getType().getCode(), e.getMessage());
		}

	}

	@Override
	public void giftToFacebook(final String dealAcquireId, final String facebookId, final String receipientName)
			throws ServiceException_t,
			TException
	{
		final Token_t token = TokenUtil.getTokenFromRequest(true);

		if (LOG.isDebugEnabled())
		{
			LOG.debug(String.format("CustomerId %s giftToFacebook dealAcquireId %s facebookId %s receipientName %s",
					token.getAccountId(),
					dealAcquireId, facebookId, receipientName));
		}

		try
		{

			customerService.giftToFacebook(UUID.fromString(token.getAccountId()), UUID.fromString(dealAcquireId),
					facebookId, receipientName);
		}
		catch (ServiceException e)
		{
			LOG.error("Problem giftToFacebook for customerId: " + token.getAccountId(), e);
			throw new ServiceException_t(e.getType().getCode(), e.getMessage());
		}

	}

	@Override
	public void giftToEmail(final String dealAcquireId, final String email, final String receipientName) throws ServiceException_t,
			TException
	{
		final Token_t token = TokenUtil.getTokenFromRequest(true);

		if (LOG.isDebugEnabled())
		{
			LOG.debug(String.format("CustomerId %s giftToEmail dealAcquireId %s email %s receipientName %s",
					token.getAccountId(),
					dealAcquireId, email, receipientName));
		}

		try
		{

			customerService.giftToEmail(UUID.fromString(token.getAccountId()), UUID.fromString(dealAcquireId),
					email, receipientName);
		}
		catch (ServiceException e)
		{
			LOG.error("Problem giftToEmail for customerId: " + token.getAccountId(), e);
			throw new ServiceException_t(e.getType().getCode(), e.getMessage());
		}

	}

	@Override
	public List<Gift_t> getGifts() throws ServiceException_t, TException
	{
		final Token_t token = TokenUtil.getTokenFromRequest(true);
		List<Gift> gifts = null;

		if (LOG.isDebugEnabled())
		{
			LOG.debug(String.format("CustomerId %s getGifts", token.getAccountId()));
		}

		try
		{
			gifts = customerService.getGifts(UUID.fromString(token.getAccountId()), PENDING_GIFT_ACCEPT);
		}
		catch (ServiceException e)
		{
			LOG.error("Problem getGifts for customerId: " + token.getAccountId(), e);
			throw new ServiceException_t(e.getType().getCode(), e.getMessage());
		}

		final List<Gift_t> thriftGifts = ConversionUtil.convertToThriftGifts(gifts);
		return CollectionUtils.isEmpty(thriftGifts) ? EMPTY_GIFTS : thriftGifts;
	}

	@Override
	public void acceptGift(final String giftId) throws ServiceException_t, TException
	{
		final Token_t token = TokenUtil.getTokenFromRequest(true);

		if (LOG.isDebugEnabled())
		{
			LOG.debug(String.format("CustomerId %s acceptGift giftId %s", token.getAccountId(), giftId));
		}

		try
		{
			customerService.acceptGift(UUID.fromString(giftId), UUID.fromString(token.getAccountId()));
		}
		catch (ServiceException e)
		{
			LOG.error(String.format("Problem acceptGift for customerId %s giftId %s", token.getAccountId(), giftId), e);
			throw new ServiceException_t(e.getType().getCode(), e.getMessage());
		}

	}

	@Override
	public void rejectGift(final String giftId) throws ServiceException_t, TException
	{
		final Token_t token = TokenUtil.getTokenFromRequest(true);

		if (LOG.isDebugEnabled())
		{
			LOG.debug(String.format("CustomerId %s rejectGift giftId %s", token.getAccountId(), giftId));
		}

		try
		{
			customerService.rejectGift(UUID.fromString(giftId), UUID.fromString(token.getAccountId()));
		}
		catch (ServiceException e)
		{
			LOG.error(String.format("Problem rejectGift for customerId %s giftId %s", token.getAccountId(), giftId), e);
			throw new ServiceException_t(e.getType().getCode(), e.getMessage());
		}
	}
}
