/**
 * Copyright 2011, Comcast Corporation. This software and its contents are
 * Comcast confidential and proprietary. It cannot be used, disclosed, or
 * distributed without Comcast's prior written permission. Modification of this
 * software is only allowed at the direction of Comcast Corporation. All allowed
 * modifications must be provided to Comcast Corporation.
 */
package com.talool.server;

import java.util.UUID;

import org.apache.commons.codec.binary.Base64;
import org.junit.Assert;
import org.junit.Test;

import com.talool.api.thrift.CTokenAccess_t;
import com.talool.api.thrift.Customer_t;
import com.talool.api.thrift.Token_t;
import com.talool.core.TokenProviderException;
import com.talool.security.BlowfishCipher;
import com.talool.security.CipherException;
import com.talool.service.ThriftBasedTokenProvider;
import com.talool.service.util.TokenUtil;

/**
 * @author clintz
 * 
 */
public class ThriftBasedTokenGeneratorTest
{

	@Test
	public void base64Test() throws CipherException
	{
		System.out.println(Base64.encodeBase64URLSafeString(
				BlowfishCipher.encrypt("89e24a09-0a02-4ec9-987f-3a2aed9e8b90".getBytes(),
						"chris".getBytes())));

	}

	@Test
	public void testToken()
	{
		UUID accountId = UUID.randomUUID();
		Customer_t cust1 = new Customer_t("Chris1", "Lintz1", "Christopher.justin@gmail.com");
		cust1.setCustomerId(accountId.toString());

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

		final ThriftBasedTokenProvider<Customer_t> tokenProvider = new ThriftBasedTokenProvider<Customer_t>(
				Customer_t.class, "myKey".getBytes());

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

}
