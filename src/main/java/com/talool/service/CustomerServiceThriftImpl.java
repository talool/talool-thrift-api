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
import com.talool.core.FactoryManager;
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

	private static final transient TaloolService taloolService = FactoryManager.get()
			.getServiceFactory().getTaloolService();

	@Override
	public void addSocialAccount(final SocialAccount_t socialAccount_t) throws ServiceException_t,
			TException
	{
		final Token_t token = TokenUtil.getTokenFromRequest(true);

		if (LOG.isDebugEnabled())
		{
			LOG.debug(String.format("addSocialAccount request from %s with social %s", token.getEmail(),
					socialAccount_t.toString()));
		}

		try
		{
			final Customer cust = taloolService.getCustomerById(Long.valueOf(token.getAccountId()));
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
			LOG.debug("save received for customer: " + cust_t.toString());
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
}
