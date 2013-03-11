package com.talool.service.util;

import java.util.Date;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.talool.api.thrift.CTokenAccess_t;
import com.talool.api.thrift.Customer_t;
import com.talool.api.thrift.Token_t;
import com.talool.core.TokenProviderException;
import com.talool.service.ThriftBasedTokenProvider;

/**
 * @author clintz
 * 
 */
public class TokenUtil
{
	private static final Logger LOG = LoggerFactory.getLogger(TokenUtil.class);
	private static final byte[] KEY_BYTES = Base64.decodeBase64("dGFsb29scm9ja3MxMzEz");

	private static final ThriftBasedTokenProvider<Token_t> tokenProvider = new ThriftBasedTokenProvider<Token_t>(
			Token_t.class, KEY_BYTES);

	public static CTokenAccess_t createTokenAccess(final Customer_t customer)
	{
		final Token_t token = new Token_t();
		token.setAccountId(String.valueOf(customer.getCustomerId()));
		token.setEmail(customer.getEmail());

		// for fun, expire in 3 months
		token.setExpires(DateUtils.addMonths(new Date(), 3).getTime());

		final CTokenAccess_t tokenAccess = new CTokenAccess_t();
		tokenAccess.setCustomer(customer);

		try
		{
			tokenAccess.setToken(tokenProvider.marshall(token));
		}
		catch (TokenProviderException e)
		{
			LOG.error("Problem creating token", e);
		}

		return tokenAccess;

	}

	public static Token_t getToken(final String tokenStr)
	{
		Token_t tokenT = null;

		try
		{
			tokenT = tokenProvider.unmarshall(tokenStr);

		}
		catch (TokenProviderException e)
		{

			LOG.error("Problem creating token", e);
		}
		return tokenT;

	}
}
