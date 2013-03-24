package com.talool.service;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TServlet;

import com.talool.api.thrift.CustomerService_t;

/**
 * 
 * 
 * 
 * @author clintz
 */
public class TServletTaloolService extends TServlet
{

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		RequestUtils.setRequest(request);
		super.doPost(request, response);
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		RequestUtils.setRequest(request);
		super.doGet(request, response);
	}

	private static final long serialVersionUID = 2766746006277115123L;

	public TServletTaloolService()
	{
		super(new CustomerService_t.Processor<CustomerServiceThriftImpl>(new CustomerServiceThriftImpl()), new TBinaryProtocol.Factory());
	}
}
