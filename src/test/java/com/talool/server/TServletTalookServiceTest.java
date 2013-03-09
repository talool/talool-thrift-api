package com.talool.server;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.THttpClient;
import org.apache.thrift.transport.TTransportException;

import com.talool.thrift.ServiceException;
import com.talool.thrift.TCustomer;
import com.talool.thrift.TSex;
import com.talool.thrift.TaloolService;

public class TServletTalookServiceTest
{
	static String servletUrl = "http://localhost:8080/talool";

	public static void main(String args[]) throws TTransportException, TException
	{

		THttpClient thc = new THttpClient(servletUrl);
		TProtocol loPFactory = new TBinaryProtocol(thc);
		TaloolService.Client client = new TaloolService.Client(loPFactory);

		TCustomer customer = new TCustomer();
		customer.setFirstName("Christopher");
		customer.setLastName("Lintz");
		customer.setSex(TSex.M);
		customer.setEmail("christopher5.justin@gmail.com");

		try
		{
			// client.registerCustomer(customer, "pass123");
			customer = client.authCustomer("christopher5.justin@gmail.com", "pass123");
			System.out.println(customer);
		}
		catch (ServiceException e)
		{
			System.out.println(String.format("Error registering %d %s", e.getErrorCode(), e.getErrorDesc()));
		}

	}
}
