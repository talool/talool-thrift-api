package com.talool.service;

import javax.servlet.http.HttpServletRequest;

/**
 * Thread local class exposing the Servlet request
 * 
 * @author clintz
 * 
 */
public class RequestUtils
{
	private static ThreadLocal<HttpServletRequest> requests = new ThreadLocal<HttpServletRequest>();

	public static void setRequest(HttpServletRequest req)
	{
		requests.set(req);
	}

	public static HttpServletRequest getRequest()
	{
		return requests.get();
	}

	public static void removeRequest()
	{
		requests.remove();

	}
}
