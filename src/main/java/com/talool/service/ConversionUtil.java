/**
 * Copyright 2011, Comcast Corporation. This software and its contents are
 * Comcast confidential and proprietary. It cannot be used, disclosed, or
 * distributed without Comcast's prior written permission. Modification of this
 * software is only allowed at the direction of Comcast Corporation. All allowed
 * modifications must be provided to Comcast Corporation.
 */
package com.talool.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import com.talool.api.thrift.Customer_t;
import com.talool.api.thrift.Sex_t;
import com.talool.api.thrift.SocialAccount_t;
import com.talool.api.thrift.SocialNetwork_t;
import com.talool.core.AccountType;
import com.talool.core.Customer;
import com.talool.core.Sex;
import com.talool.core.SocialAccount;
import com.talool.core.SocialNetwork;
import com.talool.core.service.ServiceException;

/**
 * @author clintz
 * 
 */
public final class ConversionUtil
{
	private static final Logger LOG = LoggerFactory.getLogger(ConversionUtil.class);

	/**
	 * TODO - THis needs work - we need to attach to a hibernate session (detached
	 * object)
	 * 
	 * @param thriftCustomer
	 * @return
	 */
	public static Customer convertFromThrift(Customer_t thriftCustomer)
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

	public static void copyFromThrift(final Customer_t thriftCustomer, final Customer cust) throws ServiceException
	{
		cust.setEmail(thriftCustomer.getEmail());
		cust.setFirstName(thriftCustomer.getFirstName());
		cust.setLastName(thriftCustomer.getLastName());

		if (thriftCustomer.getSex() != null)
		{
			cust.setSex(Sex.valueByLetter(thriftCustomer.getSex().name()));
		}

		final Map<SocialNetwork, SocialAccount> socialAccounts = cust.getSocialAccounts();

		if (thriftCustomer.getSocialAccounts() != null)
		{
			for (final Entry<SocialNetwork_t, SocialAccount_t> sac : thriftCustomer.getSocialAccounts().entrySet())
			{
				final SocialAccount_t sAcnt_t = sac.getValue();

				final SocialNetwork socialNetwork = ServiceFactory.get().getTaloolService()
						.getSocialNetwork(sAcnt_t.getSocalNetwork().name());

				SocialAccount sAccnt = socialAccounts.get(socialNetwork);

				// create new account or update exisitng
				if (sAccnt == null)
				{
					LOG.info("No social account for: " + sAcnt_t.getSocalNetwork().name());
					sAccnt = ServiceFactory.get().getTaloolService().newSocialAccount();
					cust.addSocialAccount(sAccnt);
				}

				copyFromThrift(sAcnt_t, sAccnt, AccountType.CUS, socialNetwork, cust.getId());

			}

		}

	}

	public static void copyFromThrift(final SocialAccount_t thriftSocialAccnt, final SocialAccount socialAccnt,
			final AccountType accountType, SocialNetwork socialNetwork, final Long userId) throws ServiceException
	{
		socialAccnt.setAccountType(accountType);
		socialAccnt.setLoginId(thriftSocialAccnt.getLoginId());
		socialAccnt.setToken(thriftSocialAccnt.getToken());
		socialAccnt.setUserId(userId);
		socialAccnt.setSocialNetwork(socialNetwork);
	}

	public static Customer_t convertToThrift(Customer customer)
	{
		final Customer_t thriftCust = new Customer_t();
		thriftCust.setCustomerId(customer.getId());
		thriftCust.setEmail(customer.getEmail());
		thriftCust.setFirstName(customer.getFirstName());
		thriftCust.setLastName(customer.getLastName());
		thriftCust.setCreated(customer.getCreated().getTime());
		thriftCust.setUpdated(customer.getUpdated().getTime());

		if (customer.getSex() != null)
		{
			thriftCust.setSex(Sex_t.valueOf(customer.getSex().getLetter()));
		}

		final Map<SocialNetwork, SocialAccount> socialAccounts = customer.getSocialAccounts();

		if (!CollectionUtils.isEmpty(socialAccounts))
		{
			final Map<SocialNetwork_t, SocialAccount_t> accnts = new HashMap<SocialNetwork_t, SocialAccount_t>();

			for (final Entry<SocialNetwork, SocialAccount> sac : socialAccounts.entrySet())
			{
				final SocialAccount_t sa_t = new SocialAccount_t();
				sa_t.setToken(sac.getValue().getToken());
				sa_t.setLoginId(sac.getValue().getLoginId());
				sa_t.setCreated(sac.getValue().getCreated().getTime());
				sa_t.setUpdated(sac.getValue().getUpdated().getTime());
				final SocialNetwork_t snt = SocialNetwork_t.valueOf(sac.getKey().getName());
				sa_t.setSocalNetwork(snt);
				accnts.put(snt, sa_t);
			}

			thriftCust.setSocialAccounts(accnts);

		}

		return thriftCust;
	}
}
