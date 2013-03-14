package com.talool.service;

import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.talool.api.thrift.CTokenAccess_t;
import com.talool.api.thrift.CustomerService_t;
import com.talool.api.thrift.Customer_t;
import com.talool.api.thrift.ServiceException_t;
import com.talool.api.thrift.SocialAccount_t;
import com.talool.api.thrift.Token_t;
import com.talool.core.AccountType;
import com.talool.core.Customer;
import com.talool.core.SocialAccount;
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

	private static final transient TaloolService taloolService = ServiceFactory.get().getTaloolService();

	@Override
	public CTokenAccess_t newToken() throws ServiceException_t, TException
	{
		final Token_t token = TokenUtil.getTokenFromRequest(true);
		Customer cust = null;

		try
		{
			cust = taloolService.getCustomerById(Long.valueOf(token.getAccountId()));
		}
		catch (Exception ex)
		{
			throw new ServiceException_t(1007, "Problem with token");
		}

		if (cust == null)
		{
			throw new ServiceException_t(1006, "Invalid token (lookup failed)");
		}

		return TokenUtil.createTokenAccess(ConversionUtil.convertToThrift(cust));

	}

	@Override
	public void addSocialAccount(final SocialAccount_t socialAccount_t) throws ServiceException_t, TException
	{
		final Token_t token = TokenUtil.getTokenFromRequest(true);

		try
		{
			final Customer cust = taloolService.getCustomerById(Long.valueOf(token.getAccountId()));
			final SocialAccount sac = taloolService.newSocialAccount(socialAccount_t.getSocalNetwork().name(), AccountType.CUS);

			ConversionUtil.copyFromThrift(socialAccount_t, sac, cust.getId());
			cust.addSocialAccount(sac);
			taloolService.save(cust);
		}
		catch (Exception e)
		{
			LOG.error("Problem registering customer: " + e);
			throw new ServiceException_t(1000, e.getLocalizedMessage());
		}

	}

	@Override
	public CTokenAccess_t createAccount(Customer_t customer, String password) throws ServiceException_t, TException
	{
		CTokenAccess_t token = null;

		if (LOG.isDebugEnabled())
		{
			LOG.debug("Received registerCustomer :" + customer);
		}

		try
		{
			taloolService.createAccount(ConversionUtil.convertFromThrift(customer), password);
			token = TokenUtil.createTokenAccess(customer);
		}
		catch (Exception e)
		{
			LOG.error("Problem registering customer: " + e);
			throw new ServiceException_t(1000, e.getLocalizedMessage());
		}

		return token;
	}

	@Override
	public CTokenAccess_t authenticate(final String email, final String password) throws ServiceException_t, TException
	{
		CTokenAccess_t token = null;

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
}
