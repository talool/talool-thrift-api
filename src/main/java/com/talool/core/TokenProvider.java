package com.talool.core;

/**
 * @author clintz
 * 
 */
public interface TokenProvider<T>
{
	public String marshall(T t) throws TokenProviderException;

	public T unmarshall(final String token) throws TokenProviderException;

}
