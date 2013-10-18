package com.talool.service.util;

import com.talool.api.thrift.TNotFoundException_t;
import com.talool.api.thrift.TServiceException_t;
import com.talool.api.thrift.TUserException_t;
import com.talool.core.service.InvalidInputException;
import com.talool.core.service.NotFoundException;
import com.talool.core.service.ServiceException;

/**
 * Simple translation of Talool service errors to Thrift errors
 * 
 * @author clintz
 * 
 */
public final class ExceptionUtil
{
	/**
	 * Guaranteed to never be null. Any unknown translations will result in
	 * ErrorCode_t.UNKNOWN and logged appropriately
	 * 
	 * @param errorCode
	 * @return ErrorCode_t
	 */

	public static TServiceException_t safelyTranslate(final ServiceException e)
	{
		return new TServiceException_t(e.getErrorCode().getCode()).setMessage(e.getMessage());
	}

	public static TNotFoundException_t safelyTranslate(final NotFoundException e)
	{
		return new TNotFoundException_t().setIdentifier(e.getIdentifier()).setKey(e.getParameter());
	}

	public static TUserException_t safelyTranslate(final InvalidInputException e)
	{
		return new TUserException_t(e.getErrorCode().getCode()).setParam(e.getParameter());
	}

}
