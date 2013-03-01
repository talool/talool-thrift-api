package com.talool.server;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.THttpClient;
import org.apache.thrift.transport.TTransportException;

import com.talool.thrift.Address;
import com.talool.thrift.Customer;
import com.talool.thrift.ServiceException;
import com.talool.thrift.TaloolService;

public class TServletTalookServiceTest
{
	static String servletUrl = "http://talool:8080/talool";

	public static void main(String args[]) throws TTransportException, TException
	{

		THttpClient thc = new THttpClient(servletUrl);
		TProtocol loPFactory = new TCompactProtocol(thc);
		TaloolService.Client client = new TaloolService.Client(loPFactory);

		final Address address = new Address();
		address.setAddress1("1267 Lafayette St.");
		address.setCity("Denver");
		address.setCountry("US");
		address.setStateProvinceCounty("CO");
		address.setZip("80218");

		final Customer customer = new Customer();
		customer.setFirstName("Christopher");
		customer.setPassword("pass123");
		customer.setLastName("Lintz");
		customer.setEmail("christopher.justin@gmail.com");
		customer.setAddress(address);

		try
		{
			client.registerCustomer(customer, "pass123");
		}
		catch (ServiceException e)
		{
			System.out.println(String.format("Error registering %d %s", e.getErrorCode(),
					e.getErrorDesc()));
		}

	}
}
