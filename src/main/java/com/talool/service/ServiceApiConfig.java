package com.talool.service;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

/**
 * Service configuration for Talool Java services
 * 
 * @author clintz
 * 
 */
public class ServiceApiConfig extends PropertiesConfiguration
{
	private static ServiceApiConfig instance;

	private static final String ALLOWED_HEALTH_CHECK_IPS = "allowable.health.check.ips";

	private ServiceApiConfig(String file) throws ConfigurationException
	{
		super(file);
	}

	public static ServiceApiConfig get()
	{
		return instance;
	}

	public String[] getAllowableHealthCheckIps()
	{
		return getStringArray(ALLOWED_HEALTH_CHECK_IPS);
	}

	public static synchronized ServiceApiConfig createInstance(final String propertyFile)
	{
		if (instance == null)
		{
			try
			{
				instance = new ServiceApiConfig(propertyFile);
			}
			catch (ConfigurationException ex)
			{
				if (instance == null)
				{
					throw new AssertionError(ex.getLocalizedMessage());
				}
			}
		}

		return instance;
	}
}
