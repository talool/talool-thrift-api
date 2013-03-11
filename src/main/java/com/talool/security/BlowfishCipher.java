package com.talool.security;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author clintz
 * 
 */
public class BlowfishCipher
{
	private static final String ALGORITHM = "Blowfish";

	public static byte[] encrypt(final byte[] toEncrypt, final byte[] strkey) throws CipherException
	{
		try
		{
			final SecretKeySpec key = new SecretKeySpec(strkey, ALGORITHM);
			Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(Cipher.ENCRYPT_MODE, key);
			return cipher.doFinal(toEncrypt);
		}
		catch (Exception e)
		{
			throw new CipherException(e.getLocalizedMessage(), e);
		}
	}

	public static byte[] decrypt(final byte[] toDecrypt, final byte[] strkey) throws CipherException
	{
		try
		{
			SecretKeySpec key = new SecretKeySpec(strkey, ALGORITHM);
			Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(Cipher.DECRYPT_MODE, key);
			return cipher.doFinal(toDecrypt);
		}
		catch (Exception e)
		{
			throw new CipherException(e.getLocalizedMessage(), e);
		}

	}

}
