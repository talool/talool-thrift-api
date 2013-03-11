package com.talool.core;

/**
 * @author clintz
 * 
 */
public class TokenProviderException extends Exception
{

	private static final long serialVersionUID = 6331230203062430587L;

	public TokenProviderException()
	{
		super();

	}

	/**
	 * @param message
	 * @param cause
	 */
	public TokenProviderException(String message, Throwable cause)
	{
		super(message, cause);

	}

	/**
	 * @param message
	 */
	public TokenProviderException(String message)
	{
		super(message);

	}

	/**
	 * @param cause
	 */
	public TokenProviderException(Throwable cause)
	{
		super(cause);

	}

}
