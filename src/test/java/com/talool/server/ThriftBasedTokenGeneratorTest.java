/**
 * Copyright 2011, Comcast Corporation. This software and its contents are
 * Comcast confidential and proprietary. It cannot be used, disclosed, or
 * distributed without Comcast's prior written permission. Modification of this
 * software is only allowed at the direction of Comcast Corporation. All allowed
 * modifications must be provided to Comcast Corporation.
 */
package com.talool.server;

import org.junit.Assert;
import org.junit.Test;

import com.talool.api.thrift.Address_t;
import com.talool.api.thrift.CTokenAccess_t;
import com.talool.api.thrift.Customer_t;
import com.talool.api.thrift.Token_t;
import com.talool.core.TokenProviderException;
import com.talool.service.ThriftBasedTokenProvider;
import com.talool.service.util.TokenUtil;

/**
 * @author clintz
 * 
 */
public class ThriftBasedTokenGeneratorTest
{

	@Test
	public void testToken()
	{
		Long accountId = 988192001781l;
		Customer_t cust1 = new Customer_t("Chris1", "Lintz1", "Christopher.justin@gmail.com");
		cust1.setCustomerId(accountId);

		CTokenAccess_t cToken = TokenUtil.createTokenAccess(cust1);

		String tokenStr = cToken.getToken();

		Token_t token = TokenUtil.getToken(tokenStr);

		Assert.assertEquals(String.valueOf(accountId), token.getAccountId());
		Assert.assertEquals(String.valueOf("Christopher.justin@gmail.com"), token.getEmail());

	}

	@Test
	public void testCustomerToken()
	{
		Customer_t cust1 = new Customer_t("Chris1", "Lintz1", "Christopher.justin@gmail.com");

		final ThriftBasedTokenProvider<Customer_t> tokenProvider = new ThriftBasedTokenProvider<Customer_t>(Customer_t.class,
				"myKey".getBytes());

		try
		{
			String token = tokenProvider.marshall(cust1);

			System.out.println(token);

			Customer_t cust2 = tokenProvider.unmarshall(token);

			Assert.assertEquals(cust1, cust2);
		}
		catch (TokenProviderException e)
		{
			e.printStackTrace();
		}

	}

	@Test
	public void testAddressToken()
	{
		Address_t addr1 = new Address_t();
		addr1.setAddress1("12661 Hunter lane");
		addr1.setAddress2("Apt 504");

		addr1.setCity("Denver");
		addr1.setStateProvinceCounty("CO");
		addr1.setCountry("US");
		addr1.setZip("80218");

		final ThriftBasedTokenProvider<Address_t> tokenProvider = new ThriftBasedTokenProvider<Address_t>(Address_t.class,
				"myKey".getBytes());

		try
		{
			String token = tokenProvider.marshall(addr1);

			System.out.println(token);

			Address_t addr2 = tokenProvider.unmarshall(token);

			Assert.assertEquals(addr1, addr2);
		}
		catch (TokenProviderException e)
		{
			e.printStackTrace();
		}

	}
}
