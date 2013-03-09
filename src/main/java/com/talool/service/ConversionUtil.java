/**
 * Copyright 2011, Comcast Corporation. This software and its contents are
 * Comcast confidential and proprietary. It cannot be used, disclosed, or
 * distributed without Comcast's prior written permission. Modification of this
 * software is only allowed at the direction of Comcast Corporation. All allowed
 * modifications must be provided to Comcast Corporation.
 */
package com.talool.service;

import com.talool.core.Customer;
import com.talool.core.Sex;
import com.talool.thrift.TCustomer;
import com.talool.thrift.TSex;

/**
 * @author clintz
 * 
 */
public final class ConversionUtil
{
	/**
	 * TODO - THis needs work - we need to attach to a hibernate session (detached
	 * object)
	 * 
	 * @param thriftCustomer
	 * @return
	 */
	public static Customer convertFromThrift(TCustomer thriftCustomer)
	{
		final Customer cust = ServiceFactory.get().getTaloolService().newCustomer();

		cust.setEmail(thriftCustomer.getEmail());
		cust.setFirstName(thriftCustomer.getFirstName());
		cust.setLastName(thriftCustomer.getLastName());

		if (thriftCustomer.getSex() != null)
		{
			cust.setSex(Sex.valueByLetter(thriftCustomer.getSex().name()));
		}

		return cust;
	}

	public static TCustomer convertToThrift(Customer customer)
	{
		final TCustomer thriftCust = new TCustomer();
		thriftCust.setCustomerId(customer.getId());
		thriftCust.setEmail(customer.getEmail());
		thriftCust.setFirstName(customer.getFirstName());
		thriftCust.setLastName(customer.getLastName());
		thriftCust.setCreated(customer.getCreated().getTime());
		thriftCust.setUpdated(customer.getUpdated().getTime());

		if (customer.getSex() != null)
		{
			thriftCust.setSex(TSex.valueOf(customer.getSex().getLetter()));
		}

		return thriftCust;
	}

}
