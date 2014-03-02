package com.talool.servlet;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Set;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.talool.core.service.ServiceException;
import com.talool.service.ServiceFactory;
import com.talool.service.mail.EmailCategory;

/**
 * A Sendgrid Webhook servlet
 * 
 * Please reference the API to understand what is hooked in this servlet
 * 
 * http://sendgrid.com/docs/API_Reference/Webhooks/event.html
 * 
 * @author clintz
 * 
 */
public class SendgridWebhookServlet extends HttpServlet
{
	private static final Logger LOG = LoggerFactory.getLogger(SendgridWebhookServlet.class);
	private static final long serialVersionUID = -5996117642251561127L;

	public static class SendgridEvent
	{
		public String event;
		public String email;
		public String status;
		public String reason;
		public Long timestamp;
		public Set<String> category;
		public String emailGiftId;
		public String merchantId;
	}

	private enum SendGridSupportedEvent
	{
		dropped, bounce
	};

	public void init() throws ServletException
	{}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		String json = null;
		final StringBuilder sb = new StringBuilder();

		response.setContentType("text/html");

		@SuppressWarnings("rawtypes")
		Enumeration enumeraton = request.getHeaderNames();

		if (LOG.isDebugEnabled())
		{
			while (enumeraton.hasMoreElements())
			{
				String name = (String) enumeraton.nextElement();
				sb.append(String.format("headerName: %s headerVal:%s ", name, request.getHeader(name)));
			}

			LOG.debug(sb.toString());
		}

		try
		{
			json = IOUtils.toString(request.getInputStream());

			parseJson(json);

		}
		catch (IOException e)
		{
			LOG.error("Problem getting json :" + e.getLocalizedMessage(), e);
			response.setStatus(500);
		}

	}

	private void parseJson(final String json)
	{
		final Gson gson = new GsonBuilder().setPrettyPrinting().create();
		final SendgridEvent[] events = gson.fromJson(json, SendgridEvent[].class);
		handleEvents(events);

		if (LOG.isDebugEnabled())
		{
			LOG.debug(gson.toJson(events));
		}

	}

	public void handleEvents(final SendgridEvent[] events)
	{

		for (SendgridEvent evt : events)
		{
			final SendGridSupportedEvent supportedEvent = SendGridSupportedEvent.valueOf(evt.event.toLowerCase());

			if (supportedEvent == null)
			{
				LOG.warn("Receiving event " + evt.event.toLowerCase() + " but not supported yet");
				continue; // we don't support it yet
			}

			switch (supportedEvent)
			{
				case bounce:
				case dropped:

					if (evt.category.contains(EmailCategory.Gift.toString()))
					{
						handleGiftEmailBounce(evt);
						return;
					}

					if (evt.category.contains(EmailCategory.PasswordRecovery.toString()) ||
							evt.category.contains(EmailCategory.Customer.toString()))
					{
						handlCustomerEmailBounce(evt);
						return;
					}

					if (evt.category.contains(EmailCategory.Merchant.toString()))
					{
						handlMerchantEmailBounce(evt);
						return;
					}

					break;

				default:
					LOG.warn("Supported event " + supportedEvent + " not wired up");
			}

		}
	}

	private void handleGiftEmailBounce(final SendgridEvent event)
	{
		if (event.emailGiftId != null)
		{
			try
			{
				if (LOG.isErrorEnabled())
				{
					LOG.debug("Sending giftId back :" + event.emailGiftId);
				}

				ServiceFactory.get().getCustomerService().giveGiftBackToGiver(UUID.fromString(event.emailGiftId), "bounced email");
			}
			catch (ServiceException e)
			{
				LOG.error("Problem giveGiftBackToGiver :" + e.getLocalizedMessage(), e);
			}
		}
	}

	private void handlCustomerEmailBounce(final SendgridEvent event)
	{

		try
		{
			if (LOG.isErrorEnabled())
			{
				LOG.debug("Marking customer email as invalid :" + event.email);
			}

			ServiceFactory.get().getTaloolService().setIsCustomerEmailValid(event.email, false);
		}
		catch (Exception e)
		{
			LOG.error("Problem with handling bounced customer email", e);
		}

	}

	private void handlMerchantEmailBounce(final SendgridEvent event)
	{

		try
		{
			if (LOG.isErrorEnabled())
			{
				LOG.debug("Marking merchantLocation email as invalid :" + event.email);
			}

			ServiceFactory.get().getTaloolService().setIsMerchantEmailValid(event.email, false);
		}
		catch (Exception e)
		{
			LOG.error("Problem with handling bounced customer email", e);
		}

	}

	public void destroy()
	{}
}