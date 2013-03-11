package com.talool.service;

import javax.servlet.http.HttpServletRequest;

import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.talool.api.thrift.CTokenAccess_t;
import com.talool.api.thrift.CustomerService_t;
import com.talool.api.thrift.Customer_t;
import com.talool.api.thrift.ServiceException_t;
import com.talool.api.thrift.SocialAccount_t;
import com.talool.core.Customer;
import com.talool.service.util.TokenUtil;

/**
 * 
 * 
 * 
 * @author clintz
 */
public class CustomerServiceThriftImpl implements CustomerService_t.Iface
{
	private static final Logger LOG = LoggerFactory.getLogger(TaloolServiceImpl.class);

	private static final transient com.talool.core.service.TaloolService taloolService = ServiceFactory.get()
			.getTaloolService();

	@Override
	public CTokenAccess_t newToken() throws ServiceException_t, TException
	{

		return null;
	}

	@Override
	public void addSocialAccount(String email, SocialAccount_t socialAccount) throws ServiceException_t, TException
	{

	}

	@Override
	public CTokenAccess_t createAccount(Customer_t customer, String password) throws ServiceException_t, TException
	{
		CTokenAccess_t token = null;

		LOG.info("Received registerCustomer :" + customer);

		try
		{
			taloolService.registerCustomer(ConversionUtil.convertFromThrift(customer), password);
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
	public CTokenAccess_t authenticate(String email, String password) throws ServiceException_t, TException
	{
		CTokenAccess_t token = null;
		final HttpServletRequest request = RequestUtils.getRequest();
		final String taloolToken = request.getHeader("ttok");

		try
		{
			final Customer customer = taloolService.authCustomer(email, password);
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
}
