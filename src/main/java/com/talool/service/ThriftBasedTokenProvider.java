package com.talool.service;

import org.apache.commons.codec.binary.Base64;
import org.apache.thrift.TBase;
import org.apache.thrift.protocol.TBinaryProtocol;

import com.talool.core.TokenProvider;
import com.talool.core.TokenProviderException;
import com.talool.security.BlowfishCipher;
import com.talool.thrift.ThriftUtil;

/**
 * A token provider implementation supporting any Thrift object. The thrift
 * object is encyrpted with Blowfish. The token string is Base64 encoded.
 * 
 * @author clintz
 * 
 */
@SuppressWarnings("rawtypes")
public class ThriftBasedTokenProvider<T extends TBase> implements TokenProvider<T>
{
	private final byte[] key;
	private final Class<T> clazz;

	public ThriftBasedTokenProvider(Class<T> clazz, final byte[] key)
	{
		this.clazz = clazz;
		this.key = key;
	}

	@Override
	public String marshall(T t) throws TokenProviderException
	{
		byte[] custBytes = null;
		String token = null;

		try
		{
			custBytes = ThriftUtil.serialize(t, new TBinaryProtocol.Factory());

			token = Base64.encodeBase64URLSafeString(BlowfishCipher.encrypt(custBytes, key));
			// token = Base64.encodeBase64String(BlowfishCipher.encrypt(custBytes,
			// key));
		}
		catch (Exception e)
		{
			throw new TokenProviderException("Problem marshaling token", e);
		}

		return token;
	}

	@Override
	public T unmarshall(final String token) throws TokenProviderException
	{
		T thriftObj = null;

		if (token == null)
		{
			return null;
		}
		try
		{
			thriftObj = clazz.newInstance();

			final byte[] bytesToken = BlowfishCipher.decrypt(Base64.decodeBase64(token), key);
			ThriftUtil.deserialize(bytesToken, thriftObj, new TBinaryProtocol.Factory());
		}
		catch (Exception e)
		{
			throw new TokenProviderException("Problem marshaling token", e);
		}

		return thriftObj;
	}

}
