package com.talool.service;

import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.server.TServlet;

import com.talool.thrift.TaloolService;

/**
 * 
 * 
 * 
 * @author clintz
 */
public class TServletTaloolService extends TServlet
{

	private static final long serialVersionUID = 2766746006277115123L;

	public TServletTaloolService()
	{
		super(new TaloolService.Processor(new TaloolThriftServiceImpl()),
				new TCompactProtocol.Factory());
	}
}
