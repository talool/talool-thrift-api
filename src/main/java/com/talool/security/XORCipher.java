/**
 * Copyright 2011, Comcast Corporation. This software and its contents are
 * Comcast confidential and proprietary. It cannot be used, disclosed, or
 * distributed without Comcast's prior written permission. Modification of this
 * software is only allowed at the direction of Comcast Corporation. All allowed
 * modifications must be provided to Comcast Corporation.
 */
package com.talool.security;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * @author clintz
 * 
 */
@SuppressWarnings("restriction")
public class XORCipher
{
	public static String encrypt(final byte[] toEncrypt, final byte[] strkey) throws CipherException
	{
		String encStr = null;

		try
		{

			byte[] encrypted = new byte[toEncrypt.length];
			for (int i = 0; i < toEncrypt.length; i++)
			{
				encrypted[i] = (byte) (toEncrypt[i] ^ strkey[i % strkey.length]);
			}

			BASE64Encoder encoder = new BASE64Encoder();
			encStr = encoder.encode(encrypted);
		}
		catch (Exception e)
		{
			throw new CipherException(e.getLocalizedMessage(), e);
		}

		return encStr;

	}

	public static byte[] decrypt(final String toDecrypt, final byte[] strkey) throws CipherException
	{
		BASE64Decoder decoder = new BASE64Decoder();
		byte[] decrypted = null;

		try
		{
			byte[] decoded = decoder.decodeBuffer(toDecrypt);

			// XOR descramble
			decrypted = new byte[decoded.length];
			for (int i = 0; i < decoded.length; i++)
			{
				decrypted[i] = (byte) (decoded[i] ^ strkey[i % strkey.length]);
			}

		}
		catch (Exception e)
		{
			throw new CipherException(e.getLocalizedMessage(), e);
		}

		return decrypted;

	}

}
