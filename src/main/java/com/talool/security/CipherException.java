package com.talool.security;

/**
 * @author clintz
 * 
 */
public class CipherException extends Exception
{
	private static final long serialVersionUID = -1330905435698990084L;

	/**
	 * 
	 */
	public CipherException()
	{
		super();
	}

	/**
	 * @param message
	 * @param cause
	 */
	public CipherException(String message, Throwable cause)
	{
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public CipherException(String message)
	{
		super(message);
	}

	/**
	 * @param cause
	 */
	public CipherException(Throwable cause)
	{
		super(cause);
	}

}
