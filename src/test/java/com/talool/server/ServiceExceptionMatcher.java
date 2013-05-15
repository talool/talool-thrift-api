package com.talool.server;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import com.talool.api.thrift.ServiceException_t;

/**
 * 
 * @author clintz
 * 
 */
public class ServiceExceptionMatcher extends TypeSafeMatcher<ServiceException_t>
{
	private int foundErrorCode;
	private final int expectedCode;

	public ServiceExceptionMatcher(int expectedCode)
	{
		this.expectedCode = expectedCode;
	}

	@Override
	public void describeTo(Description description)
	{
		description.appendValue(foundErrorCode).appendText(" was not found instead of ").appendValue(expectedCode);
	}

	@Override
	protected boolean matchesSafely(ServiceException_t exception)
	{
		foundErrorCode = exception.getErrorCode();
		return foundErrorCode == expectedCode;
	}
}
