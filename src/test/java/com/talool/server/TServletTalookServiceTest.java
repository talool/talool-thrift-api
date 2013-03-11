package com.talool.server;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.THttpClient;
import org.apache.thrift.transport.TTransportException;

import com.talool.api.thrift.CTokenAccess_t;
import com.talool.api.thrift.CustomerService_t;
import com.talool.api.thrift.Customer_t;
import com.talool.api.thrift.ServiceException_t;
import com.talool.api.thrift.Sex_t;

public class TServletTalookServiceTest
{
	static String servletUrl = "http://localhost:8080/talool";

	public static void main(String args[]) throws TTransportException, TException
	{

		THttpClient thc = new THttpClient(servletUrl);
		TProtocol loPFactory = new TBinaryProtocol(thc);
		CustomerService_t.Client client = new CustomerService_t.Client(loPFactory);

		Customer_t customer = new Customer_t();
		customer.setFirstName("Christopher");
		customer.setLastName("Lintz");
		customer.setSex(Sex_t.M);
		customer.setEmail("christopher5.justin@gmail.com");

		thc.setCustomHeader("ttok", "mytokenq348977823890234");

		try
		{
			// client.registerCustomer(customer, "pass123");
			CTokenAccess_t accessToken = client.authenticate("christopher5.justin@gmail.com", "pass123");
			System.out.println("Customer: " + accessToken.getCustomer());
			System.out.println("Token: " + accessToken.getToken());
		}
		catch (ServiceException_t e)
		{
			System.out.println(String.format("Error registering %d %s", e.getErrorCode(), e.getErrorDesc()));
		}

	}
}
