package com.talool.service;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.talool.api.thrift.CustomerService_t;
import com.talool.core.Merchant;
import com.talool.core.service.ServiceException;

/**
 * 
 * 
 * 
 * @author clintz
 */
public class TServletTaloolService extends TServlet
{
	private static final Logger LOG = LoggerFactory.getLogger(TServletTaloolService.class);
	private static final long serialVersionUID = 2766746006277115123L;
	private static String[] HEALTH_IP_SOURCES = { "10.14", "127.0", "0:0" };

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		RequestUtils.setRequest(request);
		super.doPost(request, response);
	}

	@Override
	protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException
	{
		if (request.getParameterMap().size() > 0)
		{
			doHealthCheck(request, response);
		}
		else
		{
			RequestUtils.setRequest(request);
			super.doGet(request, response);
		}
	}

	public void doHealthCheck(final HttpServletRequest request, final HttpServletResponse response)
	{
		boolean isInternalRequest = false;

		for (String ipStartsWith : HEALTH_IP_SOURCES)
		{
			if (request.getRemoteAddr().startsWith(ipStartsWith))
			{
				isInternalRequest = true;
			}
		}

		if (LOG.isDebugEnabled())
		{
			LOG.debug("Health check from IP: " + request.getRemoteAddr());
		}

		if (isInternalRequest == false)
		{
			return;
		}

		response.setContentType("text/html");
		PrintWriter out;
		try
		{
			final List<Merchant> merchants = ServiceFactory.get().getTaloolService().getMerchantByName("Talool");
			out = response.getWriter();
			out.println(merchants.get(0).getName());
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (ServiceException e)
		{
			e.printStackTrace();
		}
	}

	public TServletTaloolService()
	{
		super(new CustomerService_t.Processor<CustomerServiceThriftImpl>(new CustomerServiceThriftImpl()),
				new TBinaryProtocol.Factory());
	}
}
