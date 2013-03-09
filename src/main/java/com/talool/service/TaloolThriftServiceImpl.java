package com.talool.service;

import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.talool.thrift.ServiceException;
import com.talool.thrift.TCustomer;
import com.talool.thrift.TaloolService;

/**
 * 
 * 
 * 
 * @author clintz
 */
public class TaloolThriftServiceImpl implements TaloolService.Iface
{
	private static final Logger LOG = LoggerFactory.getLogger(TaloolServiceImpl.class);

	private static final transient com.talool.core.service.TaloolService taloolService = ServiceFactory.get()
			.getTaloolService();

	@Override
	public void registerCustomer(final TCustomer customer, final String password) throws ServiceException, TException
	{
		LOG.info("Received registerCustomer :" + customer);

		try
		{
			taloolService.registerCustomer(ConversionUtil.convertFromThrift(customer), password);
		}
		catch (Exception e)
		{
			LOG.error("Problem registering customer: " + e);
			throw new ServiceException(1000, e.getLocalizedMessage());
		}

	}

	@Override
	public TCustomer authCustomer(String email, String password) throws ServiceException, TException
	{
		try
		{
			com.talool.core.Customer cust = taloolService.authCustomer(email, password);

			return ConversionUtil.convertToThrift(cust);

		}
		catch (Exception e)
		{
			LOG.error("Problem authenticatiing customer: " + e, e);
			throw new ServiceException(1000, e.getLocalizedMessage());
		}

	}
}
