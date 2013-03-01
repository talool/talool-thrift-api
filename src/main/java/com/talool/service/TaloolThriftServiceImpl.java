package com.talool.service;

import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.talool.thrift.Customer;
import com.talool.thrift.ServiceException;
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

	private static final transient com.talool.core.service.TaloolService taloolService = ServiceFactory
			.get().getTaloolService();

	@Override
	public void registerCustomer(final Customer customer, final String password)
			throws ServiceException, TException
	{
		LOG.info("Received registerCustomer :" + customer);

		try
		{
			taloolService.registerCustomer(taloolService.newCustomer(customer), password);
		}
		catch (Exception e)
		{
			LOG.error("Problem registering customer: " + e);
			throw new ServiceException(1000, e.getLocalizedMessage());
		}

	}
}
