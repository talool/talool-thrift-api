package com.talool.service;

import java.util.List;
import java.util.UUID;

import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import com.google.common.collect.ImmutableList;
import com.talool.api.thrift.CTokenAccess_t;
import com.talool.api.thrift.CustomerService_t;
import com.talool.api.thrift.Customer_t;
import com.talool.api.thrift.DealAcquire_t;
import com.talool.api.thrift.Merchant_t;
import com.talool.api.thrift.SearchOptions_t;
import com.talool.api.thrift.ServiceException_t;
import com.talool.api.thrift.SocialAccount_t;
import com.talool.api.thrift.Token_t;
import com.talool.core.AccountType;
import com.talool.core.Customer;
import com.talool.core.DealAcquire;
import com.talool.core.FactoryManager;
import com.talool.core.Merchant;
import com.talool.core.SocialAccount;
import com.talool.core.service.ServiceException;
import com.talool.core.service.TaloolService;
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

	private static final ImmutableList<Merchant_t> EMPTY_MERCHANTS = ImmutableList.of();

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
			final Customer cust = taloolService.getCustomerById(UUID.fromString(token.getAccountId()));
			final SocialAccount sac = FactoryManager.get().getDomainFactory()
					.newSocialAccount(socialAccount_t.getSocalNetwork().name(), AccountType.CUS);

			ConversionUtil.copyFromThrift(socialAccount_t, sac, cust.getId());
			cust.addSocialAccount(sac);
			taloolService.save(cust);
		}
		catch (Exception e)
		{
			LOG.error("Problem registering customer: " + e, e);
			throw new ServiceException_t(1000, e.getLocalizedMessage());
		}

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
			taloolService.createAccount(taloolCustomer, password);

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
			final Customer customer = taloolService.authenticateCustomer(email, password);
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
	public void save(final Customer_t cust_t) throws ServiceException_t, TException
	{
		final Token_t token = TokenUtil.getTokenFromRequest(true);

		if (LOG.isDebugEnabled())
		{
			LOG.debug(String.format("Saving customer %s", cust_t.toString()));
		}

		try
		{
			final Customer customer = taloolService.getCustomerByEmail(token.getEmail());
			ConversionUtil.copyFromThrift(cust_t, customer);
			taloolService.save(customer);

		}
		catch (Exception ex)
		{
			LOG.error("Problem saving customer: " + ex, ex);
			throw new ServiceException_t(102, "Problem saving customer");
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
						"CustomerId %s getting acquired merchants with no searchOptions %s",
						token.getAccountId()));
			}

		}

		try
		{
			merchants = taloolService.getMerchantAcquires(UUID.fromString(token.getAccountId()),
					ConversionUtil.copyFromThrift(searchOptions));
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
			dealAcquires = taloolService.getDealAcquires(UUID.fromString(token.getAccountId()),
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
	public void redeem(final String dealAcquireId, final double latitude, final double longitude)
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
			final DealAcquire dealAcquire = taloolService.getDealAcquire(UUID.fromString(dealAcquireId));

			taloolService.redeemDeal(dealAcquire, UUID.fromString(token.getAccountId()));
		}
		catch (ServiceException e)
		{
			LOG.error("There was a problem redeeming deal : " + dealAcquireId, e);
			throw new ServiceException_t(e.getType().getCode(), e.getMessage());
		}

	}
}
